package calendar.controller;

import calendar.controller.export.CSVCalendarExporter;
import calendar.controller.export.CalendarExporter;
import calendar.model.Calendar;
import calendar.model.event.Event;
import calendar.model.event.RecurringEvent;
import calendar.model.event.SingleEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for calendar operations and command processing. This version delegates presentation
 * formatting to the view layer.
 */
public class CalendarController {
  private final CalendarManager calendarManager;
  private final DateTimeFormatter dtFormatter;
  private final DateTimeFormatter dateFormatter;
  private final CalendarExporter exporter;
  private Calendar activeCalendar;

  /** Constructs a CalendarController with the specified default calendar. */
  public CalendarController(Calendar defaultCalendar) {
    this.activeCalendar = defaultCalendar;
    calendarManager = new CalendarManager();
    // Add the default calendar to the manager.
    calendarManager.createCalendar(
        defaultCalendar.getName(), defaultCalendar.getTimezone().getId());
    dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    exporter = new CSVCalendarExporter();
  }

  /** Creates a new calendar with the given name and timezone. */
  public void createCalendar(String calName, String timezone) throws Exception {
    if (calendarManager.getCalendar(calName) != null) {
      throw new Exception("Calendar with name " + calName + " already exists.");
    }
    calendarManager.createCalendar(calName, timezone);
  }

  /** Edits a property (name or timezone) of the specified calendar. */
  public void editCalendar(String calName, String property, String newValue) throws Exception {
    if (calendarManager.getCalendar(calName) == null) {
      throw new Exception("Calendar " + calName + " not found.");
    }
    calendarManager.editCalendar(calName, property, newValue);
  }

  /** Sets the active calendar by its name. */
  public void useCalendar(String calName) throws Exception {
    Calendar cal = calendarManager.getCalendar(calName);
    if (cal == null) {
      throw new Exception("Calendar " + calName + " not found.");
    }
    activeCalendar = cal;
  }

  /** Creates a single timed event. */
  public void createSingleEvent(
      String name,
      String startStr,
      String endStr,
      String description,
      String location,
      boolean isPublic,
      boolean autoDecline)
      throws Exception {
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    LocalDateTime end = LocalDateTime.parse(endStr, dtFormatter);
    SingleEvent event = new SingleEvent(name, start, end, description, location, isPublic);
    activeCalendar.addEvent(event, autoDecline);
  }

  /** Creates a recurring timed event with a fixed number of occurrences. */
  public void createRecurringEventOccurrences(
      String name,
      String startStr,
      String endStr,
      String description,
      String location,
      boolean isPublic,
      String weekdaysStr,
      int occurrences,
      boolean autoDecline)
      throws Exception {
    if (occurrences < 0) {
      throw new IllegalArgumentException("Occurrence count cannot be negative.");
    }
    if (occurrences == 0) {
      return;
    }
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    LocalDateTime end = LocalDateTime.parse(endStr, dtFormatter);
    Set<DayOfWeek> weekdays = parseWeekdays(weekdaysStr);
    RecurringEvent recurringEvent =
        new RecurringEvent(
            name, start, end, description, location, isPublic, weekdays, occurrences);
    for (SingleEvent instance : recurringEvent.getEventInstances()) {
      activeCalendar.addEvent(instance, autoDecline);
    }
  }

  /** Creates a recurring timed event until a specified date-time. */
  public void createRecurringEventUntil(
      String name,
      String startStr,
      String endStr,
      String description,
      String location,
      boolean isPublic,
      String weekdaysStr,
      String untilStr,
      boolean autoDecline)
      throws Exception {
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    LocalDateTime end = LocalDateTime.parse(endStr, dtFormatter);
    LocalDateTime until = LocalDateTime.parse(untilStr, dtFormatter);
    Set<DayOfWeek> weekdays = parseWeekdays(weekdaysStr);
    RecurringEvent recurringEvent =
        new RecurringEvent(name, start, end, description, location, isPublic, weekdays, until);
    for (SingleEvent instance : recurringEvent.getEventInstances()) {
      activeCalendar.addEvent(instance, autoDecline);
    }
  }

  /** Creates a single all-day event. */
  public void createSingleAllDayEvent(
      String name,
      String dateStr,
      String description,
      String location,
      boolean isPublic,
      boolean autoDecline)
      throws Exception {
    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
    LocalDateTime start = date.atTime(0, 0);
    LocalDateTime end = date.atTime(23, 59);
    SingleEvent event = new SingleEvent(name, start, end, description, location, isPublic);
    activeCalendar.addEvent(event, autoDecline);
  }

  /** Creates a recurring all-day event with a fixed number of occurrences. */
  public void createRecurringAllDayEventOccurrences(
      String name,
      String dateStr,
      String description,
      String location,
      boolean isPublic,
      String weekdaysStr,
      int occurrences,
      boolean autoDecline)
      throws Exception {
    if (occurrences < 0) {
      throw new IllegalArgumentException("Occurrence count cannot be negative.");
    }
    if (occurrences == 0) {
      return;
    }
    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
    LocalDateTime start = date.atTime(0, 0);
    LocalDateTime end = date.atTime(23, 59);
    Set<DayOfWeek> weekdays = parseWeekdays(weekdaysStr);
    RecurringEvent recurringEvent =
        new RecurringEvent(
            name, start, end, description, location, isPublic, weekdays, occurrences);
    for (SingleEvent instance : recurringEvent.getEventInstances()) {
      activeCalendar.addEvent(instance, autoDecline);
    }
  }

  /** Creates a recurring all-day event until a specified date. */
  public void createRecurringAllDayEventUntil(
      String name,
      String dateStr,
      String description,
      String location,
      boolean isPublic,
      String weekdaysStr,
      String untilDateStr,
      boolean autoDecline)
      throws Exception {
    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
    LocalDateTime start = date.atTime(0, 0);
    LocalDateTime end = date.atTime(23, 59);
    LocalDate untilDate = LocalDate.parse(untilDateStr, dateFormatter);
    LocalDateTime until = untilDate.atTime(23, 59);
    Set<DayOfWeek> weekdays = parseWeekdays(weekdaysStr);
    RecurringEvent recurringEvent =
        new RecurringEvent(name, start, end, description, location, isPublic, weekdays, until);
    for (SingleEvent instance : recurringEvent.getEventInstances()) {
      activeCalendar.addEvent(instance, autoDecline);
    }
  }

  /** Edits a single event identified by name and start/end times. */
  public void editSingleEvent(
      String property, String eventName, String startStr, String endStr, String newValue)
      throws Exception {
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    LocalDateTime end = LocalDateTime.parse(endStr, dtFormatter);
    boolean found = activeCalendar.editSingleEvent(property, eventName, start, end, newValue);
    if (!found) {
      throw new Exception("No matching event found for editing.");
    }
  }

  /** Edits events matching the given name and start time. */
  public void editEventsFrom(String property, String eventName, String startStr, String newValue)
      throws Exception {
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    int count = activeCalendar.editEventsFrom(property, eventName, start, newValue);
    if (count == 0) {
      throw new Exception("No matching events found");
    }
  }

  /** Edits all events with the given name. */
  public void editEvents(String property, String eventName, String newValue) throws Exception {
    int count = activeCalendar.editEvents(property, eventName, newValue);
    if (count == 0) {
      throw new Exception("No matching events found");
    }
  }

  /**
   * Copies a single event from the active calendar to the target calendar with a new start time.
   */
  public void copyEvent(
      String eventName,
      String sourceDateTimeStr,
      String targetCalendarName,
      String targetDateTimeStr)
      throws Exception {
    LocalDateTime sourceDateTime = LocalDateTime.parse(sourceDateTimeStr, dtFormatter);
    Event eventToCopy = activeCalendar.findEventByNameAndStart(eventName, sourceDateTime);
    if (eventToCopy == null) {
      throw new Exception("Event " + eventName + " not found at " + sourceDateTimeStr);
    }
    Calendar targetCal = calendarManager.getCalendar(targetCalendarName);
    if (targetCal == null) {
      throw new Exception("Target calendar " + targetCalendarName + " not found.");
    }
    LocalDateTime targetDateTime = LocalDateTime.parse(targetDateTimeStr, dtFormatter);
    long duration =
        java.time.Duration.between(eventToCopy.getStart(), eventToCopy.getEnd()).toMinutes();
    SingleEvent copiedEvent =
        new SingleEvent(
            eventToCopy.getName(),
            targetDateTime,
            targetDateTime.plusMinutes(duration),
            eventToCopy.getDescription(),
            eventToCopy.getLocation(),
            eventToCopy.isPublic());
    targetCal.addEvent(copiedEvent, true);
  }

  /**
   * Copies all events on the specified date from the active calendar to the target calendar with a
   * new base date-time.
   */
  public void copyEventsOn(String dateStr, String targetCalendarName, String targetDateTimeStr)
      throws Exception {
    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
    List<Event> eventsToCopy = activeCalendar.getEventsOn(date);
    if (eventsToCopy.isEmpty()) {
      throw new Exception("No events on " + dateStr + " to copy.");
    }
    Calendar targetCal = calendarManager.getCalendar(targetCalendarName);
    if (targetCal == null) {
      throw new Exception("Target calendar " + targetCalendarName + " not found.");
    }
    LocalDateTime targetBase = LocalDateTime.parse(targetDateTimeStr, dtFormatter);
    LocalDateTime earliest =
        eventsToCopy.stream().map(Event::getStart).min(LocalDateTime::compareTo).orElse(targetBase);
    long offset = java.time.Duration.between(earliest, targetBase).toMinutes();
    for (Event e : eventsToCopy) {
      SingleEvent copiedEvent =
          new SingleEvent(
              e.getName(),
              e.getStart().plusMinutes(offset),
              e.getEnd().plusMinutes(offset),
              e.getDescription(),
              e.getLocation(),
              e.isPublic());
      targetCal.addEvent(copiedEvent, true);
    }
  }

  /**
   * Copies all events between two dates from the active calendar to the target calendar starting at
   * a new base date.
   */
  public void copyEventsBetween(
      String startDateStr, String endDateStr, String targetCalendarName, String targetDateStr)
      throws Exception {
    LocalDateTime start = LocalDate.parse(startDateStr, dateFormatter).atStartOfDay();
    LocalDateTime end = LocalDate.parse(endDateStr, dateFormatter).atTime(23, 59);
    List<Event> eventsToCopy = activeCalendar.getEventsBetween(start, end);
    if (eventsToCopy.isEmpty()) {
      throw new Exception("No events between " + startDateStr + " and " + endDateStr + " to copy.");
    }
    Calendar targetCal = calendarManager.getCalendar(targetCalendarName);
    if (targetCal == null) {
      throw new Exception("Target calendar " + targetCalendarName + " not found.");
    }
    LocalDateTime targetBase = LocalDate.parse(targetDateStr, dateFormatter).atStartOfDay();
    LocalDateTime earliest =
        eventsToCopy.stream().map(Event::getStart).min(LocalDateTime::compareTo).orElse(targetBase);
    long offset = java.time.Duration.between(earliest, targetBase).toMinutes();
    for (Event e : eventsToCopy) {
      SingleEvent copiedEvent =
          new SingleEvent(
              e.getName(),
              e.getStart().plusMinutes(offset),
              e.getEnd().plusMinutes(offset),
              e.getDescription(),
              e.getLocation(),
              e.isPublic());
      targetCal.addEvent(copiedEvent, true);
    }
  }

  /** Exports the active calendar to a file using the configured exporter. */
  public String exportCalendar(String fileName) throws Exception {
    return exporter.export(activeCalendar, fileName);
  }

  /** Returns a list of events on the specified date.*/
  public List<Event> getEventsOn(String dateStr) {
    LocalDate date = LocalDate.parse(dateStr, dateFormatter);
    return activeCalendar.getEventsOn(date);
  }

  /**
   * Returns a list of events between the specified date-times.
   */
  public List<Event> getEventsBetween(String startStr, String endStr) {
    LocalDateTime start = LocalDateTime.parse(startStr, dtFormatter);
    LocalDateTime end = LocalDateTime.parse(endStr, dtFormatter);
    return activeCalendar.getEventsBetween(start, end);
  }

  /**
   * Returns the busy status for the specified date-time.
   */
  public String getBusyStatus(String dateTimeStr) {
    boolean busy = isBusy(dateTimeStr);
    return calendar.view.CalendarView.formatBusyStatus(dateTimeStr, busy);
  }

  /** Returns the busy status as a boolean for the specified date-time. */
  public boolean isBusy(String dateTimeStr) {
    LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, dtFormatter);
    return activeCalendar.isBusy(dateTime);
  }

  public Set<String> getCalendarNames() {
    return calendarManager.getCalendarNames();
  }

  /** Helper method to parse a string of weekdays into a Set of DayOfWeek. */
  private Set<DayOfWeek> parseWeekdays(String weekdaysStr) {
    Set<DayOfWeek> weekdays = new HashSet<>();
    for (char c : weekdaysStr.toCharArray()) {
      switch (c) {
        case 'M':
          weekdays.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          weekdays.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          weekdays.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          weekdays.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          weekdays.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          weekdays.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          weekdays.add(DayOfWeek.SUNDAY);
          break;
        default:
          break;
      }
    }
    return weekdays;
  }
}
