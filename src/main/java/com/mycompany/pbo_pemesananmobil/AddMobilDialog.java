package com.mycompany.pbo_pemesananmobil;

import javax.swing.*;
import java.awt.*;

public class AddMobilDialog extends JDialog {

    private DataMobilPanel mobilPanel;

    public AddMobilDialog(JFrame parent, DataMobilPanel mobilPanel) {
        super(parent, "Tambah Mobil", true);
        this.mobilPanel = mobilPanel; // Simpan referensi ke panel mobil
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Menambahkan field input
        JTextField fotoMobilField = new JTextField(20);
        JTextField namaMobilField = new JTextField(20);
        JTextField tipeMobilField = new JTextField(20);
        JTextField tahunMobilField = new JTextField(20);
        JTextField platNomerField = new JTextField(20);
        JTextField hargaSewaPerHariField = new JTextField(20);
        JTextField statusMobilField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Foto Mobil:"), gbc);

        gbc.gridx = 1;
        add(fotoMobilField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Nama Mobil:"), gbc);

        gbc.gridx = 1;
        add(namaMobilField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Tipe Mobil:"), gbc);

        gbc.gridx = 1;
        add(tipeMobilField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Tahun Mobil:"), gbc);

        gbc.gridx = 1;
        add(tahunMobilField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Plat Nomer:"), gbc);

        gbc.gridx = 1;
        add(platNomerField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Harga Sewa per Hari:"), gbc);

        gbc.gridx = 1;
        add(hargaSewaPerHariField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Status Mobil:"), gbc);

        gbc.gridx = 1;
        add(statusMobilField, gbc);

        // Tombol simpan
        JButton saveButton = new JButton("Simpan");
        saveButton.addActionListener(e -> saveMobil(fotoMobilField.getText(), namaMobilField.getText(), tipeMobilField.getText(), tahunMobilField.getText(), platNomerField.getText(), hargaSewaPerHariField.getText(), statusMobilField.getText()));

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveButton, gbc);
    }

    private void saveMobil(String fotoMobil, String namaMobil, String tipeMobil, String tahunMobil, String platNomer, String hargaSewaPerHari, String statusMobil) {
        // Validasi input
        if (fotoMobil.isEmpty() || namaMobil.isEmpty() || tipeMobil.isEmpty() || tahunMobil.isEmpty() || platNomer.isEmpty() || hargaSewaPerHari.isEmpty() || statusMobil.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simpan ke database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        String query = "INSERT INTO mobil (foto_mobil, nama_mobil, tipe_mobil, tahun_mobil, plat_nomer, harga_sewa_per_hari, status_mobil, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        Object[] params = {fotoMobil, namaMobil, tipeMobil, tahunMobil, platNomer, hargaSewaPerHari, statusMobil};
        int rowsInserted = dbManager.updateData(query, params);

        if (rowsInserted > 0) {
            JOptionPane.showMessageDialog(this, "Mobil berhasil ditambahkan.");
            dispose(); // Tutup dialog setelah berhasil simpan
            mobilPanel.refreshData(); // Refresh data mobil di panel utama
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan mobil.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}