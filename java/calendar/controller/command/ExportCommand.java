package calendar.controller.command;

import calendar.controller.CalendarController;
import calendar.view.exceptions.InvalidCommandException;

/** Concrete implementation of the export calendar command. */
public class ExportCommand implements Command {
  private final String[] tokens;
  private final CalendarController controller;

  /** Constructor for the export calendar command. */
  public ExportCommand(String[] tokens, CalendarController controller) {
    this.tokens = tokens;
    this.controller = controller;
  }

  /** Executes the export calendar command. */
  @Override
  public String execute() throws Exception {
    if (tokens.length < 3 || !tokens[1].equalsIgnoreCase("cal")) {
      throw new InvalidCommandException("export command must be 'export cal <filename>'");
    }
    String fileName = tokens[2];
    String path = controller.exportCalendar(fileName);
    return "Calendar exported to CSV at: " + path;
  }
}
