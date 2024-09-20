package io.camunda.monitor.application.display;


import java.time.LocalDateTime;
import java.util.List;

public interface Display {
  void displayLegend( List<Object> legend);
  void display(LocalDateTime time, List<Object> content);
}
