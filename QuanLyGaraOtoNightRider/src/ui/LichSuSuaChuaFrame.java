package ui;

import java.awt.*;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class LichSuSuaChuaFrame extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    private static final DateTimeFormatter DINH_DANG_NGAY =
        DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    // Ngày, phiếu, số km, nội dung, tổng tiền, trạng thái,
    // biển số, chủ xe, hóa đơn.
    private final String[][] duLieuLichSu = {
        {
            "05/10/2026",
            "SC001",
            "40.250",
            "Bảo dưỡng / thay phanh",
            "2.484.000 đ",
            "Đang thực hiện",
            "51H-123.45",
            "Nguyễn Minh An",
            "HD001"
        },
        {
            "10/06/2026",
            "SC042",
            "35.100",
            "Thay dầu / lọc dầu",
            "950.000 đ",
            "Đã hoàn thành",
            "51H-123.45",
            "Nguyễn Minh An",
            "HD042"
        }
    };

    private final JTextField txtBienSo = taoO();
    private final JTextField txtChuXe = taoO();
    private final JTextField txtTuNgay = taoO();
    private final JTextField txtDenNgay = taoO();
    private final JTextField txtTimKiem = taoOTim();

    private final JComboBox<String> cboTrangThai =
        new JComboBox<>(
            new String[] {
                "Tất cả", "Đang thực hiện", "Đã hoàn thành"
            }
        );

    private final DefaultTableModel model =
        new DefaultTableModel(
            new String[] {
                "Ngày", "Phiếu", "Số km",
                "Nội dung", "Tổng tiền", "Trạng thái"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    private final JTable tableLichSu = new JTable(model);

    private final TableRowSorter<DefaultTableModel> boLoc =
        new TableRowSorter<>(model);

    private final JButton btnPhieu =
        taoNut("Xem phiếu sửa chữa", false);

    private final JButton btnHoaDon =
        taoNut("Xem hóa đơn", true);

    public LichSuSuaChuaFrame() {
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

        JLabel lblTieuDe = new JLabel("LỊCH SỬ SỬA CHỮA");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(CHU);

        tieuDe.add(lblTieuDe, BorderLayout.WEST);
        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 25));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));

        // ==================== THÔNG TIN XE ====================
        JPanel dau = new JPanel(new BorderLayout(0, 12));
        dau.setOpaque(false);

        JLabel lblMoTa = new JLabel(
            "Theo dõi dịch vụ, phụ tùng và bảo hành theo từng lần sửa."
        );

        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(CHU_PHU);

        dau.add(lblMoTa, BorderLayout.NORTH);

        JPanel form = new JPanel(
            new GridLayout(2, 2, 24, 10)
        );

        form.setOpaque(false);

        form.add(taoTruong("Biển số", txtBienSo));
        form.add(taoTruong("Chủ xe", txtChuXe));
        form.add(taoTruong("Từ ngày", txtTuNgay));
        form.add(taoTruong("Đến ngày", txtDenNgay));

        txtBienSo.setEditable(false);
        txtChuXe.setEditable(false);

        txtTuNgay.setText("01/01/2026");
        txtDenNgay.setText("05/10/2026");

        txtTuNgay.setToolTipText(
            "Nhập dd/MM/yyyy rồi nhấn Enter để lọc; "
            + "bỏ trống để không giới hạn"
        );

        txtDenNgay.setToolTipText(
            "Nhập dd/MM/yyyy rồi nhấn Enter để lọc; "
            + "bỏ trống để không giới hạn"
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

        // ==================== BẢNG LỊCH SỬ ====================
        tableLichSu.setFont(new Font("Arial", Font.PLAIN, 13));
        tableLichSu.setRowHeight(36);

        tableLichSu.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        tableLichSu.setRowSorter(boLoc);
        tableLichSu.setShowGrid(false);
        tableLichSu.setIntercellSpacing(new Dimension(0, 0));

        tableLichSu.setSelectionBackground(
            new Color(53, 105, 139)
        );

        tableLichSu.setSelectionForeground(Color.WHITE);

        tableLichSu.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 12)
        );

        tableLichSu.getTableHeader().setBackground(
            new Color(237, 241, 245)
        );

        tableLichSu.getTableHeader().setPreferredSize(
            new Dimension(0, 30)
        );

        tableLichSu.getTableHeader().setReorderingAllowed(false);

        tableLichSu.setDefaultRenderer(
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

        int[] doRong = {150, 140, 130, 300, 170, 200};

        for (int i = 0; i < doRong.length; i++) {
            tableLichSu.getColumnModel()
                       .getColumn(i)
                       .setPreferredWidth(doRong[i]);
        }

        // Sắp xếp ngày theo thời gian.
        boLoc.setComparator(0, (a, b) ->
            LocalDate.parse(a.toString(), DINH_DANG_NGAY)
                .compareTo(
                    LocalDate.parse(
                        b.toString(), DINH_DANG_NGAY
                    )
                )
        );

        JScrollPane cuon = new JScrollPane(tableLichSu);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(Color.WHITE);

        vungBang.add(cuon, BorderLayout.CENTER);
        noiDung.add(vungBang, BorderLayout.CENTER);

        // ==================== NÚT THAO TÁC ====================
        JPanel thaoTac = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        thaoTac.setOpaque(false);

        // Chỉ thông báo, chưa mở trang khác.
        btnPhieu.addActionListener(
            e -> thongBaoChuaKetNoi(false)
        );

        btnHoaDon.addActionListener(
            e -> thongBaoChuaKetNoi(true)
        );

        thaoTac.add(btnPhieu);
        thaoTac.add(btnHoaDon);

        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);

        add(khung, BorderLayout.CENTER);

        // ==================== DỮ LIỆU MẪU ====================
        for (String[] dong : duLieuLichSu) {
            model.addRow(new Object[] {
                dong[0], dong[1], dong[2],
                dong[3], dong[4], dong[5]
            });
        }

        // ==================== SỰ KIỆN ====================
        tableLichSu.getSelectionModel()
            .addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    hienThiThongTinXe();
                }
            });

        txtTimKiem.getDocument().addDocumentListener(
            new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    locLichSu(false);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    locLichSu(false);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    locLichSu(false);
                }
            }
        );

        cboTrangThai.addActionListener(
            e -> locLichSu(true)
        );

        txtTuNgay.addActionListener(
            e -> locLichSu(true)
        );

        txtDenNgay.addActionListener(
            e -> locLichSu(true)
        );

        locLichSu(false);
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

    private static JTextField taoO() {
        JTextField field = new JTextField();

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
                        "Tìm kiếm theo mã, tên hoặc biển số...",
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

        field.setToolTipText(
            "Tìm theo phiếu, hóa đơn, tên chủ xe, "
            + "biển số hoặc nội dung sửa chữa"
        );

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
            .toLowerCase(Locale.ROOT)
            .replaceAll("\\s+", "");
    }

    private void hienThiThongTinXe() {
        int dong = tableLichSu.getSelectedRow();

        btnPhieu.setEnabled(dong >= 0);
        btnHoaDon.setEnabled(dong >= 0);

        if (dong < 0) {
            txtBienSo.setText("");
            txtChuXe.setText("");
            return;
        }

        int dongModel =
            tableLichSu.convertRowIndexToModel(dong);

        String[] lichSu = duLieuLichSu[dongModel];

        txtBienSo.setText(lichSu[6]);
        txtChuXe.setText(lichSu[7]);
    }

    private void locLichSu(boolean thongBao) {
        LocalDate tuNgay;
        LocalDate denNgay;

        try {
            tuNgay = txtTuNgay.getText().trim().isEmpty()
                ? null
                : LocalDate.parse(
                    txtTuNgay.getText().trim(),
                    DINH_DANG_NGAY
                );

            denNgay = txtDenNgay.getText().trim().isEmpty()
                ? null
                : LocalDate.parse(
                    txtDenNgay.getText().trim(),
                    DINH_DANG_NGAY
                );

        } catch (DateTimeParseException e) {
            if (thongBao) {
                JOptionPane.showMessageDialog(
                    this,
                    "Nhập ngày hợp lệ theo dạng dd/MM/yyyy.",
                    "Ngày không hợp lệ",
                    JOptionPane.WARNING_MESSAGE
                );
            }

            return;
        }

        if (tuNgay != null
                && denNgay != null
                && tuNgay.isAfter(denNgay)) {

            if (thongBao) {
                JOptionPane.showMessageDialog(
                    this,
                    "Từ ngày không được lớn hơn đến ngày.",
                    "Khoảng ngày không hợp lệ",
                    JOptionPane.WARNING_MESSAGE
                );
            }

            return;
        }

        final LocalDate tu = tuNgay;
        final LocalDate den = denNgay;

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

                    String[] lichSu = duLieuLichSu[
                        entry.getIdentifier()
                    ];

                    LocalDate ngay = LocalDate.parse(
                        lichSu[0], DINH_DANG_NGAY
                    );

                    return (
                            tu == null || !ngay.isBefore(tu)
                        )
                        && (
                            den == null || !ngay.isAfter(den)
                        )
                        && chuanHoa(
                            String.join(" ", lichSu)
                        ).contains(tuKhoa)
                        && (
                            "Tất cả".equals(trangThai)
                            || lichSu[5].equals(trangThai)
                        );
                }
            }
        );

        if (tableLichSu.getSelectedRow() < 0
                && tableLichSu.getRowCount() > 0) {

            tableLichSu.setRowSelectionInterval(0, 0);
        }

        hienThiThongTinXe();
    }

    private void thongBaoChuaKetNoi(boolean hoaDon) {
        int dong = tableLichSu.getSelectedRow();

        if (dong < 0) {
            return;
        }

        int dongModel =
            tableLichSu.convertRowIndexToModel(dong);

        String[] lichSu = duLieuLichSu[dongModel];

        String loai = hoaDon
            ? "hóa đơn"
            : "phiếu sửa chữa";

        String ma = hoaDon
            ? lichSu[8]
            : lichSu[1];

        JOptionPane.showMessageDialog(
            this,
            "Đã chọn " + loai + ": " + ma
                + "\nChưa kết nối giao diện xem " + loai + ".",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==================== CỬA SỔ CHẠY ĐỘC LẬP ====================
    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame(
            "Lịch sử sửa chữa - Night Invader"
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
            new LichSuSuaChuaFrame().taoCuaSo().setVisible(true)
        );
    }
}