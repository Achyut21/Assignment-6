package calendar.view.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import calendar.controller.CalendarController;

public class EditCalendarDialog  extends javax.swing.JDialog {
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
