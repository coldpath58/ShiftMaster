package com.mycompany.shiftmaster.view;

import com.mycompany.shiftmaster.util.koneksi;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelPlottingJadwal extends JPanel {

    private JComboBox<String> cbKaryawan, cbHari, cbShift;
    private JButton btnSimpan, btnHapusJadwal;
    private JTable tblMatriks;
    private DefaultTableModel modelMatriks;

    public PanelPlottingJadwal() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. PANEL FORM ATAS (Form Plotting & Hapus Jadwal)
        JPanel panelForm = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Form Plotting & Hapus Jadwal");
        border.setTitleJustification(TitledBorder.CENTER);
        panelForm.setBorder(border);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbKaryawan = new JComboBox<>();
        cbHari = new JComboBox<>(new String[]{"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"});
        cbShift = new JComboBox<>(new String[]{"Pagi", "Sore", "Malam"});

        // Baris 1: Pilih Karyawan
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        panelForm.add(new JLabel("Pilih Karyawan:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panelForm.add(cbKaryawan, gbc);

        // Baris 2: Pilih Hari
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        panelForm.add(new JLabel("Pilih Hari:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panelForm.add(cbHari, gbc);

        // Baris 3: Pilih Shift
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        panelForm.add(new JLabel("Pilih Shift:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panelForm.add(cbShift, gbc);

        // Baris 4: Tombol Aksi
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnSimpan = new JButton("Simpan Jadwal");
        btnHapusJadwal = new JButton("Hapus Jadwal Karyawan");
        panelTombol.add(btnSimpan);
        panelTombol.add(btnHapusJadwal);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(panelTombol, gbc);

        add(panelForm, BorderLayout.NORTH);

        // 2. TABEL MATRIKS JADWAL DI BAGIAN BAWAH
        String[] kolom = {"Shift / Hari", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
        modelMatriks = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblMatriks = new JTable(modelMatriks);
        
        // Mengatur isi sel agar berada di tengah
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblMatriks.getColumnCount(); i++) {
            tblMatriks.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        ((DefaultTableCellRenderer) tblMatriks.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(tblMatriks);
        add(scrollPane, BorderLayout.CENTER);

        // Muat Data ComboBox dan Matriks
        loadDataKaryawanComboBox();
        loadMatriksJadwal();

        // Event Handler Tombol
        btnSimpan.addActionListener(e -> simpanJadwal());
        btnHapusJadwal.addActionListener(e -> hapusJadwal());
    }

    // Memuat nama-nama karyawan ke ComboBox dari database
    public void loadDataKaryawanComboBox() {
        cbKaryawan.removeAllItems();
        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nama FROM karyawan")) {
            while (rs.next()) {
                cbKaryawan.addItem(rs.getString("nama"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat daftar karyawan: " + e.getMessage());
        }
    }

    // Memuat data matriks jadwal mingguan
    public void loadMatriksJadwal() {
        modelMatriks.setRowCount(0);
        String[] shiftList = {"Pagi", "Sore", "Malam"};
        String[] hariList = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};

        for (String shift : shiftList) {
            Object[] rowData = new Object[8];
            rowData[0] = shift; // Kolom pertama nama Shift

            for (int i = 0; i < hariList.length; i++) {
                String hari = hariList[i];
                String namaKaryawan = "";

                String sql = "SELECT nama_karyawan FROM jadwal WHERE shift = ? AND hari = ?";
                try (Connection conn = koneksi.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, shift);
                    ps.setString(2, hari);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            namaKaryawan = rs.getString("nama_karyawan");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                rowData[i + 1] = namaKaryawan;
            }
            modelMatriks.addRow(rowData);
        }
    }

    // Logika Simpan Jadwal dengan Validasi Bentrok
    private void simpanJadwal() {
        if (cbKaryawan.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Belum ada data karyawan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nama = cbKaryawan.getSelectedItem().toString();
        String hari = cbHari.getSelectedItem().toString();
        String shift = cbShift.getSelectedItem().toString();

        // Cek apakah karyawan sudah punya jadwal di hari yang sama
        // atau apakah slot shift di hari tersebut sudah diisi orang lain
        try (Connection conn = koneksi.getConnection()) {
            // Cek bentrok karyawan di hari yg sama
            String cek1 = "SELECT * FROM jadwal WHERE nama_karyawan = ? AND hari = ?";
            try (PreparedStatement ps = conn.prepareStatement(cek1)) {
                ps.setString(1, nama);
                ps.setString(2, hari);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Karyawan " + nama + " sudah memiliki jadwal pada hari " + hari + "!", "Bentrok Jadwal", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Cek apakah slot shift sudah terisi
            String cek2 = "SELECT * FROM jadwal WHERE hari = ? AND shift = ?";
            try (PreparedStatement ps = conn.prepareStatement(cek2)) {
                ps.setString(1, hari);
                ps.setString(2, shift);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Slot shift " + shift + " pada hari " + hari + " sudah diisi oleh " + rs.getString("nama_karyawan") + "!", "Slot Terisi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Simpan ke database
            String insert = "INSERT INTO jadwal (nama_karyawan, hari, shift) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, nama);
                ps.setString(2, hari);
                ps.setString(3, shift);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Jadwal berhasil disimpan!");
                loadMatriksJadwal();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan jadwal: " + e.getMessage());
        }
    }

    // Logika Hapus Jadwal Karyawan berdasarkan pilihan Form
    private void hapusJadwal() {
        if (cbKaryawan.getItemCount() == 0) return;
        
        String nama = cbKaryawan.getSelectedItem().toString();
        String hari = cbHari.getSelectedItem().toString();
        String shift = cbShift.getSelectedItem().toString();

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Hapus jadwal " + nama + " (" + hari + " - " + shift + ")?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM jadwal WHERE nama_karyawan = ? AND hari = ? AND shift = ?";
            try (Connection conn = koneksi.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nama);
                ps.setString(2, hari);
                ps.setString(3, shift);
                int rows = ps.executeUpdate();
                
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Jadwal berhasil dihapus!");
                    loadMatriksJadwal();
                } else {
                    JOptionPane.showMessageDialog(this, "Jadwal yang cocok tidak ditemukan di database.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus jadwal: " + e.getMessage());
            }
        }
    }
}