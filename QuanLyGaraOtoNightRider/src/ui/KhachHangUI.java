package ui;

import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import connectDB.ConnectDB;
import dao.KhachHangDao;
import entity.KhachHang;

public final class KhachHangUI extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color RED = new Color(190, 22, 58);
    private static final Color BACKGROUND = new Color(240, 243, 246);
    private static final Color BORDER = new Color(220, 224, 230);

    private final JTextField customerIdField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField searchField = new JTextField();

    private final JComboBox<String> groupCombo = new JComboBox<>(
        new String[]{"Cá nhân", "Doanh nghiệp"}
    );

    private final JComboBox<String> statusCombo = new JComboBox<>(
        new String[]{"Đang hoạt động", "Ngừng hoạt động"}
    );

    private final JComboBox<String> filterCombo = new JComboBox<>(
        new String[]{
            "Tất cả", "Đang hoạt động", "Ngừng hoạt động"
        }
    );

    private final JButton homeButton =
        new JButton("Về màn hình chính");

    private final JButton newButton =
        new JButton("Nhập khách mới");

    private final JButton addButton =
        new JButton("Thêm khách hàng");

    private final JButton editButton =
        new JButton("Sửa thông tin");

    private final JButton vehiclesButton =
        new JButton("Xem xe của khách");

    private final KhachHangDao khachHangDao = new KhachHangDao();

    private ArrayList<KhachHang> danhSachKhachHang =
        new ArrayList<>();

    private final JLabel lblSoLuong =
        new JLabel("Đang tải dữ liệu...");

    private boolean dangTaiDuLieu;

    private final DefaultTableModel tableModel =
        new DefaultTableModel(
            new String[]{
                "Mã KH", "Khách hàng", "Điện thoại",
                "Email", "Địa chỉ", "Trạng thái"
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
                return String.class;
            }
        };

    private final JTable customerTable = new JTable(tableModel);

    public KhachHangUI() {
        buildInterface();
        bindEvents();

        // Database chưa có trường nhóm khách hàng.
        groupCombo.setSelectedIndex(-1);
        groupCombo.setEnabled(false);
        groupCombo.setToolTipText(
            "Database hiện chưa có trường nhóm khách hàng"
        );

        loadKhachHangFromSQL();
    }

    // ==================== GIAO DIỆN PANEL ====================

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

    // ==================== TẢI DỮ LIỆU SQL ====================

    public void loadKhachHangFromSQL() {
        if (dangTaiDuLieu) {
            return;
        }

        dangTaiDuLieu = true;
        lblSoLuong.setText("Đang tải khách hàng...");

        new SwingWorker<ArrayList<KhachHang>, Void>() {
            @Override
            protected ArrayList<KhachHang> doInBackground()
                    throws Exception {

                Connection conn = ConnectDB.getConnection();

                if (conn == null || conn.isClosed()) {
                    ConnectDB.getInstance().connect();
                    System.out.println(
                        "Kết nối database thành công!"
                    );
                }

                return khachHangDao.layDanhSachKhachHang();
            }

            @Override
            protected void done() {
                try {
                    ArrayList<KhachHang> ketQua = get();

                    customerTable.clearSelection();
                    tableModel.setRowCount(0);

                    danhSachKhachHang = ketQua;
                    clearForm();

                    for (KhachHang kh : danhSachKhachHang) {
                        tableModel.addRow(new Object[]{
                            kh.getMaKhachHang(),
                            kh.getHoTen(),
                            kh.getSoDienThoai(),
                            kh.getEmail(),
                            kh.getDiaChi(),
                            kh.isTrangThai()
                                ? "Đang hoạt động"
                                : "Ngừng hoạt động"
                        });
                    }

                    lblSoLuong.setText(
                        "Danh sách " + danhSachKhachHang.size()
                        + " khách hàng • Dữ liệu SQL"
                    );

                    System.out.println(
                        "Đã tải " + danhSachKhachHang.size()
                        + " khách hàng từ SQL."
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
                        KhachHangUI.this,
                        "Không tải được khách hàng:\n"
                            + loi.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );

                } finally {
                    dangTaiDuLieu = false;
                }

                if (customerTable.getRowCount() > 0
                        && customerTable.getSelectedRow() < 0) {
                    customerTable.setRowSelectionInterval(0, 0);
                }
            }
        }.execute();
    }

    // ==================== FORM KHÁCH HÀNG ====================

    private JPanel createFormSection() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setOpaque(false);

        JPanel titlePanel = new JPanel(
            new GridLayout(2, 1, 0, 3)
        );
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(30, 35, 45));

        JLabel description = new JLabel(
            "Quản lý khách hàng độc lập; "
            + "mỗi khách có thể sở hữu nhiều xe."
        );
        description.setFont(new Font("Arial", Font.PLAIN, 13));
        description.setForeground(Color.GRAY);

        titlePanel.add(title);
        titlePanel.add(description);
        section.add(titlePanel, BorderLayout.NORTH);

        JPanel form = new JPanel(
            new GridLayout(2, 4, 12, 6)
        );
        form.setOpaque(false);

        customerIdField.setEditable(false);

        form.add(createField("Mã khách hàng", customerIdField));
        form.add(createField("Họ tên *", nameField));
        form.add(createField("Điện thoại *", phoneField));
        form.add(createField("Email", emailField));

        form.add(createField("Địa chỉ", addressField));
        form.add(createField("Nhóm khách hàng", groupCombo));
        form.add(createField("Trạng thái", statusCombo));

        JPanel hintPanel = new JPanel(new BorderLayout());
        hintPanel.setOpaque(false);

        JLabel hint = new JLabel("* Thông tin bắt buộc");
        hint.setForeground(Color.GRAY);

        hintPanel.add(hint, BorderLayout.SOUTH);
        form.add(hintPanel);

        section.add(form, BorderLayout.CENTER);

        return section;
    }

    private JPanel createField(String label, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setOpaque(false);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        input.setFont(new Font("Arial", Font.PLAIN, 12));
        input.setPreferredSize(new Dimension(200, 26));

        panel.add(fieldLabel, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);

        return panel;
    }

    // ==================== BẢNG KHÁCH HÀNG ====================

    private JPanel createTableSection() {
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setOpaque(false);

        JPanel toolbar = new JPanel(
            new BorderLayout(15, 0)
        );
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
            "Ô tìm kiếm chưa gắn xử lý"
        );
        searchPanel.add(searchField, BorderLayout.CENTER);

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
        section.add(toolbar, BorderLayout.NORTH);

        customerTable.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );
        customerTable.setRowHeight(28);
        customerTable.setPreferredScrollableViewportSize(
            new Dimension(1100, 8 * 28)
        );
        customerTable.setFillsViewportHeight(true);
        customerTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        customerTable.setAutoCreateRowSorter(true);
        customerTable.setSelectionBackground(
            new Color(49, 106, 140)
        );
        customerTable.setSelectionForeground(Color.WHITE);
        customerTable.setShowVerticalLines(false);
        customerTable.setGridColor(BORDER);

        customerTable.getTableHeader()
            .setReorderingAllowed(false);

        customerTable.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        customerTable.getTableHeader().setPreferredSize(
            new Dimension(0, 28)
        );

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(
            BorderFactory.createLineBorder(BORDER)
        );

        section.add(scrollPane, BorderLayout.CENTER);

        lblSoLuong.setForeground(Color.GRAY);
        section.add(lblSoLuong, BorderLayout.SOUTH);

        return section;
    }

    // ==================== THANH NÚT ====================

    private JPanel createButtonSection() {
        JPanel panel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 6, 0)
        );
        panel.setOpaque(false);

        homeButton.setContentAreaFilled(false);
        homeButton.setOpaque(true);
        homeButton.setBackground(RED);
        homeButton.setForeground(Color.WHITE);
        homeButton.setFocusPainted(false);
        homeButton.setBorderPainted(false);
        homeButton.setPreferredSize(new Dimension(165, 30));

        vehiclesButton.setContentAreaFilled(false);
        vehiclesButton.setOpaque(true);
        vehiclesButton.setBackground(Color.BLUE);
        vehiclesButton.setForeground(Color.WHITE);
        vehiclesButton.setFocusPainted(false);
        vehiclesButton.setBorderPainted(false);
        vehiclesButton.setPreferredSize(new Dimension(165, 30));

        newButton.setPreferredSize(new Dimension(165, 30));
        addButton.setPreferredSize(new Dimension(165, 30));
        editButton.setPreferredSize(new Dimension(165, 30));

        panel.add(homeButton);
        panel.add(newButton);
        panel.add(addButton);
        panel.add(editButton);
        panel.add(vehiclesButton);

        return panel;
    }

    // ==================== SỰ KIỆN ====================

    private void bindEvents() {
        customerTable.getSelectionModel()
            .addListSelectionListener(event -> {
                if (event.getValueIsAdjusting()
                        || dangTaiDuLieu) {
                    return;
                }

                int selectedRow = customerTable.getSelectedRow();

                if (selectedRow >= 0) {
                    int modelRow =
                        customerTable.convertRowIndexToModel(
                            selectedRow
                        );

                    showCustomer(modelRow);
                }
            });

        homeButton.addActionListener(event -> {
            GaraManHinhChinh manHinhChinh = layManHinhChinh();

            if (manHinhChinh != null) {
                manHinhChinh.hienThiTrangChu();
            }
        });

        newButton.addActionListener(event -> clearForm());

        addButton.addActionListener(
            event -> showPreviewMessage("Thêm khách hàng")
        );

        editButton.addActionListener(
            event -> showPreviewMessage("Sửa thông tin")
        );

        vehiclesButton.addActionListener(event -> {
            GaraManHinhChinh manHinhChinh = layManHinhChinh();

            if (manHinhChinh != null) {
                manHinhChinh.chuyenManHinh("XE");
            }
        });
    }

    private GaraManHinhChinh layManHinhChinh() {
        Window cuaSo = SwingUtilities.getWindowAncestor(this);

        if (cuaSo instanceof GaraManHinhChinh) {
            return (GaraManHinhChinh) cuaSo;
        }

        JOptionPane.showMessageDialog(
            this,
            "Hãy mở giao diện này từ GaraManHinhChinh "
                + "để sử dụng điều hướng.",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE
        );

        return null;
    }

    // ==================== HIỂN THỊ THÔNG TIN ====================

    private void showCustomer(int row) {
        if (row < 0 || row >= danhSachKhachHang.size()) {
            return;
        }

        KhachHang kh = danhSachKhachHang.get(row);

        customerIdField.setText(kh.getMaKhachHang());
        nameField.setText(kh.getHoTen());
        phoneField.setText(kh.getSoDienThoai());

        emailField.setText(
            kh.getEmail() == null ? "" : kh.getEmail()
        );

        addressField.setText(
            kh.getDiaChi() == null ? "" : kh.getDiaChi()
        );

        statusCombo.setSelectedItem(
            kh.isTrangThai()
                ? "Đang hoạt động"
                : "Ngừng hoạt động"
        );

        groupCombo.setSelectedIndex(-1);
    }

    private void clearForm() {
        customerTable.clearSelection();

        customerIdField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");

        groupCombo.setSelectedIndex(-1);
        statusCombo.setSelectedIndex(0);

        nameField.requestFocusInWindow();
    }

    private void showPreviewMessage(String action) {
        JOptionPane.showMessageDialog(
            this,
            action + ": chưa gắn xử lý nghiệp vụ.",
            "Bản giao diện",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Chạy riêng để kiểm tra giao diện nếu cần.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Kiểm tra Khách hàng");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new KhachHangUI());
            frame.setSize(1340, 840);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}