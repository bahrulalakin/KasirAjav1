
package kasirapp.views;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.util.logging.Logger;

public final class MainFrame extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(MainFrame.class.getName());
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public MainFrame() {
        
        setTitle("Sistem Kasir Sederhana");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Tambahkan halaman
        mainPanel.add(new LoginPage(this), "Login");
        mainPanel.add(new AdminPage(this), "Admin");
        mainPanel.add(new KasirPage(this), "Kasir"); // Akan ditimpa saat login Kasir sukses
        mainPanel.add(new KelolaBarangPage(this), "KelolaBarang");
        mainPanel.add(new KelolaAkunPage(this), "KelolaAkun");
        mainPanel.add(new LaporanPage(this), "Laporan");

        add(mainPanel);
        showPage("Login");
        
        setVisible(true);
    }

    public void showPage(String name) {
        cardLayout.show(mainPanel, name);
    }
    
    // Method baru untuk menampilkan KasirPage dengan ID pengguna
    public void showKasirPage(int userId) {
        // Membuat instance KasirPage baru dengan ID pengguna yang valid
        KasirPage newKasirPage = new KasirPage(this, userId); 
        
        // Menambahkan page baru dengan nama yang sama ("Kasir"). CardLayout akan menimpa page lama.
        mainPanel.add(newKasirPage, "Kasir"); 
        
        cardLayout.show(mainPanel, "Kasir");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
    // ... (initComponents dan method kosong yang dihasilkan oleh NetBeans) ...



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    void setAdmin(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}