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

    public void deleteBarang(String kodeBarang) throws SQLException {
        String sql = "DELETE FROM barang WHERE kode_barang = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kodeBarang);
            stmt.executeUpdate();
        }
    }
}
