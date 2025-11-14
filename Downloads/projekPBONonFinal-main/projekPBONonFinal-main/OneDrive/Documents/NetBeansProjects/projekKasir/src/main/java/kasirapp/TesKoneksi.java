package kasirapp; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class TesKoneksi {
    
    // **Pastikan konfigurasi ini sesuai dengan database Anda**
    private static final String URL = "jdbc:mysql://localhost:3306/db_kasir";
    private static final String USER = "root";
    private static final String PASS = "";
    
    public static Connection getConnection() throws SQLException {
        try {
            // Memastikan driver dimuat (Perlu Library JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver MySQL tidak ditemukan! Pastikan library JDBC sudah ditambahkan ke proyek.", "Koneksi Error", JOptionPane.ERROR_MESSAGE);
            throw new SQLException("Driver not found: " + e.getMessage());
        }
    }
}