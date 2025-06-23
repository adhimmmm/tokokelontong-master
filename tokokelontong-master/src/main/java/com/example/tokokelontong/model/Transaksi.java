package com.example.tokokelontong.model;

import java.time.LocalDate;

public class Transaksi {
    private String idTransaksi;
    private LocalDate tanggal;
    private String namaPelanggan;
    private int jumlahItem;
    private double totalBayar;
    private String metodePembayaran;
    private String statusMember;

    public Transaksi(String idTransaksi, LocalDate tanggal, String namaPelanggan,
                     int jumlahItem, double totalBayar, String metodePembayaran, String statusMember) {
        this.idTransaksi = idTransaksi;
        this.tanggal = tanggal;
        this.namaPelanggan = namaPelanggan;
        this.jumlahItem = jumlahItem;
        this.totalBayar = totalBayar;
        this.metodePembayaran = metodePembayaran;
        this.statusMember = statusMember;
    }

    public String getIdTransaksi() { return idTransaksi; }
    public LocalDate getTanggal() { return tanggal; }
    public String getNamaPelanggan() { return namaPelanggan; }
    public int getJumlahItem() { return jumlahItem; }
    public double getTotalBayar() { return totalBayar; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public String getStatusMember() { return statusMember; }
}
