package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class CustomerFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color RED = new Color(190, 22, 58);
    private static final Color BACKGROUND = new Color(233, 237, 243);

    private final JTextField customerIdField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField searchField = new JTextField();

    private final JComboBox<String> groupCombo = new JComboBox<>(
        new String[] {"Cá nhân", "Doanh nghiệp"}
    );

    private final JComboBox<String> statusCombo = new JComboBox<>(
        new String[] {"Đang hoạt động", "Ngừng hoạt động"}
    );

    private final JComboBox<String> filterCombo = new JComboBox<>(
        new String[] {"Tất cả", "Đang hoạt động", "Ngừng hoạt động"}
    );

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new Object[][] {
            {"KH001", "Nguyễn Minh An", "0902 345 678",
                2, "05/10/2026", "Đang hoạt động"},
            {"KH002", "Lê Hoàng Nam", "0903 456 789",
                1, "05/10/2026", "Đang hoạt động"},
            {"KH003", "Võ Thu Hà", "0904 567 890",
                1, "04/10/2026", "Đang hoạt động"}
        },
        new String[] {
            "Mã KH", "Khách hàng", "Điện thoại",
            "Số xe", "Lần đến gần nhất", "Trạng thái"
        }
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

    private final JButton newButton = new JButton("Nhập khách mới");
    private final JButton addButton = new JButton("Thêm khách hàng");
    private final JButton editButton = new JButton("Sửa thông tin");
    private final JButton vehiclesButton = new JButton("Xem xe của khách");

    public CustomerFrame() {
        super("NIGHT INVADER - Quản lý khách hàng");

        configureWindow();
        buildInterface();
        bindEvents();

        customerTable.setRowSelectionInterval(0, 0);
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BACKGROUND);
        outer.setBorder(
            BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBackground(Color.WHITE);
        content.setBorder(
            BorderFactory.createEmptyBorder(22, 25, 22, 25)
        );

        content.add(createFormSection(), BorderLayout.NORTH);
        content.add(createTableSection(), BorderLayout.CENTER);
        content.add(createButtonSection(), BorderLayout.SOUTH);

        outer.add(content, BorderLayout.CENTER);
        root.add(outer, BorderLayout.CENTER);

        JLabel footer = new JLabel(
            "NIGHT INVADER • Giao diện quản lý khách hàng"
        );
        footer.setBorder(
            BorderFactory.createEmptyBorder(5, 20, 10, 20)
        );
        root.add(footer, BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(
            BorderFactory.createEmptyBorder(18, 24, 18, 24)
        );

        JLabel logo = new JLabel("NIGHT INVADER");
        logo.setForeground(RED);
        logo.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(logo, BorderLayout.WEST);

        JLabel navigation = new JLabel(
            "Hệ thống  ·  Khách hàng  ·  Xe  ·  Dịch vụ  ·  "
            + "Phụ tùng  ·  Kho hàng  ·  Hóa đơn  ·  Thống kê",
            SwingConstants.CENTER
        );
        header.add(navigation, BorderLayout.CENTER);

        return header;
    }

    private JPanel createFormSection() {
        JPanel section = new JPanel(new BorderLayout(0, 18));
        section.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 8));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        titlePanel.add(title);
        titlePanel.add(new JLabel(
            "Quản lý khách hàng độc lập; mỗi khách có thể sở hữu nhiều xe."
        ));

        section.add(titlePanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 20, 12));
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
        hintPanel.add(
            new JLabel("* Thông tin bắt buộc"),
            BorderLayout.SOUTH
        );
        form.add(hintPanel);

        section.add(form, BorderLayout.CENTER);

        return section;
    }

    private JPanel createField(String label, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        panel.add(new JLabel(label), BorderLayout.NORTH);
        input.setPreferredSize(new Dimension(200, 30));
        panel.add(input, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTableSection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setBackground(BACKGROUND);
        toolbar.setBorder(
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        searchField.setToolTipText(
            "Tìm theo mã, tên, điện thoại hoặc biển số"
        );

        JPanel filterPanel = new JPanel(new BorderLayout(8, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Trạng thái:"), BorderLayout.WEST);
        filterPanel.add(filterCombo, BorderLayout.CENTER);

        toolbar.add(searchPanel, BorderLayout.CENTER);
        toolbar.add(filterPanel, BorderLayout.EAST);

        section.add(toolbar, BorderLayout.NORTH);

        customerTable.setRowHeight(34);
        customerTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        customerTable.setAutoCreateRowSorter(true);
        customerTable.setSelectionBackground(new Color(49, 106, 140));
        customerTable.setSelectionForeground(Color.WHITE);
        customerTable.setShowVerticalLines(false);
        customerTable.setGridColor(BACKGROUND);
        customerTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        section.add(scrollPane, BorderLayout.CENTER);

        section.add(
            new JLabel("Hiển thị 3 khách hàng • Dữ liệu mẫu"),
            BorderLayout.SOUTH
        );

        return section;
    }

    private JPanel createButtonSection() {
        JPanel panel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        panel.setOpaque(false);

        JButton[] buttons = {
            newButton, addButton, editButton, vehiclesButton
        };

        for (JButton button : buttons) {
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(165, 36));
            panel.add(button);
        }

        vehiclesButton.setBackground(RED);
        vehiclesButton.setForeground(Color.WHITE);
        vehiclesButton.setOpaque(true);

        return panel;
    }

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
        customerIdField.setText(
            tableModel.getValueAt(row, 0).toString()
        );
        nameField.setText(
            tableModel.getValueAt(row, 1).toString()
        );
        phoneField.setText(
            tableModel.getValueAt(row, 2).toString()
        );

        emailField.setText(row == 0 ? "minhan@example.com" : "");
        addressField.setText(
            row == 0 ? "Quận 7, TP. Hồ Chí Minh" : "TP. Hồ Chí Minh"
        );

        groupCombo.setSelectedIndex(0);
        statusCombo.setSelectedItem(
            tableModel.getValueAt(row, 5).toString()
        );
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