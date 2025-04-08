package calendar.model.event;

import java.time.LocalDateTime;

/** Represents a single calendar event. */
public class SingleEvent extends AbstractCalendarEvent {
  /** Constructs a SingleEvent. */
  public SingleEvent(
      String name,
      LocalDateTime start,
      LocalDateTime end,
      String description,
      String location,
      boolean isPublic) {
    super(name, start, end, description, location, isPublic);
  }
}
