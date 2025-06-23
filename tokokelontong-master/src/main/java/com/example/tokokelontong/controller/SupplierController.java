package com.example.tokokelontong.controller;

// Impor BarangDAO, Barang, StringConverter, Optional tidak lagi dibutuhkan di sini karena input barang dihapus
// import com.example.tokokelontong.dao.BarangDAO;
// import com.example.tokokelontong.model.Barang;
// import javafx.util.StringConverter;
// import java.util.Optional;

import com.example.tokokelontong.dao.SupplierDAO;
import com.example.tokokelontong.model.Supplier;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.collections.*;

import java.sql.SQLException;
import java.util.List; // Tetap diperlukan untuk ObservableList
import java.util.Optional;

public class SupplierController {
    @FXML private TextField namaSupplierField, alamatField, noTeleponField;
    // cbSelectBarangForSupplier dan tfDisplayNamaBarangSuplier dihapus dari FXML, jadi tidak dideklarasikan di sini
    // @FXML private ComboBox<Barang> cbSelectBarangForSupplier; // <<< DIHAPUS
    // @FXML private TextField tfDisplayNamaBarangSuplier; // <<< DIHAPUS

    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, String> idSupplierColumn, namaSupplierColumn, alamatColumn, noTeleponColumn, namaBarangColumn;
    @FXML private TextField searchField;

    private Supplier selectedSupplier;
    private ObservableList<Supplier> supplierData = FXCollections.observableArrayList();

    // BarangDAO tidak lagi diperlukan di controller ini karena input barang telah dihapus
    // private BarangDAO barangDAO = new BarangDAO();

    @FXML
    public void initialize() {
        idSupplierColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdSupplier()));
        namaSupplierColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNamaSupplier()));
        alamatColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAlamat()));
        noTeleponColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNoTelepon()));
        namaBarangColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNamaBarang())); // Tetap, untuk menampilkan di tabel

        loadTable();
        // loadBarangToComboBox(); // <<< DIHAPUS

        supplierTable.setOnMouseClicked((MouseEvent e) -> {
            selectedSupplier = supplierTable.getSelectionModel().getSelectedItem();
            if (selectedSupplier != null) {
                namaSupplierField.setText(selectedSupplier.getNamaSupplier());
                alamatField.setText(selectedSupplier.getAlamat());
                noTeleponField.setText(selectedSupplier.getNoTelepon());
                // Logika mengisi ComboBox barang atau TextField display Nama Barang dihapus
            }
        });

        // Listener untuk ComboBox barang dihapus
        // cbSelectBarangForSupplier.setOnAction(e -> { ... }); // <<< DIHAPUS

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSupplierList(newValue);
        });
    }

    private void loadTable() {
        supplierData.setAll(SupplierDAO.getAllSuppliers()); // Menggunakan static method
        supplierTable.setItems(supplierData);
    }

    // Metode loadBarangToComboBox dihapus
    /*
    private void loadBarangToComboBox() {
        try {
            ObservableList<Barang> barangList = FXCollections.observableArrayList(barangDAO.getAllBarangWithSupplier());
            cbSelectBarangForSupplier.setItems(barangList);
        } catch (SQLException e) {
            showError("Gagal memuat daftar barang: " + e.getMessage());
            e.printStackTrace();
        }
    }
    */

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
                            (s.getNamaBarang() != null && s.getNamaBarang().toLowerCase().contains(lowerKeyword))
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
        if (namaSupplierField.getText().isEmpty() || alamatField.getText().isEmpty() ||
                noTeleponField.getText().isEmpty()) {
            showError("Nama Supplier, Alamat, dan No. Telepon harus diisi.");
            return;
        }

        // Nama Barang akan dikirim sebagai null karena tidak ada input di form ini
        Supplier s = new Supplier("", namaSupplierField.getText(), alamatField.getText(), noTeleponField.getText(), null); // Mengirim null untuk namaBarang

        try {
            SupplierDAO.insertSupplier(s); // Menggunakan static method
            loadTable();
            handleResetForm();
            showInfo("Supplier berhasil ditambahkan.");
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
        if (namaSupplierField.getText().isEmpty() || alamatField.getText().isEmpty() ||
                noTeleponField.getText().isEmpty()) {
            showError("Nama Supplier, Alamat, dan No. Telepon harus diisi.");
            return;
        }

        try {
            selectedSupplier.setNamaSupplier(namaSupplierField.getText());
            selectedSupplier.setAlamat(alamatField.getText());
            selectedSupplier.setNoTelepon(noTeleponField.getText());
            // PENTING: Nama Barang tidak diubah dari form ini. Pertahankan nilai yang sudah ada di DB.
            // selectedSupplier.setNamaBarang(null); // Jika ingin mengosongkan
            // selectedSupplier.setNamaBarang(selectedSupplier.getNamaBarang()); // Ini akan mempertahankan nilai yang sudah ada dari objek yang dimuat
            // Karena tidak ada input, kita akan membiarkan nilai lama yang ada di selectedSupplier.namaBarang masuk ke DAO.
            // Atau jika ingin di-set null saja (karena tidak ada sumber input), maka kirim null.
            // Kita akan kirim null agar konsisten dengan insert. Jika di DB ada default, akan terpakai.

            SupplierDAO.updateSupplier(selectedSupplier); // Menggunakan static method
            loadTable();
            handleResetForm();
            showInfo("Supplier berhasil diperbarui.");
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

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus Supplier");
        confirmAlert.setHeaderText("Hapus Supplier " + selectedSupplier.getNamaSupplier() + "?");
        confirmAlert.setContentText("Tindakan ini akan menghapus supplier dan SEMUA data transaksi, pembelian, dan barang yang terkait dengannya. Lanjutkan?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                SupplierDAO.deleteSupplier(selectedSupplier.getIdSupplier()); // Menggunakan static method
                loadTable();
                handleResetForm();
                showInfo("Supplier berhasil dihapus.");
            } catch (SQLException e) {
                showError("Gagal menghapus supplier: " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(400);
        textArea.setPrefHeight(150);
        alert.getDialogPane().setContent(textArea);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(450, 250);
        alert.showAndWait();
    }

    // Metode baru: showInfo untuk pesan informasi
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(400);
        textArea.setPrefHeight(150);
        alert.getDialogPane().setContent(textArea);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(450, 250);
        alert.showAndWait();
    }

    @FXML
    private void handleResetForm() {
        namaSupplierField.clear();
        alamatField.clear();
        noTeleponField.clear();
        searchField.clear();
        selectedSupplier = null;
        loadTable();
    }
}