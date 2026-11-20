package com.mycompany.shiftmaster.view;

import com.mycompany.shiftmaster.controller.KaryawanController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class FormKaryawan extends JFrame {

    // Komponen Form Input
    private JTextField txtNama, txtNoHP;
    private JComboBox<String> cbJabatan;
    private JButton btnTambah, btnEdit, btnHapus, btnBersihkan, btnCetak;

    // Komponen Tabel
    private JTable tblKaryawan;
    private DefaultTableModel modelKaryawan;

    // Panggil Controller
    private KaryawanController controller = new KaryawanController();
    private int selectedId = -1;

    // Spring ApplicationContext
    private ApplicationContext context;

    public FormKaryawan() {
        try {
            context = new ClassPathXmlApplicationContext("applicationContext.xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Konfigurasi Window
        setTitle("ShiftMaster - Manajemen Data Karyawan");
        setSize(780, 480);
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
        btnCetak = new JButton("Cetak Laporan");

        panelTombol.add(btnTambah);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);
        panelTombol.add(btnBersihkan);
        panelTombol.add(btnCetak);

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

        // 4. EVENT HANDLER & LOAD DATA DARI CONTROLLER
        initEventHandlers();
        controller.loadDataTabel(modelKaryawan);
    }

    private void initEventHandlers() {
        btnTambah.addActionListener(e -> tambahData());
        btnEdit.addActionListener(e -> editData());
        btnHapus.addActionListener(e -> hapusData());
        btnBersihkan.addActionListener(e -> bersihkanForm());
        btnCetak.addActionListener(this::reportActionPerformed);

        // Event Klik Baris Tabel
        tblKaryawan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblKaryawan.getSelectedRow();
                if (row != -1) {
                    selectedId = Integer.parseInt(modelKaryawan.getValueAt(row, 0).toString());
                    txtNama.setText(modelKaryawan.getValueAt(row, 1).toString());
                    cbJabatan.setSelectedItem(modelKaryawan.getValueAt(row, 2).toString());
                    txtNoHP.setText(modelKaryawan.getValueAt(row, 3).toString());
                }
            }
        });
    }

    private void reportActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (context == null) {
                context = new ClassPathXmlApplicationContext("applicationContext.xml");
            }

            // 1. Lokasi file .jrxml (sesuai struktur proyek Maven)
            String reportPath = "src/main/java/reports/Simple_Blue.jrxml";

            // 2. Compile file .jrxml terlebih dahulu
            JasperReport jr = JasperCompileManager.compileReport(reportPath);

            // 3. Koneksi database dari Spring DataSource
            DataSource ds = context.getBean(DataSource.class);
            try (Connection conn = ds.getConnection()) {

                // 4. Parameter (kosongkan jika tidak ada)
                Map<String, Object> param = new HashMap<>();

                // 5. Isi laporan (masukkan objek 'jr', bukan String path)
                JasperPrint jp = JasperFillManager.fillReport(jr, param, conn);

                // 6. Tampilkan laporan
                JasperViewer.viewReport(jp, false);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mencetak laporan: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void tambahData() {
        String nama = kapitalAwal(txtNama.getText().trim());
        String noHP = txtNoHP.getText().trim();
        String jabatan = cbJabatan.getSelectedItem().toString();

        if (nama.isEmpty() || noHP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan No. HP wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!noHP.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Nomor HP hanya boleh berisi angka!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (controller.tambahKaryawan(nama, jabatan, noHP)) {
            JOptionPane.showMessageDialog(this, "Data Karyawan Berhasil Ditambahkan!");
            controller.loadDataTabel(modelKaryawan);
            bersihkanForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambah data ke database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editData() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris karyawan pada tabel yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nama = kapitalAwal(txtNama.getText().trim());
        String noHP = txtNoHP.getText().trim();
        String jabatan = cbJabatan.getSelectedItem().toString();

        if (!noHP.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Nomor HP hanya boleh berisi angka!", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (controller.editKaryawan(selectedId, nama, jabatan, noHP)) {
            JOptionPane.showMessageDialog(this, "Data Karyawan Berhasil Diperbarui!");
            controller.loadDataTabel(modelKaryawan);
            bersihkanForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusData() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris karyawan pada tabel yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus karyawan ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (controller.hapusKaryawan(selectedId)) {
                JOptionPane.showMessageDialog(this, "Data Karyawan Berhasil Dihapus!");
                controller.loadDataTabel(modelKaryawan);
                bersihkanForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void bersihkanForm() {
        txtNama.setText("");
        txtNoHP.setText("");
        cbJabatan.setSelectedIndex(0);
        selectedId = -1;
        tblKaryawan.clearSelection();
        txtNama.requestFocusInWindow();
    }

    private String kapitalAwal(String kata) {
        if (kata == null || kata.isEmpty()) return kata;
        return kata.substring(0, 1).toUpperCase() + kata.substring(1).toLowerCase();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FormKaryawan().setVisible(true);
        });
    }
}