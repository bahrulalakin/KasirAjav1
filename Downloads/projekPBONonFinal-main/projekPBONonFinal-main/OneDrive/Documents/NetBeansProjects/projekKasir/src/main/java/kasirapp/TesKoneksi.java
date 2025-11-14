package kasirapp;

import java.sql.Connection;
import java.sql.DriverManager;

public class TesKoneksi {
    public static void main(String[] args) {
        try {
            // Ganti nama database, user, dan password sesuai konfigurasi XAMPP kamu
            String url = "jdbc:mysql://localhost:3306/db_kasir";
            String user = "root";
            String password = "";

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Koneksi ke database berhasil!");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Koneksi gagal: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
