package com.nikxphreaker.suryakost.Model;

import java.io.Serializable;

public class Pembayaran implements Serializable {
    private String id_user;
    private String id_kamar;
    private String status;
    private String tgl_pembayaran;
    private String bulan_depan;
    private String harga;
    private String sisa_bayar;
    private String struk;
    private String key;

    public Pembayaran(String id_user, String id_kamar, String status, String tgl_pembayaran, String bulan_depan, String harga, String struk, String sisa_bayar) {
        this.id_user = id_user;
        this.id_kamar = id_kamar;
        this.status = status;
        this.tgl_pembayaran = tgl_pembayaran;
        this.bulan_depan = bulan_depan;
        this.sisa_bayar = sisa_bayar;
        this.struk = struk;
        this.harga = harga;
    }

    public Pembayaran(){

    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_kamar() {
        return id_kamar;
    }

    public void setId_kamar(String id_kamar) {
        this.id_kamar = id_kamar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTgl_pembayaran() {
        return tgl_pembayaran;
    }

    public void setTgl_pembayaran(String tgl_pembayaran) {
        this.tgl_pembayaran = tgl_pembayaran;
    }

    public String getBulan_depan() {
        return bulan_depan;
    }

    public void setBulan_depan(String bulan_depan) {
        this.bulan_depan = bulan_depan;
    }

    public String getSisa_bayar() {
        return sisa_bayar;
    }

    public void setSisa_bayar(String sisa_bayar) {
        this.sisa_bayar = sisa_bayar;
    }

    public String getStruk() {
        return struk;
    }

    public void setStruk(String struk) {
        this.struk = struk;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
