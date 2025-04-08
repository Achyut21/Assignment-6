package calendar.view.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import calendar.controller.CalendarController;

public class EditEventDialog  extends javax.swing.JDialog {
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
