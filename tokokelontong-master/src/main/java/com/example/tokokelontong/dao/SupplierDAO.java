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


    public static void deleteSupplier(String id) throws SQLException {
        String sql = "DELETE FROM SUPLIER WHERE ID_SUPPLIER = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

}
