# Calendar Application - Documentation

## Overview

This application is a command line based calendar system capable of creating and editing events,
querying events by date, and exporting them to CSV. It supports both **interactive** and **headless
** modes, handling user commands to manage and organize calendar events.

## Code Flow

1. **CalendarApp (Main Entry Point)**
    - Instantiates the core `CalendarController`.
    - Invokes `modeFactory.getMode()` to select the appropriate mode.
    - Calls `mode.execute()` to begin program execution

2. **ModeFactory**
    - `getMode` parses a string and returns an implementation of the `Mode` interface

3. **CalendarController**
    - Maintains a reference to the core `Calendar` model.
    - Provides methods for:
        - Creating single or recurring events (timed or all-day).
        - Editing existing events (single or multiple).
        - Querying events (on a date, between dates, or checking busy status).
        - Exporting events to CSV.
    - Interprets the user's chosen mode (interactive or headless) and delegates to the appropriate
      processing method (`processInteractive` or `processHeadless`).

4. **CommandFactory**
    - Is responsible for parsing an input string and returning and implementation of the `Command`
      interface
    - The returned `Command` implementation and execute the command by calling `Command.execute()`

5. **Model Classes**
    - **Calendar**:
        - Stores and manages `Event` objects (e.g. conflict checks, add/edit methods, CSV export).
    - **Event** (interface):
        - Basic event behaviors (getters for name, time, location, etc.).
    - **AbstractCalendarEvent**:
        - Shared fields (name, start/end times, description, location, and public flag).
    - **SingleEvent**:
        - A single, non-recurring event.
    - **RecurringEvent**:
        - Generates multiple instances (`SingleEvent`) based on specified weekdays and either an
          occurrence count or an until date/time.

6. **Exceptions**
    - **InvalidCommandException**: Thrown when an unrecognized or invalid command is encountered.
    - **InvalidTokenException**: Thrown when a required token (like `to`) is missing.
    - **MissingParameterException**: Thrown when a required parameter is absent (e.g., event name,
      date/time).

## Input Command Workflow

### Interactive Mode

1. **Startup Prompt**  
   The application asks which mode to use:
    - **1** = Interactive
    - **2** = Headless
    - **3** = Exit

2. **If Interactive**
    - Prompts the user to enter commands (e.g.,
      `create event Meeting from 2025-04-01T10:00 to 2025-04-01T11:00 --autodecline`).
    - Reads each command line, then calls `CommandFactory` to parse and the string and return an
      implementation of the `Command` interface
    - The returned command is executed by calling the `.execute()`
    - This method then calls the appropriate `CalendarController` method(s).
    - Print or display results (e.g., event creation success messages or listing events).
    - Type `exit` to end the interactive session and return to the main mode prompt.

### Headless Mode

1. **Startup Prompt**
    - **2** = Headless
    - The application asks for a file path containing commands.

2. **File-based Commands**
    - Each line in the file is processed sequentially, sending each line to the `CommandFactory`.
    - The `CommandFactory` returns an implementation of the `Command` class
    - This `Command` then executes the command through the `.execute()` method
    - The final `exit` command stops execution.

### Common Commands Examples

- **Create Single Timed Event**  
  `create event Meeting from 2025-04-01T10:00 to 2025-04-01T11:00 --autodecline`
- **Create Single All-Day Event**  
  `create event Vacation on 2025-04-10`
- **Edit Single Event**  
  `edit event description Meeting from 2025-04-01T10:00 to 2025-04-01T11:00 with UpdatedDesc`
- **Print Events on a Date**  
  `print events on 2025-04-01`
- **Show Busy Status**  
  `show status on 2025-04-01T10:30`
- **Export to CSV**  
  `export cal my_calendar.csv`

# Key Changes from Assignment 4 to Assignment 5

- **Multiple Calendars Support:** Introduced a new `CalendarManager` class to manage multiple
  calendars and ensure each calendar has a unique name.
- **Timezone Support:** Added timezone support to calendars (with getters, setters, and edit
  methods) so that each calendar is associated with an IANA timezone.
- **Enhanced Command Set:** Extended the command set to include commands for creating, editing, and
  using calendars, plus new copy commands for transferring events across calendars.
- **Refactored Export Functionality:** Moved the CSV export functionality from the `Calendar` class
  into its own `CSVCalendarExporter` class that implements the `CalendarExporter` interface for
  future extensibility.
- **Default Conflict Behavior:** Changed event creation to always decline new events if a conflict
  exists, thereby making conflict declination the default behavior.
- **Improved Recurring Event Editing:** Modified the logic for editing recurring events so that
  the "edit events from" command now applies to all events starting at or after a specified time.
- **Copy Commands Added:** Implemented commands to copy a single event and groups of events (by day
  or over an interval) from one calendar to another with appropriate time adjustments.
- **Controller & Command Refactoring:** Refactored the controller and command processing to use
  concrete command classes (e.g., `CreateCommand`, `EditCommand`, `CopyCommand`, etc.) for better
  separation of concerns under the MVC architecture.

### Distribution of Work

For this assignment, Achyut primarily worked on implementing the functional parts of the rubric.
Marcus did the refactoring and worked on implementing the factories as well as the testing.
However, both Marcus and Achyut spent much of the assignment in both in person working sessions and
remote working sessions.