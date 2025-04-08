package calendar.view.exceptions;

/** Thrown when a command is invalid. */
public class InvalidCommandException extends Exception {
  public InvalidCommandException(String command) {
    super("Invalid command: " + command);
  }
}
