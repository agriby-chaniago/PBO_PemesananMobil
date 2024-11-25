package com.mycompany.pbo_pemesananmobil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataSopirPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private List<Object[]> allData;
    private DatabaseManager dbManager;

    public DataSopirPanel() {
        setLayout(new BorderLayout());
        dbManager = DatabaseManager.getInstance();

        // Membuat model tabel dengan kolom termasuk "Delete"
        model = new DefaultTableModel(new String[]{
                "ID", "Nama Sopir", "Email", "Nomor Telepon", "Alamat", "Status Sopir", "Harga Sewa", "Created At", "Delete"
        }, 0);

        table = new JTable(model);

        // Tambahkan tombol Delete pada kolom terakhir
        table.getColumn("Delete").setCellRenderer(new DeleteButtonRenderer());
        table.getColumn("Delete").setCellEditor(new DeleteButtonEditor());

        // Tambahkan MouseListener untuk mendeteksi klik pada baris
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1) {
                    int modelRow = table.convertRowIndexToModel(row);
                    Object[] rowData = new Object[model.getColumnCount()];
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        rowData[i] = model.getValueAt(modelRow, i);
                    }

                    // Buka EditSopirDialog dengan data baris yang dipilih
                    EditSopirDialog editDialog = new EditSopirDialog((JFrame) SwingUtilities.getWindowAncestor(DataSopirPanel.this), (int) rowData[0], rowData, DataSopirPanel.this);
                    editDialog.setVisible(true);
                }
            }
        });

        fetchAndDisplayData();

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // Mengambil data dari database dan menampilkan di tabel
    public void fetchAndDisplayData() {
        allData = new ArrayList<>();
        model.setRowCount(0); // Kosongkan tabel sebelum menambahkan data baru
        try {
            ResultSet rs = dbManager.fetchSopirData();
            while (rs.next()) {
                Object[] rowData = {
                        rs.getInt("id"),
                        rs.getString("nama_sopir"),
                        rs.getString("email"),
                        rs.getString("nomer_telepon"),
                        rs.getString("alamat"),
                        rs.getString("status_sopir"),
                        rs.getDouble("harga_sewa_per_hari"),
                        rs.getTimestamp("created_at"),
                        "Delete"
                };
                allData.add(rowData);
                model.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data sopir dari database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Renderer untuk kolom tombol Delete
    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("Delete");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor untuk kolom tombol Delete
    private class DeleteButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private final JButton deleteButton;
        private int currentRow;

        public DeleteButtonEditor() {
            deleteButton = new JButton("Delete");
            deleteButton.addActionListener((ActionListener) this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row; // Simpan baris yang sedang diedit
            return deleteButton;
        }

        @Override
        public Object getCellEditorValue() {
            return "Delete";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            stopCellEditing(); // Hentikan mode editing

            // Ambil ID dari baris yang dipilih
            int id = (int) model.getValueAt(currentRow, 0);

            // Konfirmasi penghapusan
            int confirm = JOptionPane.showConfirmDialog(DataSopirPanel.this,
                    "Apakah Anda yakin ingin menghapus data ini?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Hapus data dari database dan cek keberhasilannya
                boolean isDeleted = dbManager.deleteSopir(id);

                if (isDeleted) {
                    // Hapus data dari tabel jika penghapusan berhasil
                    model.removeRow(currentRow);
                    JOptionPane.showMessageDialog(DataSopirPanel.this, "Data sopir berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Tampilkan pesan jika penghapusan gagal
                    JOptionPane.showMessageDialog(DataSopirPanel.this, "Gagal menghapus data sopir. Periksa apakah ada pemesanan yang terkait.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Tambahkan metode ini di dalam DataSopirPanel
    public void refreshData() {
        fetchAndDisplayData();
    }
}