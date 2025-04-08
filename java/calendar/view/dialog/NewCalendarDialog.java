package calendar.view.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import calendar.controller.CalendarController;

public class NewCalendarDialog extends javax.swing.JDialog {
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
