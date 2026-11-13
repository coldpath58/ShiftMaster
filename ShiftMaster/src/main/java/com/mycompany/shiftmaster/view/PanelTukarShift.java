package com.mycompany.shiftmaster.view;

import com.mycompany.shiftmaster.model.JadwalModel;
import com.mycompany.shiftmaster.service.JadwalService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelTukarShift extends JPanel {

    private JComboBox<String> cbJadwal1, cbJadwal2;
    private JButton btnKonfirmasiTukar;
    private JTable tblJadwalAktif;
    private DefaultTableModel modelJadwal;

    // Memanggil JadwalService (Menghilangkan koneksi & SQL langsung dari View)
    private JadwalService jadwalService = new JadwalService();

    public PanelTukarShift() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. PANEL FORM ATAS (Form Tukar Shift)
        JPanel panelForm = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Form Tukar Shift Karyawan");
        border.setTitleJustification(TitledBorder.CENTER);
        panelForm.setBorder(border);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbJadwal1 = new JComboBox<>();
        cbJadwal2 = new JComboBox<>();

        // Baris 1: Pilih Jadwal 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        panelForm.add(new JLabel("Pilih Jadwal 1:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panelForm.add(cbJadwal1, gbc);

        // Baris 2: Pilih Jadwal 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        panelForm.add(new JLabel("Pilih Jadwal 2:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panelForm.add(cbJadwal2, gbc);

        // Baris 3: Tombol Konfirmasi Tukar
        btnKonfirmasiTukar = new JButton("Konfirmasi Tukar");
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTombol.add(btnKonfirmasiTukar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelForm.add(panelTombol, gbc);

        add(panelForm, BorderLayout.NORTH);

        // 2. TABEL DAFTAR JADWAL AKTIF DI BAGIAN BAWAH
        String[] kolom = {"ID", "Nama Karyawan", "Hari", "Shift"};
        modelJadwal = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblJadwalAktif = new JTable(modelJadwal);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblJadwalAktif.getColumnCount(); i++) {
            tblJadwalAktif.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        ((DefaultTableCellRenderer) tblJadwalAktif.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(tblJadwalAktif);
        TitledBorder borderTabel = BorderFactory.createTitledBorder("Daftar Jadwal Aktif Saat Ini");
        borderTabel.setTitleJustification(TitledBorder.CENTER);
        scrollPane.setBorder(borderTabel);
        add(scrollPane, BorderLayout.CENTER);

        // Muat data awal
        loadDataJadwal();

        // Event Handler Tombol Konfirmasi Tukar
        btnKonfirmasiTukar.addActionListener(e -> tukarShiftData());
    }

    // Memuat data menggunakan JadwalService
    public void loadDataJadwal() {
        modelJadwal.setRowCount(0);
        cbJadwal1.removeAllItems();
        cbJadwal2.removeAllItems();

        List<JadwalModel> listJadwal = jadwalService.getDaftarJadwal();
        for (JadwalModel j : listJadwal) {
            // Masukkan ke JTable
            modelJadwal.addRow(new Object[]{j.getId(), j.getNamaKaryawan(), j.getHari(), j.getShift()});

            // Format item ComboBox
            String itemCombo = "ID: " + j.getId() + " | " + j.getNamaKaryawan() + " (" + j.getHari() + " - " + j.getShift() + ")";
            cbJadwal1.addItem(itemCombo);
            cbJadwal2.addItem(itemCombo);
        }
    }

    // Logika Pemrosesan Tukar Shift via Service
    private void tukarShiftData() {
        if (cbJadwal1.getItemCount() == 0 || cbJadwal2.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Data jadwal belum tersedia untuk ditukar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String item1 = (String) cbJadwal1.getSelectedItem();
        String item2 = (String) cbJadwal2.getSelectedItem();

        if (item1 == null || item2 == null) return;

        int id1 = extractId(item1);
        int id2 = extractId(item2);

        if (id1 == id2) {
            JOptionPane.showMessageDialog(this, "Tidak dapat menukar jadwal dengan item yang sama!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menukar posisi kedua shift karyawan tersebut?", "Konfirmasi Tukar Shift", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        // Panggil fungsi proses dari Service
        boolean sukses = jadwalService.prosesTukarShift(id1, id2);
        if (sukses) {
            JOptionPane.showMessageDialog(this, "Shift karyawan berhasil ditukar!");
            loadDataJadwal();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menukar shift!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int extractId(String comboItem) {
        try {
            String sub = comboItem.split(" \\| ")[0];
            return Integer.parseInt(sub.replace("ID: ", "").trim());
        } catch (Exception e) {
            return -1;
        }
    }
}