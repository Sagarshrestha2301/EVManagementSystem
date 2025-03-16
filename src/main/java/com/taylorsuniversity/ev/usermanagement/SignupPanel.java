package com.taylorsuniversity.ev.usermanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SignupPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(SignupPanel.class.getName());
    private UserController userController;
    private JTextField fullNameField, emailField, phoneField, vehicleNumberField, vehicleModelField;
    private JPasswordField passwordField;
    private JButton signupButton;
    private JLabel loginLink;

    public SignupPanel(UserController userController) {
        this.userController = userController;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        add(createLeftSidebar(), BorderLayout.WEST);
        add(createFormPanel(), BorderLayout.CENTER);
    }

    private JPanel createLeftSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(34, 139, 87), 0, getHeight(), new Color(20, 83, 52));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(30, 20, 30, 20)));

        // Circular Logo
        JLabel logoLabel = new JLabel("EcoCharge", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 28));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(logoLabel, BorderLayout.NORTH);

        JLabel welcomeLabel = new JLabel("<html><center>Join<br><b>EcoCharge</b><br><br>Start your green journey today</center></html>");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sidebar.add(welcomeLabel, BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // Title and Login link
        addTitlePanel(formPanel, gbc);

        // Form fields
        addFormFields(formPanel, gbc);

        // Sign Up button
        addSignupButton(formPanel, gbc);

        return formPanel;
    }

    private void addTitlePanel(JPanel formPanel, GridBagConstraints gbc) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Create your account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(34, 139, 87));
        loginLink = new JLabel("Login");
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginLink.setForeground(new Color(34, 139, 87));
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToLoginPanel();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setForeground(Color.BLUE);
                loginLink.setFont(new Font("Segoe UI", Font.PLAIN | Font.ITALIC, 14));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setForeground(new Color(34, 139, 87));
                loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
        });
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(loginLink, BorderLayout.EAST);
        formPanel.add(titlePanel, gbc);
    }

    private void addFormFields(JPanel formPanel, GridBagConstraints gbc) {
        gbc.gridy++;
        gbc.gridwidth = 2;
        fullNameField = new JTextField("Full Name");
        fullNameField.setPreferredSize(new Dimension(320, 45));
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        addPlaceholder(fullNameField, "Full Name");
        formPanel.add(fullNameField, gbc);

        gbc.gridy++;
        phoneField = new JTextField("Contact Number");
        phoneField.setPreferredSize(new Dimension(320, 45));
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        addPlaceholder(phoneField, "Contact Number");
        formPanel.add(phoneField, gbc);

        gbc.gridy++;
        emailField = new JTextField("Email");
        emailField.setPreferredSize(new Dimension(320, 45));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        addPlaceholder(emailField, "Email");
        formPanel.add(emailField, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField("Password");
        passwordField.setPreferredSize(new Dimension(320, 45));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        addPlaceholder(passwordField, "Password");
        formPanel.add(passwordField, gbc);

        gbc.gridy++;
        vehicleNumberField = new JTextField("Vehicle Number");
        vehicleNumberField.setPreferredSize(new Dimension(320, 45));
        vehicleNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        vehicleNumberField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        addPlaceholder(vehicleNumberField, "Vehicle Number");
        formPanel.add(vehicleNumberField, gbc);

        gbc.gridy++;
        vehicleModelField = new JTextField("Vehicle Model");
        vehicleModelField.setPreferredSize(new Dimension(320, 45));
        vehicleModelField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        vehicleModelField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        addPlaceholder(vehicleModelField, "Vehicle Model");
        formPanel.add(vehicleModelField, gbc);
    }

    private void addSignupButton(JPanel formPanel, GridBagConstraints gbc) {
        gbc.gridy++;
        signupButton = new JButton("Create Account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        signupButton.setBackground(new Color(20, 83, 52));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        signupButton.setContentAreaFilled(false);
        signupButton.addActionListener(e -> handleSignup());
        signupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signupButton.setBackground(new Color(34, 139, 87));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                signupButton.setBackground(new Color(20, 83, 52));
            }
        });
        formPanel.add(signupButton, gbc);
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    field.setText(placeholder);
                }
            }
        });
    }

    private void navigateToLoginPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(SignupPanel.this);
        Runnable onLoginSuccess = () -> {
            frame.setContentPane(new DashboardPanel(userController.getCurrentUser()));
            frame.revalidate();
            frame.repaint();
        };
        frame.setContentPane(new LoginPanel(userController, onLoginSuccess));
        frame.revalidate();
        frame.repaint();
    }

    private void navigateAfterSignup() {
        navigateToLoginPanel();
    }

    private void handleSignup() {
        signupButton.setEnabled(false);
        signupButton.setText("Creating account...");
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String fullName = fullNameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phoneNumber = phoneField.getText().trim();
                    String vehicleNumber = vehicleNumberField.getText().trim();
                    String vehicleModel = vehicleModelField.getText().trim();
                    String password = new String(passwordField.getPassword());

                    if (fullName.equals("Full Name") || fullName.isEmpty() ||
                            email.equals("Email") || email.isEmpty() ||
                            phoneNumber.equals("Contact Number") || phoneNumber.isEmpty() ||
                            vehicleNumber.equals("Vehicle Number") || vehicleNumber.isEmpty() ||
                            vehicleModel.equals("Vehicle Model") || vehicleModel.isEmpty() ||
                            password.equals("Password") || password.isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(SignupPanel.this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                            signupButton.setEnabled(true);
                            signupButton.setText("Create Account");
                        });
                        return null;
                    }

                    UserDTO userDTO = userController.signup(fullName, email, phoneNumber, vehicleNumber, password);
                    if (userDTO != null) {
                        User user = userController.getUser(userDTO.getEmail());
                        if (user != null) {
                            user.setVehicleModel(vehicleModel);
                            userController.updateUser(user);
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(SignupPanel.this, "Signup successful! Welcome " + userDTO.getFullName());
                                navigateAfterSignup();
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(SignupPanel.this, "Signup failed: Could not retrieve user.", "Error", JOptionPane.ERROR_MESSAGE);
                                signupButton.setEnabled(true);
                                signupButton.setText("Create Account");
                            });
                            LOGGER.log(Level.WARNING, "User not found after signup for email: " + email);
                        }
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(SignupPanel.this, "Signup failed. A user with this email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                            signupButton.setEnabled(true);
                            signupButton.setText("Create Account");
                        });
                        LOGGER.log(Level.WARNING, "Signup failed for email: " + email + ". User may already exist.");
                    }
                } catch (IllegalArgumentException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(SignupPanel.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        signupButton.setEnabled(true);
                        signupButton.setText("Create Account");
                    });
                    LOGGER.log(Level.WARNING, "Validation error during signup: " + e.getMessage(), e);
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(SignupPanel.this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        signupButton.setEnabled(true);
                        signupButton.setText("Create Account");
                    });
                    LOGGER.log(Level.SEVERE, "Unexpected error during signup", e);
                }
                return null;
            }
        };
        worker.execute();
    }
}