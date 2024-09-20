package io.camunda.monitor.application.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.monitor.application.result.ResultComponent;
import io.camunda.monitor.elasticsearch.ElasticSearchConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ZeebeImporter implements Importer {
  Logger logger = LoggerFactory.getLogger(ZeebeImporter.class.getName());

  // run the monitoring on Zeebe
  // complete the Result object
  public ResultComponent monitor(String resultName, ElasticSearchConnection elasticSearchConnection) {
    ResultComponent result = new ResultComponent(ResultComponent.Importer.ZEEBE);
    try (HttpClient client = HttpClient.newHttpClient();) {
      String dataOption = "" // EOL
          + "{\"size\": 0, " // EOL
          + " \"aggs\": "// EOL
          + "  {\"value_types\": " // EOL
          + "   {\"terms\": {\"field\": \"valueType\",\"size\": 20}," // EOL
          + "    \"aggs\": {\"partitions\": " + "                {\"terms\": {\"field\": \"partitionId\"},"  // EOL
          + "                 \"aggs\": " // EOL
          + "                  { \"min_sequence\": {\"min\": " + "{\"field\": \"sequence\"}}," // EOL
          + "                    \"max_sequence\": {\"max\": {\"field\": \"sequence\"}}" // EOL
          + "                  }" // EOL
          + "                }" // EOL
          + "              }" // EOD
          + "   }" // EOL
          + "  }" // EOL
          + "}";

      String url = elasticSearchConnection.getUrl("/zeebe-record*/_search");
      logger.debug("Zeebe Import URI[{}] Body[{}]", url, dataOption);
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(dataOption))
          .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      // Parse the JSON response using Jackson
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonResponse = objectMapper.readTree(response.body());

      JsonNode buckets = jsonResponse.path("aggregations").path("value_types").path("buckets");

      // search key "process-instance
      if (buckets.isArray()) {
        for (JsonNode bucket : buckets) {
          // Access the source of each hit and convert it to a Map
          ResultComponent.TypeSequence typeSequence = getTypeSequence(bucket.get("key").asText());
          if (typeSequence == null)
            continue;

          JsonNode partitions = bucket.path("partitions").path("buckets");
          if (partitions.isArray()) {
            for (JsonNode partition : partitions) {
              Integer partitionId = partition.get("key").asInt();
              JsonNode maxSequence = partition.path("max_sequence");
              Long sequence = maxSequence.get("value").asLong();
              result.logData(typeSequence, partitionId, sequence);

            }
          }
        }
      }
    } catch (IOException | InterruptedException e) {
      logger.error("Error during get OperateImporter {}", e);
      result.addError("During OperateImporter " + e.getMessage());
    }
    return result;

  }

  private ResultComponent.TypeSequence getTypeSequence(String key) {
    if ("PROCESS_INSTANCE".equals(key))
      return ResultComponent.TypeSequence.PROCESS_INSTANCE;
    if ("PROCESS_INSTANCE_CREATION".equals(key))
      return ResultComponent.TypeSequence.PROCESS_INSTANCE_CREATION;
    else if ("PROCESS".equals(key))
      return ResultComponent.TypeSequence.PROCESS;
    else if ("INCIDENT".equals(key))
      return ResultComponent.TypeSequence.INCIDENT;
    else if ("VARIABLE".equals(key))
      return ResultComponent.TypeSequence.VARIABLE;
    else if ("JOB".equals(key))
      return ResultComponent.TypeSequence.JOB;
    else if ("DEPLOYMENT".equals(key))
      return ResultComponent.TypeSequence.DEPLOYMENT;
    else if ("COMMAND_DISTRIBUTION".equals(key))
      return ResultComponent.TypeSequence.COMMAND_DISTRIBUTION;
    logger.info("unknown type [{}]", key);
    return null;
  }

}
