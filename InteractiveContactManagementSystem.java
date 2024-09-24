import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InteractiveContactManagementSystem extends JFrame {

    private DefaultTableModel contactTableModel;
    private JTable contactTable;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextArea detailsArea;

    public InteractiveContactManagementSystem() {
        setTitle("Interactive Contact Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create contact table with columns Name and Phone
        contactTableModel = new DefaultTableModel(new Object[]{"Name", "Phone"}, 0);
        contactTable = new JTable(contactTableModel);
        contactTable.setFillsViewportHeight(true);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JLabel headerLabel = new JLabel("Contact Management System", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.BLUE);

        nameField = new JTextField(15);
        phoneField = new JTextField(15);
        setupNameField();  // Setup name field to restrict input
        setupPhoneField();  // Setup phone field to restrict input
        JButton addButton = createButton("Add Contact", new Color(76, 175, 80));
        JButton searchButton = createButton("Search Contact", new Color(33, 150, 243));
        JButton editButton = createButton("Edit Contact", new Color(255, 193, 7));
        JButton deleteButton = createButton("Delete Selected", new Color(244, 67, 54));

        customizeTableHeader(contactTable);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(addButton);
        inputPanel.add(searchButton);
        inputPanel.add(editButton);
        inputPanel.add(deleteButton);  // Ensure this line is included

        JScrollPane scrollPane = new JScrollPane(contactTable);

        detailsArea = new JTextArea(5, 30);
        detailsArea.setEditable(false);
        detailsArea.setBorder(BorderFactory.createTitledBorder("Details"));

        add(headerLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(new JScrollPane(detailsArea), BorderLayout.EAST);

        addButton.addActionListener(e -> addContact());
        searchButton.addActionListener(e -> searchContact());
        editButton.addActionListener(e -> editContact());
        deleteButton.addActionListener(e -> deleteContact());
    }

    private void setupNameField() {
        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new NameFilter());
    }

    private void setupPhoneField() {
        ((AbstractDocument) phoneField.getDocument()).setDocumentFilter(new PhoneFilter());
    }

    // Create customized button
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    // Customize table header
    private void customizeTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.LIGHT_GRAY);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 30));
    }

    // Method to add contact
    private void addContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();

        if (name.isEmpty() || phone.isEmpty() || phone.length() != 10) {
            JOptionPane.showMessageDialog(this, "Please enter both name and a valid 10-digit phone number.");
        } else {
            contactTableModel.addRow(new Object[]{name, phone});
            clearFields();
            detailsArea.setText("");
        }
    }

    // Method to search for a contact by name
    private void searchContact() {
        String name = JOptionPane.showInputDialog(this, "Enter name to search:");
        if (name != null && !name.isEmpty()) {
            StringBuilder foundContacts = new StringBuilder("Contacts found:\n");
            boolean found = false;

            for (int i = 0; i < contactTableModel.getRowCount(); i++) {
                if (contactTableModel.getValueAt(i, 0).toString().equalsIgnoreCase(name)) {
                    foundContacts.append("Name: ").append(contactTableModel.getValueAt(i, 0))
                            .append(", Phone: ").append(contactTableModel.getValueAt(i, 1)).append("\n");
                    found = true;
                }
            }

            if (found) {
                detailsArea.setText(foundContacts.toString());
            } else {
                JOptionPane.showMessageDialog(this, "Contact not found.");
                detailsArea.setText("");
            }
        }
    }

    // Method to edit selected contact
    private void editContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = nameField.getText();
            String phone = phoneField.getText();

            if (name.isEmpty() || phone.isEmpty() || phone.length() != 10) {
                JOptionPane.showMessageDialog(this, "Please enter both name and a valid 10-digit phone number.");
            } else {
                contactTableModel.setValueAt(name, selectedRow, 0);
                contactTableModel.setValueAt(phone, selectedRow, 1);
                clearFields();
                detailsArea.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a contact to edit.");
        }
    }

    // Method to delete selected contact
    private void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow >= 0) {
            contactTableModel.removeRow(selectedRow);
            clearFields();
            detailsArea.setText("");
            JOptionPane.showMessageDialog(this, "Contact deleted.");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete.");
        }
    }

    // Clear input fields
    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
    }

    // DocumentFilter to restrict name input
    private class NameFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("[a-zA-Z]*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("[a-zA-Z]*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
        }
    }

    // DocumentFilter to restrict phone number input
    private class PhoneFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("\\d*")) {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                if (currentText.length() + string.length() <= 10) {
                    super.insertString(fb, offset, string, attr);
                }
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("\\d*")) {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                if (currentText.length() - length + text.length() <= 10) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InteractiveContactManagementSystem().setVisible(true));
    }
}