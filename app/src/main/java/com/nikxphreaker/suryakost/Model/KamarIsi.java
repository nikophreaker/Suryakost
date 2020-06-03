package com.nikxphreaker.suryakost.Model;

import java.io.Serializable;

public class KamarIsi implements Serializable {
    private String id_kamar;
    private String id_user;
    private String id_pembayaran;
    private String tgl_masuk;
    private String sisa_waktu;
    private String key;

    public KamarIsi(String id_kamar, String id_user, String id_pembayaran, String tgl_masuk, String sisa_waktu) {
        this.id_kamar = id_kamar;
        this.id_user = id_user;
        this.id_pembayaran = id_pembayaran;
        this.tgl_masuk = tgl_masuk;
        this.sisa_waktu = sisa_waktu;
    }

    public KamarIsi(){
    }

    public String getId_kamar() {
        return id_kamar;
    }

    public void setId_kamar(String id_kamar) {
        this.id_kamar = id_kamar;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_pembayaran() {
        return id_pembayaran;
    }

    public void setId_pembayaran(String id_pembayaran) {
        this.id_pembayaran = id_pembayaran;
    }

    public String getTgl_masuk() {
        return tgl_masuk;
    }

    public void setTgl_masuk(String tgl_masuk) {
        this.tgl_masuk = tgl_masuk;
    }

    public String getSisa_waktu() {
        return sisa_waktu;
    }

    public void setSisa_waktu(String sisa_waktu) {
        this.sisa_waktu = sisa_waktu;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
