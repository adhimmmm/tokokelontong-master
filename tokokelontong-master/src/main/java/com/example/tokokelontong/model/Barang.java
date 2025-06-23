// File: Barang.java (Model)
package com.example.tokokelontong.model;

public class Barang {
    private String kodeBarang;
    private String namaBarang;
    private double harga;
    private int stok;
    private String jenisBarang;
    private String idSuplier;
    private String namaSuplier;


    public Barang(String kodeBarang, String namaBarang, double harga, int stok,  String jenisBarang, String idSuplier, String namaSuplier) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.harga = harga;
        this.stok = stok;
        this.jenisBarang = jenisBarang;
        this.idSuplier = idSuplier;
        this.namaSuplier = namaSuplier;

    }

    public Barang(String namaBarang, double harga, int stok,  String jenisBarang, String idSuplier, String namaSuplier) {
        this.namaBarang = namaBarang;
        this.harga = harga;
        this.stok = stok;
        this.jenisBarang = jenisBarang;
        this.idSuplier = idSuplier;
        this.namaSuplier = namaSuplier;

    }




    public String getKodeBarang() { return kodeBarang; }
    public void setKodeBarang(String kodeBarang) { this.kodeBarang = kodeBarang; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public String getJenisBarang() {
        return jenisBarang;
    }
    public void setJenisBarang(String jenisBarang) {
        this.jenisBarang = jenisBarang;
    }

    public String getIdSuplier() { return idSuplier; }
    public void setIdSuplier(String idSuplier) { this.idSuplier = idSuplier; }

    public String getNamaSuplier() { return namaSuplier; }
    public void setNamaSuplier(String namaSuplier) { this.namaSuplier = namaSuplier; }


}
