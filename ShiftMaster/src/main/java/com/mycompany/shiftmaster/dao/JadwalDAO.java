package com.mycompany.shiftmaster.dao;

import com.mycompany.shiftmaster.model.JadwalModel;
import com.mycompany.shiftmaster.util.koneksi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JadwalDAO {

    // Mengambil seluruh data jadwal dari database
    public List<JadwalModel> getAllJadwal() {
        List<JadwalModel> list = new ArrayList<>();
        String sql = "SELECT * FROM jadwal";
        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new JadwalModel(
                    rs.getInt("id"),
                    rs.getString("nama_karyawan"),
                    rs.getString("hari"),
                    rs.getString("shift")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Melakukan proses tukar shift dengan transaksi COMMIT & ROLLBACK yang aman[cite: 14]
    public boolean tukarShift(int id1, int id2) throws SQLException {
        Connection conn = null;
        try {
            conn = koneksi.getConnection();
            conn.setAutoCommit(false); // Memulai transaksi database[cite: 14]

            // 1. Ambil nama karyawan berdasarkan ID 1 dan ID 2
            String nama1 = "", nama2 = "";
            String sqlGet = "SELECT nama_karyawan FROM jadwal WHERE id = ?";

            try (PreparedStatement psGet = conn.prepareStatement(sqlGet)) {
                psGet.setInt(1, id1);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) nama1 = rs.getString("nama_karyawan");
                }
                psGet.setInt(1, id2);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) nama2 = rs.getString("nama_karyawan");
                }
            }

            // 2. Update silang nama karyawan pada kedua ID jadwal tersebut
            String sqlUpdate = "UPDATE jadwal SET nama_karyawan = ? WHERE id = ?";
            try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdate)) {
                psUpd.setString(1, nama2);
                psUpd.setInt(2, id1);
                psUpd.executeUpdate();

                psUpd.setString(1, nama1);
                psUpd.setInt(2, id2);
                psUpd.executeUpdate();
            }

            conn.commit(); // Jika sukses, simpan permanen[cite: 14]
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Batalkan jika terjadi kegagalan[cite: 14]
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}