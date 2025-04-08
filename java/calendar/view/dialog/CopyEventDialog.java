package calendar.view.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import calendar.controller.CalendarController;

public class CopyEventDialog extends javax.swing.JDialog{
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
