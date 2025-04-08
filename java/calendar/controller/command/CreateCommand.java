package calendar.controller.command;

import calendar.controller.CalendarController;
import calendar.view.exceptions.InvalidCommandException;
import calendar.view.exceptions.InvalidTokenException;
import calendar.view.exceptions.MissingParameterException;

/** Concrete implementation of the create command. */
public class CreateCommand implements Command {
  private final String[] tokens;
  private final CalendarController controller;

  /** Constructor for the create command. */
  public CreateCommand(String[] tokens, CalendarController controller) {
    this.tokens = tokens;
    this.controller = controller;
  }

  /** Processes a create calendar command. */
  private static String processCreateCalendar(String[] tokens, CalendarController controller)
      throws Exception {
    int index = 2;
    if (!tokens[index].equalsIgnoreCase("--name")) {
      throw new MissingParameterException("calendar name");
    }
    index++;
    String calName = tokens[index++];
    if (!tokens[index].equalsIgnoreCase("--timezone")) {
      throw new MissingParameterException("timezone");
    }
    index++;
    String timezone = tokens[index++];
    controller.createCalendar(calName, timezone);
    return "Calendar created: " + calName + " with timezone " + timezone;
  }

  /** Processes a create event command with the provided tokens. */
  private static String processCreate(String[] tokens, CalendarController controller)
      throws Exception {
    if (tokens.length < 3 || !tokens[1].equalsIgnoreCase("event")) {
      throw new MissingParameterException("event");
    }
    int index = 2;
    boolean autoDecline = false;
    if (tokens[index].equalsIgnoreCase("--autodecline")) {
      autoDecline = true;
      index++;
    }
    if (index >= tokens.length) {
      throw new MissingParameterException("event name");
    }
    String eventName = tokens[index++];
    if (index >= tokens.length) {
      throw new MissingParameterException("Expected from or on");
    }
    String mode = tokens[index].toLowerCase();
    if (mode.equals("from")) {
      index++;
      if (index >= tokens.length) {
        throw new MissingParameterException("start datetime");
      }
      String startDateTime = tokens[index++];
      if (index >= tokens.length || !tokens[index].equalsIgnoreCase("to")) {
        throw new InvalidTokenException("to");
      }
      index++;
      if (index >= tokens.length) {
        throw new MissingParameterException("end datetime");
      }
      String endDateTime = tokens[index++];
      if (index < tokens.length && tokens[index].equalsIgnoreCase("repeats")) {
        index++;
        if (index >= tokens.length) {
          throw new MissingParameterException("weekdays for recurring event");
        }
        String weekdays = tokens[index++];
        if (index >= tokens.length) {
          throw new MissingParameterException("Expected 'for' or 'until'");
        }
        String recurringType = tokens[index++].toLowerCase();
        if (recurringType.equals("for")) {
          if (index >= tokens.length) {
            throw new MissingParameterException("occurrence count");
          }
          int occurrences = Integer.parseInt(tokens[index++]);
          if (index >= tokens.length || !tokens[index].equalsIgnoreCase("times")) {
            throw new InvalidTokenException("times");
          }
          index++;
          controller.createRecurringEventOccurrences(
              eventName,
              startDateTime,
              endDateTime,
              "",
              "",
              true,
              weekdays,
              occurrences,
              autoDecline);
          return "Recurring timed event created with " + occurrences + " occurrences.";
        } else if (recurringType.equals("until")) {
          if (index >= tokens.length) {
            throw new MissingParameterException("until datetime");
          }
          String untilDateTime = tokens[index++];
          controller.createRecurringEventUntil(
              eventName,
              startDateTime,
              endDateTime,
              "",
              "",
              true,
              weekdays,
              untilDateTime,
              autoDecline);
          return "Recurring timed event created until " + untilDateTime + ".";
        } else {
          throw new InvalidCommandException("Recurring specification: " + recurringType);
        }
      } else {
        controller.createSingleEvent(
            eventName, startDateTime, endDateTime, "", "", true, autoDecline);
        return "Single timed event created: " + eventName;
      }
    } else if (mode.equals("on")) {
      index++;
      if (index >= tokens.length) {
        throw new MissingParameterException("date for all day event");
      }
      String date = tokens[index++];
      if (index < tokens.length && tokens[index].equalsIgnoreCase("repeats")) {
        index++;
        if (index >= tokens.length) {
          throw new MissingParameterException("weekdays for recurring all day event");
        }
        String weekdays = tokens[index++];
        if (index >= tokens.length) {
          throw new MissingParameterException("Expected 'for' or 'until'");
        }
        String recurringType = tokens[index++].toLowerCase();
        if (recurringType.equals("for")) {
          if (index >= tokens.length) {
            throw new MissingParameterException("occurrence count");
          }
          int occurrences = Integer.parseInt(tokens[index++]);
          if (index >= tokens.length || !tokens[index].equalsIgnoreCase("times")) {
            throw new InvalidTokenException("times");
          }
          index++;
          controller.createRecurringAllDayEventOccurrences(
              eventName, date, "", "", true, weekdays, occurrences, autoDecline);
          return "Recurring all day event created with " + occurrences + " occurrences.";
        } else if (recurringType.equals("until")) {
          if (index >= tokens.length) {
            throw new MissingParameterException("until date");
          }
          String untilDate = tokens[index++];
          controller.createRecurringAllDayEventUntil(
              eventName, date, "", "", true, weekdays, untilDate, autoDecline);
          return "Recurring all day event created until " + untilDate + ".";
        } else {
          throw new InvalidCommandException("Recurring specification: " + recurringType);
        }
      } else {
        controller.createSingleAllDayEvent(eventName, date, "", "", true, autoDecline);
        return "Single all day event created: " + eventName;
      }
    } else {
      throw new InvalidCommandException("Expected from or on, found: " + mode);
    }
  }

  /** Executes the create calendar command. */
  @Override
  public String execute() throws Exception {
    if (tokens[1].equalsIgnoreCase("calendar")) {
      return processCreateCalendar(tokens, controller);
    }
    return processCreate(tokens, controller);
  }
}
