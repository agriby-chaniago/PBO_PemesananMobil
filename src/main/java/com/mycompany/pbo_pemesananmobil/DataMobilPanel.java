package com.mycompany.pbo_pemesananmobil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataMobilPanel extends JPanel {

    private List<Object[]> allData;
    private DefaultTableModel model;
    private JTable table;
    private DatabaseManager dbManager;

    public DataMobilPanel() {
        setLayout(new BorderLayout());
        dbManager = DatabaseManager.getInstance();
        model = new DefaultTableModel(new String[]{
                "ID", "Foto Mobil", "Nama Mobil", "Tipe Mobil", "Tahun Mobil", "Plat Nomer",
                "Harga Sewa per Hari", "Status Mobil", "Created At", "Delete"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Hanya kolom "Delete" yang bisa diedit
            }
        };

        table = new JTable(model);

        // Set renderer dan editor untuk tombol "Delete"
        table.getColumn("Delete").setCellRenderer(new DeleteButtonRenderer());
        table.getColumn("Delete").setCellEditor(new DeleteButtonEditor());

        fetchAndDisplayData();

        // Tambahkan MouseListener untuk mendeteksi klik dua kali pada baris
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {  // Double-click
                    int selectedRow = table.convertRowIndexToModel(table.getSelectedRow());
                    Object[] rowData = allData.get(selectedRow);
                    int carId = (int) rowData[0];  // Ambil ID mobil
                    new EditCarDialog((JFrame) SwingUtilities.getWindowAncestor(DataMobilPanel.this),
                            carId, rowData, DataMobilPanel.this).setVisible(true);
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
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
                        rs.getTimestamp("created_at"),
                        "Delete" // Teks tombol "Delete"
                });
            }

            updateTableData();
            System.out.println("Data mobil berhasil dimuat, jumlah data: " + allData.size());

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data mobil dari database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableData() {
        model.setRowCount(0);
        for (Object[] rowData : allData) {
            model.addRow(rowData);
        }
    }

    public boolean hasData() {
        return allData != null && !allData.isEmpty();
    }

    // Tambahkan metode refreshData untuk memperbarui tabel setelah edit
    public void refreshData() {
        fetchAndDisplayData();
    }

    // Renderer untuk kolom "Delete"
    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("Delete");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor untuk kolom "Delete"
    private class DeleteButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton deleteButton;

        public DeleteButtonEditor() {
            deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int modelRow = table.convertRowIndexToModel(row);
                    int id = (int) model.getValueAt(modelRow, 0); // Ambil ID dari tabel

                    // Tampilkan konfirmasi sebelum menghapus
                    int confirm = JOptionPane.showConfirmDialog(DataMobilPanel.this,
                            "Apakah Anda yakin ingin menghapus data ini?",
                            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteData(id); // Jika pengguna memilih Yes, hapus data
                        model.removeRow(modelRow); // Hapus baris dari model tabel
                        refreshData(); // Memperbarui data setelah penghapusan
                    }
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return "Delete";
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return deleteButton;
        }
    }

    private void deleteData(int id) {
        // Menghapus data dari database
        dbManager.deleteMobil(id);
    }
}
