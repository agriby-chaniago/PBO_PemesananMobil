package com.mycompany.pbo_pemesananmobil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataMobilPanel extends JPanel {

    private static final int ROWS_PER_PAGE = 5; // Jumlah baris per halaman
    private int currentPage = 1; // Halaman saat ini
    private List<Object[]> allData;
    private DefaultTableModel model;
    private JTable table;
    private DatabaseManager dbManager;

    public DataMobilPanel() {
        setLayout(new BorderLayout());
        dbManager = DatabaseManager.getInstance();
        model = new DefaultTableModel(new String[]{
                "ID", "Foto Mobil", "Nama Mobil", "Tipe Mobil", "Tahun Mobil",
                "Plat Nomer", "Harga Sewa per Hari", "Status Mobil", "Created At"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Nonaktifkan edit langsung di tabel
            }
        };

        table = new JTable(model);

        // Ambil dan tampilkan data mobil
        fetchAndDisplayData();

        // Tambahkan MouseListener untuk mendeteksi klik dua kali pada baris
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) { // Double-click
                    int selectedRow = table.convertRowIndexToModel(table.getSelectedRow());
                    Object[] rowData = allData.get(selectedRow);
                    int carId = (int) rowData[0]; // Ambil ID mobil
                    new EditCarDialog((JFrame) SwingUtilities.getWindowAncestor(DataMobilPanel.this),
                            carId, rowData, DataMobilPanel.this).setVisible(true);
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tambahkan panel untuk pagination
        JPanel paginationPanel = createPaginationPanel();
        add(paginationPanel, BorderLayout.SOUTH);
    }

    public void fetchAndDisplayData() {
        allData = new ArrayList<>();
        try {
            ResultSet rs = dbManager.fetchMobilData();
            while (rs.next()) {
                allData.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("foto_mobil"),
                        rs.getString("nama_mobil"),
                        rs.getString("tipe_mobil"),
                        rs.getInt("tahun_mobil"),
                        rs.getString("plat_nomer"),
                        rs.getBigDecimal("harga_sewa_per_hari"),
                        rs.getString("status_mobil"),
                        rs.getTimestamp("created_at")
                });
            }

            displayPage(1);
            System.out.println("Data mobil berhasil dimuat, jumlah data: " + allData.size());

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data mobil dari database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayPage(int pageNumber) {
        model.setRowCount(0); // Hapus data sebelumnya dari model tabel
        int start = (pageNumber - 1) * ROWS_PER_PAGE;
        int end = Math.min(start + ROWS_PER_PAGE, allData.size());

        for (int i = start; i < end; i++) {
            model.addRow(allData.get(i));
        }

        currentPage = pageNumber;
    }

    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnPrevious = new JButton("Previous");
        JButton btnNext = new JButton("Next");

        btnPrevious.addActionListener(e -> {
            if (currentPage > 1) {
                displayPage(currentPage - 1);
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage * ROWS_PER_PAGE < allData.size()) {
                displayPage(currentPage + 1);
            }
        });

        paginationPanel.add(btnPrevious);
        paginationPanel.add(btnNext);

        return paginationPanel;
    }

    public void refreshData() {
        fetchAndDisplayData();
    }

    public boolean hasData() {
        return allData != null && !allData.isEmpty();
    }
}
