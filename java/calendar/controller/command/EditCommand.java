package calendar.controller.command;

import calendar.controller.CalendarController;
import calendar.view.exceptions.InvalidCommandException;
import calendar.view.exceptions.InvalidTokenException;
import calendar.view.exceptions.MissingParameterException;

/** Concrete implementation of the edit command. */
public class EditCommand implements Command {
  private final String[] tokens;
  private final CalendarController controller;

  /** Constructor for the edit command. */
  public EditCommand(String[] tokens, CalendarController controller) {
    this.tokens = tokens;
    this.controller = controller;
  }

  /** Processes an edit calendar command. */
  private static String processEditCalendar(String[] tokens, CalendarController controller)
      throws Exception {
    int index = 2;
    if (!tokens[index].equalsIgnoreCase("--name")) {
      throw new MissingParameterException("calendar name");
    }
    index++;
    String calName = tokens[index++];
    if (!tokens[index].equalsIgnoreCase("--property")) {
      throw new MissingParameterException("property");
    }
    index++;
    String property = tokens[index++];
    String newValue = tokens[index++];
    controller.editCalendar(calName, property, newValue);
    return "Calendar " + calName + " updated: " + property + " = " + newValue;
  }

  /** Processes an edit command with the provided tokens. */
  private static String processEdit(String[] tokens, CalendarController controller)
      throws Exception {
    if (tokens.length < 2) {
      throw new MissingParameterException("edit command");
    }
    String target = tokens[1].toLowerCase();
    if (target.equals("event")) {
      if (tokens.length < 9) {
        throw new MissingParameterException("edit event command parameters");
      }
      String property = tokens[2];
      String eventName = tokens[3];
      if (!tokens[4].equalsIgnoreCase("from")) {
        throw new InvalidTokenException("from");
      }
      String startDateTime = tokens[5];
      if (!tokens[6].equalsIgnoreCase("to")) {
        throw new InvalidTokenException("to");
      }
      String endDateTime = tokens[7];
      if (tokens.length < 10 || !tokens[8].equalsIgnoreCase("with")) {
        throw new InvalidTokenException("with");
      }
      String newValue = tokens[9];
      controller.editSingleEvent(property, eventName, startDateTime, endDateTime, newValue);
      return "Single event edited.";
    } else if (target.equals("events")) {
      String property = tokens[2];
      String eventName = tokens[3];
      if (tokens[4].equalsIgnoreCase("from")) {
        String startDateTime = tokens[5];
        if (tokens.length < 8 || !tokens[6].equalsIgnoreCase("with")) {
          throw new InvalidTokenException("with");
        }
        String newValue = tokens[7];
        controller.editEventsFrom(property, eventName, startDateTime, newValue);
        return "Events starting at " + startDateTime + " edited.";
      } else {
        String newValue = tokens[4];
        controller.editEvents(property, eventName, newValue);
        return "All events with name " + eventName + " edited.";
      }
    } else {
      throw new InvalidCommandException("Invalid edit command target: " + target);
    }
  }

  /** Executes the edit calendar command. */
  @Override
  public String execute() throws Exception {
    if (tokens[1].equalsIgnoreCase("calendar")) {
      return processEditCalendar(tokens, controller);
    }
    return processEdit(tokens, controller);
  }
}
