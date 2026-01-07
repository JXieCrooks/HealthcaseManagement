package View;


import Controller.UserController;
import Model.User;
import View.PatientView.PatientHomePageView;
import View.StaffView.StaffHomePageView;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;

    private UserController userController;

    public LoginView() {
        userController = new UserController();

        setTitle("Healthcare Referral System - Login");
        setSize(400, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);


        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);


        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"PATIENT", "STAFF"});
        panel.add(roleComboBox, gbc);


        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        loginButton = new JButton("Login");
        panel.add(loginButton, gbc);

        add(panel);

        bindEvents();
    }

    private void bindEvents() {
        loginButton.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = roleComboBox.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userController.login(username, password, role);

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username, password or role",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Login successful: " + user.getUsername());

            this.dispose(); // close loginPage


            if ("PATIENT".equalsIgnoreCase(user.getRole())) {
                PatientHomePageView patientPage = new PatientHomePageView(user);
                patientPage.showHomePage();
            } else if ("STAFF".equalsIgnoreCase(user.getRole())) {
                StaffHomePageView staffPage = new StaffHomePageView(user);
                staffPage.showHomePage();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Unknown role: " + user.getRole());
            }
        }
    }
}
