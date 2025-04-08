package calendar.controller.command;

import calendar.controller.CalendarController;
import calendar.view.exceptions.InvalidCommandException;

/** Concrete implementation of the show command. */
public class ShowCommand implements Command {
  private final String[] tokens;
  private final CalendarController controller;

  /** Constructor for the show command. */
  public ShowCommand(String[] tokens, CalendarController controller) {
    this.tokens = tokens;
    this.controller = controller;
  }

  /** Executes the show command. */
  @Override
  public String execute() throws Exception {
    if (tokens.length < 4
        || !tokens[1].equalsIgnoreCase("status")
        || !tokens[2].equalsIgnoreCase("on")) {
      throw new InvalidCommandException("show status command must be 'show status on <datetime>'");
    }
    String dateTime = tokens[3];
    return controller.getBusyStatus(dateTime);
  }
}
