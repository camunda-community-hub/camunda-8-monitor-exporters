package io.camunda.monitor.application.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultComponent {
  private Importer typeImporter;

  private List<String> listErrors = new ArrayList<>();
private Map<TypeSequence,Map<Integer,Long>> recordPhoto = new HashMap<>();
  public enum TypeSequence { PROCESS_INSTANCE, PROCESS_INSTANCE_CREATION, PROCESS, INCIDENT, VARIABLE, JOB,
    COMMAND_DISTRIBUTION, DEPLOYMENT}
  public enum Importer { ZEEBE, OPERATE}

  private int maxPartitionsId =0;
  /**
   * Constructor
   * @param typeImporter type of importer
   */
  public ResultComponent(Importer typeImporter) {
    this.typeImporter=typeImporter;
  }

  /**
   * Log data
   * @param typeSequence tyoe of sequence
   * @param partitionId partitionID
   * @param sequence sequence
   */
  public void logData( TypeSequence typeSequence, int partitionId, long sequence)
  {
    Map<Integer, Long> bucket = recordPhoto.getOrDefault(typeSequence, new HashMap<>());
    bucket.put(partitionId,sequence);
    recordPhoto.put(typeSequence, bucket);
    if (partitionId> maxPartitionsId)
      maxPartitionsId =partitionId;
  }

  /**
   * Add an error
   * @param error error description
   */
  public void addError(String error) {
    listErrors.add( error);
  }

  /**
   * return the type of importer
   * @return
   */
  public Importer getImporter() {
   return typeImporter;
  }

  /**
   * Sum all sequences to get a sum of all sequence
   * to calculate the difference between two importer, we need to do
   * Zeebe.partition1 - Operate.partition1 + Zeebe.partition2-Operate.partition2
   * ==>
   * Zeebe.partition1+Zeebe.partition2 - Operate.partition1 - Operate.partition2
   * ==>
   * Zeebe.partition1+Zeebe.partition2 - (Operate.partition1 + Operate.partition2)
   * So this method return the sum
   * ==>
   * Zeebe.getImporterSequence() - Operate.getImporterSequence()
   *
   * @param typeSequence type of sequence to sum
   * @return the sum
   */
  public long getSumSequence(TypeSequence typeSequence) {
    Map<Integer, Long> bucket = recordPhoto.getOrDefault(typeSequence, new HashMap<>());
    return bucket.values()
        .stream()
        .mapToLong(Long::longValue)
        .sum();
  }

  public boolean existSequence(TypeSequence typeSequence) {
    return recordPhoto.containsKey(typeSequence);
  }

  public Long getSequence(TypeSequence typeSequence, int partitionsId) {
    Map<Integer, Long> bucket = recordPhoto.getOrDefault(typeSequence, new HashMap<>());
    return bucket.get(partitionsId);

  }
  public int getMaxPartitionsId() {
return maxPartitionsId;  }
}
