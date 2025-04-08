package calendar.controller.command;

import calendar.controller.CalendarController;
import calendar.model.event.Event;
import calendar.view.CalendarView;
import calendar.view.exceptions.InvalidCommandException;
import calendar.view.exceptions.InvalidTokenException;
import calendar.view.exceptions.MissingParameterException;
import java.util.List;

/** Concrete implementation of the print command. */
public class PrintCommand implements Command {
  private final String[] tokens;
  private final CalendarController controller;

  public PrintCommand(String[] tokens, CalendarController controller) {
    this.tokens = tokens;
    this.controller = controller;
  }

  @Override
  public String execute() throws Exception {
    if (tokens.length < 3) {
      throw new MissingParameterException("print command");
    }
    if (!tokens[1].equalsIgnoreCase("events")) {
      throw new InvalidCommandException("print command must be 'print events ...'");
    }
    if (tokens[2].equalsIgnoreCase("on")) {
      if (tokens.length < 4) {
        throw new MissingParameterException("date for print events on");
      }
      String date = tokens[3];
      List<Event> events = controller.getEventsOn(date);
      return CalendarView.formatEventsOn(date, events);
    } else if (tokens[2].equalsIgnoreCase("from")) {
      if (tokens.length < 6 || !tokens[4].equalsIgnoreCase("to")) {
        throw new InvalidTokenException("to");
      }
      String startDateTime = tokens[3];
      String endDateTime = tokens[5];
      List<Event> events = controller.getEventsBetween(startDateTime, endDateTime);
      return CalendarView.formatEventsBetween(startDateTime, endDateTime, events);
    } else {
      throw new InvalidCommandException("Invalid print events command.");
    }
  }
}