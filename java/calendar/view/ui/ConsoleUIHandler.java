package calendar.view.ui;

import java.util.Scanner;

public class ConsoleUIHandler implements UIHandler {
  private final Scanner scanner;

  public ConsoleUIHandler() {
    scanner = new Scanner(System.in);
  }

  @Override
  public String getInput() {
    return scanner.nextLine();
  }

  @Override
  public void display(String message) {
    System.out.println(message);
  }
}