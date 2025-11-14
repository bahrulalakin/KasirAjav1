package kasirapp.views;

import kasirapp.TesKoneksi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class KelolaBarangPage extends javax.swing.JPanel {
    
    private JTable table;
    private DefaultTableModel model;
    private JPanel contentPanel;

    public KelolaBarangPage(MainFrame frame) {
        
        setLayout(new BorderLayout());
        setBackground(new Color(0, 40, 85)); 

        contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);
        add(contentPanel, BorderLayout.CENTER);

        JLabel title = new JLabel("Kelola Barang", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 20, 800, 40);
        contentPanel.add(title);

        // Tabel
        String[] kolom = {"ID", "Nama Produk", "Harga Jual", "Stok", "Satuan", "Harga Beli"};
        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        
        // Sembunyikan kolom ID (0) dan Harga Beli (5) di tampilan utama untuk menyederhanakan UI
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(150, 80, 500, 220);
        contentPanel.add(scrollPane);

        loadTableData();

        // Tombol
        JButton btnTambah = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnHapus = new JButton("Hapus");
        JButton btnKembali = new JButton("Kembali");

        styleButton(btnTambah);
        styleButton(btnEdit);
        styleButton(btnHapus);
        styleButton(btnKembali);
        btnKembali.setBackground(new Color(100, 100, 100));

        btnTambah.setBounds(170, 320, 120, 40);
        btnEdit.setBounds(310, 320, 120, 40);
        btnHapus.setBounds(450, 320, 120, 40);
        btnKembali.setBounds(590, 320, 120, 40);

        contentPanel.add(btnTambah);
        contentPanel.add(btnEdit);
        contentPanel.add(btnHapus);
        contentPanel.add(btnKembali);

        // Aksi tombol
        btnTambah.addActionListener(e -> tambahBarang());
        btnEdit.addActionListener(e -> editBarang());
        btnHapus.addActionListener(e -> hapusBarang());
        btnKembali.addActionListener(e -> frame.showPage("Admin"));

        // double-click row => isi dialog edit cepat
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String id = model.getValueAt(row, 0).toString();
                        showEditDialog(id);
                    }
                }
            }
        });
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0x003F7F));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(50, 90, 150));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0x003F7F));
            }
        });
    }

    private Connection getConnection() throws SQLException {
        return TesKoneksi.getConnection(); // Gunakan TesKoneksi
    }

    private void loadTableData() {
        model.setRowCount(0);
        String sql = "SELECT produk_id, nama_produk, harga_jual, stok, satuan, harga_beli FROM produk ORDER BY produk_id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("produk_id"),
                        rs.getString("nama_produk"),
                        rs.getDouble("harga_jual"),
                        rs.getInt("stok"),
                        rs.getString("satuan"),
                        rs.getDouble("harga_beli")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data produk: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tambahBarang() {
        JTextField tfNama = new JTextField();
        JTextField tfHargaBeli = new JTextField("0"); // Default 0
        JTextField tfHargaJual = new JTextField();
        JTextField tfStok = new JTextField();
        JTextField tfSatuan = new JTextField("pcs"); // Default pcs

        Object[] input = {
                "Nama Produk:", tfNama,
                "Harga Beli:", tfHargaBeli,
                "Harga Jual:", tfHargaJual,
                "Stok:", tfStok,
                "Satuan:", tfSatuan
        };

        int option = JOptionPane.showConfirmDialog(this, input, "Tambah Produk", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nama = tfNama.getText().trim();
                double hargaBeli = Double.parseDouble(tfHargaBeli.getText().trim());
                double hargaJual = Double.parseDouble(tfHargaJual.getText().trim());
                int stok = Integer.parseInt(tfStok.getText().trim());
                String satuan = tfSatuan.getText().trim();

                if (nama.isEmpty() || satuan.isEmpty()) throw new IllegalArgumentException("Nama dan Satuan wajib diisi.");
                if (hargaJual <= 0 || stok < 0) throw new IllegalArgumentException("Harga Jual harus positif dan Stok tidak boleh negatif.");


                String sql = "INSERT INTO produk (nama_produk, harga_beli, harga_jual, stok, satuan) VALUES (?, ?, ?, ?, ?)";
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nama);
                    ps.setDouble(2, hargaBeli);
                    ps.setDouble(3, hargaJual);
                    ps.setInt(4, stok);
                    ps.setString(5, satuan);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!");
                loadTableData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input harga/stok tidak valid (Harus angka).", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Input tidak valid: " + ex.getMessage(), "Peringatan", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menambah produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editBarang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin diedit");
            return;
        }
        String id = model.getValueAt(row, 0).toString();
        showEditDialog(id);
    }

    private void showEditDialog(String produkId) {
        // ambil data produk dari DB
        String sqlSelect = "SELECT nama_produk, harga_beli, harga_jual, stok, satuan FROM produk WHERE produk_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlSelect)) {

            ps.setInt(1, Integer.parseInt(produkId));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JTextField tfNama = new JTextField(rs.getString("nama_produk"));
                    JTextField tfHargaBeli = new JTextField(String.valueOf(rs.getDouble("harga_beli")));
                    JTextField tfHargaJual = new JTextField(String.valueOf(rs.getDouble("harga_jual")));
                    JTextField tfStok = new JTextField(String.valueOf(rs.getInt("stok")));
                    JTextField tfSatuan = new JTextField(rs.getString("satuan"));

                    Object[] input = {
                            "Nama Produk:", tfNama,
                            "Harga Beli:", tfHargaBeli,
                            "Harga Jual:", tfHargaJual,
                            "Stok:", tfStok,
                            "Satuan:", tfSatuan
                    };

                    int option = JOptionPane.showConfirmDialog(this, input, "Edit Produk (ID: " + produkId + ")", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        try {
                            String nama = tfNama.getText().trim();
                            double hargaBeli = Double.parseDouble(tfHargaBeli.getText().trim());
                            double hargaJual = Double.parseDouble(tfHargaJual.getText().trim());
                            int stok = Integer.parseInt(tfStok.getText().trim());
                            String satuan = tfSatuan.getText().trim();
                            
                            if (nama.isEmpty() || satuan.isEmpty()) throw new IllegalArgumentException("Nama dan Satuan wajib diisi.");
                            if (hargaJual <= 0 || stok < 0) throw new IllegalArgumentException("Harga Jual harus positif dan Stok tidak boleh negatif.");

                            String sqlUpdate = "UPDATE produk SET nama_produk = ?, harga_beli = ?, harga_jual = ?, stok = ?, satuan = ? WHERE produk_id = ?";
                            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                                psUpdate.setString(1, nama);
                                psUpdate.setDouble(2, hargaBeli);
                                psUpdate.setDouble(3, hargaJual);
                                psUpdate.setInt(4, stok);
                                psUpdate.setString(5, satuan);
                                psUpdate.setInt(6, Integer.parseInt(produkId));
                                int updated = psUpdate.executeUpdate();
                                if (updated > 0) {
                                    JOptionPane.showMessageDialog(this, "Produk berhasil diperbarui!");
                                } else {
                                    JOptionPane.showMessageDialog(this, "Gagal memperbarui produk (tidak ditemukan).", "Info", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                            loadTableData();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Input harga/stok tidak valid", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(this, "Input tidak valid: " + ex.getMessage(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Produk tidak ditemukan.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusBarang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin dihapus");
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        String nama = model.getValueAt(row, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus produk '" + nama + "' (ID: " + id + ") ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String sqlDelete = "DELETE FROM produk WHERE produk_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlDelete)) {

            ps.setInt(1, Integer.parseInt(id));
            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!");
            } else {
                JOptionPane.showMessageDialog(this, "Produk tidak ditemukan (gagal hapus).", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            loadTableData();
        } catch (SQLException ex) {
            // Error code 1451 adalah FOREIGN KEY constraint violation
            if (ex.getErrorCode() == 1451) { 
                 JOptionPane.showMessageDialog(this, "Gagal menghapus produk: Produk ini sudah tercatat dalam transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Gagal menghapus produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
