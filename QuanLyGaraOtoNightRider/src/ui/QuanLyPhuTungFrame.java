package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class QuanLyPhuTungFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color RED = new Color(190, 22, 58);
    private static final Color BACKGROUND = new Color(240, 243, 246);
    private static final Color BORDER = new Color(220, 224, 230);

    private final JTextField codeField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField unitField = new JTextField();
    private final JTextField purchasePriceField = new JTextField();
    private final JTextField salePriceField = new JTextField();
    private final JTextField minimumStockField = new JTextField();
    private final JTextField locationField = new JTextField();
    private final JTextField searchField = new JTextField();

    private final JComboBox<String> statusCombo = new JComboBox<>(
        new String[]{"Đang kinh doanh", "Ngừng kinh doanh"}
    );

    private final JComboBox<String> filterCombo = new JComboBox<>(
        new String[]{"Tất cả", "Đang kinh doanh", "Ngừng kinh doanh"}
    );

    private final JButton addButton = new JButton("Thêm phụ tùng");
    private final JButton editButton = new JButton("Sửa phụ tùng");
    private final JButton stopButton = new JButton("Ngừng kinh doanh");
    private final JButton searchButton = new JButton("Tìm kiếm");

    private final NumberFormat moneyFormat =
        NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

    /*
     * Các cột dữ liệu:
     * mã, tên, đơn vị, giá nhập, giá bán,
     * tồn kho, tồn tối thiểu, vị trí, trạng thái.
     */
    private final Object[][] sampleParts = {
        {
            "PT001", "Dầu động cơ 5W-30", "Lít",
            130000L, 180000L, 38, 10, "Kệ A1", "Đang kinh doanh"
        },
        {
            "PT002", "Lọc dầu Toyota", "Cái",
            120000L, 180000L, 12, 5, "Kệ A2", "Đang kinh doanh"
        },
        {
            "PT003", "Má phanh trước", "Bộ",
            700000L, 950000L, 3, 5, "Kệ B1", "Đang kinh doanh"
        },
        {
            "PT004", "Lọc gió động cơ", "Cái",
            150000L, 220000L, 20, 5, "Kệ A3", "Đang kinh doanh"
        },
        {
            "PT005", "Bugi NGK", "Cái",
            80000L, 120000L, 40, 10, "Kệ B2", "Đang kinh doanh"
        },
        {
            "PT006", "Ắc quy 12V 60Ah", "Bình",
            1200000L, 1550000L, 8, 3, "Kệ C1", "Đang kinh doanh"
        },
        {
            "PT007", "Nước làm mát", "Chai",
            70000L, 110000L, 25, 8, "Kệ A4", "Đang kinh doanh"
        },
        {
            "PT008", "Dây curoa", "Cái",
            250000L, 350000L, 15, 5, "Kệ B3", "Đang kinh doanh"
        },
        {
            "PT009", "Gạt mưa trước", "Bộ",
            180000L, 280000L, 18, 5, "Kệ C2", "Đang kinh doanh"
        },
        {
            "PT010", "Lọc gió điều hòa", "Cái",
            110000L, 170000L, 6, 4, "Kệ A5", "Ngừng kinh doanh"
        }
    };

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new String[]{
            "Mã PT", "Tên phụ tùng", "ĐVT", "Giá bán",
            "Tồn kho", "Tồn tối thiểu"
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
                return Long.class;
            }
            if (column == 4 || column == 5) {
                return Integer.class;
            }
            return String.class;
        }
    };

    private final JTable partsTable = new JTable(tableModel);

    public QuanLyPhuTungFrame() {
        super("Hệ thống Quản lý Gara Ô Tô - Quản lý phụ tùng");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        loadSampleRows();
        buildInterface();
        bindEvents();

        // pack() tính đủ chiều cao cho form và ít nhất 8 dòng bảng.
        pack();

        Dimension preferred = getSize();

        setMinimumSize(new Dimension(
            Math.max(1280, preferred.width),
            preferred.height
        ));

        setSize(
            Math.max(1340, preferred.width),
            Math.max(760, preferred.height)
        );

        setLocationRelativeTo(null);
        partsTable.setRowSelectionInterval(0, 0);
    }

    private void loadSampleRows() {
        for (Object[] part : sampleParts) {
            tableModel.addRow(new Object[]{
                part[0], part[1], part[2],
                part[4], part[5], part[6]
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

            if ("Phụ tùng".equals(menuNames[i])) {
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

        ImageIcon icon = loadIcon("/img/icon_nguoi.jpg", 20, 20);
        if (icon != null) {
            accountMenu.setIcon(icon);
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
                if ("Quản lý phụ tùng".equals(itemName)) {
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

        JLabel currentPage = new JLabel("Phụ tùng");
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

    // ==================== FORM PHỤ TÙNG ====================

    private JPanel createFormSection() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setOpaque(false);

        JPanel titlePanel = new JPanel(
            new GridLayout(2, 1, 0, 3)
        );
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("QUẢN LÝ PHỤ TÙNG");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(30, 35, 45));

        JLabel description = new JLabel(
            "Danh mục phụ tùng riêng; tồn kho cập nhật qua phiếu nhập và xuất."
        );
        description.setFont(new Font("Arial", Font.PLAIN, 13));
        description.setForeground(Color.GRAY);

        titlePanel.add(title);
        titlePanel.add(description);
        section.add(titlePanel, BorderLayout.NORTH);

        // 2 hàng giúp bảng danh sách có nhiều chiều cao hơn.
        JPanel form = new JPanel(new GridLayout(2, 4, 12, 6));
        form.setOpaque(false);

        codeField.setEditable(false);

        form.add(createField("Mã phụ tùng", codeField));
        form.add(createField("Tên phụ tùng", nameField));
        form.add(createField("Đơn vị tính", unitField));
        form.add(createField("Giá nhập (đ)", purchasePriceField));

        form.add(createField("Giá bán (đ)", salePriceField));
        form.add(createField("Tồn tối thiểu", minimumStockField));
        form.add(createField("Vị trí", locationField));
        form.add(createField("Trạng thái", statusCombo));

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

    // ==================== DANH SÁCH PHỤ TÙNG ====================

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
        searchField.setToolTipText("Tìm theo mã hoặc tên phụ tùng");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setPreferredSize(new Dimension(100, 26));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel filterPanel = new JPanel(new BorderLayout(8, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Trạng thái:"), BorderLayout.WEST);
        filterPanel.add(filterCombo, BorderLayout.CENTER);

        toolbar.add(searchPanel, BorderLayout.CENTER);
        toolbar.add(filterPanel, BorderLayout.EAST);
        section.add(toolbar, BorderLayout.NORTH);

        partsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        partsTable.setRowHeight(28);

        // Chiều cao vùng dữ liệu đủ ít nhất 8 dòng.
        partsTable.setPreferredScrollableViewportSize(
            new Dimension(1100, 8 * 28)
        );
        partsTable.setFillsViewportHeight(true);

        partsTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        partsTable.setAutoCreateRowSorter(true);
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

        partsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        partsTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        partsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        partsTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        partsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        partsTable.getColumnModel().getColumn(5).setPreferredWidth(140);

        // Hiển thị giá bán có phân cách hàng nghìn.
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
        section.add(scrollPane, BorderLayout.CENTER);

        JLabel count = new JLabel(
            "Danh sách " + tableModel.getRowCount()
            + " phụ tùng • Dữ liệu mẫu"
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
            addButton, editButton, stopButton
        };

        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setPreferredSize(new Dimension(145, 28));
            button.setFocusPainted(false);
            button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            );
            panel.add(button);
        }

        stopButton.setBackground(RED);
        stopButton.setForeground(Color.WHITE);
        stopButton.setOpaque(true);

        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(245, 247, 250));
        footer.setBorder(
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        );

        JLabel stateLabel = new JLabel(
            "Đang hoạt động | Kho chính | Vai trò: Chủ Gara"
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
        partsTable.getSelectionModel().addListSelectionListener(
            event -> {
                if (event.getValueIsAdjusting()) {
                    return;
                }

                int selectedRow = partsTable.getSelectedRow();

                if (selectedRow >= 0) {
                    int modelRow = partsTable.convertRowIndexToModel(
                        selectedRow
                    );
                    showSamplePart(modelRow);
                }
            }
        );

        addButton.addActionListener(
            event -> showPreviewMessage("Thêm phụ tùng")
        );

        editButton.addActionListener(
            event -> showPreviewMessage("Sửa phụ tùng")
        );

        stopButton.addActionListener(
            event -> showPreviewMessage("Ngừng kinh doanh")
        );
        searchButton.addActionListener(
        	    event -> showPreviewMessage("Tìm kiếm phụ tùng")
        	);

        	// Nhấn Enter trong ô tìm kiếm cũng kích hoạt nút.
        	searchField.addActionListener(
        	    event -> searchButton.doClick()
        	);
    }

    private void showSamplePart(int row) {
        Object[] part = sampleParts[row];

        codeField.setText(part[0].toString());
        nameField.setText(part[1].toString());
        unitField.setText(part[2].toString());
        purchasePriceField.setText(moneyFormat.format(part[3]));
        salePriceField.setText(moneyFormat.format(part[4]));
        minimumStockField.setText(part[6].toString());
        locationField.setText(part[7].toString());
        statusCombo.setSelectedItem(part[8].toString());
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

        // Thiếu icon vẫn chạy bình thường.
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

            new QuanLyPhuTungFrame().setVisible(true);
        });
    }
}