package calendar.controller.mode;

import calendar.controller.CalendarController;
import calendar.controller.command.Command;
import calendar.controller.command.CommandFactory;
import calendar.view.ui.ConsoleUIHandler;
import calendar.view.ui.UIHandler;
import calendar.view.ErrorView;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** Implementation of the Headless mode. */
public class HeadlessMode implements Mode {
  private final String filePath;
  private final CalendarController controller;
  private final UIHandler uiHandler;

  /** Constructor for the headless mode. */
  public HeadlessMode(String filePath, CalendarController controller) {
    this.filePath = filePath;
    this.controller = controller;
    this.uiHandler = new ConsoleUIHandler();
  }

  /** Method for executing the program in headless mode. */
  @Override
  public void execute() {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      int lineNo = 1;
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          lineNo++;
          continue;
        }
        uiHandler.display("Processing command (" + lineNo + "): " + line);
        if (line.equalsIgnoreCase("exit")) {
          uiHandler.display("Exiting Calendar App.");
          break;
        }
        try {
          Command command = CommandFactory.process(line, controller);
          String output = command.execute();
          uiHandler.display(output);
        } catch (Exception e) {
          ErrorView.displayError("Error at line " + lineNo + ": " + e.getMessage());
          break;
        }
        lineNo++;
      }
    } catch (IOException e) {
      ErrorView.displayError("Headless mode terminated due to error: " + e.getMessage());
    }
  }
}
