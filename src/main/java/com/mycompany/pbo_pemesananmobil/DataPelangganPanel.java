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

public class DataPelangganPanel extends JPanel {

    private List<Object[]> allData;
    private DefaultTableModel model;
    private JTable table;
    private DatabaseManager dbManager;

    public DataPelangganPanel() {
        setLayout(new BorderLayout());
        dbManager = DatabaseManager.getInstance();
        model = new DefaultTableModel(new String[]{"ID", "Nama Pelanggan", "Nomor Telepon", "Alamat", "Email", "Created At", "Delete"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Hanya kolom "Delete" yang bisa diedit
            }
        };
        table = new JTable(model);

        // Menambahkan renderer dan editor tombol Delete pada kolom
        table.getColumn("Delete").setCellRenderer(new DeleteButtonRenderer());
        table.getColumn("Delete").setCellEditor(new DeleteButtonEditor());

        fetchAndDisplayData();

        // Menambahkan MouseListener untuk mendeteksi klik dua kali pada baris
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {  // Double-click
                    int selectedRow = table.convertRowIndexToModel(table.getSelectedRow());
                    Object[] rowData = allData.get(selectedRow);
                    int customerId = (int) rowData[0];  // Ambil ID pelanggan
                    new EditCustomerDialog((JFrame) SwingUtilities.getWindowAncestor(DataPelangganPanel.this),
                            customerId, rowData, DataPelangganPanel.this).setVisible(true);
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void fetchAndDisplayData() {
        allData = new ArrayList<>();
        try {
            ResultSet rs = dbManager.fetchPelangganData();
            while (rs.next()) {
                allData.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama_pelanggan"),
                        rs.getString("nomor_telepon"),
                        rs.getString("alamat"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at"),
                        new JButton("Delete") // Tombol Delete
                });
            }

            updateTableData();
            System.out.println("Data pelanggan berhasil dimuat, jumlah data: " + allData.size());

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data pelanggan dari database.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public void refreshData() {
        fetchAndDisplayData();
    }

    private void deleteData(int id) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data pelanggan ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dbManager.deletePelanggan(id); // Hapus pelanggan dari database
            refreshData(); // Refresh data tabel setelah penghapusan
        }
    }

    // Renderer untuk kolom "Delete"
    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("Delete");
            setPreferredSize(new Dimension(100, 30)); // Sesuaikan ukuran tombol
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
            deleteButton.setPreferredSize(new Dimension(100, 30)); // Sesuaikan ukuran tombol
            deleteButton.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int modelRow = table.convertRowIndexToModel(row);
                    int id = (int) model.getValueAt(modelRow, 0); // Ambil ID dari tabel
                    deleteData(id); // Hapus data pelanggan
                    model.removeRow(modelRow); // Hapus baris dari model tabel
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

}
