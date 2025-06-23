package com.example.tokokelontong.dao;

import com.example.tokokelontong.model.Pegawai;
import com.example.tokokelontong.database.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PegawaiDAO {

    public List<Pegawai> getAllPegawai() throws SQLException {
        List<Pegawai> list = new ArrayList<>();
        String query = "SELECT * FROM Pegawai ORDER BY id_pegawai";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pegawai pegawai = new Pegawai(
                        rs.getString("id_pegawai"),
                        rs.getString("nama_pegawai"),
                        rs.getString("alamat"),
                        rs.getString("no_telepon"),
                        rs.getString("jabatan")
                );
                list.add(pegawai);
            }
        }

        return list;
    }

    public void insertPegawai(Pegawai pegawai) throws SQLException {
        String query = "INSERT INTO Pegawai (id_pegawai, nama_pegawai, alamat, no_telepon, jabatan) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, pegawai.getIdPegawai());
            stmt.setString(2, pegawai.getNama());
            stmt.setString(3, pegawai.getAlamat());
            stmt.setString(4, pegawai.getNoTelepon());
            stmt.setString(5, pegawai.getJabatan());

            stmt.executeUpdate();
        }
    }

    public void updatePegawai(Pegawai pegawai) throws SQLException {
        String query = "UPDATE Pegawai SET nama_pegawai = ?, alamat = ?, no_telepon = ?, jabatan = ? WHERE id_pegawai = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, pegawai.getNama());
            stmt.setString(2, pegawai.getAlamat());
            stmt.setString(3, pegawai.getNoTelepon());
            stmt.setString(4, pegawai.getJabatan());
            stmt.setString(5, pegawai.getIdPegawai());

            stmt.executeUpdate();
        }
    }

    public void deletePegawai(String idPegawai) throws SQLException {
        String query = "DELETE FROM Pegawai WHERE id_pegawai = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idPegawai);
            stmt.executeUpdate();
        }
    }

    public Pegawai getPegawaiById(String idPegawai) throws SQLException {
        String query = "SELECT * FROM Pegawai WHERE id_pegawai = ?";
        Pegawai pegawai = null;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idPegawai);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pegawai = new Pegawai(
                            rs.getString("id_pegawai"),
                            rs.getString("nama_pegawai"),
                            rs.getString("alamat"),
                            rs.getString("no_telepon"),
                            rs.getString("jabatan")
                    );
                }
            }
        }

        return pegawai;
    }

    public List<Pegawai> searchPegawai(String keyword) throws SQLException {
        List<Pegawai> list = new ArrayList<>();
        String query = "SELECT * FROM Pegawai WHERE LOWER(nama_pegawai) LIKE ? OR LOWER(jabatan) LIKE ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchKeyword = "%" + keyword.toLowerCase() + "%";
            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Pegawai pegawai = new Pegawai(
                        rs.getString("id_pegawai"),
                        rs.getString("nama_pegawai"),
                        rs.getString("alamat"),
                        rs.getString("no_telepon"),
                        rs.getString("jabatan")
                );
                list.add(pegawai);
            }
        }

        return list;
    }
}
