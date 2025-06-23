package com.example.tokokelontong.dao;

import com.example.tokokelontong.model.Barang;
import com.example.tokokelontong.database.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangDAO {

    public List<Barang> getAllBarangWithSupplier() throws SQLException {
        List<Barang> list = new ArrayList<>();
        String sql = """
            SELECT b.kode_barang, b.nama_barang, b.harga, b.stok,  b.jenis_barang,
                   b.suplier_id_suplier, s.nama_suplier
            FROM barang b
            JOIN suplier s ON b.suplier_id_suplier = s.id_suplier
            ORDER BY b.kode_barang
        """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Barang(
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang"),
                        rs.getDouble("harga"),
                        rs.getInt("stok"),
                        rs.getString("jenis_barang"),
                        rs.getString("suplier_id_suplier"),
                        rs.getString("nama_suplier")
                ));
            }
        }
        return list;
    }

    public void insertBarang(Barang barang) throws SQLException {
        String sql = "INSERT INTO barang (nama_barang, harga, stok, jenis_barang, suplier_id_suplier) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, barang.getNamaBarang());
            stmt.setDouble(2, barang.getHarga());
            stmt.setInt(3, barang.getStok());
            stmt.setString(4, barang.getJenisBarang());
            stmt.setString(5, barang.getIdSuplier());

            stmt.executeUpdate();
        }
    }


    public void updateBarang(Barang barang) throws SQLException {
        String sql = "UPDATE barang SET nama_barang = ?, harga = ?, stok = ?, jenis_barang = ?, suplier_id_suplier = ? WHERE kode_barang = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, barang.getNamaBarang());
            stmt.setDouble(2, barang.getHarga());
            stmt.setInt(3, barang.getStok());
            stmt.setString(4, barang.getJenisBarang());
            stmt.setString(5, barang.getIdSuplier());
            stmt.setString(6, barang.getKodeBarang());
            stmt.executeUpdate();
        }
    }

    // --- METODE deleteBarang() YANG DIPERBAIKI DENGAN CASCADING DELETE ---
    public void deleteBarang(String kodeBarang) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            // 1. Hapus dari DETAIL_PEMBELIAN yang merujuk barang ini
            String sqlDeleteDetailPembelian = "DELETE FROM DETAIL_PEMBELIAN WHERE KODE_BARANG = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteDetailPembelian)) {
                pstmt.setString(1, kodeBarang);
                pstmt.executeUpdate();
            }

            // 2. Hapus dari DETAIL_TRANSAKSI yang merujuk barang ini
            String sqlDeleteDetailTransaksi = "DELETE FROM DETAIL_TRANSAKSI WHERE BARANG_KODE_BARANG = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteDetailTransaksi)) {
                pstmt.setString(1, kodeBarang);
                pstmt.executeUpdate();
            }

            // 3. Terakhir, hapus BARANG induk
            String sqlDeleteBarang = "DELETE FROM BARANG WHERE KODE_BARANG = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteBarang)) {
                pstmt.setString(1, kodeBarang);
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
}
