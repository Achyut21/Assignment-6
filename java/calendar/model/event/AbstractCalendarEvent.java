package calendar.model.event;

import java.time.LocalDateTime;

/** Abstract base class for calendar events. */
public abstract class AbstractCalendarEvent implements Event {
  protected String name;
  protected LocalDateTime start;
  protected LocalDateTime end;
  protected String description;
  protected String location;
  protected boolean isPublic;

  /** Constructs an AbstractCalendarEvent. */
  public AbstractCalendarEvent(
      String name,
      LocalDateTime start,
      LocalDateTime end,
      String description,
      String location,
      boolean isPublic) {
    this.name = name;
    this.start = start;
    this.end = end;
    this.description = description;
    this.location = location;
    this.isPublic = isPublic;
  }

  /** Returns the event name. */
  @Override
  public String getName() {
    return name;
  }

  /** Sets the event name. */
  public void setName(String name) {
    this.name = name;
  }

  /** Returns the start time. */
  @Override
  public LocalDateTime getStart() {
    return start;
  }

  /** Returns the end time. */
  @Override
  public LocalDateTime getEnd() {
    return end;
  }

  /** Returns the event description. */
  @Override
  public String getDescription() {
    return description;
  }

  /** Sets the event description. */
  public void setDescription(String description) {
    this.description = description;
  }

  /** Returns the event location. */
  @Override
  public String getLocation() {
    return location;
  }

  /** Sets the event location. */
  public void setLocation(String location) {
    this.location = location;
  }

  /** Returns true if the event is public. */
  @Override
  public boolean isPublic() {
    return isPublic;
  }

  /** Sets whether the event is public. */
  public void setIsPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }
}
