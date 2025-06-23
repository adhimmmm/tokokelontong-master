package com.example.tokokelontong.dao;

import com.example.tokokelontong.model.Supplier;
import com.example.tokokelontong.database.DBUtil;
import java.sql.*;
import java.util.*;

public class SupplierDAO {

    public static List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        // Mengubah query untuk LEFT JOIN dengan tabel BARANG
        // Mengambil salah satu NAMA_BARANG dari BARANG yang disuplai supplier ini (misal yang pertama secara alfabetis)
        String sql = """
            SELECT
                s.ID_SUPLIER,
                s.NAMA_SUPLIER,
                s.ALAMAT,
                s.NO_TELEPON,
                (SELECT MIN(b.NAMA_BARANG) FROM BARANG b WHERE b.SUPLIER_ID_SUPLIER = s.ID_SUPLIER) AS NAMA_BARANG_DISPLAY
            FROM
                SUPLIER s
            ORDER BY s.ID_SUPLIER
        """;
        // Pilihan lain jika tidak ingin subquery di SELECT:
        /*
        String sql = """
            SELECT
                s.ID_SUPLIER,
                s.NAMA_SUPLIER,
                s.ALAMAT,
                s.NO_TELEPON,
                MIN(b.NAMA_BARANG) AS NAMA_BARANG_DISPLAY -- Menggunakan MIN dengan GROUP BY
            FROM
                SUPLIER s
            LEFT JOIN
                BARANG b ON b.SUPLIER_ID_SUPLIER = s.ID_SUPLIER
            GROUP BY
                s.ID_SUPLIER, s.NAMA_SUPLIER, s.ALAMAT, s.NO_TELEPON -- Harus ada semua kolom non-agregat di SELECT
            ORDER BY s.ID_SUPLIER
        """;
        */


        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Supplier(
                        rs.getString("ID_SUPLIER"),
                        rs.getString("NAMA_SUPLIER"),
                        rs.getString("ALAMAT"),
                        rs.getString("NO_TELEPON"),
                        rs.getString("NAMA_BARANG_DISPLAY") // Mengambil kolom hasil JOIN/subquery
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Optional<Supplier> getSupplierById(String idSupplier) throws SQLException {
        // Query ini tidak perlu diganti untuk fitur ini karena hanya mengambil 1 supplier
        String sql = "SELECT ID_SUPLIER, NAMA_SUPLIER, ALAMAT, NO_TELEPON, NAMA_BARANG FROM SUPLIER WHERE ID_SUPLIER = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idSupplier);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Supplier(
                            rs.getString("ID_SUPLIER"),
                            rs.getString("NAMA_SUPLIER"),
                            rs.getString("ALAMAT"),
                            rs.getString("NO_TELEPON"),
                            rs.getString("NAMA_BARANG") // Mengambil dari kolom NAMA_BARANG di tabel SUPLIER
                    ));
                }
            }
        }
        return Optional.empty();
    }


    public static void insertSupplier(Supplier s) throws SQLException {
        // Karena input Nama Barang dihapus, kita akan mengirim null atau string kosong untuk kolom NAMA_BARANG di DB
        String sql = "INSERT INTO SUPLIER (NAMA_SUPLIER, ALAMAT, NO_TELEPON, NAMA_BARANG) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNamaSupplier());
            stmt.setString(2, s.getAlamat());
            stmt.setString(3, s.getNoTelepon());
            stmt.setString(4, null); // Mengirim NULL karena tidak ada input dari form
            stmt.executeUpdate();
        }
    }


    public static void updateSupplier(Supplier s) throws SQLException {
        // Karena input Nama Barang dihapus, kita akan mengupdate dengan nilai null atau mempertahankan nilai lama
        // Dalam kasus ini, kita akan mengupdate dengan null, kecuali jika Anda ingin mempertahankan nilai lama
        // Jika ingin mempertahankan nilai lama, Anda harus mengambil nilai s.getNamaBarang() dari selectedSupplier
        String sql = "UPDATE SUPLIER SET NAMA_SUPLIER=?, ALAMAT=?, NO_TELEPON=?, NAMA_BARANG=? WHERE ID_SUPLIER=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNamaSupplier());
            stmt.setString(2, s.getAlamat());
            stmt.setString(3, s.getNoTelepon());
            stmt.setString(4, null); // Mengirim NULL karena tidak ada input dari form ini
            stmt.setString(5, s.getIdSupplier());
            stmt.executeUpdate();
        }
    }


    // --- METODE deleteSupplier() ---
    // (Tidak ada perubahan pada metode deleteSupplier ini)
    public static void deleteSupplier(String idSupplier) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            String sqlDeleteDetailPembelianByPembelian = """
                DELETE FROM DETAIL_PEMBELIAN
                WHERE ID_PEMBELIAN IN (SELECT ID_PEMBELIAN FROM PEMBELIAN WHERE ID_SUPLIER = ?)
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteDetailPembelianByPembelian)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            String sqlDeleteDetailTransaksiByTransaksi = """
                DELETE FROM DETAIL_TRANSAKSI
                WHERE TRANSAKSI_ID_TRANSAKSI IN (SELECT ID_TRANSAKSI FROM TRANSAKSI WHERE ID_SUPLIER = ?)
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteDetailTransaksiByTransaksi)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            String sqlDeletePembayaranByTransaksi = """
                DELETE FROM PEMBAYARAN
                WHERE TRANSAKSI_ID_TRANSAKSI IN (SELECT ID_TRANSAKSI FROM TRANSAKSI WHERE ID_SUPLIER = ?)
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeletePembayaranByTransaksi)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            String sqlDeleteStrukByTransaksi = """
                DELETE FROM STRUK
                WHERE ID_TRANSAKSI IN (SELECT ID_TRANSAKSI FROM TRANSAKSI WHERE ID_SUPLIER = ?)
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteStrukByTransaksi)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            String sqlDeleteBarang = "DELETE FROM BARANG WHERE SUPLIER_ID_SUPLIER = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteBarang)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            String sqlDeletePembelian = "DELETE FROM PEMBELIAN WHERE ID_SUPLIER = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeletePembelian)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            String sqlDeleteTransaksi = "DELETE FROM TRANSAKSI WHERE ID_SUPLIER = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteTransaksi)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            String sqlDeleteSupplier = "DELETE FROM SUPLIER WHERE ID_SUPLIER = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteSupplier)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Gagal melakukan rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Gagal menutup koneksi database: " + closeEx.getMessage());
                }
            }
        }
    }
}