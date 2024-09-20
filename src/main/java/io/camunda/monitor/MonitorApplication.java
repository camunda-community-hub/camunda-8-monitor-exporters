package io.camunda.monitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Enable scheduling

public class MonitorApplication {
  public static void main(String[] args) {
    SpringApplication.run(MonitorApplication.class, args);
  }
}
