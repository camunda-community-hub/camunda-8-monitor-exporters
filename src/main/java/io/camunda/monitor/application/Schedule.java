package io.camunda.monitor.application;

import io.camunda.monitor.application.display.Display;
import io.camunda.monitor.application.display.DisplayLog;
import io.camunda.monitor.application.result.ResultAnalysis;
import io.camunda.monitor.application.result.ResultPicture;
import io.camunda.monitor.elasticsearch.ElasticSearchConnection;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component

public class Schedule {
  Logger logger = LoggerFactory.getLogger(Schedule.class.getName());

  @Value("${monitor.elasticsearch.host:localhost}")
  private String elasticSearchHost;
  @Value("${monitor.elasticsearch.port:9200}")
  private Integer  elasticSearchPort;
  @Value("${monitor.elasticsearch.protocol:http}")
  private String elasticSearchProtocol;
  @Value("${monitor.level:FULL}")
  private String level;

  Display display;

  private ResultAnalysis.LevelAnalysisLevel levelAnalysisLevel = ResultAnalysis.LevelAnalysisLevel.FULL;

  MonitorExporterAPI monitorExporterAPI;

  @PostConstruct
  public void postConstruction() {
    monitorExporterAPI = new MonitorExporterAPI();
    ElasticSearchConnection elasticSearchConnection = new ElasticSearchConnection();
    elasticSearchConnection.setConnection(elasticSearchHost, elasticSearchPort, elasticSearchProtocol);

    monitorExporterAPI.setElasticSearchConnection(elasticSearchConnection);

    try {
      levelAnalysisLevel = ResultAnalysis.LevelAnalysisLevel.valueOf(level);
    }
    catch (Exception e) {
      logger.error("Can't decode monitor.level: expect {},{} ", ResultAnalysis.LevelAnalysisLevel.FULL,
          ResultAnalysis.LevelAnalysisLevel.OVERVIEW);
      levelAnalysisLevel= ResultAnalysis.LevelAnalysisLevel.FULL;
    }

    display= new DisplayLog();
  }



  ResultAnalysis resultAnalysis = new ResultAnalysis();

  // every 15 seconds
  @Scheduled(fixedDelay = 15000)
  public void runTaskWithFixedDelay() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS");

    logger.info("Take a picture at {}", LocalDateTime.now().format(formatter));
    LocalDateTime timeOfPicture= LocalDateTime.now();
    ResultPicture result = monitorExporterAPI.getPicture(timeOfPicture.format(formatter), timeOfPicture);

    resultAnalysis.analysis(levelAnalysisLevel, result, display);

  }

}
