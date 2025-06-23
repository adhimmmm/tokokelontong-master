package com.example.tokokelontong.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DetailPembelian {
    private final StringProperty idDetail;
    private final StringProperty idPembelian;
    private final ObjectProperty<LocalDate> tanggalPembelian; // <<< FIELD INI
    private final StringProperty kodeBarang;
    private final StringProperty namaBarang;
    private final IntegerProperty jumlah;
    private final ObjectProperty<BigDecimal> hargaBeli;
    private final ObjectProperty<BigDecimal> subtotal;

    // KONSTRUKTOR 1 (Digunakan saat membaca dari database - semua field ada)
    public DetailPembelian(String idDetail, String idPembelian, LocalDate tanggalPembelian, String kodeBarang, String namaBarang, int jumlah, BigDecimal hargaBeli, BigDecimal subtotal) {
        this.idDetail = new SimpleStringProperty(idDetail);
        this.idPembelian = new SimpleStringProperty(idPembelian);
        this.tanggalPembelian = new SimpleObjectProperty<>(tanggalPembelian); // <<< INI
        this.kodeBarang = new SimpleStringProperty(kodeBarang);
        this.namaBarang = new SimpleStringProperty(namaBarang);
        this.jumlah = new SimpleIntegerProperty(jumlah);
        this.hargaBeli = new SimpleObjectProperty<>(hargaBeli);
        this.subtotal = new SimpleObjectProperty<>(subtotal);
    }

    // KONSTRUKTOR 2 (Digunakan saat membuat DetailPembelian BARU dari UI - ID Detail dan ID Pembelian belum ada)
    // PERHATIKAN URUTAN PARAMETERNYA: Dimulai dengan LocalDate
    public DetailPembelian(LocalDate tanggalPembelian, String kodeBarang, String namaBarang, int jumlah, BigDecimal hargaBeli, BigDecimal subtotal) {
        this(null, null, tanggalPembelian, kodeBarang, namaBarang, jumlah, hargaBeli, subtotal); // ID Detail dan ID Pembelian awal null
    }


    // Getters for JavaFX Properties
    public StringProperty idDetailProperty() { return idDetail; }
    public StringProperty idPembelianProperty() { return idPembelian; }
    public ObjectProperty<LocalDate> tanggalPembelianProperty() { return tanggalPembelian; } // <<< PROPERTY INI
    public StringProperty kodeBarangProperty() { return kodeBarang; }
    public StringProperty namaBarangProperty() { return namaBarang; }
    public IntegerProperty jumlahProperty() { return jumlah; }
    public ObjectProperty<BigDecimal> hargaBeliProperty() { return hargaBeli; }
    public ObjectProperty<BigDecimal> subtotalProperty() { return subtotal; }

    // Getters for actual values
    public String getIdDetail() { return idDetail.get(); }
    public String getIdPembelian() { return idPembelian.get(); }
    public LocalDate getTanggalPembelian() { return tanggalPembelian.get(); } // <<< GETTER INI
    public String getKodeBarang() { return kodeBarang.get(); }
    public String getNamaBarang() { return namaBarang.get(); }
    public int getJumlah() { return jumlah.get(); }
    public BigDecimal getHargaBeli() { return hargaBeli.get(); }
    public BigDecimal getSubtotal() { return subtotal.get(); }

    // Setters for actual values
    public void setIdDetail(String idDetail) { this.idDetail.set(idDetail); }
    public void setIdPembelian(String idPembelian) { this.idPembelian.set(idPembelian); }
    public void setTanggalPembelian(LocalDate tanggalPembelian) { this.tanggalPembelian.set(tanggalPembelian); } // <<< SETTER INI
    public void setKodeBarang(String kodeBarang) { this.kodeBarang.set(kodeBarang); }
    public void setNamaBarang(String namaBarang) { this.namaBarang.set(namaBarang); }
    public void setJumlah(int jumlah) { this.jumlah.set(jumlah); }
    public void setHargaBeli(BigDecimal hargaBeli) { this.hargaBeli.set(hargaBeli); }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal.set(subtotal); }
}