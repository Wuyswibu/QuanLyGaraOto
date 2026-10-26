package ui;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import connectDB.ConnectDB;
import dao.PhuTungDao;
import entity.PhuTung;

public final class QuanLyPhuTungFrame extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color RED = new Color(190, 22, 58);
    private static final Color BACKGROUND = new Color(240, 243, 246);
    private static final Color BORDER = new Color(220, 224, 230);

    // ==================== DAO VÀ DỮ LIỆU ====================

    private final PhuTungDao phuTungDao = new PhuTungDao();

    private ArrayList<PhuTung> danhSachPhuTung = new ArrayList<>();

    private boolean dangTaiDuLieu;

    private final NumberFormat moneyFormat =
        NumberFormat.getNumberInstance(
            Locale.forLanguageTag("vi-VN")
        );

    // ==================== FORM ====================

    private final JTextField codeField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField unitField = new JTextField();
    private final JTextField descriptionField = new JTextField();
    private final JTextField salePriceField = new JTextField();
    private final JTextField stockField = new JTextField();
    private final JTextField minimumStockField = new JTextField();
    private final JTextField statusField = new JTextField();

    private final JTextField searchField = new JTextField();

    private final JComboBox<String> filterCombo =
        new JComboBox<>(new String[]{"Tất cả"});

    private final JLabel lblSoLuong =
        new JLabel("Đang tải dữ liệu...");

    // ==================== NÚT ====================

    private final JButton homeButton =
        new JButton("Về màn hình chính");

    private final JButton reloadButton =
        new JButton("Tải lại");

    private final JButton addButton =
        new JButton("Thêm phụ tùng");

    private final JButton editButton =
        new JButton("Sửa phụ tùng");

    private final JButton stopButton =
        new JButton("Ngừng kinh doanh");

    private final JButton searchButton =
        new JButton("Tìm kiếm");

    // ==================== BẢNG ====================

    private final DefaultTableModel tableModel =
        new DefaultTableModel(
            new String[]{
                "Mã PT",
                "Tên phụ tùng",
                "ĐVT",
                "Giá bán",
                "Tồn kho",
                "Tồn tối thiểu",
                "Trạng thái"
            },
            0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) {
                    return BigDecimal.class;
                }

                if (column == 4 || column == 5) {
                    return Integer.class;
                }

                return String.class;
            }
        };

    private final JTable partsTable = new JTable(tableModel);

    private final TableRowSorter<DefaultTableModel> boLoc =
        new TableRowSorter<>(tableModel);

    public QuanLyPhuTungFrame() {
        moneyFormat.setMaximumFractionDigits(2);

        buildInterface();
        bindEvents();

        loadPhuTungFromSQL();
    }

    // ==================== GIAO DIỆN ====================

    private void buildInterface() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        );

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBackground(Color.WHITE);
        content.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
            )
        );

        content.add(createFormSection(), BorderLayout.NORTH);
        content.add(createTableSection(), BorderLayout.CENTER);
        content.add(createButtonSection(), BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
    }

    private JPanel createFormSection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(false);

        JPanel titlePanel = new JPanel(
            new GridLayout(2, 1, 0, 3)
        );
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("QUẢN LÝ PHỤ TÙNG");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(30, 35, 45));

        JLabel description = new JLabel(
            "Danh mục phụ tùng; tồn kho cập nhật qua phiếu nhập và xuất."
        );
        description.setFont(new Font("Arial", Font.PLAIN, 13));
        description.setForeground(Color.GRAY);

        titlePanel.add(title);
        titlePanel.add(description);

        JPanel form = new JPanel(
            new GridLayout(2, 4, 12, 6)
        );
        form.setOpaque(false);

        codeField.setEditable(false);
        stockField.setEditable(false);
        statusField.setEditable(false);

        form.add(createField("Mã phụ tùng", codeField));
        form.add(createField("Tên phụ tùng", nameField));
        form.add(createField("Đơn vị tính", unitField));
        form.add(createField("Mô tả", descriptionField));

        form.add(createField("Giá bán (đ)", salePriceField));
        form.add(createField("Số lượng tồn", stockField));
        form.add(createField("Tồn tối thiểu", minimumStockField));
        form.add(createField("Trạng thái", statusField));

        section.add(titlePanel, BorderLayout.NORTH);
        section.add(form, BorderLayout.CENTER);

        return section;
    }

    private JPanel createField(String label, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setOpaque(false);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        input.setFont(new Font("Arial", Font.PLAIN, 12));
        input.setPreferredSize(new Dimension(180, 26));

        panel.add(fieldLabel, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTableSection() {
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setBackground(new Color(237, 240, 244));
        toolbar.setBorder(
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );

        JPanel searchPanel = new JPanel(
            new BorderLayout(8, 0)
        );
        searchPanel.setOpaque(false);

        searchPanel.add(
            new JLabel("Tìm kiếm:"),
            BorderLayout.WEST
        );

        searchField.setPreferredSize(new Dimension(300, 26));
        searchField.setToolTipText(
            "Tìm theo mã hoặc tên phụ tùng"
        );

        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setPreferredSize(new Dimension(100, 26));
        searchButton.setFocusPainted(false);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel filterPanel = new JPanel(
            new BorderLayout(8, 0)
        );
        filterPanel.setOpaque(false);

        filterPanel.add(
            new JLabel("Trạng thái:"),
            BorderLayout.WEST
        );
        filterPanel.add(filterCombo, BorderLayout.CENTER);

        toolbar.add(searchPanel, BorderLayout.CENTER);
        toolbar.add(filterPanel, BorderLayout.EAST);

        partsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        partsTable.setRowHeight(28);
        partsTable.setPreferredScrollableViewportSize(
            new Dimension(1100, 8 * 28)
        );
        partsTable.setFillsViewportHeight(true);
        partsTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        partsTable.setRowSorter(boLoc);
        partsTable.setSelectionBackground(new Color(49, 106, 140));
        partsTable.setSelectionForeground(Color.WHITE);
        partsTable.setShowVerticalLines(false);
        partsTable.setGridColor(BORDER);

        partsTable.getTableHeader().setReorderingAllowed(false);
        partsTable.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );
        partsTable.getTableHeader().setPreferredSize(
            new Dimension(0, 28)
        );

        partsTable.getColumnModel()
            .getColumn(0).setPreferredWidth(90);

        partsTable.getColumnModel()
            .getColumn(1).setPreferredWidth(300);

        partsTable.getColumnModel()
            .getColumn(2).setPreferredWidth(70);

        partsTable.getColumnModel()
            .getColumn(3).setPreferredWidth(150);

        partsTable.getColumnModel()
            .getColumn(6).setPreferredWidth(160);

        DefaultTableCellRenderer moneyRenderer =
            new DefaultTableCellRenderer() {
                private static final long serialVersionUID = 1L;

                @Override
                protected void setValue(Object value) {
                    if (value instanceof Number) {
                        setText(moneyFormat.format(value) + " đ");
                    } else {
                        super.setValue(value);
                    }
                }
            };

        moneyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        partsTable.getColumnModel()
            .getColumn(3).setCellRenderer(moneyRenderer);

        DefaultTableCellRenderer centerRenderer =
            new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        partsTable.getColumnModel()
            .getColumn(2).setCellRenderer(centerRenderer);

        partsTable.getColumnModel()
            .getColumn(4).setCellRenderer(centerRenderer);

        partsTable.getColumnModel()
            .getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(partsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(
            BorderFactory.createLineBorder(BORDER)
        );

        lblSoLuong.setForeground(Color.GRAY);

        section.add(toolbar, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);
        section.add(lblSoLuong, BorderLayout.SOUTH);

        return section;
    }

    private JPanel createButtonSection() {
        JPanel panel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 6, 0)
        );
        panel.setOpaque(false);

        homeButton.setPreferredSize(new Dimension(165, 30));
        reloadButton.setPreferredSize(new Dimension(100, 30));
        addButton.setPreferredSize(new Dimension(145, 30));
        editButton.setPreferredSize(new Dimension(145, 30));
        stopButton.setPreferredSize(new Dimension(165, 30));

        homeButton.setContentAreaFilled(false);
        homeButton.setOpaque(true);
        homeButton.setBackground(RED);
        homeButton.setForeground(Color.WHITE);
        homeButton.setFocusPainted(false);
        homeButton.setBorderPainted(false);

        stopButton.setContentAreaFilled(false);
        stopButton.setOpaque(true);
        stopButton.setBackground(RED);
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.setBorderPainted(false);

        panel.add(homeButton);
        panel.add(reloadButton);
        panel.add(addButton);
        panel.add(editButton);
        panel.add(stopButton);

        return panel;
    }

    // ==================== ĐỌC SQL ====================

    public void loadPhuTungFromSQL() {
        if (dangTaiDuLieu) {
            return;
        }

        dangTaiDuLieu = true;
        reloadButton.setEnabled(false);
        lblSoLuong.setText("Đang tải phụ tùng từ SQL...");

        new SwingWorker<ArrayList<PhuTung>, Void>() {
            @Override
            protected ArrayList<PhuTung> doInBackground()
                    throws Exception {

                Connection conn = ConnectDB.getConnection();

                if (conn == null || conn.isClosed()) {
                    ConnectDB.getInstance().connect();
                    System.out.println(
                        "Kết nối database thành công!"
                    );
                }

                return phuTungDao.layDanhSachPhuTung();
            }

            @Override
            protected void done() {
                boolean thanhCong = false;

                try {
                    ArrayList<PhuTung> ketQua = get();

                    partsTable.clearSelection();
                    boLoc.setRowFilter(null);
                    tableModel.setRowCount(0);

                    danhSachPhuTung = ketQua;
                    clearForm();

                    filterCombo.removeAllItems();
                    filterCombo.addItem("Tất cả");

                    for (PhuTung pt : danhSachPhuTung) {
                        tableModel.addRow(new Object[]{
                            pt.getMaPhuTung(),
                            pt.getTenPhuTung(),
                            pt.getDonViTinh(),
                            pt.getDonGia(),
                            pt.getSoLuongTon(),
                            pt.getMucTonToiThieu(),
                            pt.getTrangThai()
                        });

                        String trangThai = pt.getTrangThai();

                        DefaultComboBoxModel<String> modelTrangThai =
                            (DefaultComboBoxModel<String>)
                                filterCombo.getModel();

                        if (trangThai != null
                                && modelTrangThai.getIndexOf(
                                    trangThai
                                ) < 0) {
                            filterCombo.addItem(trangThai);
                        }
                    }

                    searchField.setText("");
                    filterCombo.setSelectedIndex(0);
                    thanhCong = true;

                    System.out.println(
                        "Đã tải " + danhSachPhuTung.size()
                        + " phụ tùng từ SQL."
                    );

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    lblSoLuong.setText("Đã ngắt tải dữ liệu");

                } catch (ExecutionException e) {
                    Throwable loi = e.getCause();
                    loi.printStackTrace();

                    lblSoLuong.setText(
                        "Không tải được dữ liệu SQL"
                    );

                    JOptionPane.showMessageDialog(
                        QuanLyPhuTungFrame.this,
                        "Không tải được phụ tùng:\n"
                            + loi.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );

                } finally {
                    dangTaiDuLieu = false;
                    reloadButton.setEnabled(true);
                }

                if (thanhCong) {
                    locDuLieu();

                    if (partsTable.getRowCount() > 0) {
                        partsTable.setRowSelectionInterval(0, 0);
                    }
                }
            }
        }.execute();
    }

    // ==================== SỰ KIỆN ====================

    private void bindEvents() {
        partsTable.getSelectionModel()
            .addListSelectionListener(event -> {
                if (event.getValueIsAdjusting()
                        || dangTaiDuLieu) {
                    return;
                }

                int selectedRow = partsTable.getSelectedRow();

                if (selectedRow >= 0) {
                    int modelRow =
                        partsTable.convertRowIndexToModel(
                            selectedRow
                        );

                    showPart(modelRow);
                } else {
                    clearForm();
                }
            });

        homeButton.addActionListener(event -> {
            Window cuaSo = SwingUtilities.getWindowAncestor(this);

            if (cuaSo instanceof GaraManHinhChinh) {
                ((GaraManHinhChinh) cuaSo).hienThiTrangChu();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Hãy mở giao diện từ GaraManHinhChinh.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        reloadButton.addActionListener(
            event -> loadPhuTungFromSQL()
        );

        searchButton.addActionListener(event -> locDuLieu());

        searchField.addActionListener(
            event -> searchButton.doClick()
        );

        filterCombo.addActionListener(event -> locDuLieu());

        addButton.addActionListener(
            event -> showPreviewMessage("Thêm phụ tùng")
        );

        editButton.addActionListener(
            event -> showPreviewMessage("Sửa phụ tùng")
        );

        stopButton.addActionListener(
            event -> showPreviewMessage("Ngừng kinh doanh")
        );
    }

    // Tìm và lọc trên danh sách đã tải từ SQL.
    private void locDuLieu() {
        if (dangTaiDuLieu) {
            return;
        }

        final String tuKhoa = searchField.getText()
            .trim().toLowerCase(Locale.ROOT);

        final String trangThai =
            (String) filterCombo.getSelectedItem();

        boLoc.setRowFilter(
            new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(
                        Entry<? extends DefaultTableModel,
                              ? extends Integer> entry) {

                    String ma = entry.getStringValue(0)
                        .toLowerCase(Locale.ROOT);

                    String ten = entry.getStringValue(1)
                        .toLowerCase(Locale.ROOT);

                    boolean dungTuKhoa =
                        ma.contains(tuKhoa)
                        || ten.contains(tuKhoa);

                    boolean dungTrangThai =
                        trangThai == null
                        || "Tất cả".equals(trangThai)
                        || trangThai.equals(
                            entry.getStringValue(6)
                        );

                    return dungTuKhoa && dungTrangThai;
                }
            }
        );

        lblSoLuong.setText(
            "Hiển thị: " + partsTable.getRowCount()
            + " / " + tableModel.getRowCount()
            + " phụ tùng • Dữ liệu SQL"
        );

        if (partsTable.getSelectedRow() < 0) {
            clearForm();
        }
    }

    // ==================== HIỂN THỊ LÊN FORM ====================

    private void showPart(int row) {
        if (row < 0 || row >= danhSachPhuTung.size()) {
            return;
        }

        PhuTung pt = danhSachPhuTung.get(row);

        codeField.setText(pt.getMaPhuTung());
        nameField.setText(pt.getTenPhuTung());
        unitField.setText(pt.getDonViTinh());

        descriptionField.setText(
            pt.getMoTa() == null ? "" : pt.getMoTa()
        );

        salePriceField.setText(
            pt.getDonGia() == null
                ? ""
                : moneyFormat.format(pt.getDonGia())
        );

        stockField.setText(
            String.valueOf(pt.getSoLuongTon())
        );

        minimumStockField.setText(
            String.valueOf(pt.getMucTonToiThieu())
        );

        statusField.setText(pt.getTrangThai());
    }

    private void clearForm() {
        codeField.setText("");
        nameField.setText("");
        unitField.setText("");
        descriptionField.setText("");
        salePriceField.setText("");
        stockField.setText("");
        minimumStockField.setText("");
        statusField.setText("");
    }

    private void showPreviewMessage(String action) {
        JOptionPane.showMessageDialog(
            this,
            action + ": chưa gắn xử lý ghi dữ liệu SQL.",
            "Bản giao diện",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==================== CHẠY RIÊNG ĐỂ KIỂM TRA ====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Kiểm tra Phụ tùng");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new QuanLyPhuTungFrame());
            frame.setSize(1340, 840);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}