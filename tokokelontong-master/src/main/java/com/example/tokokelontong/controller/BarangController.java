package com.example.tokokelontong.controller;

import com.example.tokokelontong.dao.BarangDAO;
import com.example.tokokelontong.dao.SupplierDAO;
import com.example.tokokelontong.model.Barang;
import com.example.tokokelontong.model.Supplier;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarangController {
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final BarangDAO barangDAO = new BarangDAO();


    private Map<String, String> supplierMap = new HashMap<>(); // id -> nama
    private final ObservableList<Barang> barangList = FXCollections.observableArrayList();

    @FXML private TableView<Barang> tableBarang;
    @FXML private TableColumn<Barang, String> colKode, colNama, ColJenisBarang, colSupplier;
    @FXML private TableColumn<Barang, Number> colHarga, colStok;
    @FXML private TableColumn<Barang, String> colIdSupplier;

    @FXML private TextField searchField, namaField, hargaField, stokField, jenisBarangField;
    @FXML private ComboBox<String> supplierComboBox;

    @FXML
    public void initialize() {
        colKode.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKodeBarang()));
        colNama.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNamaBarang()));
        colHarga.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getHarga()));
        colStok.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStok()));
        ColJenisBarang.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJenisBarang()));
        colSupplier.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNamaSuplier()));
        colIdSupplier.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdSuplier()));



        searchField.textProperty().addListener((observable, oldValue, newValue) -> onSearch());


        loadSupplierComboBox();
        loadData();

        // Tambahkan listener ketika baris diklik
        tableBarang.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
    }

    private void populateFields(Barang barang) {
        namaField.setText(barang.getNamaBarang());
        hargaField.setText(String.valueOf(barang.getHarga()));
        stokField.setText(String.valueOf(barang.getStok()));
        jenisBarangField.setText(barang.getJenisBarang());
        supplierComboBox.setValue(barang.getNamaSuplier());
    }



    private void loadSupplierComboBox() {
        try {
            List<Supplier> supplierList = supplierDAO.getAllSuppliers();
            supplierMap.clear();
            supplierComboBox.getItems().clear();

            for (Supplier supplier : supplierList) {
                supplierMap.put(supplier.getIdSupplier(), supplier.getNamaSupplier());
                supplierComboBox.getItems().add(supplier.getNamaSupplier());
            }

        } catch (Exception e) {
            showError("Gagal memuat data supplier: " + e.getMessage());
        }
    }





    private void loadData() {
        try {
            barangList.setAll(barangDAO.getAllBarangWithSupplier());
            tableBarang.setItems(barangList);
        } catch (SQLException e) {
            showError("Gagal memuat data barang: " + e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        try {
            String namaBarang = namaField.getText();
            double harga = Double.parseDouble(hargaField.getText());
            int stok = Integer.parseInt(stokField.getText());
            String jenisbarang = jenisBarangField.getText();
            String namaSupplier = supplierComboBox.getValue();

            if (namaBarang.isBlank() || namaSupplier == null) {
                showError("Harap isi semua field dengan benar.");
                return;
            }

            String idSupplier = supplierMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(namaSupplier))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);

            if (idSupplier == null) {
                showError("Supplier tidak valid.");
                return;
            }

            Barang barang = new Barang(namaBarang, harga, stok, jenisbarang, idSupplier, namaSupplier);
            barangDAO.insertBarang(barang);
            loadData();
            clearFields();
        } catch (NumberFormatException e) {
            showError("Harga dan stok harus berupa angka.");
        } catch (SQLException e) {
            showError("Gagal menambahkan barang: " + e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        Barang selected = tableBarang.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Pilih barang yang akan diedit.");
            return;
        }

        try {
            String namaBarang = namaField.getText();
            double harga = Double.parseDouble(hargaField.getText());
            int stok = Integer.parseInt(stokField.getText());
            String jenisbarang = jenisBarangField.getText();
            String namaSupplier = supplierComboBox.getValue();

            if (namaBarang.isBlank() || namaSupplier == null) {
                showError("Harap isi semua field dengan benar.");
                return;
            }

            String idSupplier = supplierMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(namaSupplier))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);

            if (idSupplier == null) {
                showError("Supplier tidak valid.");
                return;
            }

            Barang barang = new Barang(namaBarang, harga, stok, jenisbarang, idSupplier, namaSupplier);
            barang.setKodeBarang(selected.getKodeBarang());

            barangDAO.updateBarang(barang);
            loadData();
            clearFields();
        } catch (NumberFormatException e) {
            showError("Harga dan stok harus berupa angka.");
        } catch (SQLException e) {
            showError("Gagal mengedit barang: " + e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Barang selected = tableBarang.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Pilih barang yang akan dihapus.");
            return;
        }

        try {
            barangDAO.deleteBarang(selected.getKodeBarang());
            loadData();
            clearFields();
        } catch (SQLException e) {
            showError("Gagal menghapus barang: " + e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        String keyword = searchField.getText().toLowerCase();

        tableBarang.setItems(barangList.filtered(b -> {
            // Konversi nilai numerik menjadi string agar bisa dicocokkan
            String hargaStr = String.valueOf(b.getHarga());
            String stokStr = String.valueOf(b.getStok());
            String jenisBarangStr = b.getJenisBarang() != null ? b.getJenisBarang().toLowerCase() : "";
            String supplierStr = b.getNamaSuplier() != null ? b.getNamaSuplier().toLowerCase() : "";
            String idSupplierStr = b.getIdSuplier() != null ? b.getIdSuplier().toLowerCase() : "";

            return b.getNamaBarang().toLowerCase().contains(keyword) ||
                    b.getKodeBarang().toLowerCase().contains(keyword) ||
                    hargaStr.contains(keyword) ||
                    stokStr.contains(keyword) ||
                    jenisBarangStr.contains(keyword) ||
                    supplierStr.contains(keyword) ||
                    idSupplierStr.contains(keyword); // Tambahkan ini
        }));
    }



    @FXML
    private void onRefresh() {
        loadData();
        clearFields();
    }

    private void clearFields() {
        namaField.clear();
        hargaField.clear();
        stokField.clear();
        jenisBarangField.clear();
        supplierComboBox.getSelectionModel().clearSelection();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
