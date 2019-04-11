package com.nue.crudgambar;

public class GambarModel {

    String nama , url;

    public GambarModel(){}

    public GambarModel(String nama, String url) {
        this.nama = nama;
        this.url = url;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
