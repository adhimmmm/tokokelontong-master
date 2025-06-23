package com.example.tokokelontong.controller;

import com.example.tokokelontong.dao.PegawaiDAO;
import com.example.tokokelontong.model.Pegawai;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.Region;

import java.sql.SQLException;

public class PegawaiController {

    public Region spacer;
    @FXML private TextField idField;
    @FXML private TextField namaField;
    @FXML private TextField alamatField;
    @FXML private TextField teleponField;
    @FXML private TextField jabatanField;
    @FXML private TextField searchField;

    @FXML private TableView<Pegawai> pegawaiTable;
    @FXML private TableColumn<Pegawai, String> colId;
    @FXML private TableColumn<Pegawai, String> colNama;
    @FXML private TableColumn<Pegawai, String> colAlamat;
    @FXML private TableColumn<Pegawai, String> colTelepon;
    @FXML private TableColumn<Pegawai, String> colJabatan;

    private final PegawaiDAO pegawaiDAO = new PegawaiDAO();
    private ObservableList<Pegawai> dataPegawai;

    @FXML
    public void initialize() {
        // Set kolom dengan lambda (karena model tidak pakai StringProperty)
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdPegawai()));
        colNama.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNama()));
        colAlamat.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAlamat()));
        colTelepon.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNoTelepon()));
        colJabatan.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getJabatan()));

        loadPegawai();

        pegawaiTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> showDetail(newVal)
        );
    }

    private void loadPegawai() {
        try {
            dataPegawai = FXCollections.observableArrayList(pegawaiDAO.getAllPegawai());
            pegawaiTable.setItems(dataPegawai);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showDetail(Pegawai pegawai) {
        if (pegawai != null) {
            idField.setText(pegawai.getIdPegawai());
            namaField.setText(pegawai.getNama());
            alamatField.setText(pegawai.getAlamat());
            teleponField.setText(pegawai.getNoTelepon());
            jabatanField.setText(pegawai.getJabatan());
        }
    }

    @FXML
    private void onTambah(ActionEvent event) {
        try {
            Pegawai pegawai = new Pegawai(
                    idField.getText(),
                    namaField.getText(),
                    alamatField.getText(),
                    teleponField.getText(),
                    jabatanField.getText()
            );
            pegawaiDAO.insertPegawai(pegawai);
            clearForm();
            loadPegawai();
        } catch (SQLException e) {
            showAlert("Error", "Gagal menambahkan pegawai: " + e.getMessage());
        }
    }

    @FXML
    private void onEdit(ActionEvent event) {
        Pegawai selected = pegawaiTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Peringatan", "Pilih data pegawai yang ingin diedit.");
            return;
        }

        try {
            Pegawai pegawai = new Pegawai(
                    idField.getText(),
                    namaField.getText(),
                    alamatField.getText(),
                    teleponField.getText(),
                    jabatanField.getText()
            );
            pegawaiDAO.updatePegawai(pegawai);
            clearForm();
            loadPegawai();
        } catch (SQLException e) {
            showAlert("Error", "Gagal mengedit pegawai: " + e.getMessage());
        }
    }

    @FXML
    private void onHapus(ActionEvent event) {
        Pegawai selected = pegawaiTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Peringatan", "Pilih data pegawai yang ingin dihapus.");
            return;
        }

        try {
            pegawaiDAO.deletePegawai(selected.getIdPegawai());
            clearForm();
            loadPegawai();
        } catch (SQLException e) {
            showAlert("Error", "Gagal menghapus pegawai: " + e.getMessage());
        }
    }

    @FXML
    private void onCari(ActionEvent event) {
        String keyword = searchField.getText().toLowerCase();
        ObservableList<Pegawai> filteredList = FXCollections.observableArrayList();

        for (Pegawai p : dataPegawai) {
            if (p.getNama().toLowerCase().contains(keyword) || p.getJabatan().toLowerCase().contains(keyword)) {
                filteredList.add(p);
            }
        }

        pegawaiTable.setItems(filteredList);
    }

    private void clearForm() {
        idField.clear();
        namaField.clear();
        alamatField.clear();
        teleponField.clear();
        jabatanField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void onSearch(ActionEvent actionEvent) {
    }

    public void onAdd(ActionEvent actionEvent) {
    }

    public void onDelete(ActionEvent actionEvent) {
    }

    public void onRefresh(ActionEvent actionEvent) {

    }
}
