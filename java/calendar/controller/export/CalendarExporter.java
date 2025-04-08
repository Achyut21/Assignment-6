package calendar.controller.export;

/** Interface for exporting a calendar to a file. */
public interface CalendarExporter {
  /** Exports the given calendar to the specified file. */
  String export(calendar.model.Calendar calendar, String fileName) throws Exception;
}
