package com.example.tokokelontong.dao;

import com.example.tokokelontong.model.Pembelian;
import com.example.tokokelontong.model.DetailPembelian;
import com.example.tokokelontong.database.DBUtil;
import java.sql.*;
import java.time.LocalDate; // Import LocalDate
import java.util.List;
import java.math.BigDecimal; // <<< PASTIKAN INI DIIMPORT, KARENA MENGGUNAKAN BIGDECIMAL

public class PembelianDAO {

    public String savePembelianTransaction(Pembelian pembelian, List<DetailPembelian> detailItems) throws SQLException {
        String generatedPembelianId = null;

        String sqlPembelian = "INSERT INTO PEMBELIAN (TANGGAL, ID_SUPLIER, TOTAL) VALUES (?, ?, ?)";
        String sqlDetail = "INSERT INTO DETAIL_PEMBELIAN (ID_PEMBELIAN, KODE_BARANG, JUMLAH, HARGA_BELI, SUBTOTAL) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateStok = "UPDATE BARANG SET STOK = STOK + ? WHERE KODE_BARANG = ?";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            // 1. Insert Pembelian (Parent) - MENGAMBIL ID YANG DIHASILKAN DB
            try (PreparedStatement psPembelian = conn.prepareStatement(sqlPembelian, new String[]{"ID_PEMBELIAN"})) {
                psPembelian.setDate(1, Date.valueOf(pembelian.getTanggal()));
                psPembelian.setString(2, pembelian.getIdSuplier());
                psPembelian.setBigDecimal(3, pembelian.getTotal()); // <<< MENGGUNAKAN setBigDecimal()

                psPembelian.executeUpdate();

                try (ResultSet rs = psPembelian.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedPembelianId = rs.getString(1);
                        System.out.println("DEBUG: Inserted PEMBELIAN with DB-generated ID: " + generatedPembelianId);
                        pembelian.setIdPembelian(generatedPembelianId); // Update objek Pembelian di memori
                    } else {
                        throw new SQLException("Failed to retrieve generated ID for PEMBELIAN.");
                    }
                }
            }

            if (generatedPembelianId == null) {
                throw new SQLException("Generated PEMBELIAN ID is NULL after insert, cannot proceed.");
            }

            // 2. Insert Detail Pembelian (Child) dan Update Stok Barang
            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                 PreparedStatement psStok = conn.prepareStatement(sqlUpdateStok)) {
                for (DetailPembelian detail : detailItems) {
                    psDetail.setString(1, generatedPembelianId);
                    psDetail.setString(2, detail.getKodeBarang());
                    psDetail.setInt(3, detail.getJumlah());
                    psDetail.setBigDecimal(4, detail.getHargaBeli()); // <<< MENGGUNAKAN setBigDecimal()
                    psDetail.setBigDecimal(5, detail.getSubtotal());  // <<< MENGGUNAKAN setBigDecimal()

                    psDetail.addBatch();
                    System.out.println("DEBUG: Adding DETAIL_PEMBELIAN for KODE_BARANG: " + detail.getKodeBarang() + " with ID_PEMBELIAN: " + generatedPembelianId);

                    // Untuk update stok BARANG
                    psStok.setInt(1, detail.getJumlah());
                    psStok.setString(2, detail.getKodeBarang());
                    psStok.addBatch();
                }
                psDetail.executeBatch();
                psStok.executeBatch();
            }

            conn.commit();
            System.out.println("DEBUG: Transaction committed successfully.");
            return generatedPembelianId;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaksi dibatalkan karena error: " + e.getMessage());
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