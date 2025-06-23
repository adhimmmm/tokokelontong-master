package com.example.tokokelontong.dao;

import com.example.tokokelontong.model.Pembelian;
import com.example.tokokelontong.model.DetailPembelian;
import com.example.tokokelontong.database.DBUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

public class PembelianDAO {

    public String savePembelianTransaction(Pembelian pembelian, List<DetailPembelian> detailItems) throws SQLException {
        String generatedPembelianId = null;

        // SQL INSERT untuk PEMBELIAN (ID_PEMBELIAN akan digenerate trigger)
        String sqlPembelian = "INSERT INTO PEMBELIAN (TANGGAL, ID_SUPLIER, TOTAL) VALUES (?, ?, ?)";

        // SQL INSERT untuk DETAIL_PEMBELIAN (ID_DETAIL akan digenerate trigger)
        // KARENA ID_DETAIL DIGENERASI TRIGGER DAN TIDAK DIAMBIL DI JAVA, KITA TIDAK MINTA GENERATED KEYS UNTUKNYA
        String sqlDetail = "INSERT INTO DETAIL_PEMBELIAN (ID_PEMBELIAN, KODE_BARANG, JUMLAH, HARGA_BELI, SUBTOTAL) VALUES (?, ?, ?, ?, ?)";

        String sqlUpdateStok = "UPDATE BARANG SET STOK = STOK + ? WHERE KODE_BARANG = ?";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Pembelian (Parent)
            try (PreparedStatement psPembelian = conn.prepareStatement(sqlPembelian, new String[]{"ID_PEMBELIAN"})) {
                psPembelian.setDate(1, Date.valueOf(pembelian.getTanggal()));
                psPembelian.setString(2, pembelian.getIdSuplier());
                psPembelian.setBigDecimal(3, pembelian.getTotal());

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
            // Perbaikan utama di sini: psDetail tidak lagi meminta generated keys
            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail); // <<< PERBAIKAN: Hapus new String[]{"ID_DETAIL"}
                 PreparedStatement psStok = conn.prepareStatement(sqlUpdateStok)) {
                for (DetailPembelian detail : detailItems) {
                    // ID_DETAIL akan digenerate oleh trigger, tidak perlu diset dari Java
                    // Parameter JDBC sekarang diatur ulang karena tidak ada ID_DETAIL yang diset di indeks 1
                    // Urutan parameter sesuai dengan kolom di sqlDetail (ID_PEMBELIAN, KODE_BARANG, JUMLAH, HARGA_BELI, SUBTOTAL)

                    psDetail.setString(1, generatedPembelianId); // Parameter 1: ID_PEMBELIAN (dari induk)
                    psDetail.setString(2, detail.getKodeBarang()); // Parameter 2: KODE_BARANG
                    psDetail.setInt(3, detail.getJumlah());       // Parameter 3: JUMLAH
                    psDetail.setBigDecimal(4, detail.getHargaBeli()); // Parameter 4: HARGA_BELI
                    psDetail.setBigDecimal(5, detail.getSubtotal());  // Parameter 5: SUBTOTAL

                    psDetail.addBatch(); // <<< Baris 72 Anda. Sekarang seharusnya tidak NullPointerException.
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