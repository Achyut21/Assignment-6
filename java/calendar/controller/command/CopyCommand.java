package calendar.controller.command;

import calendar.controller.CalendarController;
import calendar.view.exceptions.InvalidCommandException;
import calendar.view.exceptions.MissingParameterException;

/** Concrete implementation of the Copy command. */
public class CopyCommand implements Command {
  private final String[] tokens;
  private final CalendarController controller;

  /** Constructor for copy command. */
  public CopyCommand(String[] tokens, CalendarController controller) {
    this.tokens = tokens;
    this.controller = controller;
  }

  /** Executes the copy command. */
  @Override
  public String execute() throws Exception {
    int index = 1;
    if (tokens[index].equalsIgnoreCase("event")) {
      index++;
      String eventName = tokens[index++];
      if (!tokens[index].equalsIgnoreCase("on")) {
        throw new MissingParameterException("on");
      }
      index++;
      String sourceDateTime = tokens[index++];
      if (!tokens[index].equalsIgnoreCase("--target")) {
        throw new MissingParameterException("target calendar");
      }
      index++;
      String targetCal = tokens[index++];
      if (!tokens[index].equalsIgnoreCase("to")) {
        throw new MissingParameterException("to");
      }
      index++;
      String targetDateTime = tokens[index++];
      controller.copyEvent(eventName, sourceDateTime, targetCal, targetDateTime);
      return "Event " + eventName + " copied to calendar " + targetCal + ".";
    } else if (tokens[index].equalsIgnoreCase("events")) {
      index++;
      if (tokens[index].equalsIgnoreCase("on")) {
        index++;
        String date = tokens[index++];
        if (!tokens[index].equalsIgnoreCase("--target")) {
          throw new MissingParameterException("target calendar");
        }
        index++;
        String targetCal = tokens[index++];
        if (!tokens[index].equalsIgnoreCase("to")) {
          throw new MissingParameterException("to");
        }
        index++;
        String targetDateTime = tokens[index++];
        controller.copyEventsOn(date, targetCal, targetDateTime);
        return "Events on " + date + " copied to calendar " + targetCal + ".";
      } else if (tokens[index].equalsIgnoreCase("between")) {
        index++;
        String startDate = tokens[index++];
        if (!tokens[index].equalsIgnoreCase("and")) {
          throw new MissingParameterException("and");
        }
        index++;
        String endDate = tokens[index++];
        if (!tokens[index].equalsIgnoreCase("--target")) {
          throw new MissingParameterException("target calendar");
        }
        index++;
        String targetCal = tokens[index++];
        if (!tokens[index].equalsIgnoreCase("to")) {
          throw new MissingParameterException("to");
        }
        index++;
        String targetDate = tokens[index++];
        controller.copyEventsBetween(startDate, endDate, targetCal, targetDate);
        return "Events between "
            + startDate
            + " and "
            + endDate
            + " copied to calendar "
            + targetCal
            + ".";
      } else {
        throw new InvalidCommandException("copy");
      }
    }
    throw new InvalidCommandException("copy");
  }
}
