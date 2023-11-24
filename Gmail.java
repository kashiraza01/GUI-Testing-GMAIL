import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Gmail {

    private JFrame f;
    private Connection connection;

    public Gmail() {
        initializeDatabase();
        f = new JFrame("User Authentication App");
        f.setSize(400, 200);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new CardLayout());

        JPanel register = createregister();
        JPanel login = createlogin();
        
        f.add(register, "register");
        f.add(login, "login");


        CardLayout cardLayout = (CardLayout) f.getContentPane().getLayout();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.addActionListener(e -> cardLayout.show(f.getContentPane(), "login"));
        registerButton.addActionListener(e -> cardLayout.show(f.getContentPane(), "register"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);


        f.add(buttonPanel, BorderLayout.SOUTH);
        f.setVisible(true);
    }

    private void initializeDatabase() {
        try {

            String url = "jdbc:mysql://localhost:3306/Gmail";

            connection = DriverManager.getConnection(url);

            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255), password VARCHAR(255))";
            String createMessagesTable = "CREATE TABLE IF NOT EXISTS Messages (id INT AUTO_INCREMENT PRIMARY KEY, sender VARCHAR(255), recipient VARCHAR(255), message_content TEXT)";

            try (PreparedStatement createUsersTableStatement = connection.prepareStatement(createUsersTable);
                    PreparedStatement createMessagesTableStatement = connection.prepareStatement(createMessagesTable)) {

                createUsersTableStatement.executeUpdate();
                createMessagesTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private JPanel createlogin() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JTextField username = new JTextField(20);
        JPasswordField pass = new JPasswordField(20);

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = username.getText();
                String password = new String(pass.getPassword());

                if (checkLogin(user, password)) {
                    JOptionPane.showMessageDialog(f, "Login Successful!");
                } else {
                    JOptionPane.showMessageDialog(f, "Login Failed. Please check your credentials.");
                }
            }
        });

        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("Password:"));
        panel.add(pass);
        panel.add(loginButton);

        return panel;
    }

    private boolean checkLogin(String username, String password) {

        try {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                return preparedStatement.executeQuery().next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private JPanel createregister() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JTextField username = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JButton registerButton = new JButton("Register");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user= username.getText();
                String password = new String(passwordField.getPassword());

                if (registerUser(user, password)) {
                    JOptionPane.showMessageDialog(f, "Registration Successful!");
                } else {
                    JOptionPane.showMessageDialog(f, "Username already exists. Please choose a different one.");
                }
            }
        });

        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(registerButton);

        return panel;
    }

    private boolean registerUser(String username, String password) {

        try {
            String query = "INSERT INTO Users (username, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                preparedStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        Gmail gmail = new Gmail();
    }
}
