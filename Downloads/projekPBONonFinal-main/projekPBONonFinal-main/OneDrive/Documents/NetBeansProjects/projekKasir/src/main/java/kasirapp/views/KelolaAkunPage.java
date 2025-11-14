package kasirapp.views;

import kasirapp.TesKoneksi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

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
    
    private Connection getConnection() throws SQLException {
        return TesKoneksi.getConnection(); // Gunakan TesKoneksi
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
        String[] kolom = {"ID", "Username", "Password", "Role"}; // Tambahkan ID
        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                return super.getColumnClass(columnIndex);
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        table.getColumnModel().getColumn(0).setMinWidth(0); // Sembunyikan ID
        table.getColumnModel().getColumn(0).setMaxWidth(0); // Sembunyikan ID
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);

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

        cmbRole = new JComboBox<>(new String[]{"Admin", "Kasir"}); // Kapital A
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
                        txtUsername.setText(model.getValueAt(row, 1).toString());
                        txtPassword.setText(model.getValueAt(row, 2).toString());
                        cmbRole.setSelectedItem(model.getValueAt(row, 3).toString());
                        // Saat double-click, kita hanya mengisi form, eksekusi edit dilakukan lewat tombol Edit
                    }
                }
            }
        });

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
        try (Connection conn = getConnection()) {
            String sql = "SELECT id_user, username, password, role FROM users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_user"), // Kolom ID
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                });
            }
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
        
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, role);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Akun berhasil ditambahkan!");
            txtUsername.setText("");
            txtPassword.setText("");
            cmbRole.setSelectedIndex(0);
            loadData();
        } catch (SQLException e) {
            // Error code 1062 = Duplicate entry (Username sudah ada)
            if (e.getErrorCode() == 1062) {
                 JOptionPane.showMessageDialog(this, "Username sudah ada. Gunakan username lain.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Gagal menambah akun: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void prepareEditAkun() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih akun yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Ambil ID user dari kolom 0
        int userId = (int) model.getValueAt(row, 0); 
        String username = model.getValueAt(row, 1).toString();
        
        // Pastikan form terisi dari baris terpilih
        txtUsername.setText(username);
        txtPassword.setText(model.getValueAt(row, 2).toString());
        cmbRole.setSelectedItem(model.getValueAt(row, 3).toString());
        
        // Prompt pengguna untuk mengedit field yang sudah terisi di form
        int option = JOptionPane.showConfirmDialog(this, 
                "<html>Edit data di form (teks di bawah tabel) lalu klik OK untuk menyimpan perubahan. <br>Username tidak dapat diubah!</html>", 
                "Edit Akun " + username, 
                JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            // Gunakan nilai dari form yang sudah terisi/diedit
            String newPass = txtPassword.getText().trim();
            String newRole = cmbRole.getSelectedItem().toString();
            editAkun(userId, newPass, newRole); 
        }
    }

    // edit menggunakan id_user sebagai key
    private void editAkun(int userIdKey, String newPass, String newRole) {
        if (newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = getConnection()) {
            // Update berdasarkan id_user
            String sql = "UPDATE users SET password = ?, role = ? WHERE id_user = ?"; 
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPass);
            ps.setString(2, newRole);
            ps.setInt(3, userIdKey); 
            int updated = ps.executeUpdate();

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

        int userId = (int) model.getValueAt(row, 0); 
        String username = model.getValueAt(row, 1).toString();

        if (username.equalsIgnoreCase("admin")) {
            JOptionPane.showMessageDialog(this, "Akun Admin tidak dapat dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus akun '" + username + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = getConnection()) {
            // Hapus berdasarkan id_user
            String sql = "DELETE FROM users WHERE id_user = ?"; 
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId); 
            int deleted = ps.executeUpdate();

            if (deleted > 0) {
                JOptionPane.showMessageDialog(this, "Akun berhasil dihapus!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Akun tidak ditemukan (gagal hapus).", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
             // Cek jika akun sudah terikat dengan transaksi (FK error)
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus akun: Akun ini sudah tercatat dalam transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus akun: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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
