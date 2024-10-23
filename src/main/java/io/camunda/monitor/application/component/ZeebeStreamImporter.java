/**
 * Monitor the Stream inside Zeebe. Difference between the execution and the exporter
 */
package io.camunda.monitor.application.component;

/**
 * call zeebe-gateway:9600/actuator/prometheus
 * Format is
 * zeebe_exporter_last_exported_position{exporter="elasticsearch",partition="1",} 2295191.0
 */
public class ZeebeStreamImporter {
}
