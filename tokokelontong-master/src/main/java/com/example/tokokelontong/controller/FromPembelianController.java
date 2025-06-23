package com.example.tokokelontong.controller;

import com.example.tokokelontong.dao.BarangDAO;
import com.example.tokokelontong.dao.PembelianDAO;
import com.example.tokokelontong.dao.SupplierDAO;
import com.example.tokokelontong.model.Barang;
import com.example.tokokelontong.model.DetailPembelian;
import com.example.tokokelontong.model.Pembelian;
import com.example.tokokelontong.model.Supplier;
import com.example.tokokelontong.database.PdfStrukGenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FromPembelianController {

    @FXML private DatePicker dpTanggal;
    @FXML private ComboBox<Supplier> cbSuplier;
    @FXML private ComboBox<Barang> cbBarang;
    @FXML private TextField tfHargaBeli;
    @FXML private TextField tfJumlah;
    @FXML private Label lblTotal;

    @FXML private TableView<DetailPembelian> tableDetail;
    @FXML private TableColumn<DetailPembelian, LocalDate> colTanggalPembelian;
    @FXML private TableColumn<DetailPembelian, String> colKodeBarang;
    @FXML private TableColumn<DetailPembelian, String> colNamaBarang;
    @FXML private TableColumn<DetailPembelian, Integer> colJumlah;
    @FXML private TableColumn<DetailPembelian, BigDecimal> colHarga;
    @FXML private TableColumn<DetailPembelian, BigDecimal> colSubtotal;

    private ObservableList<DetailPembelian> detailList = FXCollections.observableArrayList();
    private BigDecimal total = BigDecimal.ZERO;

    private PembelianDAO pembelianDAO = new PembelianDAO();
    private SupplierDAO suplierDAO = new SupplierDAO();
    private BarangDAO barangDAO = new BarangDAO();

    private List<Barang> allBarangFromDb = new ArrayList<>();

    @FXML
    public void initialize() {
        colTanggalPembelian.setCellValueFactory(new PropertyValueFactory<>("tanggalPembelian"));
        colKodeBarang.setCellValueFactory(new PropertyValueFactory<>("kodeBarang"));
        colNamaBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("hargaBeli"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tableDetail.setItems(detailList);
        lblTotal.setText("Rp 0.00");
        dpTanggal.setValue(LocalDate.now());

        cbSuplier.setConverter(new StringConverter<Supplier>() {
            @Override
            public String toString(Supplier suplier) {
                return suplier != null ? suplier.getIdSupplier() + " - " + suplier.getNamaSupplier() : "";
            }
            @Override
            public Supplier fromString(String string) { return null; }
        });

        cbBarang.setConverter(new StringConverter<Barang>() {
            @Override
            public String toString(Barang barang) {
                return barang != null ? barang.getKodeBarang() + " - " + barang.getNamaBarang() : "";
            }
            @Override
            public Barang fromString(String string) { return null; }
        });

        loadSuplierToComboBox();
        loadAllBarangFromDatabase();

        cbSuplier.setOnAction(e -> onSupplierSelected());
        cbBarang.setOnAction(e -> onBarangSelected());

        tfJumlah.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfJumlah.setText(oldValue);
            }
        });
        tfHargaBeli.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                tfHargaBeli.setText(oldValue);
            }
        });
    }

    private void loadSuplierToComboBox() {
        try {
            ObservableList<Supplier> suplierObservableList = FXCollections.observableArrayList(suplierDAO.getAllSuppliers());
            cbSuplier.setItems(suplierObservableList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Gagal memuat data supplier: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAllBarangFromDatabase() {
        try {
            allBarangFromDb = barangDAO.getAllBarangWithSupplier();
        }
        catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal memuat semua data barang dari database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onSupplierSelected() {
        cbBarang.getItems().clear();
        cbBarang.getSelectionModel().clearSelection();
        tfHargaBeli.clear();
        tfJumlah.clear();

        Supplier selectedSuplier = cbSuplier.getSelectionModel().getSelectedItem();
        if (selectedSuplier == null) return;

        String idSuplier = selectedSuplier.getIdSupplier();
        List<Barang> filteredBarang = allBarangFromDb.stream()
                .filter(barang -> barang.getIdSuplier().equals(idSuplier))
                .collect(Collectors.toList());
        cbBarang.setItems(FXCollections.observableArrayList(filteredBarang));
    }

    private void onBarangSelected() {
        Barang selectedBarang = cbBarang.getSelectionModel().getSelectedItem();
        if (selectedBarang != null) {
            tfHargaBeli.setText(String.valueOf(selectedBarang.getHarga()));
        } else {
            tfHargaBeli.clear();
        }
    }

    @FXML
    public void onTambah() {
        if (cbBarang.getValue() == null || tfHargaBeli.getText().isEmpty() || tfJumlah.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Barang, harga beli, dan jumlah harus diisi.");
            return;
        }

        try {
            Barang selectedBarang = cbBarang.getSelectionModel().getSelectedItem();
            if (selectedBarang == null) {
                showAlert(Alert.AlertType.WARNING, "Pilih barang yang valid.");
                return;
            }
            if (dpTanggal.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Tanggal Pembelian harus diisi.");
                return;
            }

            LocalDate tanggalPembelian = dpTanggal.getValue();
            String kodeBarang = selectedBarang.getKodeBarang();
            String namaBarang = selectedBarang.getNamaBarang();
            int jumlah = Integer.parseInt(tfJumlah.getText());

            BigDecimal hargaBeliInput = new BigDecimal(tfHargaBeli.getText());

            if (jumlah <= 0 || hargaBeliInput.compareTo(BigDecimal.ZERO) < 0) {
                showAlert(Alert.AlertType.WARNING, "Jumlah dan harga beli harus angka positif.");
                return;
            }

            BigDecimal subtotal = hargaBeliInput.multiply(new BigDecimal(jumlah));

            System.out.println("DEBUG: Tanggal Pembelian yang akan ditambahkan: " + tanggalPembelian);

            Optional<DetailPembelian> existingDetail = detailList.stream()
                    .filter(d -> d.getKodeBarang().equals(kodeBarang))
                    .findFirst();

            if (existingDetail.isPresent()) {
                DetailPembelian d = existingDetail.get();
                total = total.subtract(d.getSubtotal());
                d.setJumlah(d.getJumlah() + jumlah);
                d.setSubtotal(hargaBeliInput.multiply(new BigDecimal(d.getJumlah())));
                total = total.add(d.getSubtotal());
                tableDetail.refresh();
            } else {
                DetailPembelian detail = new DetailPembelian(tanggalPembelian, kodeBarang, namaBarang, jumlah, hargaBeliInput, subtotal);
                detailList.add(detail);
                total = total.add(subtotal);
            }

            lblTotal.setText(String.format("Rp %,.2f", total));

            tfJumlah.clear();
            tfHargaBeli.clear();
            cbBarang.getSelectionModel().clearSelection();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Jumlah dan harga beli harus berupa angka yang valid.");
        }
    }

    @FXML
    public void onSimpan() {
        if (dpTanggal.getValue() == null || cbSuplier.getValue() == null || detailList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lengkapi data sebelum menyimpan.");
            return;
        }

        LocalDate tanggal = dpTanggal.getValue();

        Supplier selectedSuplier = cbSuplier.getSelectionModel().getSelectedItem();
        if (selectedSuplier == null) {
            showAlert(Alert.AlertType.WARNING, "Pilih supplier yang valid.");
            return;
        }
        String idSuplier = selectedSuplier.getIdSupplier();
        String namaSuplier = selectedSuplier.getNamaSupplier();

        String finalPembelianId = null; // Untuk menyimpan ID yang dihasilkan oleh database

        try {
            Pembelian pembelianToSave = new Pembelian(tanggal, idSuplier, namaSuplier, total);

            for (DetailPembelian detail : detailList) {
                detail.setIdPembelian(null); // Set ke null dulu, DAO akan mengisi dengan ID yang benar
            }

            finalPembelianId = pembelianDAO.savePembelianTransaction(pembelianToSave, detailList);

            // Tampilkan alert sukses transaksi
            showAlert(Alert.AlertType.INFORMATION, "Pembelian berhasil disimpan!");

            // --- Bagian Konfirmasi Cetak Struk ---
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Konfirmasi Cetak Struk");
            confirmAlert.setHeaderText("Transaksi berhasil disimpan.");
            confirmAlert.setContentText("Apakah Anda ingin mencetak struk pembelian?");

            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) { // Jika user menekan OK/Yes
                try {
                    PdfStrukGenerator.generateStrukPembelian(
                            pembelianToSave.getIdPembelian(), // Objek pembelianToSave sudah diperbarui dengan ID dari DB
                            pembelianToSave.getTanggal(),
                            pembelianToSave.getNamaSuplier(),
                            new ArrayList<>(detailList), // Menggunakan salinan detailList
                            pembelianToSave.getTotal()
                    );
                    showAlert(Alert.AlertType.INFORMATION, "Struk PDF berhasil dibuat.");
                } catch (Exception pdfEx) {
                    pdfEx.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Gagal mencetak struk PDF.\n" + pdfEx.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Pencetakan struk dibatalkan.");
            }
            // --- Akhir Bagian Konfirmasi Cetak Struk ---

            clearForm();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal menyimpan pembelian ke database.\n" + e.getMessage());
        } catch (Exception e) { // Ini akan menangkap Exception dari PdfStrukGenerator jika tidak ditangkap di blok dalamnya
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Terjadi kesalahan yang tidak terduga.\n" + e.getMessage());
        }
    }

    private void clearForm() {
        dpTanggal.setValue(LocalDate.now());
        cbSuplier.getSelectionModel().clearSelection();
        cbBarang.getItems().clear();
        cbBarang.getSelectionModel().clearSelection();
        tfHargaBeli.clear();
        tfJumlah.clear();
        detailList.clear();
        total = BigDecimal.ZERO;
        lblTotal.setText("Rp 0.00");
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }
}