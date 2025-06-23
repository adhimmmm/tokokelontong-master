package com.example.tokokelontong.controller;

import com.example.tokokelontong.dao.SupplierDAO;
import com.example.tokokelontong.model.Supplier;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.collections.*;

import java.sql.SQLException;

public class SupplierController {
    @FXML private TextField namaSupplierField, alamatField, noTeleponField, namaBarangField;
    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, String> idSupplierColumn, namaSupplierColumn, alamatColumn, noTeleponColumn, namaBarangColumn;
    @FXML private TextField searchField;

    private Supplier selectedSupplier;
    private ObservableList<Supplier> supplierData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idSupplierColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdSupplier()));
        namaSupplierColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNamaSupplier()));
        alamatColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAlamat()));
        noTeleponColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNoTelepon()));
        namaBarangColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNamaBarang()));

        loadTable();

        supplierTable.setOnMouseClicked((MouseEvent e) -> {
            selectedSupplier = supplierTable.getSelectionModel().getSelectedItem();
            if (selectedSupplier != null) {
                namaSupplierField.setText(selectedSupplier.getNamaSupplier());
                alamatField.setText(selectedSupplier.getAlamat());
                noTeleponField.setText(selectedSupplier.getNoTelepon());
                namaBarangField.setText(selectedSupplier.getNamaBarang());
            }
        });

        // Pencarian langsung saat ketik
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSupplierList(newValue);
        });
    }

    private void loadTable() {
        supplierData.setAll(SupplierDAO.getAllSuppliers());
        supplierTable.setItems(supplierData);
    }

    private void filterSupplierList(String keyword) {
        String lowerKeyword = keyword.toLowerCase().trim();

        if (lowerKeyword.isEmpty()) {
            supplierTable.setItems(supplierData);
            return;
        }

        ObservableList<Supplier> filtered = FXCollections.observableArrayList();
        for (Supplier s : supplierData) {
            if (
                    s.getIdSupplier().toLowerCase().contains(lowerKeyword) ||
                            s.getNamaSupplier().toLowerCase().contains(lowerKeyword) ||
                            s.getAlamat().toLowerCase().contains(lowerKeyword) ||
                            s.getNoTelepon().toLowerCase().contains(lowerKeyword) ||
                            s.getNamaBarang().toLowerCase().contains(lowerKeyword)
            ) {
                filtered.add(s);
            }
        }

        supplierTable.setItems(filtered);
    }

    @FXML
    private void handleCariSupplier() {
        filterSupplierList(searchField.getText());
    }

    @FXML
    private void handleTambahSupplier() {
        Supplier s = new Supplier("", namaSupplierField.getText(), alamatField.getText(), noTeleponField.getText(), namaBarangField.getText());

        try {
            SupplierDAO.insertSupplier(s);
            loadTable();
            handleResetForm();
        } catch (SQLException e) {
            showError("Gagal menambahkan supplier: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateSupplier() {
        if (selectedSupplier == null) {
            showError("Pilih supplier yang akan diperbarui.");
            return;
        }

        try {
            selectedSupplier.setNamaSupplier(namaSupplierField.getText());
            selectedSupplier.setAlamat(alamatField.getText());
            selectedSupplier.setNoTelepon(noTeleponField.getText());
            selectedSupplier.setNamaBarang(namaBarangField.getText());

            SupplierDAO.updateSupplier(selectedSupplier);
            loadTable();
            handleResetForm();
        } catch (SQLException e) {
            showError("Gagal memperbarui supplier: " + e.getMessage());
        }
    }

    @FXML
    private void handleHapusSupplier() {
        if (selectedSupplier == null) {
            showError("Pilih supplier yang akan dihapus.");
            return;
        }

        try {
            SupplierDAO.deleteSupplier(selectedSupplier.getIdSupplier());
            loadTable();
            handleResetForm();
        } catch (SQLException e) {
            showError("Gagal menghapus supplier: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    private void handleResetForm() {
        namaSupplierField.clear();
        alamatField.clear();
        noTeleponField.clear();
        namaBarangField.clear();
        selectedSupplier = null;
    }
}
