package calendar.controller.mode;

import calendar.controller.CalendarController;
import java.util.Scanner;

/** Class responsible for creating a concrete class implementing the mode interface. */
public class ModeFactory {

  private final CalendarController controller;

  /** Constructor for the Mode factory. */
  public ModeFactory(CalendarController controller) {
    this.controller = controller;
  }

  /** Method for parsing input and returning a concrete class implementing the mode interface. */
  public Mode getMode(String[] args) {
    String modeArg = args[1];

    if (modeArg.equalsIgnoreCase("headless")) {
      if (args.length < 3) {
        System.err.println("Headless mode requires a script file path.");
        System.exit(1);
      }

      return new HeadlessMode(args[2], controller);
    }

    else if (modeArg.equalsIgnoreCase("interactive")) {
      return new InteractiveMode(controller);
    }

    else {
      System.err.println("Invalid mode specified: " + modeArg);
      System.exit(1);
    }
    return null; // Unreachable, required by method signature
  }
}
