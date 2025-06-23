package com.example.tokokelontong.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Pembelian {
    private final StringProperty idPembelian;
    private final ObjectProperty<LocalDate> tanggal;
    private final StringProperty idSuplier;
    private final StringProperty namaSuplier;
    private final ObjectProperty<BigDecimal> total;

    // Constructor untuk data yang berasal dari database (ID sudah ada)
    public Pembelian(String idPembelian, LocalDate tanggal, String idSuplier, String namaSuplier, BigDecimal total) {
        this.idPembelian = new SimpleStringProperty(idPembelian);
        this.tanggal = new SimpleObjectProperty<>(tanggal);
        this.idSuplier = new SimpleStringProperty(idSuplier);
        this.namaSuplier = new SimpleStringProperty(namaSuplier);
        this.total = new SimpleObjectProperty<>(total);
    }

    // Constructor untuk data BARU yang akan di-insert (ID akan digenerate oleh DB)
    public Pembelian(LocalDate tanggal, String idSuplier, String namaSuplier, BigDecimal total) {
        this(null, tanggal, idSuplier, namaSuplier, total); // ID akan diisi setelah insert DB
    }

    // Getters for properties
    public StringProperty idPembelianProperty() { return idPembelian; }
    public ObjectProperty<LocalDate> tanggalProperty() { return tanggal; }
    public StringProperty idSuplierProperty() { return idSuplier; }
    public StringProperty namaSuplierProperty() { return namaSuplier; }
    public ObjectProperty<BigDecimal> totalProperty() { return total; }

    // Getters for values
    public String getIdPembelian() { return idPembelian.get(); }
    public LocalDate getTanggal() { return tanggal.get(); }
    public String getIdSuplier() { return idSuplier.get(); }
    public String getNamaSuplier() { return namaSuplier.get(); }
    public BigDecimal getTotal() { return total.get(); }

    // SETTER PENTING: Untuk mengisi ID setelah digenerate oleh database
    public void setIdPembelian(String idPembelian) { this.idPembelian.set(idPembelian); }
    public void setTanggal(LocalDate tanggal) { this.tanggal.set(tanggal); }
    public void setIdSuplier(String idSuplier) { this.idSuplier.set(idSuplier); }
    public void setNamaSuplier(String namaSuplier) { this.namaSuplier.set(namaSuplier); }
    public void setTotal(BigDecimal total) { this.total.set(total); }
}