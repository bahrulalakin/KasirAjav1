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
import kasirapp.views.MainFrame;

/**
 *
 * @author REFI
 */
public class KelolaBarangPage extends javax.swing.JPanel {
    
    private final JTable table;
    private final DefaultTableModel model;
    private final java.util.List<Barang> daftarBarang;
    private final JPanel contentPanel;
    
    public KelolaBarangPage(MainFrame frame) {

        setLayout(new BorderLayout());
        setBackground(new Color(0, 40, 85)); // Warna utama biru tua elegan

        // Panel tengah (responsive center)
        contentPanel = new JPanel(null);
        contentPanel.setOpaque(false); // transparan agar warna latar belakang utama tetap terlihat
        add(contentPanel, BorderLayout.CENTER);

        JLabel title = new JLabel("Kelola Barang", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 20, 800, 40);
        contentPanel.add(title);

        // Data dummy
        daftarBarang = new ArrayList<>();
        daftarBarang.add(new Barang("B001", "Kopi Hitam", 10000, 20));
        daftarBarang.add(new Barang("B002", "Teh Manis", 8000, 25));
        daftarBarang.add(new Barang("B003", "Roti Bakar", 15000, 15));

        // Tabel
        String[] kolom = {"Kode", "Nama Barang", "Harga", "Stok"};
        model = new DefaultTableModel(kolom, 0);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);

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
        btnKembali.setBackground(new Color(100, 100, 100)); // abu-abu untuk kontras tombol kembali

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
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0x003F7F)); // biru lebih terang
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(100, 100, 100));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(100, 100, 100));
            }
        });
    }

    private void loadTableData() {
        model.setRowCount(0);
        for (Barang b : daftarBarang) {
            model.addRow(new Object[]{b.kode, b.nama, b.harga, b.stok});
        }
    }

    private void tambahBarang() {
    JTextField tfNama = new JTextField();
    JTextField tfHargaBeli = new JTextField();
    JTextField tfHargaJual = new JTextField();
    JTextField tfStok = new JTextField();
    JTextField tfSatuan = new JTextField();

        Object[] input = {
        "Nama Produk:", tfNama,
        "Harga Beli:", tfHargaBeli,
        "Harga Jual:", tfHargaJual,
        "Stok:", tfStok,
        "Satuan:", tfSatuan
    };
        int option = JOptionPane.showConfirmDialog(this, input, "Tambah Barang", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
        try {
            String nama = tfNama.getText();
            double hargaBeli = Double.parseDouble(tfHargaBeli.getText());
            double hargaJual = Double.parseDouble(tfHargaJual.getText());
            int stok = Integer.parseInt(tfStok.getText());
            String satuan = tfSatuan.getText();

            String url = "jdbc:mysql://localhost:3306/db_kasir";
            String user = "root"; // ganti sesuai user MySQL kamu
            String password = ""; // isi kalau MySQL kamu pakai password

            Connection conn = DriverManager.getConnection(url, user, password);

            String sql = "INSERT INTO produk (nama_produk, harga_beli, harga_jual, stok, satuan) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setDouble(2, hargaBeli);
            ps.setDouble(3, hargaJual);
            ps.setInt(4, stok);
            ps.setString(5, satuan);

            ps.executeUpdate();
            ps.close();
            conn.close();

            JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!");
            loadTableData(); // panggil fungsi untuk refresh tabel
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input harga/stok harus berupa angka!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan ke database: " + ex.getMessage());
        }
    }
    }

    private void editBarang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang ingin diedit");
            return;
        }

        Barang b = daftarBarang.get(row);
        JTextField tfNama = new JTextField(b.nama);
        JTextField tfHarga = new JTextField(String.valueOf(b.harga));
        JTextField tfStok = new JTextField(String.valueOf(b.stok));

        Object[] input = {
                "Nama:", tfNama,
                "Harga:", tfHarga,
                "Stok:", tfStok
        };

        int option = JOptionPane.showConfirmDialog(this, input, "Edit Barang", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                b.nama = tfNama.getText();
                b.harga = Double.parseDouble(tfHarga.getText());
                b.stok = Integer.parseInt(tfStok.getText());
                loadTableData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input harga/stok tidak valid");
            }
        }
    }

    private void hapusBarang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang ingin dihapus");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus barang ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            daftarBarang.remove(row);
            loadTableData();
        }
    }

    // Inner class Barang (sementara)
    class Barang {
        String kode, nama;
        double harga;
        int stok;

        Barang(String kode, String nama, double harga, int stok) {
            this.kode = kode;
            this.nama = nama;
            this.harga = harga;
            this.stok = stok;
        }
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
