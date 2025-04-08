package calendar.model;

import calendar.model.event.AbstractCalendarEvent;
import calendar.model.event.Event;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/** Represents a calendar containing events associated with a unique name and time zone.
 * this class provides functionality for managing events, checking for scheduling conflicts,
 * and editing calendar or event properties. */
public class Calendar {
  private final List<Event> events = new ArrayList<>();
  private String name;
  private ZoneId timezone;

  /** Constructs a Calendar with the specified name and timezone. */
  public Calendar(String name, ZoneId timezone) {
    this.name = name;
    this.timezone = timezone;
  }

  /** Returns the calendar name. */
  public String getName() {
    return name;
  }

  /** Sets the calendar name. */
  public void setName(String name) {
    this.name = name;
  }

  /** Returns the calendar timezone. */
  public ZoneId getTimezone() {
    return timezone;
  }

  /** Sets the calendar timezone using the IANA timezone ID. */
  public void setTimezone(String timezone) {
    this.timezone = ZoneId.of(timezone);
  }

  /** Edits a property of the calendar. */
  public void editProperty(String property, String newValue) throws Exception {
    if ("name".equalsIgnoreCase(property)) {
      this.name = newValue;
    } else if ("timezone".equalsIgnoreCase(property)) {
      this.timezone = ZoneId.of(newValue);
    } else {
      throw new Exception("Invalid calendar property: " + property);
    }
  }

  /** Adds an event to the calendar, checking conflicts if autoDecline is true. */
  public void addEvent(Event event, boolean autoDecline) throws Exception {
    if (autoDecline) {
      for (Event e : events) {
        if (conflict(e, event)) {
          throw new Exception("Event conflict detected.");
        }
      }
    }
    events.add(event);
  }

  /** Returns the list of events on a given date. */
  public List<Event> getEventsOn(LocalDate date) {
    List<Event> result = new ArrayList<>();
    for (Event e : events) {
      if (e.getStart().toLocalDate().equals(date)) {
        result.add(e);
      }
    }
    return result;
  }

  /** Returns the list of events between the given start and end date-times. */
  public List<Event> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    List<Event> result = new ArrayList<>();
    for (Event e : events) {
      if (!e.getStart().isAfter(end) && !e.getEnd().isBefore(start)) {
        result.add(e);
      }
    }
    return result;
  }

  /** Returns true if an event covers the given date-time. */
  public boolean isBusy(LocalDateTime dateTime) {
    for (Event e : events) {
      if (!dateTime.isBefore(e.getStart()) && !dateTime.isAfter(e.getEnd())) {
        return true;
      }
    }
    return false;
  }

  /** Checks if two events conflict. */
  private boolean conflict(Event e1, Event e2) {
    return !(e2.getEnd().isBefore(e1.getStart()) || e2.getStart().isAfter(e1.getEnd()));
  }

  /** Edits a single event matching name and start/end times. */
  public boolean editSingleEvent(
      String property, String name, LocalDateTime start, LocalDateTime end, String newValue) {
    for (Event event : events) {
      if (event.getName().equals(name)
          && event.getStart().equals(start)
          && event.getEnd().equals(end)) {
        updateProperty((AbstractCalendarEvent) event, property, newValue);
        return true;
      }
    }
    return false;
  }

  /** Edits events with the given name and start time. */
  public int editEventsFrom(String property, String name, LocalDateTime start, String newValue) {
    int count = 0;
    for (Event event : events) {
      if (event.getName().equals(name)
          && (event.getStart().equals(start) || event.getStart().isAfter(start))) {
        updateProperty((AbstractCalendarEvent) event, property, newValue);
        count++;
      }
    }
    return count;
  }

  /** Edits all events with the given name. */
  public int editEvents(String property, String name, String newValue) {
    int count = 0;
    for (Event event : events) {
      if (event.getName().equals(name)) {
        updateProperty((AbstractCalendarEvent) event, property, newValue);
        count++;
      }
    }
    return count;
  }

  /** Updates an event property based on a string identifier. */
  private void updateProperty(AbstractCalendarEvent event, String property, String newValue) {
    switch (property.toLowerCase()) {
      case "name":
        event.setName(newValue);
        break;
      case "description":
        event.setDescription(newValue);
        break;
      case "location":
        event.setLocation(newValue);
        break;
      case "ispublic":
        event.setIsPublic(Boolean.parseBoolean(newValue));
        break;
      default:
        break;
    }
  }

  /** Finds an event by its name and start time. */
  public Event findEventByNameAndStart(String eventName, LocalDateTime start) {
    for (Event e : events) {
      if (e.getName().equals(eventName) && e.getStart().equals(start)) {
        return e;
      }
    }
    return null;
  }
}
