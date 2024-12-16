package com.example.mobileptc;

public class HelperClass {
    String nama, alamat, email, pass, konfPass, id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HelperClass(String id, String nama, String alamat, String email, String pass, String konfPass) {
        this.nama = nama;
        this.alamat = alamat;
        this.email = email;
        this.pass = pass;
        this.konfPass = konfPass;
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getKonfPass() {
        return konfPass;
    }

    public void setKonfPass(String konfPass) {
        this.konfPass = konfPass;
    }


}
