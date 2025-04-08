package calendar.view.exceptions;

/** Thrown when a required parameter is missing. */
public class MissingParameterException extends Exception {
  public MissingParameterException(String parameter) {
    super("Missing parameter: " + parameter);
  }
}
