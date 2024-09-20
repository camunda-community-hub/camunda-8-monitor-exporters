package io.camunda.monitor.application.result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResultPicture {
  LocalDateTime timeOfPicture;
private List<ResultComponent> resultComponentList = new ArrayList<>();

  public ResultPicture(LocalDateTime timeOfPicture) {
    this.timeOfPicture=timeOfPicture;
  }

  public void addResultComponent( ResultComponent resultComponent) {
    resultComponentList.add( resultComponent);
  }

  public ResultComponent getResultComponentPerImporter(ResultComponent.Importer importer) {
    for(ResultComponent resultComponent : resultComponentList) {
      if (resultComponent.getImporter() == importer)
        return resultComponent;
    }
      return null;
  }
}
