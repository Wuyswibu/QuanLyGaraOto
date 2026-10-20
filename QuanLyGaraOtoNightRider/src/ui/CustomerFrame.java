package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class CustomerFrame extends JFrame {

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

    // Dữ liệu mẫu, chỉ dùng hiển thị giao diện.
    private final Object[][] sampleCustomers = {
        {
            "KH001", "Nguyễn Minh An", "0902 345 678",
            2, "05/10/2026", "Đang hoạt động",
            "minhan@example.com", "Quận 7, TP. Hồ Chí Minh", "Cá nhân"
        },
        {
            "KH002", "Lê Hoàng Nam", "0903 456 789",
            1, "05/10/2026", "Đang hoạt động",
            "", "TP. Hồ Chí Minh", "Cá nhân"
        },
        {
            "KH003", "Võ Thu Hà", "0904 567 890",
            1, "04/10/2026", "Đang hoạt động",
            "", "TP. Hồ Chí Minh", "Cá nhân"
        },
        {
            "KH004", "Nguyễn Minh An", "0902 345 678",
            2, "05/10/2026", "Đang hoạt động",
            "minhan@example.com", "Quận 7, TP. Hồ Chí Minh", "Cá nhân"
        },
        {
            "KH005", "Lê Hoàng Nam", "0903 456 789",
            1, "05/10/2026", "Đang hoạt động",
            "", "TP. Hồ Chí Minh", "Cá nhân"
        },
        {
            "KH006", "Võ Thu Hà", "0904 567 890",
            1, "04/10/2026", "Đang hoạt động",
            "", "TP. Hồ Chí Minh", "Cá nhân"
        }
    };

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new String[]{
            "Mã KH", "Khách hàng", "Điện thoại",
            "Số xe", "Lần đến gần nhất", "Trạng thái"
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
            return column == 3 ? Integer.class : String.class;
        }
    };

    private final JTable customerTable = new JTable(tableModel);

    public CustomerFrame() {
        super("Hệ thống Quản lý Gara Ô Tô - Quản lý khách hàng");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        loadSampleRows();
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
        customerTable.setRowSelectionInterval(0, 0);
    }

    private void loadSampleRows() {
        for (Object[] customer : sampleCustomers) {
            tableModel.addRow(new Object[]{
                customer[0], customer[1], customer[2],
                customer[3], customer[4], customer[5]
            });
        }
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
                if ("Quản lý khách hàng".equals(itemName)) {
                    nameField.requestFocusInWindow();
                } else {
                    showPreviewMessage(itemName);
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

        JLabel count = new JLabel(
            "Danh sách " + tableModel.getRowCount()
            + " khách hàng • Dữ liệu mẫu"
        );
        count.setForeground(Color.GRAY);

        section.add(count, BorderLayout.SOUTH);

        return section;
    }

    // ==================== NÚT VÀ FOOTER ====================

    private JPanel createButtonSection() {
        JPanel panel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 6, 0)
        );
        panel.setOpaque(false);

        JButton[] buttons = {
            newButton, addButton, editButton, vehiclesButton
        };

        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(145, 28));
            button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            );

            panel.add(button);
        }

        vehiclesButton.setBackground(RED);
        vehiclesButton.setForeground(Color.WHITE);
        vehiclesButton.setOpaque(true);

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
                    showSampleCustomer(modelRow);
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

        vehiclesButton.addActionListener(
            event -> showPreviewMessage("Xem xe của khách")
        );
    }

    private void showSampleCustomer(int row) {
        Object[] customer = sampleCustomers[row];

        customerIdField.setText(customer[0].toString());
        nameField.setText(customer[1].toString());
        phoneField.setText(customer[2].toString());
        statusCombo.setSelectedItem(customer[5].toString());
        emailField.setText(customer[6].toString());
        addressField.setText(customer[7].toString());
        groupCombo.setSelectedItem(customer[8].toString());
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

        groupCombo.setSelectedIndex(0);
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

            new CustomerFrame().setVisible(true);
        });
    }
}