package com.nikxphreaker.suryakost.Model;

import java.io.Serializable;

public class Kamar implements Serializable {
    private String nomor;
    private String lokasi;
    private String fasilitas;
    private String luas;
    private String harga;
    private String gambar;
    private String tersedia;
    private String key;

    public Kamar(String nomor,String lokasi, String fasilitas, String luas, String harga, String gambar, String tersedia) {
        this.nomor = nomor;
        this.lokasi = lokasi;
        this.fasilitas = fasilitas;
        this.luas = luas;
        this.harga = harga;
        this.gambar = gambar;
        this.tersedia = tersedia;
    }

    public Kamar(){
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public void setFasilitas(String fasilitas) {
        this.fasilitas = fasilitas;
    }

    public String getLuas() {
        return luas;
    }

    public void setLuas(String luas) {
        this.luas = luas;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getTersedia() {
        return tersedia;
    }

    public void setTersedia(String tersedia) {
        this.tersedia = tersedia;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
