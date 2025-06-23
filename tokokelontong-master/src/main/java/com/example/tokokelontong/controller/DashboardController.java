// File: DashboardController.java
package com.example.tokokelontong.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import com.example.tokokelontong.database.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardController {

    @FXML
    private Label totalBarang, totalTransaksi, totalPelanggan, totalSuplier, totalPendapatan;

    @FXML
    private BarChart<String, Number> transaksiChart;

    @FXML
    private LineChart<String, Number> pendapatanChart;

    private final Locale localeID = new Locale("id", "ID");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeID);

    public void initialize() {
        showSummary();
        showTransaksiChart();
        showPendapatanChart();
    }

    private void showSummary() {
        try (Connection conn = DBUtil.getConnection()) {
            totalBarang.setText(getTotal(conn, "SELECT COUNT(*) FROM BARANG"));
            totalTransaksi.setText(getTotal(conn, "SELECT COUNT(*) FROM TRANSAKSI"));
            totalPelanggan.setText(getTotal(conn, "SELECT COUNT(*) FROM PELANGGAN"));
            totalSuplier.setText(getTotal(conn, "SELECT COUNT(*) FROM SUPLIER"));

            String sqlPendapatan = "SELECT NVL(SUM(JUMLAH_BAYAR),0) FROM PEMBAYARAN";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPendapatan);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalPendapatan.setText(currencyFormat.format(rs.getDouble(1)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTotal(Connection conn, String query) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getString(1);
        }
        return "0";
    }

    private void showTransaksiChart() {
        transaksiChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Transaksi/Bulan");
        String sql = "SELECT TO_CHAR(TANGGAL, 'Mon') AS BULAN, COUNT(*) FROM TRANSAKSI GROUP BY TO_CHAR(TANGGAL, 'Mon'), TO_CHAR(TANGGAL, 'MM') ORDER BY TO_CHAR(TANGGAL, 'MM')";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString(1), rs.getInt(2)));
            }
            transaksiChart.getData().add(series);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPendapatanChart() {
        pendapatanChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pendapatan");
        String sql = "SELECT TO_CHAR(TANGGAL_BAYAR, 'DD Mon') AS HARI, SUM(JUMLAH_BAYAR) FROM PEMBAYARAN GROUP BY TO_CHAR(TANGGAL_BAYAR, 'DD Mon'), TANGGAL_BAYAR ORDER BY TANGGAL_BAYAR";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString(1), rs.getDouble(2)));
            }
            pendapatanChart.getData().add(series);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}