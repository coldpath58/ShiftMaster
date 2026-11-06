package com.mycompany.shiftmaster.view;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {

    public MainApp() {
        setTitle("ShiftMaster - Aplikasi Manajemen Shift & Karyawan");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Membuat JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Form Karyawan
        FormKaryawan panelKaryawan = new FormKaryawan();
        tabbedPane.addTab("1. Karyawan", panelKaryawan.getContentPane());

        // Tab 2: Panel Plotting Jadwal
        PanelPlottingJadwal panelPlotting = new PanelPlottingJadwal();
        tabbedPane.addTab("2. Plotting Jadwal", panelPlotting);

        // --- TAMBAHKAN KODE INI ---
        // Otomatis refresh data karyawan di tab 2 setiap kali tab tersebut diklik/dibuka
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) { // Indeks 1 adalah tab "2. Plotting Jadwal"
                panelPlotting.loadDataKaryawanComboBox();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}