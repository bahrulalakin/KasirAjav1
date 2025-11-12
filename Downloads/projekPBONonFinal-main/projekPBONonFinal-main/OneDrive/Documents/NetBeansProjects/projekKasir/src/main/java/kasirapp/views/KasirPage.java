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
 * @author Refi
 */
public class KasirPage extends javax.swing.JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtNamaBarang, txtHarga, txtJumlah;
    private JLabel lblTotal;
    private int totalHarga = 0;

    public KasirPage(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 40, 85)); // Warna biru muda lembut

        // ======= HEADER =======
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 85, 170)); // biru utama
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Transaksi Penjualan Kasir", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout, new Color(220, 53, 69));
        btnLogout.addActionListener(e -> frame.showPage("Login"));
        headerPanel.add(btnLogout, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ======= ISI KONTEN UTAMA =======
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // PANEL INPUT BARANG
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBackground(Color.WHITE);
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Barang"));
        GridBagConstraints inputGbc = new GridBagConstraints();
        inputGbc.insets = new Insets(5, 10, 5, 10);
        inputGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNama = new JLabel("Nama Barang:");
        inputGbc.gridx = 0; inputGbc.gridy = 0;
        panelInput.add(lblNama, inputGbc);
        txtNamaBarang = new JTextField();
        inputGbc.gridx = 1; inputGbc.gridy = 0; inputGbc.weightx = 1;
        panelInput.add(txtNamaBarang, inputGbc);

        JLabel lblHarga = new JLabel("Harga:");
        inputGbc.gridx = 2; inputGbc.gridy = 0; inputGbc.weightx = 0;
        panelInput.add(lblHarga, inputGbc);
        txtHarga = new JTextField();
        inputGbc.gridx = 3; inputGbc.gridy = 0; inputGbc.weightx = 0.5;
        panelInput.add(txtHarga, inputGbc);

        JLabel lblJumlah = new JLabel("Jumlah:");
        inputGbc.gridx = 4; inputGbc.gridy = 0;
        panelInput.add(lblJumlah, inputGbc);
        txtJumlah = new JTextField();
        inputGbc.gridx = 5; inputGbc.gridy = 0;
        panelInput.add(txtJumlah, inputGbc);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTombol.setBackground(Color.WHITE);
        JButton btnTambah = new JButton("Tambah");
        styleButton(btnTambah, new Color(0, 85, 170));
        JButton btnEdit = new JButton("Edit");
        styleButton(btnEdit, new Color(0, 85, 170));
        JButton btnHapus = new JButton("Hapus");
        styleButton(btnHapus, new Color(220, 53, 69));
        panelTombol.add(btnTambah);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);

        inputGbc.gridx = 0; inputGbc.gridy = 1; inputGbc.gridwidth = 6;
        panelInput.add(panelTombol, inputGbc);

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(panelInput, gbc);

        // ======= TABEL TRANSAKSI =======
        String[] kolom = {"Nama Barang", "Harga", "Jumlah", "Subtotal"};
        model = new DefaultTableModel(kolom, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(table);
        gbc.gridy = 1; gbc.weighty = 3;
        mainPanel.add(scrollPane, gbc);

        // ======= PANEL TOTAL =======
        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setBackground(Color.WHITE);
        panelTotal.setBorder(BorderFactory.createTitledBorder("Total Pembayaran"));
        panelTotal.setPreferredSize(new Dimension(100, 100));

        lblTotal = new JLabel("Total: Rp 0", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(new Color(33, 37, 41));
        panelTotal.add(lblTotal, BorderLayout.EAST);

        JButton btnSelesai = new JButton("Simpan");
        styleButton(btnSelesai, new Color(0, 85, 170));
        panelTotal.add(btnSelesai, BorderLayout.WEST);

        gbc.gridy = 2; gbc.weighty = 0.5;
        mainPanel.add(panelTotal, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // ======= AKSI =======
        btnTambah.addActionListener(e -> tambahBarang());
        btnEdit.addActionListener(e -> editBarang());
        btnHapus.addActionListener(e -> hapusBarang());
        btnSelesai.addActionListener(e -> JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!"));
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(140, 35));
    }

    private void tambahBarang() {
        String nama = txtNamaBarang.getText();
        String hargaStr = txtHarga.getText();
        String jumlahStr = txtJumlah.getText();

        if (nama.isEmpty() || hargaStr.isEmpty() || jumlahStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua data!");
            return;
        }

        try {
            int harga = Integer.parseInt(hargaStr);
            int jumlah = Integer.parseInt(jumlahStr);
            int subtotal = harga * jumlah;
            model.addRow(new Object[]{nama, harga, jumlah, subtotal});
            totalHarga += subtotal;
            lblTotal.setText("Total: Rp " + totalHarga);
            clearInput();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga dan jumlah harus berupa angka!");
        }
    }

    private void editBarang() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit!");
            return;
        }

        try {
            String nama = txtNamaBarang.getText();
            int harga = Integer.parseInt(txtHarga.getText());
            int jumlah = Integer.parseInt(txtJumlah.getText());
            int subtotal = harga * jumlah;

            int oldSubtotal = (int) model.getValueAt(selectedRow, 3);
            totalHarga -= oldSubtotal;
            totalHarga += subtotal;

            model.setValueAt(nama, selectedRow, 0);
            model.setValueAt(harga, selectedRow, 1);
            model.setValueAt(jumlah, selectedRow, 2);
            model.setValueAt(subtotal, selectedRow, 3);

            lblTotal.setText("Total: Rp " + totalHarga);
            clearInput();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga dan jumlah harus berupa angka!");
        }
    }

    private void hapusBarang() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!");
            return;
        }

        int subtotal = (int) model.getValueAt(selectedRow, 3);
        totalHarga -= subtotal;
        model.removeRow(selectedRow);
        lblTotal.setText("Total: Rp " + totalHarga);
    }

    private void clearInput() {
        txtNamaBarang.setText("");
        txtHarga.setText("");
        txtJumlah.setText("");
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