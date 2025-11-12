package kasirapp.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        // Panel konten utama di tengah (responsif)
        contentPanel = new JPanel(null);
        contentPanel.setOpaque(false); // biar background biru tetap terlihat
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
        btnKembali.setBackground(new Color(100, 100, 100)); // abu-abu elegan
        btnKembali.setBounds(630, 350, 100, 35);
        contentPanel.add(btnKembali);

        // ====== Event Dummy ======
        btnTampilkan.addActionListener(e -> tampilkanDummy());
        btnKembali.addActionListener(e -> frame.showPage("Admin"));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0x003F7F)); // biru lebih terang
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        // Hover effect lembut
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0x0055AA));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0x003F7F));
            }
        });
    }

    private void tampilkanDummy() {
        model.setRowCount(0); // hapus data lama
        String jenis = cmbJenis.getSelectedItem().toString();
        double total = 0;

        if (jenis.equals("Harian")) {
            model.addRow(new Object[]{"T001", LocalDate.now(), "Kasir1", 50000});
            model.addRow(new Object[]{"T002", LocalDate.now(), "Kasir2", 80000});
            total = 130000;
        } else {
            model.addRow(new Object[]{"T001", LocalDate.now().minusDays(5), "Kasir1", 50000});
            model.addRow(new Object[]{"T002", LocalDate.now().minusDays(4), "Kasir2", 80000});
            model.addRow(new Object[]{"T003", LocalDate.now().minusDays(3), "Kasir1", 100000});
            total = 230000;
        }

        lblTotal.setText("Total Penjualan: Rp " + total);
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
