package kasirapp.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 *
 * @author REFI
 */
public class LaporanPage extends javax.swing.JPanel {
    
   private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotal;
    private JComboBox<String> cmbJenis;
    private JTextField txtTanggal;
    private JPanel contentPanel;

    public LaporanPage(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(0x002855)); // Warna utama biru tua elegan

        contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);
        add(contentPanel, BorderLayout.CENTER);

        JLabel lblTitle = new JLabel("Laporan Penjualan", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 20, 800, 40);
        contentPanel.add(lblTitle);

        JLabel lblJenis = new JLabel("Jenis Laporan:");
        lblJenis.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblJenis.setForeground(Color.WHITE);
        lblJenis.setBounds(150, 80, 120, 25);
        contentPanel.add(lblJenis);

        cmbJenis = new JComboBox<>(new String[]{"Harian", "Mingguan"});
        cmbJenis.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbJenis.setBounds(270, 80, 130, 28);
        contentPanel.add(cmbJenis);

        JLabel lblTgl = new JLabel("Tanggal:");
        lblTgl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTgl.setForeground(Color.WHITE);
        lblTgl.setBounds(420, 80, 70, 25);
        contentPanel.add(lblTgl);

        txtTanggal = new JTextField(LocalDate.now().toString());
        txtTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTanggal.setBounds(490, 80, 100, 28);
        contentPanel.add(txtTanggal);

        JButton btnTampilkan = new JButton("Tampilkan");
        styleButton(btnTampilkan);
        btnTampilkan.setBounds(610, 80, 120, 35);
        contentPanel.add(btnTampilkan);

        // ====== Tabel Laporan ======
        String[] kolom = {"ID Transaksi", "Tanggal", "Kasir", "Total"};
        model = new DefaultTableModel(kolom, 0);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(120, 130, 610, 200);
        contentPanel.add(scrollPane);

        // ====== Label Total ======
        lblTotal = new JLabel("Total Penjualan: Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setBounds(120, 350, 300, 25);
        contentPanel.add(lblTotal);

        JButton btnKembali = new JButton("Kembali");
        styleButton(btnKembali);
        btnKembali.setBackground(new Color(100, 100, 100));
        btnKembali.setBounds(630, 350, 100, 35);
        contentPanel.add(btnKembali);

        // ====== Event Tombol ======
        btnTampilkan.addActionListener(e -> tampilkanLaporan());
        btnKembali.addActionListener(e -> frame.showPage("Admin"));
    }

    // ================= STYLE BUTTON =================
    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0x003F7F));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0x0055AA));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0x003F7F));
            }
        });
    }

    // ================= METHOD DATABASE =================
    private void tampilkanLaporan() {
        model.setRowCount(0);
        double total = 0;

        String jenis = cmbJenis.getSelectedItem().toString();
        String tanggalInput = txtTanggal.getText().trim();

        String url = "jdbc:mysql://localhost:3306/db_kasir";
        String dbUser = "root";
        String dbPass = "";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String sql = "";

            if (jenis.equals("Harian")) {
                sql = "SELECT id_transaksi, tanggal, kasir, total FROM transaksi WHERE DATE(tanggal) = ?";
            } else if (jenis.equals("Mingguan")) {
                sql = "SELECT id_transaksi, tanggal, kasir, total FROM transaksi WHERE tanggal BETWEEN DATE_SUB(?, INTERVAL 7 DAY) AND ?";
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (jenis.equals("Harian")) {
                    ps.setString(1, tanggalInput);
                } else {
                    ps.setString(1, tanggalInput);
                    ps.setString(2, tanggalInput);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String id = rs.getString("id_transaksi");
                        String tgl = rs.getString("tanggal");
                        String kasir = rs.getString("kasir");
                        double totalTransaksi = rs.getDouble("total");
                        total += totalTransaksi;
                        model.addRow(new Object[]{id, tgl, kasir, totalTransaksi});
                    }
                }
            }

            lblTotal.setText("Total Penjualan: Rp " + String.format("%,.0f", total));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data: " + ex.getMessage(),
                    "Error Database", JOptionPane.ERROR_MESSAGE);
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
