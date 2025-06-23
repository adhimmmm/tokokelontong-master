package com.example.tokokelontong.model;

import javafx.beans.property.SimpleStringProperty;

public class Supplier {
    private SimpleStringProperty idSupplier;
    private SimpleStringProperty namaSupplier;
    private SimpleStringProperty alamat;
    private SimpleStringProperty noTelepon;
    private SimpleStringProperty namaBarang;

    public Supplier(String idSupplier, String namaSupplier, String alamat, String noTelepon, String namaBarang) {
        this.idSupplier = new SimpleStringProperty(idSupplier);
        this.namaSupplier = new SimpleStringProperty(namaSupplier);
        this.alamat = new SimpleStringProperty(alamat);
        this.noTelepon = new SimpleStringProperty(noTelepon);
        this.namaBarang = new SimpleStringProperty(namaBarang);
    }

    public String getIdSupplier() { return idSupplier.get(); }
    public String getNamaSupplier() { return namaSupplier.get(); }
    public String getAlamat() { return alamat.get(); }
    public String getNoTelepon() { return noTelepon.get(); }
    public String getNamaBarang() { return namaBarang.get(); }

    public void setNamaSupplier(String namaSupplier) { this.namaSupplier.set(namaSupplier); }
    public void setAlamat(String alamat) { this.alamat.set(alamat); }
    public void setNoTelepon(String noTelepon) { this.noTelepon.set(noTelepon); }
    public void setNamaBarang(String namaBarang) { this.namaBarang.set(namaBarang); }
}

