package calendar.model.event;

import java.time.LocalDateTime;

/** Interface for calendar events. */
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
