package calendar.model.event;

import java.time.LocalDateTime;

/** Interface for calendar events.This interface
 * Defines the basic structure and required methods
 * for any calendar event implementation, including name,
 * time range, description, location, and visibility */
public interface Event {
  /** Returns the event name. */
  String getName();

  /** Returns the start time. */
  LocalDateTime getStart();

  /** Returns the end time. */
  LocalDateTime getEnd();

  /** Returns the event description. */
  String getDescription();

  /** Returns the event location. */
  String getLocation();

  /** Returns true if the event is public. */
  boolean isPublic();
}
