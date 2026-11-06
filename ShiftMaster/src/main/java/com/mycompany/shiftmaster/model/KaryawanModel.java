package com.mycompany.shiftmaster.model;

public class KaryawanModel {
    private int id;
    private String nama;
    private String jabatan;
    private String noHp;

    // Konstruktor
    public KaryawanModel(int id, String nama, String jabatan, String noHp) {
        this.id = id;
        this.nama = nama;
        this.jabatan = jabatan;
        this.noHp = noHp;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJabatan() { return jabatan; }
    public void setJabatan(String jabatan) { this.jabatan = jabatan; }

    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }
}