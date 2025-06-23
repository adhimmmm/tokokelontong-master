package com.example.tokokelontong.dao;

import com.example.tokokelontong.model.Transaksi;
import com.example.tokokelontong.database.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    public static List<Transaksi> getAllTransaksi() {
        List<Transaksi> list = new ArrayList<>();
        String query = """
                SELECT t.ID_TRANSAKSI, t.TANGGAL, p.NAMA AS NAMA_PELANGGAN,
                       NVL(SUM(dt.JUMLAH), 0) AS JUMLAH_ITEM,
                       NVL(pm.JUMLAH_BAYAR, 0) AS TOTAL_BAYAR,
                       pm.METODE_PEMBAYARAN,
                       p.STATUS_MEMBER
                FROM TRANSAKSI t
                JOIN PELANGGAN p ON t.PELANGGAN_ID_PELANGGAN = p.ID_PELANGGAN
                LEFT JOIN DETAIL_TRANSAKSI dt ON dt.TRANSAKSI_ID_TRANSAKSI = t.ID_TRANSAKSI
                LEFT JOIN PEMBAYARAN pm ON pm.TRANSAKSI_ID_TRANSAKSI = t.ID_TRANSAKSI
                GROUP BY t.ID_TRANSAKSI, t.TANGGAL, p.NAMA, pm.JUMLAH_BAYAR, pm.METODE_PEMBAYARAN, p.STATUS_MEMBER
                ORDER BY t.TANGGAL DESC
                """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Transaksi t = new Transaksi(
                        rs.getString("ID_TRANSAKSI"),
                        rs.getDate("TANGGAL").toLocalDate(),
                        rs.getString("NAMA_PELANGGAN"),
                        rs.getInt("JUMLAH_ITEM"),
                        rs.getDouble("TOTAL_BAYAR"),
                        rs.getString("METODE_PEMBAYARAN"),
                        rs.getString("STATUS_MEMBER")
                );
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
