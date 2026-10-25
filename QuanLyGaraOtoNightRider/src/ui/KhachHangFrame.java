package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import connectDB.ConnectDB;
import dao.KhachHangDao;
import entity.KhachHang;

public final class KhachHangFrame extends JFrame {

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
        new String[]{"Tất cả", "Đang hoạt động", "Ngừng hoạt động"}
    );

    private final JButton newButton = new JButton("Nhập khách mới");
    private final JButton addButton = new JButton("Thêm khách hàng");
    private final JButton editButton = new JButton("Sửa thông tin");
    private final JButton vehiclesButton = new JButton("Xem xe của khách");

    private final KhachHangDao khachHangDao = new KhachHangDao();

    private ArrayList<KhachHang> danhSachKhachHang = new ArrayList<>();

    private final JLabel lblSoLuong = new JLabel("Đang tải dữ liệu...");

    private final DefaultTableModel tableModel = new DefaultTableModel(
    	    new String[] {
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

    public KhachHangFrame() {
        super("Hệ thống Quản lý Gara Ô Tô - Quản lý khách hàng");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        buildInterface();
        bindEvents();

        // Tính kích thước theo form và vùng bảng đủ 8 dòng.
        pack();

        Dimension layoutSize = getSize();

        setMinimumSize(new Dimension(
            Math.max(1280, layoutSize.width),
            layoutSize.height
        ));

        setSize(
            Math.max(1340, layoutSize.width),
            Math.max(760, layoutSize.height)
        );

        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
     // SQL chưa có trường nhóm khách hàng.
        groupCombo.setSelectedIndex(-1);
        groupCombo.setEnabled(false);

        loadKhachHangFromSQL();
    }

    private void loadKhachHangFromSQL() {
        lblSoLuong.setText("Đang tải khách hàng...");

        new SwingWorker<ArrayList<KhachHang>, Void>() {
            @Override
            protected ArrayList<KhachHang> doInBackground() throws Exception {
                Connection conn = ConnectDB.getConnection();

                if (conn == null || conn.isClosed()) {
                    ConnectDB.getInstance().connect();
                    System.out.println("Kết nối database thành công!");
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
                        tableModel.addRow(new Object[] {
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

                    if (customerTable.getRowCount() > 0) {
                        customerTable.setRowSelectionInterval(0, 0);
                    }

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

                    lblSoLuong.setText("Không tải được dữ liệu");

                    JOptionPane.showMessageDialog(
                        KhachHangFrame.this,
                        "Không tải được khách hàng:\n" + loi.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        setContentPane(root);

        setJMenuBar(createMenuBar());
        root.add(createQuickToolbar(), BorderLayout.NORTH);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BACKGROUND);

        // Giảm khoảng trống bên ngoài.
        outer.setBorder(
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

        outer.add(content, BorderLayout.CENTER);
        root.add(outer, BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);
    }

    // ==================== MENU CHÍNH ====================

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        JLabel logo = new JLabel("NIGHT INVADER");
        logo.setFont(new Font("Arial", Font.BOLD, 15));
        logo.setForeground(new Color(210, 30, 30));
        logo.setBorder(
            BorderFactory.createEmptyBorder(5, 10, 5, 20)
        );
        menuBar.add(logo);

        String[] menuNames = {
            "Hệ thống", "Khách hàng", "Xe", "Dịch vụ",
            "Phụ tùng", "Kho hàng", "Hóa đơn",
            "Thống kê", "Nhân viên"
        };

        String[][] menuItems = {
            {
                "Tài khoản cá nhân", "Đổi mật khẩu",
                "-", "Đăng xuất"
            },
            {
                "Quản lý khách hàng"
            },
            {
                "Quản lý xe", "Tra cứu xe", "Lịch sử sửa chữa"
            },
            {
                "Quản lý dịch vụ", "Thêm dịch vụ",
                "Sửa dịch vụ", "Xóa dịch vụ", "-",
                "Lập phiếu tiếp nhận xe",
                "Lập phiếu sửa chữa / Báo giá",
                "Cập nhật tình trạng sửa xe"
            },
            {
                "Quản lý phụ tùng"
            },
            {
                "Lập phiếu nhập kho", "Lập phiếu xuất kho"
            },
            {
                "Lập hóa đơn", "Tra cứu hóa đơn"
            },
            {
                "Thống kê doanh thu", "Thống kê tồn kho",
                "Phụ tùng bán chạy", "Phụ tùng bán chậm"
            },
            {
                "Quản lý nhân sự", "Phân công kỹ thuật viên"
            }
        };

        for (int i = 0; i < menuNames.length; i++) {
            JMenu menu = createDropdownMenu(
                menuNames[i], menuItems[i]
            );

            if ("Khách hàng".equals(menuNames[i])) {
                menu.setForeground(RED);
            }

            menuBar.add(menu);
        }

        menuBar.add(Box.createHorizontalGlue());

        JMenu accountMenu = createDropdownMenu(
            "Xin chào, Chủ Gara",
            new String[]{
                "Tài khoản cá nhân", "Đổi mật khẩu",
                "-", "Đăng xuất"
            }
        );

        ImageIcon userIcon = loadIcon(
            "/img/icon_nguoi.jpg", 20, 20
        );

        if (userIcon != null) {
            accountMenu.setIcon(userIcon);
        }

        menuBar.add(accountMenu);
        return menuBar;
    }

    private JMenu createDropdownMenu(
            String title, String[] itemNames) {

        JMenu menu = new JMenu(title);
        menu.setFont(new Font("Arial", Font.BOLD, 16));
        menu.setMargin(new Insets(5, 10, 5, 10));

        for (String itemName : itemNames) {
            if ("-".equals(itemName)) {
                menu.addSeparator();
                continue;
            }

            JMenuItem item = new JMenuItem(itemName);
            item.setFont(new Font("Arial", Font.PLAIN, 14));
            item.setBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 18)
            );

            item.addActionListener(event -> {
                switch (itemName) {

                    // ================= HỆ THỐNG =================
                    case "Tài khoản cá nhân":
                        new GaraTaiKhoanCaNhan().setVisible(true);
                        dispose();
                        break;

                    case "Đổi mật khẩu":
                        showPreviewMessage(itemName);
                        break;

                    case "Đăng xuất":
                        showPreviewMessage(itemName);
                        break;

                    // ================= KHÁCH HÀNG =================
                    case "Quản lý khách hàng":
                        nameField.requestFocusInWindow();
                        break;

                    // ================= XE =================
                    case "Quản lý xe":
                        new QuanLyXeFrame().setVisible(true);
                        dispose();
                        break;

                    case "Tra cứu xe": {
                        QuanLyXeFrame manHinhXe = new QuanLyXeFrame();
                        manHinhXe.setVisible(true);
                        manHinhXe.chuyenDenTraCuu();
                        dispose();
                        break;
                    }

                    case "Lịch sử sửa chữa":
                        new LichSuSuaChuaFrame().setVisible(true);
                        dispose();
                        break;

                    // ================= DỊCH VỤ =================
                    case "Quản lý dịch vụ":
                    	new QuanLyDichVuFrame().setVisible(true);
                        dispose();
                        break;
                    case "Lập phiếu tiếp nhận xe":
                        new TiepNhanXeFrame().setVisible(true);
                        dispose();
                        break;

                    case "Lập phiếu sửa chữa / Báo giá":
                        new LapPhieuSuaChuaFrame().setVisible(true);
                        dispose();
                        break;

                    case "Cập nhật tình trạng sửa xe":
                        showPreviewMessage(itemName);
                        break;

                    // ================= PHỤ TÙNG =================
                    case "Quản lý phụ tùng":
                        new QuanLyPhuTungFrame().setVisible(true);
                        dispose();
                        break;

                    // ================= KHO HÀNG =================
                    case "Lập phiếu nhập kho":
                        showPreviewMessage(itemName);
                        break;

                    case "Lập phiếu xuất kho":
                        showPreviewMessage(itemName);
                        break;

                    // ================= HÓA ĐƠN =================
                    case "Lập hóa đơn":
                        showPreviewMessage(itemName);
                        break;

                    case "Tra cứu hóa đơn":
                        new GaraTraCuuHoaDon().setVisible(true);
                        dispose();
                        break;

                    // ================= THỐNG KÊ =================
                    case "Thống kê doanh thu":
                        new GaraThongKe(0).setVisible(true);
                        dispose();
                        break;

                    case "Thống kê tồn kho":
                        new GaraThongKe(1).setVisible(true);
                        dispose();
                        break;

                    case "Phụ tùng bán chạy":
                        new GaraThongKe(2).setVisible(true);
                        dispose();
                        break;

                    case "Phụ tùng bán chậm":
                        new GaraThongKe(3).setVisible(true);
                        dispose();
                        break;

                    // ================= NHÂN VIÊN =================
                    case "Quản lý nhân sự":
                        new GaraQuanLyNhanSu().setVisible(true);
                        dispose();
                        break;

                    case "Phân công kỹ thuật viên":
                        new GaraPhanCongKTV().setVisible(true);
                        dispose();
                        break;

                    default:
                        showPreviewMessage(itemName);
                        break;
                }
            });

            menu.add(item);
        }

        return menu;
    }

    // ==================== THANH NÚT NHANH ====================

    private JPanel createQuickToolbar() {
        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 6)
        );
        buttons.setBackground(Color.WHITE);

        buttons.add(createQuickButton(
            "Tiếp nhận xe", "F2", "/img/icon_xe.png"
        ));
        buttons.add(createQuickButton(
            "Tình trạng sửa xe", "F5", "/img/icon-vector.png"
        ));
        buttons.add(createQuickButton(
            "Lập hóa đơn", "F3", "/img/icon_hoadon.png"
        ));
        buttons.add(createQuickButton(
            "Nhập kho", "F4", "/img/icon_nhapkho.jpg"
        ));

        JLabel currentPage = new JLabel("Khách hàng");
        currentPage.setFont(new Font("Arial", Font.BOLD, 13));
        currentPage.setForeground(RED);
        currentPage.setBorder(
            BorderFactory.createEmptyBorder(0, 10, 0, 0)
        );
        buttons.add(currentPage);

        JPanel information = new JPanel(
            new GridLayout(2, 1, 0, 4)
        );
        information.setBackground(Color.WHITE);
        information.setBorder(
            BorderFactory.createEmptyBorder(6, 10, 6, 15)
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "EEEE, dd/MM/yyyy",
            Locale.forLanguageTag("vi-VN")
        );

        JLabel dateLabel = new JLabel(
            LocalDate.now().format(formatter),
            SwingConstants.RIGHT
        );
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel branchLabel = new JLabel(
            "Chi nhánh: Gara chính",
            SwingConstants.RIGHT
        );
        branchLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        branchLabel.setForeground(Color.GRAY);

        information.add(dateLabel);
        information.add(branchLabel);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER)
        );

        toolbar.add(buttons, BorderLayout.CENTER);
        toolbar.add(information, BorderLayout.EAST);

        return toolbar;
    }

    private JButton createQuickButton(
            String title, String shortcut, String iconPath) {

        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 0));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(200, 205, 210)
                ),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            )
        );

        ImageIcon icon = loadIcon(iconPath, 22, 22);

        if (icon != null) {
            button.add(new JLabel(icon), BorderLayout.WEST);
        }

        JPanel textPanel = new JPanel(
            new GridLayout(2, 1, 0, 3)
        );
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel shortcutLabel = new JLabel(shortcut);
        shortcutLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        shortcutLabel.setForeground(Color.GRAY);

        textPanel.add(titleLabel);
        textPanel.add(shortcutLabel);
        button.add(textPanel, BorderLayout.CENTER);

        Action action = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                showPreviewMessage(title);
            }
        };

        button.addActionListener(action);

        String actionKey = "quick-" + shortcut;

        getRootPane()
            .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(shortcut), actionKey);

        getRootPane().getActionMap().put(actionKey, action);

        return button;
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
            "Quản lý khách hàng độc lập; mỗi khách có thể sở hữu nhiều xe."
        );
        description.setFont(new Font("Arial", Font.PLAIN, 13));
        description.setForeground(Color.GRAY);

        titlePanel.add(title);
        titlePanel.add(description);
        section.add(titlePanel, BorderLayout.NORTH);

        // Thu gọn form thành 2 hàng, 4 cột.
        JPanel form = new JPanel(new GridLayout(2, 4, 12, 6));
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
        panel.add(fieldLabel, BorderLayout.NORTH);

        input.setFont(new Font("Arial", Font.PLAIN, 12));
        input.setPreferredSize(new Dimension(200, 26));
        panel.add(input, BorderLayout.CENTER);

        return panel;
    }

    // ==================== BẢNG KHÁCH HÀNG ====================

    private JPanel createTableSection() {
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setBackground(new Color(237, 240, 244));
        toolbar.setBorder(
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);

        searchField.setPreferredSize(new Dimension(300, 26));
        searchField.setToolTipText(
            "Tìm theo mã, tên, điện thoại hoặc biển số"
        );

        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new BorderLayout(8, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Trạng thái:"), BorderLayout.WEST);
        filterPanel.add(filterCombo, BorderLayout.CENTER);

        toolbar.add(searchPanel, BorderLayout.CENTER);
        toolbar.add(filterPanel, BorderLayout.EAST);

        section.add(toolbar, BorderLayout.NORTH);

        customerTable.setFont(new Font("Arial", Font.PLAIN, 13));
        customerTable.setRowHeight(28);

        // Vùng hiển thị ưu tiên đủ 8 dòng khách hàng.
        customerTable.setPreferredScrollableViewportSize(
            new Dimension(1100, 8 * 28)
        );
        customerTable.setFillsViewportHeight(true);

        customerTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        customerTable.setAutoCreateRowSorter(true);
        customerTable.setSelectionBackground(new Color(49, 106, 140));
        customerTable.setSelectionForeground(Color.WHITE);
        customerTable.setShowVerticalLines(false);
        customerTable.setGridColor(BORDER);

        customerTable.getTableHeader().setReorderingAllowed(false);
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

    // ==================== NÚT VÀ FOOTER ====================

    private JPanel createButtonSection() {
    	JButton homeButton = new JButton("Về màn hình chính");
    	homeButton.addActionListener(e -> {
    	    new GaraManHinhChinh().setVisible(true);
    	    dispose();
    	});
        JPanel panel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 6, 0)
        );
        panel.setOpaque(false);

     // Nút về màn hình chính: nền đỏ, chữ trắng.
        homeButton.setContentAreaFilled(false);
        homeButton.setOpaque(true);
        homeButton.setBackground(RED);
        homeButton.setForeground(Color.WHITE);
        homeButton.setFocusPainted(false);
        homeButton.setBorderPainted(false);
        homeButton.setPreferredSize(new Dimension(165, 30));

        // Nút xem xe: nền đỏ, chữ trắng.
        vehiclesButton.setContentAreaFilled(false);
        vehiclesButton.setOpaque(true);
        vehiclesButton.setBackground(Color.BLUE);
        vehiclesButton.setForeground(Color.WHITE);
        vehiclesButton.setFocusPainted(false);
        vehiclesButton.setBorderPainted(false);
        vehiclesButton.setPreferredSize(new Dimension(165, 30));

        // Kích thước các nút còn lại.
        newButton.setPreferredSize(new Dimension(165, 30));
        addButton.setPreferredSize(new Dimension(165, 30));
        editButton.setPreferredSize(new Dimension(165, 30));

        // Thêm từng nút vào thanh nút.
        panel.add(homeButton);
        panel.add(newButton);
        panel.add(addButton);
        panel.add(editButton);
        panel.add(vehiclesButton);
        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(245, 247, 250));
        footer.setBorder(
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        );

        JLabel stateLabel = new JLabel(
            "Đang hoạt động | Vai trò: Chủ Gara | Chi nhánh: Gara chính"
        );
        stateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        stateLabel.setForeground(Color.DARK_GRAY);

        JLabel brandLabel = new JLabel("NIGHT INVADER • Gara ô tô");
        brandLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        brandLabel.setForeground(Color.GRAY);

        footer.add(stateLabel, BorderLayout.WEST);
        footer.add(brandLabel, BorderLayout.EAST);

        return footer;
    }

    // ==================== SỰ KIỆN XEM TRƯỚC UI ====================

    private void bindEvents() {
        customerTable.getSelectionModel().addListSelectionListener(
            event -> {
                if (event.getValueIsAdjusting()) {
                    return;
                }

                int selectedRow = customerTable.getSelectedRow();

                if (selectedRow >= 0) {
                    int modelRow = customerTable.convertRowIndexToModel(
                        selectedRow
                    );
                    showCustomer(modelRow);
                }
            }
        );

        newButton.addActionListener(event -> clearForm());

        addButton.addActionListener(
            event -> showPreviewMessage("Thêm khách hàng")
        );

        editButton.addActionListener(
            event -> showPreviewMessage("Sửa thông tin")
        );

        vehiclesButton.addActionListener(event -> {
            QuanLyXeFrame manHinhXe = new QuanLyXeFrame();
            manHinhXe.setVisible(true);
            dispose();
        });
    }

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

        JTextField[] fields = {
            customerIdField, nameField, phoneField,
            emailField, addressField
        };

        for (JTextField field : fields) {
            field.setText("");
        }

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

    private ImageIcon loadIcon(String path, int width, int height) {
        URL resource = getClass().getResource(path);

        // Thiếu icon vẫn chạy được giao diện.
        if (resource == null) {
            return null;
        }

        ImageIcon original = new ImageIcon(resource);
        Image scaled = original.getImage().getScaledInstance(
            width, height, Image.SCALE_SMOOTH
        );

        return new ImageIcon(scaled);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            new KhachHangFrame().setVisible(true);
        });
    }
}