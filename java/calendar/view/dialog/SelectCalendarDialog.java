package calendar.view.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import calendar.controller.CalendarController;

public class SelectCalendarDialog extends javax.swing.JDialog {
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

