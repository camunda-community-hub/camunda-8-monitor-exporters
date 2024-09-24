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
import java.util.StringTokenizer;

public class OperateImporter implements Importer {
  Logger logger = LoggerFactory.getLogger(OperateImporter.class.getName());

  // run the monitoring on Zeebe
  // complete the Result object
  public ResultComponent monitor(String resultName, ElasticSearchConnection elasticSearchConnection) {

    ResultComponent result = new ResultComponent(ResultComponent.Importer.OPERATE);
    HttpClient client = HttpClient.newHttpClient();
    try {
      String url = elasticSearchConnection.getUrl("/operate-import-position*/_search?size=300");
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
      logger.debug("Operate Import URI[{}] ", url);

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      // Parse the JSON response using Jackson
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonResponse = objectMapper.readTree(response.body());

      JsonNode hits = jsonResponse.path("hits").path("hits");

      // search key "process-instance
      if (hits.isArray()) {
        for (JsonNode hit : hits) {
          // Access the source of each hit and convert it to a Map
          JsonNode source = hit.path("_source");
          String id = source.get("id").asText("");
          ResultComponent.TypeSequence typeSequence = getTypeSequence(id);
          if (typeSequence == null)
            continue;

          StringTokenizer st = new StringTokenizer(id, "-");
          int partitionId = Integer.parseInt(st.nextToken());
          result.logData(typeSequence, partitionId, source.get("sequence").asLong());

        }
      }
    } catch (IOException | InterruptedException e) {
      logger.error("Error during get OperateImporter {}", e);
      result.addError("During OperateImporter " + e.getMessage());
    }
    return result;
  }

  private ResultComponent.TypeSequence getTypeSequence(String key) {
    if (key != null && key.endsWith("-process-instance"))
      return ResultComponent.TypeSequence.PROCESS_INSTANCE;
    else if (key != null && key.endsWith("-process"))
      return ResultComponent.TypeSequence.PROCESS;
    else if (key != null && key.endsWith("-incident"))
      return ResultComponent.TypeSequence.INCIDENT;
    else if (key != null && key.endsWith("-variable"))
      return ResultComponent.TypeSequence.VARIABLE;
    else if (key != null && key.endsWith("-job"))
      return ResultComponent.TypeSequence.JOB;
    else {
      logger.info("unknown type [{}]", key);
      return null;
    }
  }
}
