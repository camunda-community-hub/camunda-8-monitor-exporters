package io.camunda.monitor.application.display;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DisplayLog implements Display {
  Logger logger = LoggerFactory.getLogger(DisplayLog.class.getName());

  static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /**
   * @param legend
   */
  public void displayLegend( List<Object> legend) {
    String line = legend.stream().map(DisplayLog::formatElement).collect(Collectors.joining(" | "));
    logger.info( line);
  }


  /**
   * @param content
   */
  public void display(LocalDateTime time, List<Object> content) {

    String line = content.stream().map(DisplayLog::formatElement).collect(Collectors.joining(" | "));
    logger.info( line);
  }

  public static String formatElement(Object value) {
    if (value == null) {
      return String.format("%" + CST_SIZE_LONG + "s", "");
    } else if (value instanceof LocalDateTime valueDateTime) {
      return String.format("%" + CST_SIZE_DATE + "s", valueDateTime.format( formatter));
    } else if (value instanceof Integer) {
      // Format Long to 20 characters wide, right-aligned
      return String.format("%" + CST_SIZE_INTEGER + "d", (Integer) value);
    } else if (value instanceof Long) {
      // Format Long to 20 characters wide, right-aligned
      return String.format("%" + CST_SIZE_LONG + "d", (Long) value);
    } else if (value instanceof String valueSt) {
      // Format String to 10 characters wide, left-aligned

      // ATTENTION: if the string end by a format prefix, we know the size
      int size = getSizeFromValue(valueSt);
      if (size != -1)
        valueSt = valueSt.substring(0, valueSt.length() - 2);
      else
        size = 20;
      return String.format("%-" + size + "s", valueSt);
    } else {
      // Optional: handle other types if needed
      return value.toString();
    }
  }

  private static final int CST_SIZE_LONG = 20;
  private static final int CST_SIZE_STRING = 20;
  private static final int CST_SIZE_INTEGER = 8;
  public static final int CST_SIZE_DATE=25;

  private static int getSizeFromValue(String value) {
    if (value.endsWith(":L"))
      return CST_SIZE_LONG;
    if (value.endsWith(":S"))
      return CST_SIZE_STRING;
    if (value.endsWith(":I"))
      return CST_SIZE_INTEGER;
    if (value.endsWith(":D"))
      return CST_SIZE_DATE;
    return -1;
  }
}
