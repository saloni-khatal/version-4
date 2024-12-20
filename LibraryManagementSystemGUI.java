import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LibraryManagementSystemGUI {
    private JFrame frame;
    private JTextField titleField, authorField, yearField;
    private JTextArea outputArea;
    private Connection connection;

    public LibraryManagementSystemGUI() {
       try {
    connection = DriverManager.getConnection(
        "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12753008?useSSL=false&serverTimezone=UTC", 
        "sql12753008", 
        "9wkJQuCjWi"
    );
} catch (SQLException e) {
    JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    e.printStackTrace();  // This will print the stack trace to the console for debugging.
}
        // Create the frame and set layout
        frame = new JFrame("Library Management");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create input fields panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("Year:"));
        yearField = new JTextField();
        inputPanel.add(yearField);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Book");
        JButton removeButton = new JButton("Remove Book");
        JButton listButton = new JButton("List Books");
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(listButton);

        // Text area for output
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add components to frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        // Action Listeners
        addButton.addActionListener(e -> addBook());
        removeButton.addActionListener(e -> removeBook());
        listButton.addActionListener(e -> listBooks());

        // Show the frame
        frame.setVisible(true);
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        int year = Integer.parseInt(yearField.getText());

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO books (title, author, year) VALUES (?, ?, ?)")) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setInt(3, year);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Book added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error adding book!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeBook() {
        String title = titleField.getText();
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM books WHERE title = ?")) {
            stmt.setString(1, title);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Book removed successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error removing book!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listBooks() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            outputArea.setText("");  // Clear output area
            while (rs.next()) {
                outputArea.append(rs.getString("title") + " by " + rs.getString("author") + " (" + rs.getInt("year") + ")\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error listing books!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryManagementSystemGUI::new);
    }
}