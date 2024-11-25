package com.mycompany.pbo_pemesananmobil;

import java.awt.*;
import javax.swing.*;

public class EditSopirDialog extends JDialog {

    private JTextField namaSopirField;
    private JTextField emailField;
    private JPasswordField passwordField;
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
        passwordField = new JPasswordField(20); // Password field is not displayed in the table, so it's empty
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
        String namaSopir = namaSopirField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String nomerTelepon = nomerTeleponField.getText().trim();
        String alamat = alamatField.getText().trim();
        String statusSopir = statusSopirComboBox.getSelectedItem().toString();
        String hargaSewaPerHari = hargaSewaPerHariField.getText().trim();

        // Validate input
        if (namaSopir.isEmpty() || !namaSopir.matches("[a-zA-Z0-9' ]+")) {
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong dan tidak boleh menggunakan karakter khusus kecuali petik satu (').", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Format email tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!nomerTelepon.matches("\\d{11,}")) {
            JOptionPane.showMessageDialog(this, "Nomer telepon harus minimal 11 angka.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")) {
            JOptionPane.showMessageDialog(this, "Password harus menggunakan kombinasi huruf, angka, dan karakter khusus, minimal 8 karakter.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Double.parseDouble(hargaSewaPerHari);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga sewa per hari harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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