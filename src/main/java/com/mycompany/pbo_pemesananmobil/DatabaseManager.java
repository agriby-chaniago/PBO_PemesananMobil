package com.mycompany.pbo_pemesananmobil;

import java.sql.*;
import javax.swing.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String URL = "jdbc:mysql://localhost:3306/db_sewa_mobil"; // URL koneksi database
    private static final String USER = "root"; // Username
    private static final String PASSWORD = "root"; // Password

    // Private constructor to prevent instantiation
    private DatabaseManager() {}

    // Method to get the singleton instance
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Koneksi database
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method untuk mengambil data mobil
    public ResultSet fetchMobilData() {
        String query = "SELECT * FROM mobil ORDER BY id DESC"; // Query untuk mengambil data mobil
        return executeQuery(query);
    }

    // Method untuk mengambil data pelanggan
    public ResultSet fetchPelangganData() {
        String query = "SELECT * FROM pelanggan ORDER BY id DESC"; // Query untuk mengambil data pelanggan
        return executeQuery(query);
    }

    // Method untuk mengambil data sopir
    public ResultSet fetchSopirData() {
        String query = "SELECT * FROM sopir ORDER BY id DESC"; // Query untuk mengambil data sopir
        return executeQuery(query);
    }

    // Method untuk mengeksekusi query dan mengembalikan ResultSet
    public ResultSet executeQuery(String query) {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace(); // Log error jika terjadi kesalahan
            return null; // Return null jika terjadi kesalahan
        }
    }

    // Method untuk update data
    public int updateData(String query, Object[] params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean canDeleteMobil(int mobilId) {
        String checkSql = "SELECT COUNT(*) FROM pemesan_mobil WHERE id_mobil = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {

            ps.setInt(1, mobilId);  // Set ID mobil yang akan dicek
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Jika ada pemesanan yang terkait dengan mobil tersebut
                if (rs.getInt(1) > 0) {
                    // Ada pemesanan, berarti mobil tidak bisa dihapus
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // Jika tidak ada pemesanan terkait, mobil bisa dihapus
    }

    // Hapus mobil
    public void deleteMobil(int mobilId) {
        // Periksa apakah mobil bisa dihapus
        if (canDeleteMobil(mobilId)) {
            String deleteSql = "DELETE FROM mobil WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, mobilId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Mobil berhasil dihapus.");
                    JOptionPane.showMessageDialog(null, "Mobil berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    System.out.println("Gagal menghapus mobil.");
                    JOptionPane.showMessageDialog(null, "Gagal menghapus mobil.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menghapus data mobil.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Jika mobil tidak dapat dihapus karena ada pemesanan terkait
            System.out.println("Mobil tidak dapat dihapus karena masih ada pemesanan yang terkait.");
            JOptionPane.showMessageDialog(null, "Mobil tidak dapat dihapus karena masih ada pemesanan yang terkait.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hapus data pelanggan
    public void deletePelanggan(int pelangganId) {
        // Cek apakah pelanggan dapat dihapus
        if (canDeletePelanggan(pelangganId)) {
            String deleteSql = "DELETE FROM pelanggan WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, pelangganId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    // Jika penghapusan berhasil, refresh data
                    refreshData(); // Pastikan ini dipanggil setelah penghapusan berhasil
                } else {
                    JOptionPane.showMessageDialog(null, "Pelanggan tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menghapus data pelanggan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Jika pelanggan tidak dapat dihapus karena ada pemesanan terkait
            JOptionPane.showMessageDialog(null, "Pelanggan tidak dapat dihapus karena masih ada pemesanan yang terkait.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metode untuk menyegarkan data pada panel
    public void refreshData() {
        fetchPelangganData();  // Pastikan data diperbarui setelah penghapusan
    }

    // Cek apakah pelanggan dapat dihapus
    public boolean canDeletePelanggan(int pelangganId) {
        String checkSql = "SELECT COUNT(*) FROM pemesan_mobil WHERE id_pelanggan = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, pelangganId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Jika ada pemesanan terkait, return false
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean canDeleteSopir(int sopirId) {
        String checkSql = "SELECT COUNT(*) FROM pemesan_mobil WHERE id_sopir = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {

            ps.setInt(1, sopirId);  // Set ID sopir yang akan dicek
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Jumlah pemesanan yang terkait dengan sopir ID " + sopirId + ": " + count);
                if (count > 0) {
                    // Jika ada pemesanan, berarti sopir tidak bisa dihapus
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // Jika tidak ada pemesanan terkait, sopir bisa dihapus
    }

    public boolean deleteSopir(int sopirId) {
        // Periksa apakah sopir bisa dihapus
        if (canDeleteSopir(sopirId)) {
            String deleteSql = "DELETE FROM sopir WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, sopirId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Sopir berhasil dihapus.");
                    return true; // Berhasil dihapus
                } else {
                    System.out.println("Gagal menghapus sopir.");
                    return false; // Gagal dihapus
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false; // Gagal karena kesalahan SQL
            }
        } else {
            // Jika sopir tidak dapat dihapus karena ada pemesanan terkait
            System.out.println("Sopir tidak dapat dihapus karena masih ada pemesanan yang terkait.");
            return false; // Tidak dapat dihapus
        }
    }

}
