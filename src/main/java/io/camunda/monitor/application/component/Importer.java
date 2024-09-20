package io.camunda.monitor.application.component;

import io.camunda.monitor.application.result.ResultComponent;
import io.camunda.monitor.elasticsearch.ElasticSearchConnection;

public interface Importer {
  ResultComponent monitor(String resultName, ElasticSearchConnection elasticSearchConnection);
}
