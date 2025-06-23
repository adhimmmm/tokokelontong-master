package com.example.tokokelontong.dao;

import com.example.tokokelontong.model.Supplier;
import com.example.tokokelontong.database.DBUtil;
import java.sql.*;
import java.util.*;

public class SupplierDAO {

    public static List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM SUPLIER ORDER BY ID_SUPLIER";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Supplier(
                        rs.getString("ID_SUPLIER"),
                        rs.getString("NAMA_SUPLIER"),
                        rs.getString("ALAMAT"),
                        rs.getString("NO_TELEPON"),
                        rs.getString("NAMA_BARANG")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void insertSupplier(Supplier s) throws SQLException {
        String sql = "INSERT INTO SUPLIER (NAMA_SUPLIER, ALAMAT, NO_TELEPON, NAMA_BARANG) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNamaSupplier());
            stmt.setString(2, s.getAlamat());
            stmt.setString(3, s.getNoTelepon());
            stmt.setString(4, s.getNamaBarang());
            stmt.executeUpdate();
        }
    }


    public static void updateSupplier(Supplier s) throws SQLException {
        String sql = "UPDATE SUPLIER SET NAMA_SUPLIER=?, ALAMAT=?, NO_TELEPON=?, NAMA_BARANG=? WHERE ID_SUPLIER=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNamaSupplier());
            stmt.setString(2, s.getAlamat());
            stmt.setString(3, s.getNoTelepon());
            stmt.setString(4, s.getNamaBarang());
            stmt.setString(5, s.getIdSupplier());
            stmt.executeUpdate();
        }
    }


    // --- METODE deleteSupplier() YANG DIPERBAIKI DENGAN CASCADING DELETE ---
    // Metode ini akan menghapus data dari tabel anak terdalam terlebih dahulu.
    public static void deleteSupplier(String idSupplier) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            // Urutan penghapusan harus dari anak paling dalam ke induk:

            // 1. Hapus DETAIL_PEMBELIAN yang terkait dengan PEMBELIAN dari supplier ini
            String sqlDeleteDetailPembelianByPembelian = """
                DELETE FROM DETAIL_PEMBELIAN
                WHERE ID_PEMBELIAN IN (SELECT ID_PEMBELIAN FROM PEMBELIAN WHERE ID_SUPLIER = ?)
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteDetailPembelianByPembelian)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            // 2. Hapus DETAIL_TRANSAKSI yang terkait dengan TRANSAKSI dari supplier ini
            String sqlDeleteDetailTransaksiByTransaksi = """
                DELETE FROM DETAIL_TRANSAKSI
                WHERE TRANSAKSI_ID_TRANSAKSI IN (SELECT ID_TRANSAKSI FROM TRANSAKSI WHERE ID_SUPLIER = ?)
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteDetailTransaksiByTransaksi)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            // 3. Hapus PEMBAYARAN yang terkait dengan TRANSAKSI dari supplier ini
            String sqlDeletePembayaranByTransaksi = """
                DELETE FROM PEMBAYARAN
                WHERE TRANSAKSI_ID_TRANSAKSI IN (SELECT ID_TRANSAKSI FROM TRANSAKSI WHERE ID_SUPLIER = ?)
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeletePembayaranByTransaksi)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            // 4. Hapus STRUK yang terkait dengan TRANSAKSI dari supplier ini (BARU DITAMBAHKAN)
            String sqlDeleteStrukByTransaksi = """
                DELETE FROM STRUK
                WHERE ID_TRANSAKSI IN (SELECT ID_TRANSAKSI FROM TRANSAKSI WHERE ID_SUPLIER = ?)
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteStrukByTransaksi)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            // 5. Hapus BARANG yang disuplai oleh supplier ini
            String sqlDeleteBarang = "DELETE FROM BARANG WHERE SUPLIER_ID_SUPLIER = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteBarang)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            // 6. Hapus PEMBELIAN yang terkait dengan supplier ini
            String sqlDeletePembelian = "DELETE FROM PEMBELIAN WHERE ID_SUPLIER = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeletePembelian)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            // 7. Hapus TRANSAKSI yang terkait dengan supplier ini
            String sqlDeleteTransaksi = "DELETE FROM TRANSAKSI WHERE ID_SUPLIER = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteTransaksi)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            // 8. Terakhir, hapus SUPLIER induk
            String sqlDeleteSupplier = "DELETE FROM SUPLIER WHERE ID_SUPLIER = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteSupplier)) {
                pstmt.setString(1, idSupplier);
                pstmt.executeUpdate();
            }

            conn.commit(); // Komit transaksi jika semua berhasil
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback jika ada error
                } catch (SQLException rbEx) {
                    System.err.println("Gagal melakukan rollback: " + rbEx.getMessage());
                }
            }
            throw e; // Lemparkan exception agar controller tahu ada masalah
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Kembalikan auto-commit
                    conn.close(); // Tutup koneksi
                } catch (SQLException closeEx) {
                    System.err.println("Gagal menutup koneksi database: " + closeEx.getMessage());
                }
            }
        }
    }
    // --- AKHIR DARI METODE deleteSupplier() YANG DIPERBAIKI ---

}
