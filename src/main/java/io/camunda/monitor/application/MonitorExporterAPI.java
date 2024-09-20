package io.camunda.monitor.application;

import io.camunda.monitor.application.component.Importer;
import io.camunda.monitor.application.component.OperateImporter;
import io.camunda.monitor.application.result.ResultComponent;
import io.camunda.monitor.application.component.ZeebeImporter;
import io.camunda.monitor.application.result.ResultPicture;
import io.camunda.monitor.elasticsearch.ElasticSearchConnection;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * this class is the main access to the library
 */
public class MonitorExporterAPI {
  ElasticSearchConnection elasticSearchConnection;
  public void setElasticSearchConnection(ElasticSearchConnection elasticSearchConnection) {
    this.elasticSearchConnection = elasticSearchConnection;
  }

  private List<Importer> listComponent = Arrays.asList(new OperateImporter(), new ZeebeImporter());


  public ResultPicture getPicture(String resultName, LocalDateTime timeOfPicture) {
    ResultPicture result = new ResultPicture(timeOfPicture);
    for (Importer componentMonitor : listComponent) {
      ResultComponent resultComponent = componentMonitor.monitor(resultName, elasticSearchConnection);
      result.addResultComponent( resultComponent);
    }
    return result;
  }

}
