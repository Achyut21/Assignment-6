package calendar.controller.command;

/** Interface to be implemented by commands for executing the given command. */
public interface Command {
  String execute() throws Exception;
}
