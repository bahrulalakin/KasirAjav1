
package kasirapp.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import kasirapp.TesKoneksi;

public class KasirPage extends javax.swing.JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JTextField txtNamaBarang, txtHarga, txtJumlah;
    private JLabel lblTotal;
    private int totalHarga = 0;
    private MainFrame frame;
    private int loggedInUserId; // Variabel baru untuk Kasir ID

    private JPopupMenu suggestionMenu = new JPopupMenu();
    
    // Konstruktor 1 (Dipanggil saat MainFrame init, ID default 0)
    public KasirPage(MainFrame frame) {
        this(frame, 0); 
    }

    // Konstruktor 2 (KODE KRITIS: Menerima ID User)
    public KasirPage(MainFrame frame, int userId) {
        this.frame = frame;
        this.loggedInUserId = userId; // Simpan ID Kasir
        
        setLayout(new BorderLayout());
        setBackground(new Color(0, 40, 85));

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 85, 170));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Transaksi Penjualan Kasir");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout, new Color(220, 53, 69));
        btnLogout.addActionListener(e -> frame.showPage("Login"));
        headerPanel.add(btnLogout, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ===== MAIN =====
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // ===== INPUT (Kode UI) =====
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBackground(Color.WHITE);
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Barang"));
        GridBagConstraints in = new GridBagConstraints();
        in.insets = new Insets(5, 10, 5, 10);
        
        // ... (Label dan Field: txtNamaBarang, txtHarga, txtJumlah) ...
        in.gridx = 0; in.gridy = 0; panelInput.add(new JLabel("Nama Barang:"), in);
        txtNamaBarang = new JTextField(); in.gridx = 1; in.weightx = 1; in.fill = GridBagConstraints.HORIZONTAL; panelInput.add(txtNamaBarang, in);
        in.gridx = 2; in.weightx = 0; panelInput.add(new JLabel("Harga:"), in);
        txtHarga = new JTextField(); txtHarga.setEditable(false); in.gridx = 3; in.weightx = 0.3; panelInput.add(txtHarga, in);
        in.gridx = 4; panelInput.add(new JLabel("Jumlah:"), in);
        txtJumlah = new JTextField(); in.gridx = 5; panelInput.add(txtJumlah, in);

        // --- Tombol ---
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTombol.setBackground(Color.WHITE);

        JButton btnTambah = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnHapus = new JButton("Hapus");

        styleButton(btnTambah, new Color(0, 85, 170));
        styleButton(btnEdit, new Color(0, 85, 170));
        styleButton(btnHapus, new Color(220, 53, 69));

        panelTombol.add(btnTambah);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);

        in.gridx = 0; in.gridy = 1; in.gridwidth = 6;
        panelInput.add(panelTombol, in);

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(panelInput, gbc);

        // ===== TABEL =====
        String[] kolom = {"Produk ID", "Nama Barang", "Harga", "Jumlah", "Subtotal"};
        model = new DefaultTableModel(kolom, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setMinWidth(0); // Sembunyikan ID
        table.getColumnModel().getColumn(0).setMaxWidth(0); // Sembunyikan ID

        gbc.gridy = 1; gbc.weighty = 3;
        mainPanel.add(new JScrollPane(table), gbc);

        // ===== TOTAL =====
        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setBackground(Color.WHITE);
        panelTotal.setBorder(BorderFactory.createTitledBorder("Total Pembayaran"));

        lblTotal = new JLabel("Total: Rp 0", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panelTotal.add(lblTotal, BorderLayout.EAST);

        JButton btnSelesai = new JButton("Simpan Transaksi");
        styleButton(btnSelesai, new Color(0, 85, 170));
        panelTotal.add(btnSelesai, BorderLayout.WEST);

        gbc.gridy = 2; gbc.weighty = 0.4;
        mainPanel.add(panelTotal, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // EVENTS
        setupSuggestionFeature();
        btnTambah.addActionListener(e -> tambahBarang());
        btnEdit.addActionListener(e -> editBarang());
        btnHapus.addActionListener(e -> hapusBarang());
        btnSelesai.addActionListener(e -> simpanTransaksi());
    }

    private void styleButton(JButton btn, Color c) {
        btn.setBackground(c);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(140, 35));
    }
    
    private Connection getConnection() throws SQLException {
        return TesKoneksi.getConnection();
    }

    private void setupSuggestionFeature() {
        suggestionMenu.setFocusable(false);

        txtNamaBarang.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String keyword = txtNamaBarang.getText().trim();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    pilihProduk(keyword);
                    suggestionMenu.setVisible(false);
                    return;
                }
                showSuggestions(keyword);
            }
        });
    }

    private void showSuggestions(String keyword) {
        suggestionMenu.removeAll();
        if (keyword.isEmpty()) {
            suggestionMenu.setVisible(false);
            return;
        }

        ArrayList<String> hasil = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT nama_produk FROM produk WHERE nama_produk LIKE ? LIMIT 5";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) hasil.add(rs.getString("nama_produk"));
        } catch (Exception e) { e.printStackTrace(); }

        if (hasil.isEmpty()) return;

        for (String nama : hasil) {
            JMenuItem item = new JMenuItem(nama);
            item.addActionListener(e -> {
                pilihProduk(nama);
                suggestionMenu.setVisible(false);
            });
            suggestionMenu.add(item);
        }
        suggestionMenu.show(txtNamaBarang, 0, txtNamaBarang.getHeight());
    }

    private void pilihProduk(String nama) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT produk_id, harga_jual FROM produk WHERE nama_produk = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nama);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNamaBarang.setText(nama);
                txtHarga.setText(String.valueOf(rs.getInt("harga_jual")));
                txtJumlah.requestFocus();
            }

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void tambahBarang() {
        String nama = txtNamaBarang.getText();
        String hargaStr = txtHarga.getText();
        String jumlahStr = txtJumlah.getText();

        if (nama.isEmpty() || hargaStr.isEmpty() || jumlahStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harap lengkapi data!");
            return;
        }
        
        int jumlahInt, hargaInt;
        try {
            jumlahInt = Integer.parseInt(jumlahStr);
            hargaInt = Integer.parseInt(hargaStr);
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "Jumlah atau Harga harus berupa angka valid!", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        try (Connection conn = getConnection()) {
            String sql = "SELECT produk_id, stok FROM produk WHERE nama_produk = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nama);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Produk tidak ditemukan!");
                return;
            }

            int produkId = rs.getInt("produk_id");
            int stokDB = rs.getInt("stok");

            if (jumlahInt > stokDB) {
                JOptionPane.showMessageDialog(this, "Stok tidak cukup! Stok tersedia: " + stokDB);
                return;
            }

            int subtotal = hargaInt * jumlahInt;
            model.addRow(new Object[]{produkId, nama, hargaInt, jumlahInt, subtotal});

            totalHarga += subtotal;
            lblTotal.setText("Total: Rp " + totalHarga);

            clearInput();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menambah barang: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editBarang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit!");
            return;
        }

        try {
            int oldSubtotal = (int) model.getValueAt(row, 4);

            int harga = Integer.parseInt(txtHarga.getText());
            int jumlah = Integer.parseInt(txtJumlah.getText());
            int subtotal = harga * jumlah;

            // Update Total Harga
            totalHarga = totalHarga - oldSubtotal + subtotal;

            // Update baris tabel
            model.setValueAt(txtNamaBarang.getText(), row, 1);
            model.setValueAt(harga, row, 2);
            model.setValueAt(jumlah, row, 3);
            model.setValueAt(subtotal, row, 4);

            lblTotal.setText("Total: Rp " + totalHarga);
            clearInput();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data tidak valid untuk diedit!");
        }
    }

    private void hapusBarang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!");
            return;
        }

        int subtotal = (int) model.getValueAt(row, 4);
        totalHarga -= subtotal;

        model.removeRow(row);
        lblTotal.setText("Total: Rp " + totalHarga);
        clearInput();
    }

    private void simpanTransaksi() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada barang dalam transaksi!");
            return;
        }
        
        if (loggedInUserId == 0) {
            JOptionPane.showMessageDialog(this, "ID Kasir tidak ditemukan. Mohon Login ulang.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            // 1) INSERT TRANSAKSI (Gunakan ID Kasir)
            String sqlTrans = "INSERT INTO transaksi (total_harga, tanggal_transaksi, id_user_kasir) VALUES (?, NOW(), ?)";
            PreparedStatement ps1 = conn.prepareStatement(sqlTrans, Statement.RETURN_GENERATED_KEYS);
            ps1.setInt(1, totalHarga);
            ps1.setInt(2, loggedInUserId); // Gunakan ID user yang login
            ps1.executeUpdate();

            // Ambil ID Transaksi
            int transaksiId = 0;
            try (ResultSet keys = ps1.getGeneratedKeys()) {
                if (keys.next()) {
                    transaksiId = keys.getInt(1);
                }
            }
            ps1.close();

            // 2) INSERT DETAIL
            String sqlDetail = "INSERT INTO detail_transaksi (transaksi_id, produk_id, nama_produk, jumlah, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps2 = conn.prepareStatement(sqlDetail);

            // 3) UPDATE STOK
            String sqlStok = "UPDATE produk SET stok = stok - ? WHERE produk_id = ?";
            PreparedStatement ps3 = conn.prepareStatement(sqlStok);

            for (int i = 0; i < model.getRowCount(); i++) {
                int produkId = (int) model.getValueAt(i, 0);
                String namaProduk = model.getValueAt(i, 1).toString();
                int jumlah = (int) model.getValueAt(i, 3);
                int subtotal = (int) model.getValueAt(i, 4);

                ps2.setInt(1, transaksiId);
                ps2.setInt(2, produkId);
                ps2.setString(3, namaProduk);
                ps2.setInt(4, jumlah);
                ps2.setInt(5, subtotal);
                ps2.addBatch();

                ps3.setInt(1, jumlah);
                ps3.setInt(2, produkId);
                ps3.addBatch();
            }

            ps2.executeBatch();
            ps3.executeBatch();
            ps2.close();
            ps3.close();

            conn.commit(); // Commit transaksi

            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan! ID: " + transaksiId);

            model.setRowCount(0);
            totalHarga = 0;
            lblTotal.setText("Total: Rp 0");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi! " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearInput() {
        txtNamaBarang.setText("");
        txtHarga.setText("");
        txtJumlah.setText("");
        txtNamaBarang.requestFocus();
    }


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