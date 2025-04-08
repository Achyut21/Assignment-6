package calendar.controller;

import calendar.model.Calendar;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Manages multiple calendars by providing methods to create, edit, and retrieve calendars. */
public class CalendarManager {
  private final Map<String, Calendar> calendars = new HashMap<>();

  /** Creates a new calendar with the specified name and timezone. */
  public void createCalendar(String name, String timezoneStr) {
    if (calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar name must be unique.");
    }
    Calendar cal = new Calendar(name, ZoneId.of(timezoneStr));
    calendars.put(name, cal);
  }

  /** Edits an existing calendar's property. */
  public void editCalendar(String name, String property, String newValue) {
    Calendar cal = calendars.get(name);
    if (cal == null) {
      throw new IllegalArgumentException("Calendar not found.");
    }
    if (property.equalsIgnoreCase("name")) {
      if (calendars.containsKey(newValue)) {
        throw new IllegalArgumentException("New calendar name must be unique.");
      }
      calendars.remove(name);
      cal.setName(newValue);
      calendars.put(newValue, cal);
    } else if (property.equalsIgnoreCase("timezone")) {
      cal.setTimezone(newValue);
    } else {
      throw new IllegalArgumentException("Invalid property for calendar.");
    }
  }

  public Set<String> getCalendarNames() {
    return new HashSet<>(calendars.keySet());
  }

  /** Returns the calendar with the specified name. */
  public Calendar getCalendar(String name) {
    return calendars.get(name);
  }
}
