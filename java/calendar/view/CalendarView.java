package calendar.view;

import calendar.model.event.Event;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CalendarView {
  private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

  public static String formatEventsOn(String date, List<Event> events) {
    if (events.isEmpty()) {
      return "No events on " + date;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("Events on ").append(date).append(":\n");
    for (Event e : events) {
      boolean isAllDay = (e.getStart().getHour() == 0 && e.getStart().getMinute() == 0 &&
          e.getEnd().getHour() == 23 && e.getEnd().getMinute() == 59);
      sb.append(" - ").append(e.getName());
      if (isAllDay) {
        sb.append(" All Day Event ");
      } else {
        sb.append(" (")
            .append(timeFormatter.format(e.getStart()))
            .append(" to ")
            .append(timeFormatter.format(e.getEnd()))
            .append(")");
      }
      sb.append(" at ").append(e.getLocation()).append("\n");
    }
    return sb.toString();
  }

  public static String formatEventsBetween(String start, String end, List<Event> events) {
    if (events.isEmpty()) {
      return "No events between " + start + " and " + end;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("Events from ").append(start).append(" to ").append(end).append(":\n");
    for (Event e : events) {
      sb.append(" - ")
          .append(e.getName())
          .append(" (")
          .append(timeFormatter.format(e.getStart()))
          .append(" to ")
          .append(timeFormatter.format(e.getEnd()))
          .append(") at ")
          .append(e.getLocation())
          .append("\n");
    }
    return sb.toString();
  }

  public static String formatBusyStatus(String dateTime, boolean busy) {
    return "Status at " + dateTime + ": " + (busy ? "Busy" : "Available");
  }
}