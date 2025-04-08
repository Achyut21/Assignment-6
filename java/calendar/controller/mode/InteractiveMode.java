package calendar.controller.mode;

import calendar.controller.CalendarController;
import calendar.controller.command.Command;
import calendar.controller.command.CommandFactory;
import calendar.view.ui.ConsoleUIHandler;
import calendar.view.ui.UIHandler;
import calendar.view.ErrorView;

/** Implementation of the Interactive mode. */
public class InteractiveMode implements Mode {

  private final CalendarController controller;
  private final UIHandler uiHandler;

  /** Constructor for the interactive mode. */
  public InteractiveMode(CalendarController controller) {
    this.controller = controller;
    this.uiHandler =  new ConsoleUIHandler();
  }

  /** Method for executing the program in interactive mode. */
  @Override
  public void execute() {
    while (true) {
      uiHandler.display("Enter command: ");
      String input = uiHandler.getInput();
      if (input.equalsIgnoreCase("exit")) {
        uiHandler.display("Exiting Calendar App.");
        break;
      }
      try {
        Command command = CommandFactory.process(input, controller);
        String output = command.execute();
        uiHandler.display(output);
      } catch (Exception e) {
        ErrorView.displayError(e.getMessage());
      }
    }
  }
}
