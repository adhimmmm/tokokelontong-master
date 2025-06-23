package com.example.tokokelontong.model;

public class Pegawai {
    private String idPegawai;
    private String nama;
    private String alamat;
    private String noTelepon;
    private String jabatan;

    public Pegawai() {
    }

    public Pegawai(String idPegawai, String nama, String alamat, String noTelepon, String jabatan) {
        this.idPegawai = idPegawai;
        this.nama = nama;
        this.alamat = alamat;
        this.noTelepon = noTelepon;
        this.jabatan = jabatan;
    }

    public String getIdPegawai() {
        return idPegawai;
    }

    public void setIdPegawai(String idPegawai) {
        this.idPegawai = idPegawai;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    @Override
    public String toString() {
        return nama + " - " + jabatan;
    }
}
