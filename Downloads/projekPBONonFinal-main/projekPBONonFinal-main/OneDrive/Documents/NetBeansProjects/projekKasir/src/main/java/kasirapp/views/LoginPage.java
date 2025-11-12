/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kasirapp.views;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * @author Refi
 */
public class LoginPage extends javax.swing.JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private MainFrame frame;

    public LoginPage(MainFrame frame) {
        this.frame = frame;
        initComponents();
        customInit();
    }

    private void customInit() {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 40, 85)); // Warna utama biru tua (#002855)

        // Panel utama transparan agar background full
        JPanel mainPanel = new JPanel(null);
        mainPanel.setOpaque(false);

        // Judul
        JLabel lblTitle = new JLabel("Login Sistem KasirAja");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(250, 50, 400, 40);
        mainPanel.add(lblTitle);

        // Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(Color.WHITE);
        lblUser.setBounds(230, 150, 100, 25);
        mainPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(330, 150, 200, 30);
        mainPanel.add(txtUsername);

        // Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(230, 200, 100, 25);
        mainPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(330, 200, 200, 30);
        mainPanel.add(txtPassword);

        // Tombol Login
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBounds(310, 260, 150, 35);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(btnLogin);

        // Footer kecil
        JLabel lblFooter = new JLabel("©2025 KasirAja by Refi, Dzul, Bahrul");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(180, 200, 230));
        lblFooter.setBounds(260, 330, 300, 20);
        mainPanel.add(lblFooter);

        // Tambahkan panel utama
        add(mainPanel, BorderLayout.CENTER);

        // Responsif saat window diperbesar/diperkecil
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                mainPanel.setBounds((w - 700) / 2, (h - 400) / 2, 700, 400);
            }
        });

        // Event tombol login
        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Isi username dan password terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // koneksi ke database
                String url = "jdbc:mysql://localhost:3306/db_kasir";
                String dbUser = "root";
                String dbPass = "";

                Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

                String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    JOptionPane.showMessageDialog(this, "Login berhasil sebagai " + role + "!");
                    if (role.equalsIgnoreCase("admin")) {
                        frame.showPage("Admin");
                    } else {
                        frame.showPage("Kasir");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                rs.close();
                ps.close();
                conn.close();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Koneksi database gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
