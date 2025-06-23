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
import java.util.Optional; // Import Optional

public class BarangController {
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final BarangDAO barangDAO = new BarangDAO();


    private Map<String, String> supplierMap = new HashMap<>(); // id -> nama
    private final ObservableList<Barang> barangList = FXCollections.observableArrayList();

    @FXML private TableView<Barang> tableBarang;
    @FXML private TableColumn<Barang, String> colKode, colNama, ColJenisBarang, colSupplier;
    @FXML private TableColumn<Barang, Number> colHarga, colStok;
    @FXML private TableColumn<Barang, String> colIdSupplier; // Untuk menampilkan ID Supplier di kolom terpisah jika perlu

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

        // Listener untuk validasi input numerik
        hargaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                hargaField.setText(oldValue);
            }
        });
        stokField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                stokField.setText(oldValue);
            }
        });
    }

    private void populateFields(Barang barang) {
        namaField.setText(barang.getNamaBarang());
        hargaField.setText(String.valueOf(barang.getHarga()));
        stokField.setText(String.valueOf(barang.getStok()));
        jenisBarangField.setText(barang.getJenisBarang());
        supplierComboBox.setValue(barang.getNamaSuplier()); // Mengisi ComboBox dengan nama supplier
    }



    private void loadSupplierComboBox() {
        try {
            List<Supplier> supplierList = SupplierDAO.getAllSuppliers(); // Menggunakan static method
            supplierMap.clear();
            supplierComboBox.getItems().clear();

            for (Supplier supplier : supplierList) {
                supplierMap.put(supplier.getIdSupplier(), supplier.getNamaSupplier());
                supplierComboBox.getItems().add(supplier.getNamaSupplier());
            }

        } catch (Exception e) {
            showError("Gagal memuat data supplier untuk ComboBox: " + e.getMessage());
        }
    }





    private void loadData() {
        try {
            barangList.setAll(barangDAO.getAllBarangWithSupplier());
            tableBarang.setItems(barangList);
        } catch (SQLException e) {
            showError("Gagal memuat data barang dari database: " + e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        try {
            String namaBarang = namaField.getText();
            double harga = Double.parseDouble(hargaField.getText());
            int stok = Integer.parseInt(stokField.getText());
            String jenisbarang = jenisBarangField.getText();
            String namaSupplierYangDipilih = supplierComboBox.getValue(); // Mengambil nama supplier dari ComboBox

            if (namaBarang.isBlank() || namaSupplierYangDipilih == null) {
                showError("Harap isi semua field (Nama Barang, Harga, Stok, Jenis Barang, Supplier).");
                return;
            }
            if (harga < 0 || stok < 0) {
                showError("Harga dan stok tidak boleh bernilai negatif.");
                return;
            }

            // Mencari ID Supplier berdasarkan nama yang dipilih di ComboBox
            String idSupplier = supplierMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(namaSupplierYangDipilih))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);

            if (idSupplier == null) {
                showError("Supplier yang dipilih tidak valid atau tidak ditemukan.");
                return;
            }

            Barang barang = new Barang(namaBarang, harga, stok, jenisbarang, idSupplier, namaSupplierYangDipilih); // ID Barang akan digenerate di DB jika ada trigger
            barangDAO.insertBarang(barang);
            loadData();
            clearFields();
            showInfo("Barang berhasil ditambahkan."); // Pesan sukses
        } catch (NumberFormatException e) {
            showError("Harga dan stok harus berupa angka yang valid.");
        } catch (SQLException e) {
            // Penanganan SQLException spesifik
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated
                showError("Gagal menambahkan barang: Kode barang sudah ada. Harap gunakan kode barang yang unik.");
            } else if (e.getErrorCode() == 2291) { // ORA-02291: integrity constraint (parent key not found)
                showError("Gagal menambahkan barang: Supplier tidak valid. Pastikan supplier terdaftar.");
            } else {
                showError("Gagal menambahkan barang: " + e.getMessage());
            }
            e.printStackTrace();
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
            String namaSupplierYangDipilih = supplierComboBox.getValue();

            if (namaBarang.isBlank() || namaSupplierYangDipilih == null) {
                showError("Harap isi semua field dengan benar.");
                return;
            }
            if (harga < 0 || stok < 0) {
                showError("Harga dan stok tidak boleh bernilai negatif.");
                return;
            }

            String idSupplier = supplierMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(namaSupplierYangDipilih))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);

            if (idSupplier == null) {
                showError("Supplier tidak valid.");
                return;
            }

            Barang barang = new Barang(namaBarang, harga, stok, jenisbarang, idSupplier, namaSupplierYangDipilih);
            barang.setKodeBarang(selected.getKodeBarang()); // Pastikan Kode Barang yang diedit tetap sama

            barangDAO.updateBarang(barang);
            loadData();
            clearFields();
            showInfo("Barang berhasil diperbarui."); // Pesan sukses
        } catch (NumberFormatException e) {
            showError("Harga dan stok harus berupa angka.");
        } catch (SQLException e) {
            // Penanganan SQLException spesifik
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated (jika update menyebabkan duplikasi PK/Unique key)
                showError("Gagal memperbarui barang: Kode barang sudah ada atau data duplikat.");
            } else if (e.getErrorCode() == 2291) { // ORA-02291: integrity constraint (parent key not found)
                showError("Gagal memperbarui barang: Supplier tidak valid. Pastikan supplier terdaftar.");
            } else {
                showError("Gagal memperbarui barang: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        Barang selected = tableBarang.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Pilih barang yang akan dihapus.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus Barang");
        confirmAlert.setHeaderText("Hapus Barang " + selected.getNamaBarang() + "?");
        confirmAlert.setContentText("Tindakan ini akan menghapus barang dan SEMUA detail pembelian/transaksi yang terkait dengannya. Lanjutkan?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                barangDAO.deleteBarang(selected.getKodeBarang());
                loadData();
                clearFields();
                showInfo("Barang berhasil dihapus."); // Pesan sukses
            } catch (SQLException e) {
                // Penanganan SQLException spesifik
                if (e.getErrorCode() == 2292) { // ORA-02292: integrity constraint (child record found)
                    showError("Gagal menghapus barang: Barang masih terhubung dengan catatan transaksi atau pembelian. Pastikan semua detail terkait sudah dihapus terlebih dahulu.");
                } else {
                    showError("Gagal menghapus barang: " + e.getMessage());
                }
                e.printStackTrace();
            }
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
                    (b.getKodeBarang() != null && b.getKodeBarang().toLowerCase().contains(keyword)) || // Tambah cek null untuk kode barang
                    hargaStr.contains(keyword) ||
                    stokStr.contains(keyword) ||
                    jenisBarangStr.contains(keyword) ||
                    supplierStr.contains(keyword) ||
                    idSupplierStr.contains(keyword);
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
        // searchField.clear(); // Opsional: bersihkan juga searchField
        // tableBarang.getSelectionModel().clearSelection(); // Opsional: clear selection di tabel
    }

    // Metode showError yang diperbaiki
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

    // Metode showInfo baru untuk pesan sukses/informasi
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
}