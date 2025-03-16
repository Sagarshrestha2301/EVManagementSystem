package com.taylorsuniversity.ev.usermanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel extends JPanel {
    private UserController userController;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton showPasswordButton;
    private JLabel signupLink;
    private Runnable onLoginSuccess;

    public LoginPanel(UserController userController, Runnable onLoginSuccess) {
        this.userController = userController;
        this.onLoginSuccess = onLoginSuccess;
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

        JLabel welcomeLabel = new JLabel("<html><center>Welcome back to<br><b>EcoCharge</b><br><br>Your EV companion</center></html>");
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

        // Title and Sign Up link
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Log in to your account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(34, 139, 87));
        signupLink = new JLabel("Sign Up");
        signupLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        signupLink.setForeground(new Color(34, 139, 87));
        signupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(LoginPanel.this);
                frame.setContentPane(new SignupPanel(userController));
                frame.revalidate();
                frame.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                signupLink.setForeground(Color.BLUE);
                signupLink.setFont(new Font("Segoe UI", Font.PLAIN | Font.ITALIC, 14));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                signupLink.setForeground(new Color(34, 139, 87));
                signupLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
        });
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(signupLink, BorderLayout.EAST);
        formPanel.add(titlePanel, gbc);

        // Email field
        gbc.gridy++;
        gbc.gridwidth = 2;
        emailField = new JTextField("Email");
        emailField.setPreferredSize(new Dimension(320, 45));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        addPlaceholder(emailField, "Email");
        formPanel.add(emailField, gbc);

        // Password field with Show button
        gbc.gridy++;
        gbc.gridwidth = 1;
        passwordField = new JPasswordField("Password");
        passwordField.setPreferredSize(new Dimension(270, 45));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        addPlaceholder(passwordField, "Password");
        formPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        showPasswordButton = new JButton("Show");
        showPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordButton.setForeground(Color.BLUE);
        showPasswordButton.setBorderPainted(false);
        showPasswordButton.setContentAreaFilled(false);
        showPasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        showPasswordButton.setToolTipText("Show/Hide password");
        showPasswordButton.addActionListener(e -> {
            if (showPasswordButton.getText().equals("Show")) {
                passwordField.setEchoChar((char) 0);
                showPasswordButton.setText("Hide");
            } else {
                passwordField.setEchoChar('•');
                showPasswordButton.setText("Show");
            }
        });
        formPanel.add(showPasswordButton, gbc);

        // Forgot Password link
        gbc.gridx = 1;
        gbc.gridy++;
        JLabel forgotPasswordLink = new JLabel("Forgot password?");
        forgotPasswordLink.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordLink.setForeground(Color.BLUE);
        forgotPasswordLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordLink.setToolTipText("Click to reset your password");
        forgotPasswordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginPanel.this, "Password reset functionality not implemented yet.");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLink.setFont(new Font("Segoe UI", Font.PLAIN | Font.ITALIC, 12));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLink.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
        });
        formPanel.add(forgotPasswordLink, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        loginButton = new JButton("Log In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(20, 83, 52));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        loginButton.setContentAreaFilled(false);
        loginButton.addActionListener(e -> handleLogin());
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(34, 139, 87));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(20, 83, 52));
            }
        });
        formPanel.add(loginButton, gbc);

        return formPanel;
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
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    field.setText(placeholder);
                }
            }
        });
    }

    private void handleLogin() {
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String email = emailField.getText();
                    String password = new String(passwordField.getPassword());
                    if (email.equals("Email") || password.equals("Password")) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(LoginPanel.this, "Please enter your email and password.", "Error", JOptionPane.ERROR_MESSAGE);
                            loginButton.setEnabled(true);
                            loginButton.setText("Log In");
                        });
                        return null;
                    }
                    UserDTO userDTO = userController.login(email, password);
                    if (userDTO != null) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(LoginPanel.this, "Login successful! Welcome " + userDTO.getFullName());
                            onLoginSuccess.run();
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(LoginPanel.this, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
                            loginButton.setEnabled(true);
                            loginButton.setText("Log In");
                        });
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(LoginPanel.this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        loginButton.setEnabled(true);
                        loginButton.setText("Log In");
                    });
                    e.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }
}