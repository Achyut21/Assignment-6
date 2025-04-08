package calendar;

import calendar.controller.CalendarController;
import calendar.controller.mode.HeadlessMode;
import calendar.controller.mode.InteractiveMode;
import calendar.controller.mode.Mode;
import calendar.controller.mode.ModeFactory;
import calendar.model.Calendar;
import calendar.view.CalendarGUI;
import java.time.ZoneId;

/**
 * Main application class for the Calendar.
 *
 * Command-line modes:
 *   --mode headless path-of-script-file : Runs in headless (script) mode.
 *   --mode interactive                  : Runs in interactive text mode.
 *   (no arguments)                      : Launches the GUI.
 */
public class CalendarApp {
  public static void main(String[] args) {
    // Create a default calendar using the system default timezone.
    Calendar defaultCalendar = new Calendar("Default Calendar", ZoneId.systemDefault());
    CalendarController controller = new CalendarController(defaultCalendar);

    if (args.length == 0) {
      javax.swing.SwingUtilities.invokeLater(() -> {
        CalendarGUI gui = new CalendarGUI(controller);
        gui.setVisible(true);
      });
    }

    else if (args.length >= 2 && args[0].equalsIgnoreCase("--mode")) {
      ModeFactory modeFactory = new ModeFactory(controller);
      Mode mode = modeFactory.getMode(args);
      mode.execute();
    }

    else {
      System.err.println("Invalid command-line arguments.");
      System.exit(1);
    }
  }
}