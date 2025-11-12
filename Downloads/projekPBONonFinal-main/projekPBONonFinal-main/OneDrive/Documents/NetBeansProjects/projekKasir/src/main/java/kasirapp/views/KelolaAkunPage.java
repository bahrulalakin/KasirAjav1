/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kasirapp.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author REFI
 */
public class KelolaAkunPage extends javax.swing.JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtUsername, txtPassword;
    private JComboBox<String> cmbRole;

    public KelolaAkunPage(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(0, 40, 85)); // warna utama biru tua elegan

        // ===== PANEL UTAMA =====
        JPanel panelUtama = new JPanel(null);
        panelUtama.setBackground(new Color(255, 255, 255, 25)); // transparan lembut
        panelUtama.setPreferredSize(new Dimension(780, 450));
        panelUtama.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 2, true));

        JLabel lblTitle = new JLabel("Kelola Akun Kasir", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 20, 780, 30);
        panelUtama.add(lblTitle);

        // ====== TABLE ======
        String[] kolom = {"Username", "Password", "Role"};
        Object[][] data = {
            {"admin", "admin", "Admin"},
            {"kasir1", "1234", "Kasir"},
            {"kasir2", "abcd", "Kasir"}
        };
        model = new DefaultTableModel(data, kolom);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(80, 70, 620, 150);
        panelUtama.add(scrollPane);

        // ====== FORM INPUT ======
        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setBounds(100, 250, 80, 25);
        panelUtama.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(190, 250, 150, 25);
        panelUtama.add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(360, 250, 80, 25);
        panelUtama.add(lblPass);

        txtPassword = new JTextField();
        txtPassword.setBounds(450, 250, 150, 25);
        panelUtama.add(txtPassword);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setForeground(Color.WHITE);
        lblRole.setBounds(100, 290, 80, 25);
        panelUtama.add(lblRole);

        cmbRole = new JComboBox<>(new String[]{"Admin", "Kasir"});
        cmbRole.setBounds(190, 290, 150, 25);
        panelUtama.add(cmbRole);

        // ====== TOMBOL AKSI ======
        JButton btnTambah = createStyledButton("Tambah", new Color(0, 123, 255));
        btnTambah.setBounds(360, 290, 100, 28);
        panelUtama.add(btnTambah);

        JButton btnEdit = createStyledButton("Edit", new Color(0, 123, 255));
        btnEdit.setBounds(470, 290, 80, 28);
        panelUtama.add(btnEdit);

        JButton btnHapus = createStyledButton("Hapus", new Color(220, 53, 69));
        btnHapus.setBounds(560, 290, 100, 28);
        panelUtama.add(btnHapus);

        JButton btnKembali = createStyledButton("Kembali", new Color(108, 117, 125));
        btnKembali.setBounds(340, 340, 120, 35);
        panelUtama.add(btnKembali);

        // ====== Tambahkan panel ke tengah layar ======
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(panelUtama, gbc);

        // ====== EVENT HANDLING ======
        btnTambah.addActionListener(e -> {
            String user = txtUsername.getText();
            String pass = txtPassword.getText();
            String role = cmbRole.getSelectedItem().toString();

            if (!user.isEmpty() && !pass.isEmpty()) {
                model.addRow(new Object[]{user, pass, role});
                txtUsername.setText("");
                txtPassword.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Lengkapi data terlebih dahulu!");
            }
        });

        btnEdit.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                model.setValueAt(txtUsername.getText(), selected, 0);
                model.setValueAt(txtPassword.getText(), selected, 1);
                model.setValueAt(cmbRole.getSelectedItem(), selected, 2);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit!");
            }
        });

        btnHapus.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                model.removeRow(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!");
            }
        });

        btnKembali.addActionListener(e -> frame.showPage("Admin"));
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // efek hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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
