package calendar.view;

import calendar.controller.CalendarController;
import calendar.model.event.Event;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CalendarGUI extends JFrame {

  private CalendarController controller;
  private LocalDate currentDate;
  private JPanel monthPanel;
  private JLabel monthLabel;
  private JTextArea eventTextArea;
  private DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
  private LocalDate currentSelectedDate = null;

  public CalendarGUI(CalendarController controller) {
    this.controller = controller;
    this.currentDate = LocalDate.now();
    initUI();
  }

  private void initUI() {
    setTitle("Calendar Application - GUI");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1000, 700);
    setLocationRelativeTo(null);

    // Setup Menu Bar
    JMenuBar menuBar = new JMenuBar();

    // File Menu: Import, Export, Exit
    JMenu fileMenu = new JMenu("File");
    JMenuItem importItem = new JMenuItem("Import CSV");
    JMenuItem exportItem = new JMenuItem("Export CSV");
    JMenuItem exitItem = new JMenuItem("Exit");
    fileMenu.add(importItem);
    fileMenu.add(exportItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);
    menuBar.add(fileMenu);

    // Calendar Menu: New Calendar, Select Calendar, Edit Calendar Timezone
    JMenu calendarMenu = new JMenu("Calendar");
    JMenuItem newCalItem = new JMenuItem("New Calendar");
    JMenuItem selectCalItem = new JMenuItem("Select Calendar");
    JMenuItem editCalItem = new JMenuItem("Edit Calendar Timezone");
    calendarMenu.add(newCalItem);
    calendarMenu.add(selectCalItem);
    calendarMenu.add(editCalItem);
    menuBar.add(calendarMenu);

    // Event Menu: Create Event, Edit Event, Copy Event
    JMenu eventMenu = new JMenu("Event");
    JMenuItem createEventItem = new JMenuItem("Create Event");
    JMenuItem editEventItem = new JMenuItem("Edit Event");
    JMenuItem copyEventItem = new JMenuItem("Copy Event");
    eventMenu.add(createEventItem);
    eventMenu.add(editEventItem);
    eventMenu.add(copyEventItem);
    menuBar.add(eventMenu);

    setJMenuBar(menuBar);

    // Top panel: Month navigation
    JPanel topPanel = new JPanel();
    JButton prevButton = new JButton("<");
    JButton nextButton = new JButton(">");
    monthLabel = new JLabel();
    updateMonthLabel();
    topPanel.add(prevButton);
    topPanel.add(monthLabel);
    topPanel.add(nextButton);

    // Month view panel: grid with 7 columns for days
    monthPanel = new JPanel(new GridLayout(0, 7));
    drawMonth();

    // Side panel: display events for selected day
    eventTextArea = new JTextArea();
    eventTextArea.setEditable(false);
    JScrollPane eventScrollPane = new JScrollPane(eventTextArea);
    eventScrollPane.setPreferredSize(new java.awt.Dimension(300, 700));

    // Layout the main frame
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(topPanel, BorderLayout.NORTH);
    cp.add(monthPanel, BorderLayout.CENTER);
    cp.add(eventScrollPane, BorderLayout.EAST);

    // Navigation listeners
    prevButton.addActionListener((ActionEvent e) -> {
      currentDate = currentDate.minusMonths(1);
      updateMonthLabel();
      drawMonth();
    });

    nextButton.addActionListener((ActionEvent e) -> {
      currentDate = currentDate.plusMonths(1);
      updateMonthLabel();
      drawMonth();
    });

    // Menu listeners
    exitItem.addActionListener(e -> System.exit(0));
    exportItem.addActionListener(e -> exportCalendar());
    importItem.addActionListener(e -> importCalendar());
    newCalItem.addActionListener(e -> new NewCalendarDialog(this, controller).setVisible(true));
    selectCalItem.addActionListener(e -> new SelectCalendarDialog(this, controller).setVisible(true));
    editCalItem.addActionListener(e -> new EditCalendarDialog(this, controller).setVisible(true));
    createEventItem.addActionListener(e -> new CreateEventDialog(this, controller, currentDate).setVisible(true));
    editEventItem.addActionListener(e -> new EditEventDialog(this, controller).setVisible(true));
    copyEventItem.addActionListener(e -> new CopyEventDialog(this, controller).setVisible(true));
  }

  private void updateMonthLabel() {
    monthLabel.setText(currentDate.format(monthFormatter));
  }

  private void drawMonth() {
    monthPanel.removeAll();
    // Add day-of-week headers (Sunday first)
    String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (String header : headers) {
      JLabel headerLabel = new JLabel(header, JLabel.CENTER);
      headerLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      monthPanel.add(headerLabel);
    }
    LocalDate firstOfMonth = currentDate.withDayOfMonth(1);
    int firstDayValue = firstOfMonth.getDayOfWeek().getValue(); // Monday=1,..., Sunday=7
    int startIndex = firstDayValue % 7; // Adjust so Sunday=0

    int daysInMonth = currentDate.lengthOfMonth();
    // Fill empty cells before the first day
    for (int i = 0; i < startIndex; i++) {
      monthPanel.add(new JLabel(""));
    }
    // Define lavender color (RGB 230, 230, 250)
    Color lavender = new Color(230, 230, 250);

    // Add day buttons with color change if events exist
    for (int day = 1; day <= daysInMonth; day++) {
      JButton dayButton = new JButton(String.valueOf(day));
      LocalDate date = currentDate.withDayOfMonth(day);
      try {
        List<Event> events = controller.getEventsOn(date.toString());
        System.out.println("For date " + date + " found " + events.size() + " events.");
        if (!events.isEmpty()) {
          dayButton.setBackground(lavender);
        }
      } catch (Exception ex) {
        System.err.println("Error retrieving events for " + date + ": " + ex.getMessage());
        dayButton.setBackground(new Color(255, 200, 200));
      }
      dayButton.addActionListener((ActionEvent e) -> displayEventsForDay(date));
      monthPanel.add(dayButton);
    }
    monthPanel.revalidate();
    monthPanel.repaint();
  }

  public void refreshView() {
    drawMonth();
    // Update the event panel for the currently selected day, if any.
    if (currentSelectedDate != null) {
      displayEventsForDay(currentSelectedDate);
    }
  }

  private void displayEventsForDay(LocalDate date) {
    currentSelectedDate = date;
    try {
      List<Event> events = controller.getEventsOn(date.toString());
      String eventsText = CalendarView.formatEventsOn(date.toString(), events);
      eventTextArea.setText(eventsText);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void exportCalendar() {
    JFileChooser chooser = new JFileChooser();
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      String path = chooser.getSelectedFile().getAbsolutePath();
      try {
        String result = controller.exportCalendar(path);
        JOptionPane.showMessageDialog(this, "Calendar exported to CSV at:\n" + result);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void importCalendar() {
    JFileChooser chooser = new JFileChooser();
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String path = chooser.getSelectedFile().getAbsolutePath();
      try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
        String line;
        boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
          if (firstLine) {
            firstLine = false;
            continue; // Skip header
          }
          String[] tokens = line.split(",");
          if (tokens.length < 9) continue;
          String subject = tokens[0].replace("\"", "");
          String startDate = tokens[1].replace("\"", "");
          String startTime = tokens[2].replace("\"", "");
          String endDate = tokens[3].replace("\"", "");
          String endTime = tokens[4].replace("\"", "");
          String allDay = tokens[5].replace("\"", "");
          String description = tokens[6].replace("\"", "");
          String location = tokens[7].replace("\"", "");
          String startDateTime = startDate + "T" + (allDay.equalsIgnoreCase("True") ? "00:00" : startTime);
          String endDateTime = endDate + "T" + (allDay.equalsIgnoreCase("True") ? "23:59" : endTime);
          controller.createSingleEvent(subject, startDateTime, endDateTime, description, location, true, false);
          CalendarGUI.this.refreshView();
          JOptionPane.showMessageDialog(this, "Event created successfully!");
          dispose();
        }
        JOptionPane.showMessageDialog(this, "Import successful.");
        drawMonth();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error importing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }


  // New Calendar Dialog – create a new calendar with a name and timezone.
  private class NewCalendarDialog extends javax.swing.JDialog {
    private JTextField nameField;
    private JTextField timezoneField;
    private JButton createButton;
    private JButton cancelButton;
    private CalendarController controller;

    public NewCalendarDialog(JFrame parent, CalendarController controller) {
      super(parent, "New Calendar", true);
      this.controller = controller;
      initComponents();
    }

    private void initComponents() {
      nameField = new JTextField(20);
      timezoneField = new JTextField(20);
      createButton = new JButton("Create");
      cancelButton = new JButton("Cancel");

      JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
      panel.add(new JLabel("Calendar Name:"));
      panel.add(nameField);
      panel.add(new JLabel("Timezone (e.g., America/New_York):"));
      panel.add(timezoneField);
      panel.add(createButton);
      panel.add(cancelButton);

      add(panel);
      pack();
      setLocationRelativeTo(getParent());

      createButton.addActionListener((ActionEvent e) -> {
        String name = nameField.getText().trim();
        String timezone = timezoneField.getText().trim();
        if (name.isEmpty() || timezone.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        try {
          controller.createCalendar(name, timezone);
          JOptionPane.showMessageDialog(this, "Calendar created: " + name);
          dispose();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      });

      cancelButton.addActionListener((ActionEvent e) -> dispose());
    }
  }

  // Select Calendar Dialog – lists available calendars.
  private class SelectCalendarDialog extends javax.swing.JDialog {
    public SelectCalendarDialog(JFrame parent, CalendarController controller) {
      super(parent, "Select Calendar", true);
      java.util.Set<String> calNames = controller.getCalendarNames();
      if (calNames == null || calNames.isEmpty()) {
        add(new JLabel("No calendars available."));
      } else {
        // Create a combo box for calendar selection.
        javax.swing.JComboBox<String> calendarCombo = new javax.swing.JComboBox<>(calNames.toArray(new String[0]));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        // Panel for combo box and label.
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new javax.swing.BoxLayout(selectionPanel, javax.swing.BoxLayout.Y_AXIS));
        selectionPanel.add(new JLabel("Select a calendar:"));
        selectionPanel.add(calendarCombo);

        // Panel for buttons.
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Main panel.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action for OK button: switch to the selected calendar.
        okButton.addActionListener((ActionEvent e) -> {
          String selectedCal = (String) calendarCombo.getSelectedItem();
          try {
            controller.useCalendar(selectedCal);
            JOptionPane.showMessageDialog(this, "Switched to calendar: " + selectedCal);
            dispose();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

        // Cancel button closes the dialog.
        cancelButton.addActionListener((ActionEvent e) -> dispose());
      }
      pack();
      setLocationRelativeTo(getParent());
    }
  }

  // Edit Calendar Dialog – allows changing the timezone of the current calendar.
  private class EditCalendarDialog extends javax.swing.JDialog {
    private JTextField timezoneField;
    private JButton saveButton;
    private JButton cancelButton;
    private CalendarController controller;

    public EditCalendarDialog(JFrame parent, CalendarController controller) {
      super(parent, "Edit Calendar Timezone", true);
      this.controller = controller;
      initComponents();
    }

    private void initComponents() {
      timezoneField = new JTextField(20);
      saveButton = new JButton("Save");
      cancelButton = new JButton("Cancel");

      JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
      panel.add(new JLabel("New Timezone:"));
      panel.add(timezoneField);
      panel.add(saveButton);
      panel.add(cancelButton);
      add(panel);
      pack();
      setLocationRelativeTo(getParent());

      saveButton.addActionListener((ActionEvent e) -> {
        String newTimezone = timezoneField.getText().trim();
        if (newTimezone.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Please enter a timezone.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        String currentCalName = JOptionPane.showInputDialog(this, "Enter current calendar name:");
        if (currentCalName == null || currentCalName.trim().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Calendar name required.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        try {
          controller.editCalendar(currentCalName, "timezone", newTimezone);
          JOptionPane.showMessageDialog(this, "Calendar timezone updated to: " + newTimezone);
          dispose();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      });
      cancelButton.addActionListener((ActionEvent e) -> dispose());
    }
  }

  // Create Event Dialog – for creating single or recurring events.
  private class CreateEventDialog extends javax.swing.JDialog {
    private JTextField nameField;
    private JTextField dateField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextField descriptionField;
    private JTextField locationField;
    private javax.swing.JCheckBox recurringCheck;
    private JTextField weekdaysField;
    private JTextField occurrencesField;
    private JTextField untilField;
    private JButton createButton;
    private JButton cancelButton;
    private CalendarController controller;
    private LocalDate defaultDate;

    public CreateEventDialog(JFrame parent, CalendarController controller, LocalDate defaultDate) {
      super(parent, "Create Event", true);
      this.controller = controller;
      this.defaultDate = defaultDate;
      initComponents();
    }

    private void initComponents() {
      nameField = new JTextField(20);
      dateField = new JTextField(10);
      dateField.setText(defaultDate.toString());
      startTimeField = new JTextField(5);
      startTimeField.setText("10:00");
      endTimeField = new JTextField(5);
      endTimeField.setText("11:00");
      descriptionField = new JTextField(20);
      locationField = new JTextField(20);
      recurringCheck = new javax.swing.JCheckBox("Recurring");
      weekdaysField = new JTextField(5);
      occurrencesField = new JTextField(5);
      untilField = new JTextField(15);
      createButton = new JButton("Create");
      cancelButton = new JButton("Cancel");

      JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
      panel.add(new JLabel("Event Name:"));
      panel.add(nameField);
      panel.add(new JLabel("Date (yyyy-MM-dd):"));
      panel.add(dateField);
      panel.add(new JLabel("Start Time (HH:mm):"));
      panel.add(startTimeField);
      panel.add(new JLabel("End Time (HH:mm):"));
      panel.add(endTimeField);
      panel.add(new JLabel("Description:"));
      panel.add(descriptionField);
      panel.add(new JLabel("Location:"));
      panel.add(locationField);
      panel.add(recurringCheck);
      panel.add(new JLabel(""));
      panel.add(new JLabel("Weekdays (e.g., MTW):"));
      panel.add(weekdaysField);
      panel.add(new JLabel("Occurrences (optional):"));
      panel.add(occurrencesField);
      panel.add(new JLabel("Until DateTime (optional, yyyy-MM-dd'T'HH:mm):"));
      panel.add(untilField);
      panel.add(createButton);
      panel.add(cancelButton);

      add(panel);
      pack();
      setLocationRelativeTo(getParent());

      createButton.addActionListener((ActionEvent e) -> {
        String name = nameField.getText().trim();
        String dateStr = dateField.getText().trim();
        String startTime = startTimeField.getText().trim();
        String endTime = endTimeField.getText().trim();
        String description = descriptionField.getText().trim();
        String location = locationField.getText().trim();
        boolean isRecurring = recurringCheck.isSelected();
        try {
          String startDateTime = dateStr + "T" + startTime;
          String endDateTime = dateStr + "T" + endTime;
          if (!isRecurring) {
            controller.createSingleEvent(name, startDateTime, endDateTime, description, location, true, false);
            JOptionPane.showMessageDialog(this, "Event created successfully!");
          } else {
            String weekdays = weekdaysField.getText().trim();
            String occStr = occurrencesField.getText().trim();
            String untilStr = untilField.getText().trim();
            if (!occStr.isEmpty()) {
              int occurrences = Integer.parseInt(occStr);
              controller.createRecurringEventOccurrences(name, startDateTime, endDateTime, description, location, true, weekdays, occurrences, false);
              JOptionPane.showMessageDialog(this, "Recurring event created with " + occurrences + " occurrences.");
            } else if (!untilStr.isEmpty()) {
              controller.createRecurringEventUntil(name, startDateTime, endDateTime, description, location, true, weekdays, untilStr, false);
              JOptionPane.showMessageDialog(this, "Recurring event created until " + untilStr + ".");
            } else {
              JOptionPane.showMessageDialog(this, "Please specify either occurrences or an until date for recurring event.", "Error", JOptionPane.ERROR_MESSAGE);
              return;
            }
          }
          dispose();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      });

      cancelButton.addActionListener((ActionEvent e) -> dispose());
    }
  }

  private class EditEventDialog extends javax.swing.JDialog {
    private JTextField nameField;
    private JTextField startField; // expects yyyy-MM-dd'T'HH:mm
    private JTextField endField;   // expects yyyy-MM-dd'T'HH:mm
    private JTextField propertyField;
    private JTextField newValueField;
    private JButton saveButton;
    private JButton cancelButton;
    private CalendarController controller;

    public EditEventDialog(JFrame parent, CalendarController controller) {
      super(parent, "Edit Event", true);
      this.controller = controller;
      initComponents();
    }

    private void initComponents() {
      nameField = new JTextField(20);
      startField = new JTextField(15);
      endField = new JTextField(15);
      propertyField = new JTextField(10);
      newValueField = new JTextField(20);
      saveButton = new JButton("Save");
      cancelButton = new JButton("Cancel");

      JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
      panel.add(new JLabel("Event Name:"));
      panel.add(nameField);
      panel.add(new JLabel("Start DateTime (yyyy-MM-dd'T'HH:mm):"));
      panel.add(startField);
      panel.add(new JLabel("End DateTime (yyyy-MM-dd'T'HH:mm):"));
      panel.add(endField);
      panel.add(new JLabel("Property (name, description, location, ispublic):"));
      panel.add(propertyField);
      panel.add(new JLabel("New Value:"));
      panel.add(newValueField);
      panel.add(saveButton);
      panel.add(cancelButton);

      add(panel);
      pack();
      setLocationRelativeTo(getParent());

      saveButton.addActionListener((ActionEvent e) -> {
        String name = nameField.getText().trim();
        String startDT = startField.getText().trim();
        String endDT = endField.getText().trim();
        String property = propertyField.getText().trim();
        String newValue = newValueField.getText().trim();
        if (name.isEmpty() || startDT.isEmpty() || endDT.isEmpty() || property.isEmpty() || newValue.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        try {
          controller.editSingleEvent(property, name, startDT, endDT, newValue);
          JOptionPane.showMessageDialog(this, "Event edited successfully!");
          dispose();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error editing event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      });

      cancelButton.addActionListener((ActionEvent e) -> dispose());
    }
  }

  private class CopyEventDialog extends javax.swing.JDialog {
    private JTextField nameField;
    private JTextField sourceDateTimeField;
    private JTextField targetCalField;
    private JTextField targetDateTimeField;
    private JButton copyButton;
    private JButton cancelButton;
    private CalendarController controller;

    public CopyEventDialog(JFrame parent, CalendarController controller) {
      super(parent, "Copy Event", true);
      this.controller = controller;
      initComponents();
    }

    private void initComponents() {
      nameField = new JTextField(20);
      sourceDateTimeField = new JTextField(15);
      targetCalField = new JTextField(20);
      targetDateTimeField = new JTextField(15);
      copyButton = new JButton("Copy");
      cancelButton = new JButton("Cancel");

      JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
      panel.add(new JLabel("Event Name:"));
      panel.add(nameField);
      panel.add(new JLabel("Source DateTime (yyyy-MM-dd'T'HH:mm):"));
      panel.add(sourceDateTimeField);
      panel.add(new JLabel("Target Calendar Name:"));
      panel.add(targetCalField);
      panel.add(new JLabel("Target DateTime (yyyy-MM-dd'T'HH:mm):"));
      panel.add(targetDateTimeField);
      panel.add(copyButton);
      panel.add(cancelButton);

      add(panel);
      pack();
      setLocationRelativeTo(getParent());

      copyButton.addActionListener((ActionEvent e) -> {
        String name = nameField.getText().trim();
        String sourceDT = sourceDateTimeField.getText().trim();
        String targetCal = targetCalField.getText().trim();
        String targetDT = targetDateTimeField.getText().trim();
        if (name.isEmpty() || sourceDT.isEmpty() || targetCal.isEmpty() || targetDT.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        try {
          controller.copyEvent(name, sourceDT, targetCal, targetDT);
          JOptionPane.showMessageDialog(this, "Event copied successfully!");
          dispose();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error copying event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      });

      cancelButton.addActionListener((ActionEvent e) -> dispose());
    }
  }
}