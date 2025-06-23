package com.example.tokokelontong.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class AdminPanelController {

    @FXML
    private StackPane contentPane;



    public void loadBarangView() {
        loadView("/com/example/tokokelontong/view/BarangView.fxml");
    }

    public void loadPegawaiView() {
        loadView("/com/example/tokokelontong/view/PegawaiView.fxml");
    }

    public void loadSupplierView() {
        loadView("/com/example/tokokelontong/view/SupplierView.fxml");
    }

    public void loadTransaksiView() {
        loadView("/com/example/tokokelontong/view/TransaksiView.fxml");
    }

    public void loadLaporanView() {
        loadView("/com/example/tokokelontong/view/LaporanView.fxml");
    }

    public void loadDashboardView() {
        loadView("/com/example/tokokelontong/view/DashboardView.fxml");
    }

    public void logout() {
        System.exit(0);
    }

    private void loadView(String fxmlPath) {
        try {
            Pane pane = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
