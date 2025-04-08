package calendar.view;

import calendar.controller.CalendarController;
import calendar.model.event.Event;
import calendar.view.dialog.CopyEventDialog;
import calendar.view.dialog.CreateEventDialog;
import calendar.view.dialog.EditCalendarDialog;
import calendar.view.dialog.EditEventDialog;
import calendar.view.dialog.NewCalendarDialog;
import calendar.view.dialog.SelectCalendarDialog;

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

}