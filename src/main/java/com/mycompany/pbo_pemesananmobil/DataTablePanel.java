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

public class DataTablePanel extends JPanel {

    private static final int ROWS_PER_PAGE = 56;
    private int currentPage = 1;
    private List<Object[]> allData;
    private DefaultTableModel model;
    private JTable table;
    private DatabaseManager dbManager;

    public DataTablePanel() {
        setLayout(new BorderLayout());
        dbManager = DatabaseManager.getInstance();

        // Tambahkan kolom "Delete" di model tabel
        model = new DefaultTableModel(new String[]{
                "ID", "Nama Pelanggan", "Nama Mobil", "Nama Sopir",
                "Tanggal Mulai", "Tanggal Selesai", "Tanggal Kembali",
                "Total Harga", "Status", "Denda", "Created At", "Delete"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 11; // Hanya kolom "Delete" yang bisa diedit
            }
        };

        // Inisialisasi tabel
        table = new JTable(model);

        // Set renderer dan editor khusus untuk kolom "Delete"
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

                    // Buka EditDialog dengan data baris yang dipilih
                    EditDialog editDialog = new EditDialog(SwingUtilities.getWindowAncestor(DataTablePanel.this), rowData, dbManager, modelRow, DataTablePanel.this, true);
                    editDialog.setVisible(true);
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Ambil dan tampilkan data dari database
        fetchAndDisplayData();

        // Tambahkan panel untuk pagination
        JPanel paginationPanel = createPaginationPanel();
        add(paginationPanel, BorderLayout.SOUTH);
    }

    public void fetchAndDisplayData() {
        allData = new ArrayList<>();
        try {
            // Query dengan JOIN untuk mengambil nama pelanggan, mobil, dan sopir
            String query = "SELECT p.id, pel.nama_pelanggan, m.nama_mobil, s.nama_sopir, " +
                    "p.tanggal_mulai, p.tanggal_selesai, p.tanggal_kembali, " +
                    "p.total_harga, p.status_pemesanan, p.denda, p.created_at " +
                    "FROM pemesan_mobil p " +
                    "JOIN pelanggan pel ON p.id_pelanggan = pel.id " +
                    "JOIN mobil m ON p.id_mobil = m.id " +
                    "JOIN sopir s ON p.id_sopir = s.id " +
                    "ORDER BY p.id DESC";

            ResultSet rs = dbManager.executeQuery(query);
            while (rs.next()) {
                allData.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama_pelanggan"),
                        rs.getString("nama_mobil"),
                        rs.getString("nama_sopir"),
                        rs.getDate("tanggal_mulai"),
                        rs.getDate("tanggal_selesai"),
                        rs.getDate("tanggal_kembali"),
                        rs.getDouble("total_harga"),
                        rs.getString("status_pemesanan"),
                        rs.getDouble("denda"),
                        rs.getDate("created_at"),
                        "Delete" // Teks tombol "Delete"
                });
            }

            displayPage(1);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data dari database.", "Error", JOptionPane.ERROR_MESSAGE);
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
                    int confirm = JOptionPane.showConfirmDialog(DataTablePanel.this,
                            "Apakah Anda yakin ingin menghapus data ini?",
                            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteData(id); // Jika pengguna memilih Yes, hapus data
                        model.removeRow(modelRow); // Hapus baris dari model tabel
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
        String query = "DELETE FROM pemesan_mobil WHERE id = ?";
        dbManager.updateData(query, new Object[]{id});
        JOptionPane.showMessageDialog(this, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }
}