/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package kasirapp.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author REFI
 */
public class KelolaAkunPage extends javax.swing.JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtUsername, txtPassword;
    private JComboBox<String> cmbRole;
    private final MainFrame frame;

    public KelolaAkunPage(MainFrame frame) {
        this.frame = frame;
        initComponents();
        customInit();
    }

    private void customInit() {
        setLayout(new GridBagLayout());
        setBackground(new Color(0, 40, 85));

        JPanel panelUtama = new JPanel(null);
        panelUtama.setBackground(new Color(255, 255, 255, 25));
        panelUtama.setPreferredSize(new Dimension(780, 450));
        panelUtama.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 2, true));

        JLabel lblTitle = new JLabel("Kelola Akun Kasir", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 20, 780, 30);
        panelUtama.add(lblTitle);

        // Tabel
        String[] kolom = {"Username", "Password", "Role"};
        model = new DefaultTableModel(kolom, 0) {
            // agar kolom username tidak bisa diedit langsung di table
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(80, 70, 620, 150);
        panelUtama.add(scrollPane);

        // Form input
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

        cmbRole = new JComboBox<>(new String[]{"admin", "kasir"});
        cmbRole.setBounds(190, 290, 150, 25);
        panelUtama.add(cmbRole);

        // Tombol
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

        // Add panelUtama
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(panelUtama, gbc);

        // Event handling
        btnTambah.addActionListener(e -> tambahAkun());
        btnEdit.addActionListener(e -> prepareEditAkun());
        btnHapus.addActionListener(e -> hapusAkun());
        btnKembali.addActionListener(e -> frame.showPage("Admin"));

        // double-click baris untuk isi form (mempermudah edit)
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        txtUsername.setText(model.getValueAt(row, 0).toString());
                        txtPassword.setText(model.getValueAt(row, 1).toString());
                        cmbRole.setSelectedItem(model.getValueAt(row, 2).toString());
                        // biarkan username tetap bisa diedit saat mau buat akun baru,
                        // tapi saat edit kita akan gunakan username yang ada di tabel sebagai key
                    }
                }
            }
        });

        // load data awal dari database
        loadData();
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

    private void loadData() {
        model.setRowCount(0);
        String url = "jdbc:mysql://localhost:3306/db_kasir";
        String dbUser = "root";
        String dbPass = "";
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String sql = "SELECT username, password, role FROM users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                });
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data akun: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tambahAkun() {
        String user = txtUsername.getText().trim();
        String pass = txtPassword.getText().trim();
        String role = cmbRole.getSelectedItem().toString();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua field!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String url = "jdbc:mysql://localhost:3306/db_kasir";
        String dbUser = "root";
        String dbPass = "";

        // cek duplikat username
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String cekSql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement cekPs = conn.prepareStatement(cekSql);
            cekPs.setString(1, user);
            ResultSet cekRs = cekPs.executeQuery();
            if (cekRs.next()) {
                JOptionPane.showMessageDialog(this, "Username sudah ada. Gunakan username lain.", "Info", JOptionPane.INFORMATION_MESSAGE);
                cekRs.close();
                cekPs.close();
                return;
            }
            cekRs.close();
            cekPs.close();

            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, role);
            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(this, "Akun berhasil ditambahkan!");
            txtUsername.setText("");
            txtPassword.setText("");
            cmbRole.setSelectedIndex(0);
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambah akun: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // siapkan form edit (ambil username dari baris terpilih)
    private void prepareEditAkun() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih akun yang ingin diedit!");
            return;
        }
        String username = model.getValueAt(row, 0).toString();
        // Isi form dengan data terpilih
        txtUsername.setText(username);
        txtPassword.setText(model.getValueAt(row, 1).toString());
        cmbRole.setSelectedItem(model.getValueAt(row, 2).toString());

        // Tampilkan dialog konfirmasi untuk edit agar tidak langsung mengeksekusi
        int option = JOptionPane.showConfirmDialog(this, createEditPanel(), "Edit Akun", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            editAkun(username); // gunakan username lama sebagai key
        }
    }

    // panel bantu untuk dialog edit (menampilkan kembali field)
    private JPanel createEditPanel() {
        JPanel p = new JPanel(null);
        p.setPreferredSize(new Dimension(420, 140));

        JLabel l1 = new JLabel("Username:");
        l1.setBounds(10, 10, 80, 25);
        p.add(l1);

        JTextField tUser = new JTextField(txtUsername.getText());
        tUser.setBounds(100, 10, 300, 25);
        tUser.setEditable(false); // username tidak bisa diganti lewat dialog edit untuk menyederhanakan
        p.add(tUser);

        JLabel l2 = new JLabel("Password:");
        l2.setBounds(10, 45, 80, 25);
        p.add(l2);

        JTextField tPass = new JTextField(txtPassword.getText());
        tPass.setBounds(100, 45, 300, 25);
        p.add(tPass);

        JLabel l3 = new JLabel("Role:");
        l3.setBounds(10, 80, 80, 25);
        p.add(l3);

        JComboBox<String> cb = new JComboBox<>(new String[]{"admin", "kasir"});
        cb.setBounds(100, 80, 150, 25);
        cb.setSelectedItem(cmbRole.getSelectedItem());
        p.add(cb);

        // saat OK nanti ambil kembali isian ke txtPassword & cmbRole agar editAkun membaca nilai yang terbaru
        // kita simpan sementara ke komponen form utama
        // lakukan override action pada tombol OK lewat dialog (lebih sederhana: setelah dialog OK, kita set nilai)
        // tetapi karena JOptionPane tidak memberi akses langsung ke tombol OK di sini, kita lakukan setelah dialog kembali di prepareEditAkun()
        // untuk memastikan nilai terbaru, update txtPassword & cmbRole sekarang dengan nilai form dialog:
        // (NOTE: dialog values already set by the fields txtPassword and cmbRole earlier; to reflect edited fields,
        //  we will read directly from these dialog components after JOptionPane returns — but since we can't read them directly,
        //  we instead show separate modal input using JOptionPane.showConfirmDialog that returns OK/Cancel. Simpler approach:)
        // To keep code simple and predictable, we'll show a new JOptionPane in editAkun() that re-reads values from the visible form fields.
        // So here we just return a panel that user edits; after OK, prepareEditAkun() calls editAkun(username) which will read current txtPassword/cmbRole.
        // To make that work, before calling JOptionPane.showConfirmDialog we need to make sure txtPassword and cmbRole reflect what user typed in this panel.
        // We'll synchronize by copying panel's components back to main fields just before returning from dialog.

        // To synchronize: add a focus listener on panel that will transfer values on dialog close — but it's complex.
        // Simpler: avoid the custom panel complexity: we already set main form fields before showing dialog, so user will edit those main fields directly.
        // So here, instead of using custom panel, we will prompt user to edit the form fields already visible (main form). To keep UX simple,
        // return a small informative panel that tells user to edit fields in the form then press OK.
        JPanel info = new JPanel(null);
        info.setPreferredSize(new Dimension(420, 80));
        JLabel infoLabel = new JLabel("<html>Edit data di form (teks di bawah tabel) lalu klik OK untuk menyimpan perubahan.</html>");
        infoLabel.setBounds(10, 10, 400, 60);
        info.add(infoLabel);
        return info;
    }

    // edit menggunakan username lama sebagai key
    private void editAkun(String usernameKey) {
        String newPass = txtPassword.getText().trim();
        String newRole = cmbRole.getSelectedItem().toString();

        if (newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String url = "jdbc:mysql://localhost:3306/db_kasir";
        String dbUser = "root";
        String dbPass = "";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String sql = "UPDATE users SET password = ?, role = ? WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPass);
            ps.setString(2, newRole);
            ps.setString(3, usernameKey);
            int updated = ps.executeUpdate();
            ps.close();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Akun berhasil diperbarui!");
                txtUsername.setText("");
                txtPassword.setText("");
                cmbRole.setSelectedIndex(0);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Akun tidak ditemukan (gagal update).", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengedit akun: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusAkun() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih akun yang ingin dihapus!");
            return;
        }

        String username = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus akun '" + username + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String url = "jdbc:mysql://localhost:3306/db_kasir";
        String dbUser = "root";
        String dbPass = "";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String sql = "DELETE FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            int deleted = ps.executeUpdate();
            ps.close();

            if (deleted > 0) {
                JOptionPane.showMessageDialog(this, "Akun berhasil dihapus!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Akun tidak ditemukan (gagal hapus).", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus akun: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
