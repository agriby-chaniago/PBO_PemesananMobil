package com.mycompany.pbo_pemesananmobil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class EditCarDialog extends JDialog {

   private JTextField fotoMobilField;
    private JTextField namaMobilField;
    private JTextField tipeMobilField;
    private JTextField tahunMobilField;
    private JTextField platNomerField;
    private JTextField hargaSewaPerHariField;
    private JTextField statusMobilField;
    private DatabaseManager dbManager;
    private int carId;
    private DataMobilPanel mobilPanel;

    public EditCarDialog(JFrame parent, int carId, Object[] carData, DataMobilPanel mobilPanel) {
        super(parent, "Edit Data Mobil", true);
        this.dbManager = DatabaseManager.getInstance();
        this.carId = carId;
        this.mobilPanel = mobilPanel;

        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Menambahkan field input
        fotoMobilField = new JTextField(carData[1].toString(), 20);
        namaMobilField = new JTextField(carData[2].toString(), 20);
        tipeMobilField = new JTextField(carData[3].toString(), 20);
        tahunMobilField = new JTextField(carData[4].toString(), 20);
        platNomerField = new JTextField(carData[5].toString(), 20);
        hargaSewaPerHariField = new JTextField(carData[6].toString(), 20);
        statusMobilField = new JTextField(carData[7].toString(), 20);

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
        saveButton.addActionListener(e -> saveCarData());

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveButton, gbc);
    }

    private void saveCarData() {
        String fotoMobil = fotoMobilField.getText();
        String namaMobil = namaMobilField.getText();
        String tipeMobil = tipeMobilField.getText();
        String tahunMobil = tahunMobilField.getText();
        String platNomer = platNomerField.getText();
        String hargaSewaPerHari = hargaSewaPerHariField.getText();
        String statusMobil = statusMobilField.getText();

        // Update data di database
        String query = "UPDATE mobil SET foto_mobil = ?, nama_mobil = ?, tipe_mobil = ?, tahun_mobil = ?, plat_nomer = ?, harga_sewa_per_hari = ?, status_mobil = ? WHERE id = ?";
        Object[] params = {fotoMobil, namaMobil, tipeMobil, tahunMobil, platNomer, hargaSewaPerHari, statusMobil, carId};

        int rowsUpdated = dbManager.updateData(query, params);

        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, "Data mobil berhasil diperbarui.");
            mobilPanel.refreshData(); // Refresh data di tabel setelah update
            dispose(); // Tutup dialog
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data mobil.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
