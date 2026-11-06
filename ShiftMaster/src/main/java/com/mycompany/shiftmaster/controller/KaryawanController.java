package com.mycompany.shiftmaster.controller;

import com.mycompany.shiftmaster.util.koneksi;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class KaryawanController {

    // Mengambil data karyawan dari database untuk dimasukkan ke JTable
    public void loadDataTabel(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT * FROM karyawan";
        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("jabatan"),
                    rs.getString("no_hp")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Menambah data karyawan baru
    public boolean tambahKaryawan(String nama, String jabatan, String noHp) {
        String sql = "INSERT INTO karyawan (nama, jabatan, no_hp) VALUES (?, ?, ?)";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, jabatan);
            ps.setString(3, noHp);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mengubah/Edit data karyawan
    public boolean editKaryawan(int id, String nama, String jabatan, String noHp) {
        String sql = "UPDATE karyawan SET nama = ?, jabatan = ?, no_hp = ? WHERE id = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, jabatan);
            ps.setString(3, noHp);
            ps.setInt(4, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Menghapus data karyawan berdasarkan ID
    public boolean hapusKaryawan(int id) {
        String sql = "DELETE FROM karyawan WHERE id = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}