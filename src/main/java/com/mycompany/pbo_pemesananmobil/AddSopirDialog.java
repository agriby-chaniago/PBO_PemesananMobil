package com.mycompany.pbo_pemesananmobil;

import java.awt.*;
import javax.swing.*;

public class AddSopirDialog extends JDialog {

    private DataSopirPanel sopirPanel;

    public AddSopirDialog(JFrame parent, DataSopirPanel sopirPanel) {
        super(parent, "Tambah Sopir", true);
        this.sopirPanel = sopirPanel; // Simpan referensi ke panel sopir
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Menambahkan field input
        JTextField namaSopirField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField passwordField = new JTextField(20);
        JTextField nomerTeleponField = new JTextField(20);
        JTextField alamatField = new JTextField(20);
        JComboBox<String> statusSopirComboBox = new JComboBox<>(new String[]{"Tersedia", "Tidak Tersedia"});
        JTextField hargaSewaPerHariField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Nama Sopir:"), gbc);

        gbc.gridx = 1;
        add(namaSopirField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Nomer Telepon:"), gbc);

        gbc.gridx = 1;
        add(nomerTeleponField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Alamat:"), gbc);

        gbc.gridx = 1;
        add(alamatField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Status Sopir:"), gbc);

        gbc.gridx = 1;
        add(statusSopirComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Harga Sewa per Hari:"), gbc);

        gbc.gridx = 1;
        add(hargaSewaPerHariField, gbc);

        // Tombol simpan
        JButton saveButton = new JButton("Simpan");
        saveButton.addActionListener(e -> saveSopir(namaSopirField.getText(), emailField.getText(), passwordField.getText(), nomerTeleponField.getText(), alamatField.getText(), statusSopirComboBox.getSelectedItem().toString(), hargaSewaPerHariField.getText()));

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveButton, gbc);
    }

    private void saveSopir(String namaSopir, String email, String password, String nomerTelepon, String alamat, String statusSopir, String hargaSewaPerHari) {
        // Validasi input
        if (namaSopir.isEmpty() || email.isEmpty() || password.isEmpty() || nomerTelepon.isEmpty() || alamat.isEmpty() || statusSopir.isEmpty() || hargaSewaPerHari.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simpan ke database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        String query = "INSERT INTO sopir (nama_sopir, email, password, nomer_telepon, alamat, status_sopir, harga_sewa_per_hari, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        Object[] params = {namaSopir, email, password, nomerTelepon, alamat, statusSopir, hargaSewaPerHari};
        int rowsInserted = dbManager.updateData(query, params);

        if (rowsInserted > 0) {
            JOptionPane.showMessageDialog(this, "Sopir berhasil ditambahkan.");
            dispose(); // Tutup dialog setelah berhasil simpan
            sopirPanel.refreshData(); // Refresh data sopir di panel utama
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan sopir.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}