package kasirapp.views;

import kasirapp.TesKoneksi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class LaporanPage extends javax.swing.JPanel {
    
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotal;
    private JComboBox<String> cmbJenis;
    private JTextField txtTanggal;
    private JPanel contentPanel;

    public LaporanPage(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(0x002855)); 

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

        JLabel lblTgl = new JLabel("Tanggal (YYYY-MM-DD):");
        lblTgl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTgl.setForeground(Color.WHITE);
        lblTgl.setBounds(420, 80, 150, 25);
        contentPanel.add(lblTgl);

        txtTanggal = new JTextField(LocalDate.now().toString());
        txtTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTanggal.setBounds(570, 80, 100, 28);
        contentPanel.add(txtTanggal);

        JButton btnTampilkan = new JButton("Tampilkan");
        styleButton(btnTampilkan);
        btnTampilkan.setBounds(680, 80, 100, 35);
        contentPanel.add(btnTampilkan);

        // ====== Tabel Laporan ======
        String[] kolom = {"ID Transaksi", "Tanggal & Waktu", "Kasir", "Total"};
        model = new DefaultTableModel(kolom, 0);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(120, 130, 660, 200);
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
        btnKembali.setBounds(680, 350, 100, 35);
        contentPanel.add(btnKembali);

        // ====== Event Tombol ======
        btnTampilkan.addActionListener(e -> tampilkanLaporan());
        btnKembali.addActionListener(e -> frame.showPage("Admin"));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0x003F7F));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0x0055AA));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0x003F7F));
            }
        });
    }
    
    private Connection getConnection() throws SQLException {
        return TesKoneksi.getConnection(); // Gunakan TesKoneksi
    }

    private void tampilkanLaporan() {
        model.setRowCount(0);
        double total = 0;

        String jenis = cmbJenis.getSelectedItem().toString();
        String tanggalInput = txtTanggal.getText().trim();
        
        if (tanggalInput.isEmpty() || !tanggalInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Format tanggal harus YYYY-MM-DD", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = getConnection()) { 
            String sql = "";

            // Query JOIN Transaksi (t) dengan Users (u)
            String baseSql = "SELECT t.id_transaksi, t.tanggal_transaksi, u.username AS nama_kasir, t.total_harga "
                           + "FROM transaksi t JOIN users u ON t.id_user_kasir = u.id_user ";

            if (jenis.equals("Harian")) {
                sql = baseSql + "WHERE DATE(t.tanggal_transaksi) = ?"; 
            } else if (jenis.equals("Mingguan")) {
                // Mencari data dalam rentang 7 hari (dari TGL_INPUT mundur 6 hari)
                sql = baseSql + "WHERE t.tanggal_transaksi BETWEEN DATE_SUB(?, INTERVAL 6 DAY) AND ?"; 
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (jenis.equals("Harian")) {
                    ps.setString(1, tanggalInput);
                } else {
                    ps.setString(1, tanggalInput); // Tanggal awal (6 hari sebelum tanggal input)
                    ps.setString(2, tanggalInput); // Tanggal akhir
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String id = rs.getString("id_transaksi");
                        String tgl = rs.getString("tanggal_transaksi");
                        String kasir = rs.getString("nama_kasir"); 
                        double totalTransaksi = rs.getDouble("total_harga"); 
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
