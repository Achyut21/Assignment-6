package calendar.model.event;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Represents a recurring event composed of multiple single events. A recurring event is defined by
 * either a fixed number of occurrences or an end date,
 * and repeats on specified days of the week.
 * Each occurrence is stored as a separate SingleEvent instance.
 */
public class RecurringEvent extends AbstractCalendarEvent {
  private final Set<DayOfWeek> weekdays;
  private final int occurrences;
  private final LocalDateTime until;
  private List<SingleEvent> eventInstances;

  /** Constructs a recurring event using an occurrences count. */
  public RecurringEvent(
      String name,
      LocalDateTime start,
      LocalDateTime end,
      String description,
      String location,
      boolean isPublic,
      Set<DayOfWeek> weekdays,
      int occurrences) {
    super(name, start, end, description, location, isPublic);
    this.weekdays = weekdays;
    this.occurrences = occurrences;
    this.until = null;
    generateEventInstances();
  }

  /** Constructs a recurring event using an until date. */
  public RecurringEvent(
      String name,
      LocalDateTime start,
      LocalDateTime end,
      String description,
      String location,
      boolean isPublic,
      Set<DayOfWeek> weekdays,
      LocalDateTime until) {
    super(name, start, end, description, location, isPublic);
    this.weekdays = weekdays;
    this.until = until;
    this.occurrences = 0;
    generateEventInstances();
  }

  /** Generates instances for the recurring event. */
  private void generateEventInstances() {
    eventInstances = new ArrayList<>();
    LocalDate currentDate = start.toLocalDate();
    int count = 0;
    if (until == null) {
      if (occurrences <= 0) {
        return;
      }
      while (true) {
        if (weekdays.contains(currentDate.getDayOfWeek())) {
          LocalDateTime instanceStart = LocalDateTime.of(currentDate, start.toLocalTime());
          LocalDateTime instanceEnd = LocalDateTime.of(currentDate, end.toLocalTime());
          eventInstances.add(
              new SingleEvent(name, instanceStart, instanceEnd, description, location, isPublic));
          count++;
          if (count >= occurrences) {
            break;
          }
        }
        currentDate = currentDate.plusDays(1);
      }
    } else {
      while (true) {
        if (weekdays.contains(currentDate.getDayOfWeek())) {
          LocalDateTime instanceStart = LocalDateTime.of(currentDate, start.toLocalTime());
          LocalDateTime instanceEnd = LocalDateTime.of(currentDate, end.toLocalTime());
          eventInstances.add(
              new SingleEvent(name, instanceStart, instanceEnd, description, location, isPublic));
        }
        currentDate = currentDate.plusDays(1);
        if (currentDate.atStartOfDay().isAfter(until)) {
          break;
        }
      }
    }
  }

  /** Returns the list of single event instances. */
  public List<SingleEvent> getEventInstances() {
    return eventInstances;
  }
}
