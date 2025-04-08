package calendar.view.exceptions;

/** Thrown when a required token is missing or incorrect. */
public class InvalidTokenException extends Exception {
  public InvalidTokenException(String token) {
    super("Expected '" + token + "' token.");
  }
}
