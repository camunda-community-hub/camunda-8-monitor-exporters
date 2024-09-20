package io.camunda.monitor.application.result;

import io.camunda.monitor.application.display.Display;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultAnalysis {

  public enum LevelAnalysisLevel {FULL, OVERVIEW}

  private Map<ResultComponent.TypeSequence, Long > savGeneralDeltaMap = new HashMap<>();

  private ResultPicture previousPicture = null;


  public void analysis(LevelAnalysisLevel level,
                       ResultPicture currentPicture,
                       Display display) {

    if (level == LevelAnalysisLevel.OVERVIEW) {
      display.displayLegend(
          List.of( currentPicture.timeOfPicture, "ZeebeSequence:L", "ZeebeDelta:L", "OperateSequence:L", "OperateDelta:L", "SequenceDelta:L",
              "Status:S", "ZeebeThroughput:S", "OperateThroughput:S"));
    }

    for (ResultComponent.TypeSequence typeSequence : ResultComponent.TypeSequence.values()) {
      ResultComponent zeebePrevious = previousPicture != null ?
          previousPicture.getResultComponentPerImporter(ResultComponent.Importer.ZEEBE) :
          null;
      ResultComponent operatePrevious = previousPicture != null ?
          previousPicture.getResultComponentPerImporter(ResultComponent.Importer.OPERATE) :
          null;
      ResultComponent zeebeCurrent = currentPicture.getResultComponentPerImporter(ResultComponent.Importer.ZEEBE);
      ResultComponent operateCurrent = currentPicture.getResultComponentPerImporter(ResultComponent.Importer.OPERATE);

      // type must be in zeebe And in Operate
      if (! zeebeCurrent.existSequence(typeSequence) || ! operateCurrent.existSequence(typeSequence))
        continue;

      if (level == LevelAnalysisLevel.FULL) {
        display.displayLegend(
          List.of(typeSequence.toString()+":S", "partition:I", "ZeebeSequence:L", "ZeebeDelta:L", "OperateSequence:L", "OperateDelta:L", "SequenceDelta:L",
              "Status:S", "ZeebeThroughput:S", "OperateThroughput:S"));
        }
      Long zeebeCurrentSequenceSum = null;
      Long operateCurrentSequenceSum = null;

      Long zeebePreviousSequenceSum = null;
      Long operatePreviousSequenceSum = null;
      // log sequence on each partition and comparison
      for (int i = 1; i <= zeebeCurrent.getMaxPartitionsId(); i++) {
        Long zeebeCurrentSequence = zeebeCurrent.getSequence(typeSequence, i);
        Long operateCurrentSequence = operateCurrent.getSequence(typeSequence, i);

        Long zeebePreviousSequence = zeebePrevious != null ? zeebePrevious.getSequence(typeSequence, i) : null;
        Long operatePreviousSequence = operatePrevious != null ? operatePrevious.getSequence(typeSequence, i) : null;


        zeebeCurrentSequenceSum = addLong( zeebeCurrentSequenceSum, zeebeCurrentSequence);
        operateCurrentSequenceSum = addLong(operateCurrentSequenceSum,  operateCurrentSequence );

        zeebePreviousSequenceSum = addLong(zeebePreviousSequenceSum, zeebePreviousSequence);
        operatePreviousSequenceSum = addLong(operatePreviousSequenceSum, operatePreviousSequence);

        if (level == LevelAnalysisLevel.FULL) {
          List<Object> list = new ArrayList();
          // use List because List.of() does not accept null
          list.add(currentPicture.timeOfPicture); // time
          list.add(i); // Zeebe partition
          list.add(zeebeCurrentSequence); // Zeebe sequence
          list.add(getDelta(zeebeCurrentSequence, zeebePreviousSequence)); // Zeebe delta
          list.add(operateCurrentSequence); // operate sequence
          list.add(getDelta(operateCurrentSequence, operatePreviousSequence)); //operate delta
          list.add(getDelta(zeebeCurrentSequence, operateCurrentSequence)); // Zeebe-operate backlog
          list.add(calculateStatus(getDelta(zeebeCurrentSequence, operateCurrentSequence),0L)); // Status
          list.add(getThroughputPerMin(previousPicture != null ? previousPicture.timeOfPicture : null, // previous time
              zeebePreviousSequence, // previous value
              currentPicture.timeOfPicture, // current value
              zeebeCurrentSequence // current value
          ));
          list.add(
              getThroughputPerMin(previousPicture != null ? previousPicture.timeOfPicture : null, zeebePreviousSequence,
                  currentPicture.timeOfPicture, operateCurrentSequence));
          display.display(currentPicture.timeOfPicture, list);

        }
      }
      Long currentDelta = getDelta(zeebeCurrentSequenceSum, operateCurrentSequenceSum);
      Long previousDelta = savGeneralDeltaMap.get( typeSequence);
      Long evolution = previousDelta!=null && currentDelta!=null? currentDelta - previousDelta:null;
      savGeneralDeltaMap.put(typeSequence, currentDelta);

      if (level == LevelAnalysisLevel.OVERVIEW) {
        List<Object> list = new ArrayList();        list.add(typeSequence.toString()+":D");
        list.add(zeebeCurrentSequenceSum);
        list.add(getDelta(zeebeCurrentSequenceSum, zeebePreviousSequenceSum));
        list.add(operateCurrentSequenceSum);
        list.add(getDelta(operateCurrentSequenceSum, operatePreviousSequenceSum));
        list.add(getDelta(zeebeCurrentSequenceSum, operateCurrentSequenceSum));
        list.add(calculateStatus(currentDelta, evolution));
        list.add(getThroughputPerMin(previousPicture!=null? previousPicture.timeOfPicture: null, // previous time
            zeebePreviousSequenceSum, // previous sequence
            currentPicture.timeOfPicture, // current time
            zeebeCurrentSequenceSum));// current value
        list.add(getThroughputPerMin(previousPicture!=null? previousPicture.timeOfPicture: null, // previous time
            operatePreviousSequenceSum, //
            currentPicture.timeOfPicture, //
            operateCurrentSequenceSum));
        display.display(currentPicture.timeOfPicture, list);
      }

    } // end per type sequence

    previousPicture= currentPicture;
  }

  private Long addLong(Long value1, Long value2) {
    if (value1==null && value2==null)
      return null;
    BigInteger big1 = BigInteger.valueOf(value1==null? 0: value1);
    BigInteger big2 = BigInteger.valueOf(value2==null? 0: value2);
    return big1.add(big2).longValue();
  }
  private Long getDelta(Long value1, Long value2) {
    if (value1 == null || value2 == null)
      return null;
    BigInteger big1 = BigInteger.valueOf(value1==null? 0: value1);
    BigInteger big2 = BigInteger.valueOf(value2==null? 0: value2);
    return big1.subtract(big2).longValue();
  }

  /**
   * calculate the throughput of a sequence.
   * @param previousTime time of the previous picture when the value is collected
   * @param previousValue value
   * @param currentTime time when the current value is collected
   * @param currentValue value
   * @return a string giving the througput with the legend
   */
  private String getThroughputPerMin(LocalDateTime previousTime,
                                     Long previousValue,
                                     LocalDateTime currentTime,
                                     Long currentValue) {

    if (previousTime==null || currentTime==null)
      return "- rec/mn";

    Duration duration = Duration.between(previousTime, currentTime);
    BigInteger big1 = BigInteger.valueOf(previousValue);
    BigInteger big2 = BigInteger.valueOf(currentValue);

    long delta = big2.subtract(big1).longValue();
    long throughput = (60*delta) / duration.getSeconds();

    return String.format("%,10d rec/mn", throughput);
  }

  /**
   * calculate a status according the sequence, and the evolution
   * @param delta the delta between Zeebe and the next importer
   * @param evolution evolution of the delta from the previous picture
   * @return the calulation
   */
  private String calculateStatus(Long delta, Long evolution) {
    if (delta == null)
      return "";
    String result="";
    if (delta == 0)
      result+= "sync;";
    else if (delta < 2000)
      result+="inpr;";
    else result+= "BCKL;";

    if (evolution!=null) {
      if (evolution==0) {}
      else if (Math.abs(evolution.longValue())<1000)
        result+="-";
      else if (evolution<0)
        result+=String.format("dec. %,d",evolution);
      else
        result+=String.format("INC. %,d",evolution);
    }
    return result;
  }

  public class Calculation{
    public String synthesis;
    public boolean isBacklog=false;
    public boolean evolutionIncrease=false;
  }
  /**
   * Format a string on 25 digit
   * @param value string to be formatted
   * @return string formated
   */
  private String formatString(Long value) {
    return value == null ? "<null>" : String.format("%25d", value);
  }
}
