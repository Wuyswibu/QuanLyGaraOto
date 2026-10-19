package ui;

import java.awt.*;
import java.io.File;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class LapPhieuTiepNhanXePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    private static final String[] XAC_NHAN = {
        "Đã kiểm tra", "Đã xác nhận", "Chờ chẩn đoán"
    };

    private static final DateTimeFormatter DINH_DANG_GIO =
        DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    private final JTextField txtMaTiepNhan =
        taoO("TN001");

    private final JTextField txtBienSoKhach =
        taoO("51H-123.45 • Nguyễn Minh An");

    private final JTextField txtThoiGianNhan =
        taoO("05/10/2026 • 08:30");

    private final JTextField txtHenTra =
        taoO("05/10/2026 • 16:00");

    private final JTextField txtSoKmNhienLieu =
        taoO("40.250 km / ½ bình");

    private final JTextField txtYeuCau =
        taoO("Bảo dưỡng 40.000 km; phanh có tiếng kêu");

    private final JTextField txtTimKiem = taoOTim();

    private final JComboBox<String> cboTrangThai =
        new JComboBox<>(
            new String[] {
                "Tất cả", "Đã kiểm tra",
                "Đã xác nhận", "Chờ chẩn đoán"
            }
        );

    private final DefaultTableModel model =
        new DefaultTableModel(
            new String[] {
                "Kiểm tra", "Hiện trạng", "Ghi chú", "Xác nhận"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }
        };

    private final JTable tableKiemTra = new JTable(model);

    private final TableRowSorter<DefaultTableModel> boLoc =
        new TableRowSorter<>(model);

    private final JButton btnChonAnh =
        taoNut("Chọn ảnh xe", false);

    private final List<File> anhXe = new ArrayList<>();

    // Dữ liệu phiếu mẫu đã lưu trong phiên chạy.
    private String[] thongTinDaLuu;
    private Object[][] kiemTraDaLuu;
    private List<File> anhDaLuu = new ArrayList<>();

    public LapPhieuTiepNhanXePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(216, 220, 228));
        setBorder(new EmptyBorder(20, 65, 15, 65));

        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));

        // ==================== TIÊU ĐỀ ====================
        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTieuDe = new JLabel("LẬP PHIẾU TIẾP NHẬN XE");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(CHU);

        tieuDe.add(lblTieuDe, BorderLayout.WEST);
        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 25));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));

        // ==================== THÔNG TIN TIẾP NHẬN ====================
        JPanel dau = new JPanel(new BorderLayout(0, 12));
        dau.setOpaque(false);

        JLabel lblMoTa = new JLabel(
            "Ghi nhận tình trạng ban đầu và yêu cầu của khách trước khi sửa."
        );

        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(CHU_PHU);

        dau.add(lblMoTa, BorderLayout.NORTH);

        JPanel form = new JPanel(
            new GridLayout(3, 2, 24, 10)
        );

        form.setOpaque(false);
        form.add(taoTruong("Mã tiếp nhận", txtMaTiepNhan));
        form.add(taoTruong("Biển số / khách", txtBienSoKhach));
        form.add(taoTruong("Thời gian nhận", txtThoiGianNhan));
        form.add(taoTruong("Hẹn trả", txtHenTra));
        form.add(taoTruong("Số km / nhiên liệu", txtSoKmNhienLieu));
        form.add(taoTruong("Yêu cầu", txtYeuCau));

        txtThoiGianNhan.setToolTipText(
            "Nhập dd/MM/yyyy • HH:mm hoặc dd/MM/yyyy HH:mm"
        );

        txtHenTra.setToolTipText(
            "Nhập dd/MM/yyyy • HH:mm hoặc dd/MM/yyyy HH:mm"
        );

        dau.add(form, BorderLayout.CENTER);
        noiDung.add(dau, BorderLayout.NORTH);

        // ==================== THANH TÌM KIẾM ====================
        JPanel vungBang = new JPanel(new BorderLayout(0, 12));
        vungBang.setOpaque(false);

        JPanel thanhLoc = new JPanel(new BorderLayout(20, 0));
        thanhLoc.setBackground(new Color(237, 241, 245));
        thanhLoc.setBorder(new EmptyBorder(5, 10, 5, 10));

        thanhLoc.add(txtTimKiem, BorderLayout.CENTER);

        JPanel locTrangThai = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 5, 0)
        );

        locTrangThai.setOpaque(false);

        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));

        cboTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        cboTrangThai.setBackground(Color.WHITE);

        locTrangThai.add(lblTrangThai);
        locTrangThai.add(cboTrangThai);

        thanhLoc.add(locTrangThai, BorderLayout.EAST);
        vungBang.add(thanhLoc, BorderLayout.NORTH);

        // ==================== BẢNG KIỂM TRA ====================
        tableKiemTra.setFont(new Font("Arial", Font.PLAIN, 13));
        tableKiemTra.setRowHeight(36);

        tableKiemTra.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        tableKiemTra.setRowSorter(boLoc);
        boLoc.setSortsOnUpdates(true);

        tableKiemTra.setShowGrid(false);
        tableKiemTra.setIntercellSpacing(new Dimension(0, 0));

        tableKiemTra.setSelectionBackground(
            new Color(53, 105, 139)
        );

        tableKiemTra.setSelectionForeground(Color.WHITE);

        tableKiemTra.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 12)
        );

        tableKiemTra.getTableHeader().setBackground(
            new Color(237, 241, 245)
        );

        tableKiemTra.getTableHeader().setPreferredSize(
            new Dimension(0, 30)
        );

        tableKiemTra.getTableHeader().setReorderingAllowed(false);

        tableKiemTra.putClientProperty(
            "terminateEditOnFocusLost", Boolean.TRUE
        );

        // Cột xác nhận dùng danh sách lựa chọn.
        tableKiemTra.getColumnModel()
            .getColumn(3)
            .setCellEditor(
                new DefaultCellEditor(
                    new JComboBox<>(XAC_NHAN)
                )
            );

        tableKiemTra.setDefaultRenderer(
            Object.class,
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table,
                        Object value,
                        boolean selected,
                        boolean focus,
                        int row,
                        int column) {

                    super.getTableCellRendererComponent(
                        table, value, selected, focus, row, column
                    );

                    setBorder(new EmptyBorder(0, 8, 0, 8));

                    if (!selected) {
                        setBackground(
                            row % 2 == 0
                                ? Color.WHITE
                                : new Color(247, 248, 250)
                        );

                        setForeground(CHU);
                    }

                    return this;
                }
            }
        );

        int[] doRong = {220, 280, 330, 240};

        for (int i = 0; i < doRong.length; i++) {
            tableKiemTra.getColumnModel()
                       .getColumn(i)
                       .setPreferredWidth(doRong[i]);
        }

        JScrollPane cuon = new JScrollPane(tableKiemTra);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(Color.WHITE);

        vungBang.add(cuon, BorderLayout.CENTER);
        noiDung.add(vungBang, BorderLayout.CENTER);

        // ==================== NÚT THAO TÁC ====================
        JPanel thaoTac = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        thaoTac.setOpaque(false);

        JButton btnLuu = taoNut("Lưu và lập báo giá", true);

        btnChonAnh.addActionListener(e -> chonAnhXe());
        btnLuu.addActionListener(e -> luuPhieu());

        thaoTac.add(btnChonAnh);
        thaoTac.add(btnLuu);

        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);

        add(khung, BorderLayout.CENTER);

        // ==================== DỮ LIỆU MẪU ====================
        model.addRow(new Object[] {
            "Ngoại thất",
            "Xước nhẹ cản sau",
            "Chụp ảnh tình trạng xe",
            "Đã kiểm tra"
        });

        model.addRow(new Object[] {
            "Tài sản",
            "01 chìa khóa",
            "Khách tự giữ tài sản cá nhân",
            "Đã xác nhận"
        });

        model.addRow(new Object[] {
            "Phanh",
            "Có tiếng kêu",
            "Cần kiểm tra má phanh",
            "Chờ chẩn đoán"
        });

        tableKiemTra.setRowSelectionInterval(0, 0);

        // ==================== SỰ KIỆN TÌM KIẾM ====================
        txtTimKiem.getDocument().addDocumentListener(
            new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    locKiemTra();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    locKiemTra();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    locKiemTra();
                }
            }
        );

        cboTrangThai.addActionListener(e -> locKiemTra());
    }

    private JPanel taoTruong(
            String ten, JComponent thanhPhan) {

        JPanel o = new JPanel(new BorderLayout(0, 4));
        o.setOpaque(false);

        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("Arial", Font.BOLD, 12));
        nhan.setForeground(new Color(87, 100, 117));
        nhan.setLabelFor(thanhPhan);

        o.add(nhan, BorderLayout.NORTH);
        o.add(thanhPhan, BorderLayout.CENTER);

        return o;
    }

    private static JTextField taoO(String giaTri) {
        JTextField field = new JTextField(giaTri);

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setForeground(CHU);
        field.setBackground(Color.WHITE);

        field.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VIEN),
                new EmptyBorder(6, 8, 6, 8)
            )
        );

        return field;
    }

    private static JTextField taoOTim() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                    );

                    g2.setColor(CHU_PHU);
                    g2.setFont(getFont());

                    int y = (
                        getHeight() - g2.getFontMetrics().getHeight()
                    ) / 2 + g2.getFontMetrics().getAscent();

                    g2.drawString(
                        "Tìm kiếm theo hạng mục, hiện trạng hoặc ghi chú...",
                        getInsets().left + 1,
                        y
                    );

                    g2.dispose();
                }
            }
        };

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setForeground(CHU);
        field.setBackground(new Color(237, 241, 245));
        field.setBorder(new EmptyBorder(5, 4, 5, 4));

        return field;
    }

    private static JButton taoNut(String ten, boolean chinh) {
        JButton nut = new JButton(ten);

        nut.setFont(new Font("Arial", Font.BOLD, 12));
        nut.setContentAreaFilled(false);
        nut.setOpaque(true);

        nut.setBackground(chinh ? DO : Color.WHITE);
        nut.setForeground(chinh ? Color.WHITE : CHU);
        nut.setFocusPainted(false);

        nut.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        nut.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    chinh ? DO : VIEN
                ),
                new EmptyBorder(9, 20, 9, 20)
            )
        );

        return nut;
    }

    private static String chuanHoa(String chuoi) {
        return Normalizer.normalize(
                chuoi, Normalizer.Form.NFD
            )
            .replaceAll("\\p{M}", "")
            .replace('đ', 'd')
            .replace('Đ', 'D')
            .toLowerCase(Locale.ROOT);
    }

    private void locKiemTra() {
        if (tableKiemTra.isEditing()
                && !tableKiemTra.getCellEditor().stopCellEditing()) {
            return;
        }

        final String tuKhoa = chuanHoa(
            txtTimKiem.getText().trim()
        );

        final String trangThai =
            (String) cboTrangThai.getSelectedItem();

        boLoc.setRowFilter(
            new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(
                    Entry<? extends DefaultTableModel,
                          ? extends Integer> entry) {

                    String noiDung =
                        entry.getStringValue(0) + " "
                        + entry.getStringValue(1) + " "
                        + entry.getStringValue(2);

                    boolean dungTuKhoa =
                        chuanHoa(noiDung).contains(tuKhoa);

                    boolean dungTrangThai =
                        "Tất cả".equals(trangThai)
                        || entry.getStringValue(3).equals(trangThai);

                    return dungTuKhoa && dungTrangThai;
                }
            }
        );

        if (tableKiemTra.getSelectedRow() < 0
                && tableKiemTra.getRowCount() > 0) {

            tableKiemTra.setRowSelectionInterval(0, 0);
        }
    }

    private void chonAnhXe() {
        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Chọn ảnh tình trạng xe");
        chooser.setMultiSelectionEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);

        chooser.setFileFilter(
            new FileNameExtensionFilter(
                "Ảnh xe (JPG, JPEG, PNG)",
                "jpg", "jpeg", "png"
            )
        );

        if (chooser.showOpenDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            for (File file : chooser.getSelectedFiles()) {
                String ten = file.getName()
                    .toLowerCase(Locale.ROOT);

                boolean dungDinhDang =
                    ten.endsWith(".jpg")
                    || ten.endsWith(".jpeg")
                    || ten.endsWith(".png");

                if (file.isFile()
                        && dungDinhDang
                        && !anhXe.contains(file)) {

                    anhXe.add(file);
                }
            }

            btnChonAnh.setText(
                anhXe.isEmpty()
                    ? "Chọn ảnh xe"
                    : "Ảnh xe (" + anhXe.size() + ")"
            );

            btnChonAnh.setToolTipText(
                anhXe.size() + " ảnh đã chọn trong phiên chạy"
            );
        }
    }

    private static LocalDateTime docThoiGian(String text) {
        String giaTri = text.replace('•', ' ')
            .trim()
            .replaceAll("\\s+", " ");

        return LocalDateTime.parse(giaTri, DINH_DANG_GIO);
    }

    private String kiemTraThongTin() {
        JTextField[] fields = {
            txtMaTiepNhan,
            txtBienSoKhach,
            txtThoiGianNhan,
            txtHenTra,
            txtSoKmNhienLieu,
            txtYeuCau
        };

        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return "Vui lòng nhập đầy đủ thông tin tiếp nhận xe.";
            }
        }

        try {
            LocalDateTime nhan = docThoiGian(
                txtThoiGianNhan.getText()
            );

            LocalDateTime tra = docThoiGian(
                txtHenTra.getText()
            );

            if (tra.isBefore(nhan)) {
                return "Thời gian hẹn trả không được trước thời gian nhận.";
            }

        } catch (DateTimeParseException e) {
            return "Nhập thời gian hợp lệ theo dạng dd/MM/yyyy • HH:mm.";
        }

        return null;
    }

    private void luuPhieu() {
        if (tableKiemTra.isEditing()
                && !tableKiemTra.getCellEditor().stopCellEditing()) {
            return;
        }

        String loi = kiemTraThongTin();

        if (loi != null) {
            JOptionPane.showMessageDialog(
                this,
                loi,
                "Thông tin chưa hợp lệ",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        thongTinDaLuu = new String[] {
            txtMaTiepNhan.getText().trim(),
            txtBienSoKhach.getText().trim(),
            txtThoiGianNhan.getText().trim(),
            txtHenTra.getText().trim(),
            txtSoKmNhienLieu.getText().trim(),
            txtYeuCau.getText().trim()
        };

        // Lưu tất cả hạng mục, kể cả dòng bị ẩn bởi bộ lọc.
        kiemTraDaLuu = new Object[
            model.getRowCount()
        ][model.getColumnCount()];

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                kiemTraDaLuu[i][j] = model.getValueAt(i, j);
            }
        }

        anhDaLuu = new ArrayList<>(anhXe);

        JOptionPane.showMessageDialog(
            this,
            "Đã lưu phiếu tiếp nhận mẫu: " + thongTinDaLuu[0]
                + "\nSố ảnh xe: " + anhDaLuu.size()
                + "\nDữ liệu chỉ lưu trong phiên chạy."
                + "\nChưa kết nối giao diện lập báo giá.",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==================== CỬA SỔ CHẠY ĐỘC LẬP ====================
    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame(
            "Lập phiếu tiếp nhận xe - Night Invader"
        );

        cuaSo.setSize(1340, 840);
        cuaSo.setLocationRelativeTo(null);
        cuaSo.setResizable(true);
        cuaSo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cuaSo.setLayout(new BorderLayout());

        // ==================== MENU BAR ====================
        JMenuBar menuBar = new JMenuBar();

        menuBar.setBackground(new Color(245, 247, 250));

        menuBar.setBorder(
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        Font menuFont = new Font("Arial", Font.BOLD, 16);

        JMenu menuLogo = new JMenu("NIGHT INVADER");

        menuLogo.setFont(new Font("Arial", Font.BOLD, 15));
        menuLogo.setForeground(new Color(210, 30, 30));
        menuLogo.setMargin(new Insets(5, 10, 5, 10));

        menuBar.add(menuLogo);

        String[] tenCacMenu = {
            "Hệ thống", "Khách hàng ", "Xe ", "Dịch vụ ",
            "Phụ tùng", "Kho hàng ", "Hóa đơn ",
            "Thống kê ", "Nhân viên "
        };

        String[][] cacItemMenu = {
            {
                "Tài khoản cá nhân",
                "Đổi mật khẩu",
                "-",
                "Đăng xuất"
            },
            {
                "Quản lý khách hàng"
            },
            {
                "Quản lý xe",
                "Tra cứu xe",
                "Lịch sử sửa chữa"
            },
            {
                "Quản lý dịch vụ",
                "Thêm dịch vụ",
                "Sửa dịch vụ",
                "Xóa dịch vụ",
                "-",
                "Lập phiếu tiếp nhận xe",
                "Lập phiếu sửa chữa / Báo giá",
                "Cập nhật tình trạng sửa xe"
            },
            {
                "Quản lý phụ tùng"
            },
            {
                "Lập phiếu nhập kho",
                "Lập phiếu xuất kho"
            },
            {
                "Lập hóa đơn",
                "Tra cứu hóa đơn"
            },
            {
                "Thống kê doanh thu",
                "Thống kê tồn kho",
                "Phụ tùng bán chạy",
                "Phụ tùng bán chậm"
            },
            {
                "Quản lý nhân sự",
                "Phân công kỹ thuật viên"
            }
        };

        for (int i = 0; i < tenCacMenu.length; i++) {
            JMenu menu = taoMenuDropdown(
                tenCacMenu[i], cacItemMenu[i]
            );

            menu.setFont(menuFont);
            menu.setMargin(new Insets(5, 10, 5, 10));

            menuBar.add(menu);
        }

        JMenu menuTaiKhoan = new JMenu("Xin chào, Chủ Gara");

        menuTaiKhoan.setFont(menuFont);
        menuTaiKhoan.setMargin(new Insets(5, 10, 5, 10));

        ImageIcon iconUserMenu = loadIcon(
            "/img/icon_nguoi.jpg", 20, 20
        );

        if (iconUserMenu != null) {
            menuTaiKhoan.setIcon(iconUserMenu);
        }

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuTaiKhoan);

        cuaSo.setJMenuBar(menuBar);

        // ==================== THANH CÔNG CỤ ====================
        JPanel panelToolbar = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 15, 10)
        );

        panelToolbar.setBackground(Color.WHITE);

        panelToolbar.setBorder(
            BorderFactory.createMatteBorder(
                0, 0, 1, 0,
                new Color(220, 224, 230)
            )
        );

        ImageIcon iconXeTB = loadIcon(
            "/img/icon_xe.png", 22, 22
        );

        ImageIcon iconHoaDonTB = loadIcon(
            "/img/icon_hoadon.png", 22, 22
        );

        ImageIcon iconNhapKhoTB = loadIcon(
            "/img/icon_nhapkho.jpg", 22, 22
        );

        ImageIcon iconVectorTB = loadIcon(
            "/img/icon-vector.png", 22, 22
        );

        panelToolbar.add(
            taoNutNhanh("Tiếp nhận xe", "F2", iconXeTB)
        );

        panelToolbar.add(
            taoNutNhanh(
                "Tình trạng sửa xe", "F5", iconVectorTB
            )
        );

        panelToolbar.add(
            taoNutNhanh("Lập hóa đơn", "F3", iconHoaDonTB)
        );

        panelToolbar.add(
            taoNutNhanh("Nhập kho", "F4", iconNhapKhoTB)
        );

        JPanel panelThongTinPhu = new JPanel(
            new GridLayout(2, 1)
        );

        panelThongTinPhu.setBackground(Color.WHITE);

        JLabel lblNgay = new JLabel(
            "Thứ Ba, 06/10/2026", JLabel.RIGHT
        );

        lblNgay.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel lblChiNhanh = new JLabel(
            "Chi nhánh: Gara chính", JLabel.RIGHT
        );

        lblChiNhanh.setFont(
            new Font("Arial", Font.PLAIN, 11)
        );

        lblChiNhanh.setForeground(Color.GRAY);

        panelThongTinPhu.add(lblNgay);
        panelThongTinPhu.add(lblChiNhanh);

        JPanel panelTopContainer = new JPanel(
            new BorderLayout()
        );

        panelTopContainer.add(
            panelToolbar, BorderLayout.CENTER
        );

        panelTopContainer.add(
            panelThongTinPhu, BorderLayout.EAST
        );

        panelTopContainer.setBorder(
            BorderFactory.createEmptyBorder(0, 10, 0, 15)
        );

        // ==================== FOOTER ====================
        JPanel panelFooter = new JPanel(new BorderLayout());

        panelFooter.setBackground(
            new Color(245, 247, 250)
        );

        panelFooter.setBorder(
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        );

        JLabel lblTrangThai = new JLabel(
            "Đang hoạt động | Vai trò: Chủ Gara | Chi nhánh: Gara chính"
        );

        lblTrangThai.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );

        lblTrangThai.setForeground(Color.DARK_GRAY);

        JLabel lblBanQuyen = new JLabel(
            "NIGHT INVADER • Gara ô tô"
        );

        lblBanQuyen.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );

        lblBanQuyen.setForeground(Color.GRAY);

        panelFooter.add(lblTrangThai, BorderLayout.WEST);
        panelFooter.add(lblBanQuyen, BorderLayout.EAST);

        cuaSo.add(panelTopContainer, BorderLayout.NORTH);
        cuaSo.add(this, BorderLayout.CENTER);
        cuaSo.add(panelFooter, BorderLayout.SOUTH);

        return cuaSo;
    }

    private ImageIcon loadIcon(
            String path, int width, int height) {

        try {
            java.net.URL imgURL = getClass().getResource(path);

            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);

                Image scaledImg = icon.getImage()
                    .getScaledInstance(
                        width, height, Image.SCALE_SMOOTH
                    );

                return new ImageIcon(scaledImg);
            }

        } catch (Exception e) {
            System.out.println(
                "Không thể tải ảnh từ đường dẫn: " + path
            );
        }

        return null;
    }

    private JMenu taoMenuDropdown(
            String tenMenu, String[] cacItem) {

        JMenu menu = new JMenu(tenMenu);

        for (String itemName : cacItem) {
            if (itemName.equals("-")) {
                menu.addSeparator();

            } else {
                JMenuItem item = new JMenuItem(itemName);

                item.addActionListener(e ->
                    JOptionPane.showMessageDialog(
                        this,
                        "Đang mở chức năng: " + itemName,
                        "Thông báo hệ thống",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                );

                menu.add(item);
            }
        }

        return menu;
    }

    private JButton taoNutNhanh(
            String tieuDe, String phimTat, ImageIcon icon) {

        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 0));

        if (icon != null) {
            JLabel lblIcon = new JLabel(icon);
            btn.add(lblIcon, BorderLayout.WEST);
        }

        JPanel pText = new JPanel(new GridLayout(2, 1));
        pText.setOpaque(false);

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(
            new Font("Arial", Font.BOLD, 12)
        );

        JLabel lblPhimTat = new JLabel(phimTat);
        lblPhimTat.setFont(
            new Font("Arial", Font.PLAIN, 11)
        );

        lblPhimTat.setForeground(Color.GRAY);

        pText.add(lblTieuDe);
        pText.add(lblPhimTat);

        btn.add(pText, BorderLayout.CENTER);

        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(200, 205, 210), 1
                ),
                BorderFactory.createEmptyBorder(
                    6, 10, 6, 10
                )
            )
        );

        btn.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "Đang mở nhanh chức năng: "
                    + tieuDe + " (" + phimTat + ")",
                "Phím tắt kích hoạt",
                JOptionPane.INFORMATION_MESSAGE
            )
        );

        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() ->
            new LapPhieuTiepNhanXePanel()
                .taoCuaSo()
                .setVisible(true)
        );
    }
}