package com.mycompany.shiftmaster.view;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {

    public MainApp() {
        setTitle("ShiftMaster - Aplikasi Manajemen Shift & Karyawan");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Membuat JTabbedPane utama
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Form Karyawan
        FormKaryawan panelKaryawan = new FormKaryawan();
        tabbedPane.addTab("1. Karyawan", panelKaryawan.getContentPane());

        // Tab 2: Panel Plotting Jadwal
        PanelPlottingJadwal panelPlotting = new PanelPlottingJadwal();
        tabbedPane.addTab("2. Plotting Jadwal", panelPlotting);

        // Tab 3: Panel Tukar Shift
        PanelTukarShift panelTukarShift = new PanelTukarShift();
        tabbedPane.addTab("3. Tukar Shift", panelTukarShift);

        // Listener untuk Auto-Refresh data saat berpindah tab
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            if (index == 1) { // Tab 2: Plotting Jadwal
                panelPlotting.loadDataKaryawanComboBox();
                panelPlotting.loadMatriksJadwal();
            } else if (index == 2) { // Tab 3: Tukar Shift
                panelTukarShift.loadDataJadwal();
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