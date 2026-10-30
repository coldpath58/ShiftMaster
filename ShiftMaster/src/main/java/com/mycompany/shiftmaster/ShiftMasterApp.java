package com.mycompany.shiftmaster;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShiftMasterApp {

    // Struktur Data
    static class Karyawan {
        int id; String nama, jabatan, noHp;
        public Karyawan(int id, String nama, String jabatan, String noHp) {
            this.id = id; this.nama = nama; this.jabatan = jabatan; this.noHp = noHp;
        }
    }

    static class Jadwal {
        int id; String namaKaryawan, hari, shift;
        public Jadwal(int id, String namaKaryawan, String hari, String shift) {
            this.id = id; this.namaKaryawan = namaKaryawan; this.hari = hari; this.shift = shift;
        }
    }

    private static List<Karyawan> listKaryawan = new ArrayList<>();
    private static List<Jadwal> listJadwal = new ArrayList<>();
    private static int karyawanIdCounter = 1;
    private static int jadwalIdCounter = 1;

    private static String kapitalAwal(String kata) {
        if (kata == null || kata.isEmpty()) return kata;
        return kata.substring(0, 1).toUpperCase() + kata.substring(1).toLowerCase();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean berjalan = true;

        // Data Awal
        listKaryawan.add(new Karyawan(karyawanIdCounter++, "Budi", "Barista", "08123456789"));
        listKaryawan.add(new Karyawan(karyawanIdCounter++, "Siti", "Kasir", "08987654321"));

        while (berjalan) {
            System.out.println("\n=================================");
            System.out.println("   ShiftMaster - Menu Utama      ");
            System.out.println("=================================");
            System.out.println("1. Manajemen Data Karyawan");
            System.out.println("2. Plotting Jadwal Mingguan");
            System.out.println("3. Tukar Shift Karyawan");
            System.out.println("4. Ekspor Jadwal ke File (.txt)");
            System.out.println("5. Keluar Aplikasi");
            System.out.println("=================================");
            System.out.print("Pilih menu (1-5): ");

            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1": jalankanFiturKaryawan(scanner); break;
                case "2": jalankanFiturJadwal(scanner); break;
                case "3": jalankanFiturTukarShift(scanner); break;
                case "4": jalankanFiturEkspor(); break;
                case "5":
                    System.out.println("\n[Sistem] Keluar dari aplikasi. Terima kasih!");
                    berjalan = false; break;
                default: System.out.println("\n[Peringatan] Pilihan tidak valid!");
            }
        }
        scanner.close();
    }

    // --- FITUR 1: MANAJEMEN KARYAWAN ---
    private static void jalankanFiturKaryawan(Scanner scanner) {
        boolean diMenu = true;
        while (diMenu) {
            System.out.println("\n--- MANAJEMEN KARYAWAN ---");
            System.out.println("1. Lihat Daftar Karyawan\n2. Tambah Karyawan\n3. Hapus Karyawan\n4. Kembali");
            System.out.print("Pilih: ");
            String pil = scanner.nextLine().trim();

            if (pil.equals("1")) tampilKaryawan();
            else if (pil.equals("2")) {
                System.out.print("Masukkan Nama: ");
                String nama = kapitalAwal(scanner.nextLine().trim());
                String jabatan = "", noHp = "";
                
                while (true) {
                    System.out.print("Masukkan Jabatan (Kasir/Barista/Kitchen): ");
                    jabatan = scanner.nextLine().trim();
                    if (jabatan.equalsIgnoreCase("Kasir") || jabatan.equalsIgnoreCase("Barista") || jabatan.equalsIgnoreCase("Kitchen")) {
                        jabatan = kapitalAwal(jabatan); break;
                    }
                    System.out.println("[Error] Jabatan tidak valid!");
                }
                while (true) {
                    System.out.print("Masukkan No HP (Angka): ");
                    noHp = scanner.nextLine().trim();
                    if (noHp.matches("\\d+")) break;
                    System.out.println("[Error] No HP hanya angka!");
                }
                listKaryawan.add(new Karyawan(karyawanIdCounter++, nama, jabatan, noHp));
                System.out.println("[Sukses] Karyawan ditambahkan!");
            } else if (pil.equals("3")) {
                tampilKaryawan();
                System.out.print("Masukkan ID Karyawan yang dihapus: ");
                try {
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    if (listKaryawan.removeIf(k -> k.id == id)) {
                        int newId = 1;
                        for (Karyawan k : listKaryawan) k.id = newId++;
                        karyawanIdCounter = newId;
                        System.out.println("[Sukses] Karyawan dihapus!");
                    } else System.out.println("[Error] ID tidak ditemukan.");
                } catch (Exception e) { System.out.println("[Error] ID harus angka!"); }
            } else if (pil.equals("4")) diMenu = false;
        }
    }

    private static void tampilKaryawan() {
        System.out.println("\nDAFTAR KARYAWAN:");
        if (listKaryawan.isEmpty()) { System.out.println("(Kosong)"); return; }
        System.out.printf("%-5s | %-15s | %-10s | %-15s\n", "ID", "Nama", "Jabatan", "No HP");
        System.out.println("---------------------------------------------------------");
        for (Karyawan k : listKaryawan) System.out.printf("%-5d | %-15s | %-10s | %-15s\n", k.id, k.nama, k.jabatan, k.noHp);
    }

    // --- FITUR 2: PLOTTING JADWAL ---
    private static void jalankanFiturJadwal(Scanner scanner) {
        boolean diMenu = true;
        while (diMenu) {
            System.out.println("\n--- PLOTTING JADWAL ---");
            System.out.println("1. Lihat Matriks Jadwal\n2. Tambah Jadwal\n3. Kosongkan Jadwal\n4. Kembali");
            System.out.print("Pilih: ");
            String pil = scanner.nextLine().trim();

            if (pil.equals("1")) cetakMatriksJadwal(false);
            else if (pil.equals("2")) {
                tampilKaryawan();
                if (listKaryawan.isEmpty()) continue;
                try {
                    System.out.print("Masukkan ID Karyawan: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    Karyawan terpilih = null;
                    for (Karyawan k : listKaryawan) if (k.id == id) terpilih = k;
                    if (terpilih == null) { System.out.println("[Error] Tidak ditemukan!"); continue; }

                    System.out.print("Masukkan Hari (Senin-Minggu): ");
                    String hari = kapitalAwal(scanner.nextLine().trim());
                    System.out.print("Masukkan Shift (Pagi/Sore/Malam): ");
                    String shift = kapitalAwal(scanner.nextLine().trim());

                    boolean bentrok = false, terisi = false;
                    String pengisi = "";
                    for (Jadwal j : listJadwal) {
                        if (j.namaKaryawan.equalsIgnoreCase(terpilih.nama) && j.hari.equalsIgnoreCase(hari)) bentrok = true;
                        if (j.hari.equalsIgnoreCase(hari) && j.shift.equalsIgnoreCase(shift)) { terisi = true; pengisi = j.namaKaryawan; }
                    }

                    if (bentrok) System.out.println("[Bentrok] Karyawan sudah dijadwalkan di hari " + hari + "!");
                    else if (terisi) System.out.println("[Bentrok] Slot sudah diisi oleh " + pengisi + "!");
                    else {
                        listJadwal.add(new Jadwal(jadwalIdCounter++, terpilih.nama, hari, shift));
                        System.out.println("[Sukses] Jadwal ditambahkan!");
                    }
                } catch (Exception e) { System.out.println("[Error] Input tidak valid!"); }
            } else if (pil.equals("3")) { listJadwal.clear(); System.out.println("[Sukses] Dikosongkan!"); }
            else if (pil.equals("4")) diMenu = false;
        }
    }

    private static String cetakMatriksJadwal(boolean returnStringOnly) {
        StringBuilder sb = new StringBuilder();
        String[] hariList = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
        String[] shiftList = {"Pagi", "Sore", "Malam"};

        sb.append("\nMATRIKS JADWAL:\n").append(String.format("%-10s", "SHIFT"));
        for (String h : hariList) sb.append(String.format(" | %-12s", h));
        sb.append("\n------------------------------------------------------------------------------------------------------\n");

        for (String s : shiftList) {
            sb.append(String.format("%-10s", s));
            for (String h : hariList) {
                String nama = "-";
                for (Jadwal j : listJadwal) if (j.shift.equalsIgnoreCase(s) && j.hari.equalsIgnoreCase(h)) nama = j.namaKaryawan;
                sb.append(String.format(" | %-12s", nama));
            }
            sb.append("\n");
        }
        if (!returnStringOnly) System.out.println(sb.toString());
        return sb.toString();
    }

    // --- FITUR 3: TUKAR SHIFT ---
    private static void jalankanFiturTukarShift(Scanner scanner) {
        System.out.println("\n--- TUKAR SHIFT ---");
        if (listJadwal.size() < 2) { System.out.println("[Error] Minimal 2 jadwal diperlukan."); return; }
        for (Jadwal j : listJadwal) System.out.printf("ID: %d | %s | %s | %s\n", j.id, j.namaKaryawan, j.hari, j.shift);
        try {
            System.out.print("\nMasukkan ID Jadwal ke-1: "); int id1 = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Masukkan ID Jadwal ke-2: "); int id2 = Integer.parseInt(scanner.nextLine().trim());
            Jadwal j1 = null, j2 = null;
            for (Jadwal j : listJadwal) { if (j.id == id1) j1 = j; if (j.id == id2) j2 = j; }
            if (j1 != null && j2 != null && j1 != j2) {
                String temp = j1.namaKaryawan; j1.namaKaryawan = j2.namaKaryawan; j2.namaKaryawan = temp;
                System.out.println("[Sukses] Shift ditukar!");
            } else System.out.println("[Error] ID tidak valid!");
        } catch (Exception e) { System.out.println("[Error] Input angka!"); }
    }

    // --- FITUR 4: EKSPOR FILE ---
    private static void jalankanFiturEkspor() {
        System.out.println("\n--- EKSPOR JADWAL ---");
        try {
            String userHome = System.getProperty("user.home");
            File docsDir = new File(userHome, "OneDrive" + File.separator + "Dokumen");
            if (!docsDir.exists()) docsDir = new File(userHome, "Dokumen");
            if (!docsDir.exists()) docsDir = new File(userHome, "Documents");

            File folder = new File(docsDir, "Jadwal Karyawan");
            if (!folder.exists()) folder.mkdirs();

            File file = new File(folder, "Jadwal_Mingguan.txt");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("DOKUMEN JADWAL KERJA - SHIFTMASTER\n");
                writer.write(cetakMatriksJadwal(true));
            }
            System.out.println("[Sukses] File diekspor ke: " + file.getAbsolutePath());
        } catch (IOException e) { System.out.println("[Error] Gagal ekspor: " + e.getMessage()); }
    }
}