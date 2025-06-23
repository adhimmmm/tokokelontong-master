package com.example.tokokelontong.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.tokokelontong.dao.TransaksiDAO;
import com.example.tokokelontong.model.Transaksi;

public class TransaksiController {

    @FXML private TableView<Transaksi> tableTransaksi;
    @FXML private TableColumn<Transaksi, String> colId;
    @FXML private TableColumn<Transaksi, String> colTanggal;
    @FXML private TableColumn<Transaksi, String> colPelanggan;
    @FXML private TableColumn<Transaksi, Integer> colJumlahItem;
    @FXML private TableColumn<Transaksi, Double> colTotal;
    @FXML private TableColumn<Transaksi, String> colMetode;
    @FXML private TableColumn<Transaksi, String> colStatus;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colPelanggan.setCellValueFactory(new PropertyValueFactory<>("namaPelanggan"));
        colJumlahItem.setCellValueFactory(new PropertyValueFactory<>("jumlahItem"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalBayar"));
        colMetode.setCellValueFactory(new PropertyValueFactory<>("metodePembayaran"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusMember"));

        tableTransaksi.setItems(FXCollections.observableArrayList(TransaksiDAO.getAllTransaksi()));
    }

    public void onSearch(ActionEvent actionEvent) {
    }

    public void onAdd(ActionEvent actionEvent) {
    }

    public void onEdit(ActionEvent actionEvent) {
    }

    public void onDelete(ActionEvent actionEvent) {
    }

    public void onRefresh(ActionEvent actionEvent) {

    }
}
