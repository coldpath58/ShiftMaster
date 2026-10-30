package com.mycompany.ShiftMaster;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FormKaryawan extends JFrame {

    // Komponen Form Input
    private JTextField txtNama, txtNoHP;
    private JComboBox<String> cbJabatan;
    private JButton btnTambah, btnEdit, btnHapus, btnBersihkan;

    // Komponen Tabel
    private JTable tblKaryawan;
    private DefaultTableModel modelKaryawan;

    // Variabel Penanda ID & Baris Terpilih
    private int idCounter = 1;
    private int selectedRow = -1;

    public FormKaryawan() {
        // Konfigurasi Window
        setTitle("ShiftMaster - Manajemen Data Karyawan");
        setSize(750, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. PANEL FORM INPUT (Judul Border di Tengah)
        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        
        TitledBorder borderForm = BorderFactory.createTitledBorder("Input Data Karyawan");
        borderForm.setTitleJustification(TitledBorder.CENTER);
        panelForm.setBorder(borderForm);

        txtNama = new JTextField();
        txtNoHP = new JTextField();
        cbJabatan = new JComboBox<>(new String[]{"Kasir", "Barista", "Kitchen"});

        panelForm.add(new JLabel("Nama Karyawan:"));
        panelForm.add(txtNama);
        panelForm.add(new JLabel("Jabatan:"));
        panelForm.add(cbJabatan);
        panelForm.add(new JLabel("No. HP:"));
        panelForm.add(txtNoHP);

        // 2. PANEL TOMBOL
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnTambah = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnBersihkan = new JButton("Bersihkan");

        panelTombol.add(btnTambah);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);
        panelTombol.add(btnBersihkan);

        // Penggabungan Panel Form dan Tombol
        JPanel panelAtas = new JPanel(new BorderLayout(5, 5));
        panelAtas.add(panelForm, BorderLayout.CENTER);
        panelAtas.add(panelTombol, BorderLayout.SOUTH);

        // 3. TABEL DATA KARYAWAN
        String[] kolom = {"ID", "Nama", "Jabatan", "No. HP"};
        modelKaryawan = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblKaryawan = new JTable(modelKaryawan);

        // Mengetengahkan Isi Sel dan Header Tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < tblKaryawan.getColumnCount(); i++) {
            tblKaryawan.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        ((DefaultTableCellRenderer) tblKaryawan.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(tblKaryawan);

        // Menambahkan Komponen ke Window Utama
        add(panelAtas, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 4. EVENT HANDLER / LOGIKA TOMBOL
        initEventHandlers();
    }

    private void initEventHandlers() {
        btnTambah.addActionListener(e -> tambahData());
        btnEdit.addActionListener(e -> editData());
        btnHapus.addActionListener(e -> hapusData());
        btnBersihkan.addActionListener(e -> bersihkanForm());

        // Event Klik Baris Tabel
        tblKaryawan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRow = tblKaryawan.getSelectedRow();
                if (selectedRow != -1) {
                    txtNama.setText(modelKaryawan.getValueAt(selectedRow, 1).toString());
                    cbJabatan.setSelectedItem(modelKaryawan.getValueAt(selectedRow, 2).toString());
                    txtNoHP.setText(modelKaryawan.getValueAt(selectedRow, 3).toString());
                }
            }
        });
    }

    private void tambahData() {
        String nama = kapitalAwal(txtNama.getText().trim());
        String noHP = txtNoHP.getText().trim();

        if (nama.isEmpty() || noHP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan No. HP wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!noHP.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Nomor HP hanya boleh berisi angka!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        modelKaryawan.addRow(new Object[]{
            idCounter++,
            nama,
            cbJabatan.getSelectedItem().toString(),
            noHP
        });

        bersihkanForm();
        JOptionPane.showMessageDialog(this, "Data Karyawan Berhasil Ditambahkan!");
    }

    private void editData() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris karyawan pada tabel yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nama = kapitalAwal(txtNama.getText().trim());
        String noHP = txtNoHP.getText().trim();

        if (!noHP.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Nomor HP hanya boleh berisi angka!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        modelKaryawan.setValueAt(nama, selectedRow, 1);
        modelKaryawan.setValueAt(cbJabatan.getSelectedItem().toString(), selectedRow, 2);
        modelKaryawan.setValueAt(noHP, selectedRow, 3);

        bersihkanForm();
        JOptionPane.showMessageDialog(this, "Data Karyawan Berhasil Diperbarui!");
    }

    private void hapusData() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris karyawan pada tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus karyawan ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            modelKaryawan.removeRow(selectedRow);
            urutkanUlangID();
            bersihkanForm();
            JOptionPane.showMessageDialog(this, "Data Karyawan Berhasil Dihapus!");
        }
    }

    private void urutkanUlangID() {
        for (int i = 0; i < modelKaryawan.getRowCount(); i++) {
            modelKaryawan.setValueAt(i + 1, i, 0);
        }
        idCounter = modelKaryawan.getRowCount() + 1;
    }

    // --- METODE BERSIHKAN FORM (DIPERBAIKI) ---
    private void bersihkanForm() {
        txtNama.setText("");
        txtNoHP.setText("");
        cbJabatan.setSelectedIndex(0);
        
        selectedRow = -1;
        tblKaryawan.clearSelection();
        tblKaryawan.getSelectionModel().clearSelection();
        
        txtNama.requestFocusInWindow();
    }

    private String kapitalAwal(String kata) {
        if (kata == null || kata.isEmpty()) return kata;
        return kata.substring(0, 1).toUpperCase() + kata.substring(1).toLowerCase();
    }

    public static void main(String[] args) {
        // Disederhanakan tanpa lambda agar NetBeans tidak salah membaca kelas $1
        FormKaryawan frame = new FormKaryawan();
        frame.setVisible(true);
    }
}