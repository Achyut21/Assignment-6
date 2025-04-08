package calendar.controller.command;

import calendar.controller.CalendarController;
import calendar.view.exceptions.InvalidCommandException;
import calendar.view.exceptions.MissingParameterException;

/**
 * Processes commands for the calendar application, returning an implementation of the Command
 * class.
 */
public class CommandFactory {

  /** Processes a command string and return a concrete implementation of the Command interface. */
  public static Command process(String input, CalendarController controller) throws Exception {
    String[] tokens = input.trim().split("\\s+");

    if (tokens.length == 0) {
      throw new MissingParameterException("command");
    }

    String commandType = tokens[0].toLowerCase();

    switch (commandType) {
      case "create":
        return new CreateCommand(tokens, controller);
      case "edit":
        return new EditCommand(tokens, controller);
      case "use":
        return new UseCommand(tokens, controller);
      case "copy":
        return new CopyCommand(tokens, controller);
      case "print":
        return new PrintCommand(tokens, controller);
      case "export":
        return new ExportCommand(tokens, controller);
      case "show":
        return new ShowCommand(tokens, controller);
      default:
        {
          throw new InvalidCommandException(commandType);
        }
    }
  }
}
