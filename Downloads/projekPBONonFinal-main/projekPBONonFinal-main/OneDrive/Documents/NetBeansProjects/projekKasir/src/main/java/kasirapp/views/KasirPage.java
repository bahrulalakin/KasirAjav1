




/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
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

  
public class KasirPage extends javax.swing.JPanel {

    private DefaultTableModel model;
    private JTable table;
    // COMBO menggantikan txtNamaBarang
    private JComboBox<ProductItem> cbProduk;
    private JTextField txtHarga, txtJumlah;
    private JLabel lblTotal;
    private int totalHarga = 0;

    public KasirPage(MainFrame frame) {
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

        // ===== INPUT =====
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBackground(Color.WHITE);
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Barang"));
        GridBagConstraints in = new GridBagConstraints();
        in.insets = new Insets(5, 10, 5, 10);
        in.fill = GridBagConstraints.HORIZONTAL;

        // --- Produk (ComboBox) ---
        in.gridx = 0; in.gridy = 0;
        panelInput.add(new JLabel("Pilih Produk:"), in);

        cbProduk = new JComboBox<>();
        in.gridx = 1; in.weightx = 1;
        panelInput.add(cbProduk, in);

        // --- Harga ---
        in.gridx = 2; in.weightx = 0;
        panelInput.add(new JLabel("Harga:"), in);

        txtHarga = new JTextField();
        txtHarga.setEditable(false);
        in.gridx = 3; in.weightx = 0.3;
        panelInput.add(txtHarga, in);

        // --- Jumlah ---
        in.gridx = 4;
        panelInput.add(new JLabel("Jumlah:"), in);

        txtJumlah = new JTextField();
        in.gridx = 5; in.weightx = 0;
        panelInput.add(txtJumlah, in);

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
        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // mencegah edit langsung di tabel
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);

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

        // ===== inisialisasi data produk ke combo & event =====
        loadProductsToCombo();
        cbProduk.addActionListener(e -> onProdukSelected());
        btnTambah.addActionListener(e -> tambahBarang());
        btnEdit.addActionListener(e -> editBarang());
        btnHapus.addActionListener(e -> hapusBarang());
        btnSelesai.addActionListener(e -> simpanTransaksi());

        // klik baris tabel -> isi form untuk edit cepat
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r != -1) {
                    int produkId = (int) model.getValueAt(r, 0);
                    String nama = (String) model.getValueAt(r, 1);
                    int harga = (int) model.getValueAt(r, 2);
                    int jumlah = (int) model.getValueAt(r, 3);

                    // set combo ke produk yang sesuai
                    setComboToProductId(produkId);
                    txtHarga.setText(String.valueOf(harga));
                    txtJumlah.setText(String.valueOf(jumlah));
                }
            }
        });
    }

    // ============================================================
    // STYLE
    // ============================================================
    private void styleButton(JButton btn, Color c) {
        btn.setBackground(c);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(140, 35));
    }

    // ============================================================
    // PRODUCT LOADING & SELECT HANDLING
    // ============================================================
    private void loadProductsToCombo() {
        cbProduk.removeAllItems();
        cbProduk.addItem(null); // placeholder (index 0)

        String sql = "SELECT produk_id, nama_produk, harga_jual FROM produk ORDER BY nama_produk";
        try (Connection conn = TesKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("produk_id");
                String nama = rs.getString("nama_produk");
                int harga = (int) Math.round(rs.getDouble("harga_jual"));
                cbProduk.addItem(new ProductItem(id, nama, harga));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onProdukSelected() {
        ProductItem sel = (ProductItem) cbProduk.getSelectedItem();
        if (sel != null) {
            txtHarga.setText(String.valueOf(sel.harga));
            // fokus ke jumlah supaya kasir langsung mengetik jumlah
            txtJumlah.requestFocus();
        } else {
            txtHarga.setText("");
        }
    }

    // set combo selection by product id (dipakai saat edit cepat)
    private void setComboToProductId(int produkId) {
        for (int i = 0; i < cbProduk.getItemCount(); i++) {
            ProductItem it = cbProduk.getItemAt(i);
            if (it != null && it.id == produkId) {
                cbProduk.setSelectedIndex(i);
                return;
            }
        }
    }

    // ============================================================
    // TAMBAH
    // ============================================================
    private void tambahBarang() {
        ProductItem sel = (ProductItem) cbProduk.getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu!");
            return;
        }

        String jumlahStr = txtJumlah.getText().trim();
        if (jumlahStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah!");
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahStr);
            if (jumlah <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka positif!");
            return;
        }

        // cek stok saat ingin menambahkan
        try (Connection conn = TesKoneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT stok FROM produk WHERE produk_id = ?")) {

            ps.setInt(1, sel.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int stok = rs.getInt("stok");
                if (jumlah > stok) {
                    JOptionPane.showMessageDialog(this, "Stok tidak cukup! Sisa stok: " + stok);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Produk tidak ditemukan di database!");
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memeriksa stok: " + ex.getMessage());
            return;
        }

        int subtotal = sel.harga * jumlah;
        model.addRow(new Object[]{sel.id, sel.name, sel.harga, jumlah, subtotal});
        totalHarga += subtotal;
        lblTotal.setText("Total: Rp " + totalHarga);

        // clear jumlah (biarkan produk tetap terpilih agar lebih cepat tambah banyak item)
        txtJumlah.setText("");
        txtJumlah.requestFocus();
    }

    // ============================================================
    // EDIT
    // ============================================================
    private void editBarang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris untuk diedit!");
            return;
        }

        ProductItem sel = (ProductItem) cbProduk.getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Pilih produk!");
            return;
        }

        int harga = sel.harga;
        int jumlah;
        try {
            jumlah = Integer.parseInt(txtJumlah.getText().trim());
            if (jumlah <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah harus angka!");
            return;
        }

        int oldSubtotal = (int) model.getValueAt(row, 4);
        int newSubtotal = harga * jumlah;

        totalHarga = totalHarga - oldSubtotal + newSubtotal;

        model.setValueAt(sel.id, row, 0);
        model.setValueAt(sel.name, row, 1);
        model.setValueAt(harga, row, 2);
        model.setValueAt(jumlah, row, 3);
        model.setValueAt(newSubtotal, row, 4);

        lblTotal.setText("Total: Rp " + totalHarga);
        txtJumlah.setText("");
    }

    // ============================================================
    // HAPUS
    // ============================================================
    private void hapusBarang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris untuk dihapus!");
            return;
        }
        int subtotal = (int) model.getValueAt(row, 4);
        totalHarga -= subtotal;
        model.removeRow(row);
        lblTotal.setText("Total: Rp " + totalHarga);
    }

    // ============================================================
    // SIMPAN TRANSAKSI (INSERT transaksi + detail_transaksi + update stok)
    // ============================================================
    private void simpanTransaksi() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada barang!");
            return;
        }

        try (Connection conn = TesKoneksi.getConnection()) {
            conn.setAutoCommit(false);

            String sqlTrans = "INSERT INTO transaksi (total_harga, tanggal_transaksi) VALUES (?, NOW())";
            PreparedStatement psTrans = conn.prepareStatement(sqlTrans, Statement.RETURN_GENERATED_KEYS);
            psTrans.setInt(1, totalHarga);
            psTrans.executeUpdate();

            ResultSet gen = psTrans.getGeneratedKeys();
            gen.next();
            int transaksiId = gen.getInt(1);

            String sqlDetail = "INSERT INTO detail_transaksi (transaksi_id, produk_id, jumlah, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);

            String sqlStok = "UPDATE produk SET stok = stok - ? WHERE produk_id = ?";
            PreparedStatement psStok = conn.prepareStatement(sqlStok);

            for (int i = 0; i < model.getRowCount(); i++) {
                int produkId = (int) model.getValueAt(i, 0);
                int jumlah = (int) model.getValueAt(i, 3);
                int harga = (int) model.getValueAt(i, 2);
                int subtotal = (int) model.getValueAt(i, 4);

                psDetail.setInt(1, transaksiId);
                psDetail.setInt(2, produkId);
                psDetail.setInt(3, jumlah);
                psDetail.setInt(4, harga);
                psDetail.setInt(5, subtotal);
                psDetail.addBatch();

                psStok.setInt(1, jumlah);
                psStok.setInt(2, produkId);
                psStok.addBatch();
            }

            psDetail.executeBatch();
            psStok.executeBatch();

            conn.commit();

            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan! ID: " + transaksiId);

            // reset UI
            model.setRowCount(0);
            totalHarga = 0;
            lblTotal.setText("Total: Rp 0");
            cbProduk.setSelectedIndex(0);
            txtHarga.setText("");
            txtJumlah.setText("");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + ex.getMessage());
        }
    }

    private void clearInput() {
        txtNamaBarang.setText("");
        txtHarga.setText("");
        txtJumlah.setText("");
    }
    
    

    // inner helper class untuk menyimpan produk di combo
    private static class ProductItem {
        int id;
        String name;
        int harga;

        ProductItem(int id, String name, int harga) {
            this.id = id;
            this.name = name;
            this.harga = harga;
        }

        @Override
        public String toString() {
            return name;
        }
    }
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