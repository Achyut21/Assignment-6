package calendar.controller.export;

import calendar.model.Calendar;
import calendar.model.event.Event;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Exports a calendar to a CSV file formatted for Google Calendar import. */
public class CSVCalendarExporter implements CalendarExporter {

  /** Exports the given calendar to a CSV file. */
  @Override
  public String export(Calendar calendar, String fileName) throws IOException {
    String absPath = Paths.get(fileName).toAbsolutePath().toString();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
      // Write header with all fields quoted.
      writer.write(
          "\"Subject\",\"Start Date\",\"Start Time\",\"End Date\","
              + "\"End Time\",\"All Day Event\",\"Description\",\"Location\",\"Private\"");
      writer.newLine();
      // Retrieve all events using a wide range.
      List<Event> events =
          calendar.getEventsBetween(
              LocalDateTime.of(1, 1, 1, 0, 0), LocalDateTime.of(9999, 12, 31, 23, 59));
      for (Event event : events) {
        String subject = event.getName();
        String startDate = dateFormatter.format(event.getStart());
        String endDate = dateFormatter.format(event.getEnd());
        String startTime = timeFormatter.format(event.getStart());
        String endTime = timeFormatter.format(event.getEnd());
        boolean isAllDay =
            (event.getStart().getHour() == 0
                && event.getStart().getMinute() == 0
                && event.getEnd().getHour() == 23
                && event.getEnd().getMinute() == 59);
        String allDay = isAllDay ? "True" : "False";
        if (isAllDay) {
          startTime = "";
          endTime = "";
        }
        String isPrivate = (!event.isPublic()) ? "True" : "False";
        String description = event.getDescription();
        String location = event.getLocation();
        // Wrap each field in quotes.
        writer.write(
            String.format(
                "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                subject,
                startDate,
                startTime,
                endDate,
                endTime,
                allDay,
                description,
                location,
                isPrivate));
        writer.newLine();
      }
    }
    return absPath;
  }
}
