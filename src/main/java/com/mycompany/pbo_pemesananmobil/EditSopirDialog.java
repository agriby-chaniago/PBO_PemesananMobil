package com.mycompany.pbo_pemesananmobil;

import java.awt.*;
import javax.swing.*;

public class EditSopirDialog extends JDialog {

    private JTextField namaSopirField;
    private JTextField emailField;
    private JTextField passwordField;
    private JTextField nomerTeleponField;
    private JTextField alamatField;
    private JComboBox<String> statusSopirComboBox;
    private JTextField hargaSewaPerHariField;
    private DatabaseManager dbManager;
    private int sopirId;
    private DataSopirPanel sopirPanel;

    public EditSopirDialog(JFrame parent, int sopirId, Object[] sopirData, DataSopirPanel sopirPanel) {
        super(parent, "Edit Data Sopir", true);
        this.dbManager = DatabaseManager.getInstance();
        this.sopirId = sopirId;
        this.sopirPanel = sopirPanel;

        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Menambahkan field input
        namaSopirField = new JTextField(sopirData[1].toString(), 20);
        emailField = new JTextField(sopirData[2].toString(), 20);
        passwordField = new JTextField(20); // Password field is not displayed in the table, so it's empty
        nomerTeleponField = new JTextField(sopirData[3].toString(), 20);
        alamatField = new JTextField(sopirData[4].toString(), 20);
        statusSopirComboBox = new JComboBox<>(new String[]{"Tersedia", "Tidak Tersedia"});
        statusSopirComboBox.setSelectedItem(sopirData[5].toString());
        hargaSewaPerHariField = new JTextField(sopirData[6].toString(), 20);

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
        saveButton.addActionListener(e -> saveSopirData());

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveButton, gbc);
    }

    private void saveSopirData() {
        String namaSopir = namaSopirField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String nomerTelepon = nomerTeleponField.getText();
        String alamat = alamatField.getText();
        String statusSopir = statusSopirComboBox.getSelectedItem().toString();
        String hargaSewaPerHari = hargaSewaPerHariField.getText();

        // Update data di database
        String query = "UPDATE sopir SET nama_sopir = ?, email = ?, password = ?, nomer_telepon = ?, alamat = ?, status_sopir = ?, harga_sewa_per_hari = ? WHERE id = ?";
        Object[] params = {namaSopir, email, password, nomerTelepon, alamat, statusSopir, hargaSewaPerHari, sopirId};

        int rowsUpdated = dbManager.updateData(query, params);

        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, "Data sopir berhasil diperbarui.");
            sopirPanel.refreshData(); // Refresh data di tabel setelah update
            dispose(); // Tutup dialog
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data sopir.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}