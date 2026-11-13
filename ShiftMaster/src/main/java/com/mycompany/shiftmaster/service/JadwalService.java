package com.mycompany.shiftmaster.service;

import com.mycompany.shiftmaster.dao.JadwalDAO;
import com.mycompany.shiftmaster.model.JadwalModel;

import java.sql.SQLException;
import java.util.List;

public class JadwalService {
    private JadwalDAO jadwalDAO = new JadwalDAO();

    // Mengambil daftar jadwal
    public List<JadwalModel> getDaftarJadwal() {
        return jadwalDAO.getAllJadwal();
    }

    // Layanan untuk mengeksekusi tukar shift
    boolean statusTukar = false;
    public boolean prosesTukarShift(int id1, int id2) {
        try {
            return jadwalDAO.tukarShift(id1, id2);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}