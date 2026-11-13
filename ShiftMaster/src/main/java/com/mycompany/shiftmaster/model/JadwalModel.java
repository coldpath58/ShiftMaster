package com.mycompany.shiftmaster.model;

public class JadwalModel {
    private int id;
    private String namaKaryawan;
    private String hari;
    private String shift;

    public JadwalModel() {}

    public JadwalModel(int id, String namaKaryawan, String hari, String shift) {
        this.id = id;
        this.namaKaryawan = namaKaryawan;
        this.hari = hari;
        this.shift = shift;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNamaKaryawan() { return namaKaryawan; }
    public void setNamaKaryawan(String namaKaryawan) { this.namaKaryawan = namaKaryawan; }

    public String getHari() { return hari; }
    public void setHari(String hari) { this.hari = hari; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
}