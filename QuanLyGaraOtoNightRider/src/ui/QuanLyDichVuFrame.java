package ui;

import java.awt.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class QuanLyDichVuFrame extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    private static final String[] NHAN = {
        "Mã dịch vụ", "Tên dịch vụ",
        "Đơn giá tiền công", "Thời gian dự kiến",
        "Đơn vị tính", "Trạng thái"
    };

    private static final String[] TRANG_THAI = {
        "Đang áp dụng", "Ngừng áp dụng"
    };

    // Mã, tên, đơn vị, đơn giá, thời gian, trạng thái.
    private final List<String[]> danhSachDichVu =
        new ArrayList<>();

    private final JTextField[] thongTin =
        new JTextField[6];

    private final JTextField txtTimKiem = taoOTim();

    private final JComboBox<String> cboTrangThai =
        new JComboBox<>(
            new String[] {
                "Tất cả", "Đang áp dụng", "Ngừng áp dụng"
            }
        );

    private final DefaultTableModel model =
        new DefaultTableModel(
            new String[] {
                "Mã DV", "Tên dịch vụ", "Đơn vị",
                "Đơn giá công", "Thời gian", "Trạng thái"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    private final JTable tableDichVu = new JTable(model);

    private final TableRowSorter<DefaultTableModel> boLoc =
        new TableRowSorter<>(model);

    private final JButton btnSua =
        taoNut("Sửa dịch vụ", false);

    private final JButton btnXoa =
        taoNut("Xóa dịch vụ", true);

    public QuanLyDichVuFrame() {
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

        JLabel lblTieuDe = new JLabel("QUẢN LÝ DỊCH VỤ");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(CHU);

        tieuDe.add(lblTieuDe, BorderLayout.WEST);
        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 25));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));

        // ==================== THÔNG TIN DỊCH VỤ ====================
        JPanel dau = new JPanel(new BorderLayout(0, 12));
        dau.setOpaque(false);

        JLabel lblMoTa = new JLabel(
            "Danh mục dịch vụ độc lập với phụ tùng và kho hàng."
        );

        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(CHU_PHU);

        dau.add(lblMoTa, BorderLayout.NORTH);

        // Chỉ hiển thị thông tin; chỉnh sửa bằng nút Sửa dịch vụ.
        dau.add(taoForm(thongTin, false), BorderLayout.CENTER);

        noiDung.add(dau, BorderLayout.NORTH);

        // ==================== TÌM KIẾM ====================
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

        // ==================== BẢNG DỊCH VỤ ====================
        tableDichVu.setFont(new Font("Arial", Font.PLAIN, 13));
        tableDichVu.setRowHeight(36);

        tableDichVu.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        tableDichVu.setRowSorter(boLoc);
        tableDichVu.setShowGrid(false);
        tableDichVu.setIntercellSpacing(new Dimension(0, 0));

        tableDichVu.setSelectionBackground(
            new Color(53, 105, 139)
        );

        tableDichVu.setSelectionForeground(Color.WHITE);

        tableDichVu.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 12)
        );

        tableDichVu.getTableHeader().setBackground(
            new Color(237, 241, 245)
        );

        tableDichVu.getTableHeader().setPreferredSize(
            new Dimension(0, 30)
        );

        tableDichVu.getTableHeader().setReorderingAllowed(false);

        tableDichVu.setDefaultRenderer(
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

        int[] doRong = {150, 270, 130, 190, 160, 200};

        for (int i = 0; i < doRong.length; i++) {
            tableDichVu.getColumnModel()
                       .getColumn(i)
                       .setPreferredWidth(doRong[i]);
        }

        JScrollPane cuon = new JScrollPane(tableDichVu);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(Color.WHITE);

        vungBang.add(cuon, BorderLayout.CENTER);
        noiDung.add(vungBang, BorderLayout.CENTER);

        // ==================== NÚT THAO TÁC ====================
        JPanel thaoTac = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        thaoTac.setOpaque(false);

        JButton btnThem = taoNut("Thêm dịch vụ", false);

        btnThem.addActionListener(
            e -> moHopThoaiDichVu(false)
        );

        btnSua.addActionListener(
            e -> moHopThoaiDichVu(true)
        );

        btnXoa.addActionListener(
            e -> xoaDichVu()
        );

        thaoTac.add(btnThem);
        thaoTac.add(btnSua);
        thaoTac.add(btnXoa);

        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);

        add(khung, BorderLayout.CENTER);

        // ==================== SỰ KIỆN ====================
        tableDichVu.getSelectionModel()
            .addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    hienThiThongTinDichVu();
                }
            });

        txtTimKiem.getDocument().addDocumentListener(
            new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    locDichVu();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    locDichVu();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    locDichVu();
                }
            }
        );

        cboTrangThai.addActionListener(e -> locDichVu());

        // ==================== DỮ LIỆU MẪU ====================
        danhSachDichVu.add(new String[] {
            "DV001", "Thay dầu động cơ", "Lần",
            "150.000 đ", "30 phút", "Đang áp dụng"
        });

        danhSachDichVu.add(new String[] {
            "DV002", "Thay má phanh trước", "Lần",
            "300.000 đ", "60 phút", "Đang áp dụng"
        });

        danhSachDichVu.add(new String[] {
            "DV003", "Kiểm tra điều hòa", "Lần",
            "200.000 đ", "45 phút", "Đang áp dụng"
        });

        capNhatBang(0);
    }

    private JPanel taoForm(
            JTextField[] fields, boolean choSua) {

        JPanel form = new JPanel(
            new GridLayout(3, 2, 24, 10)
        );

        form.setOpaque(false);

        for (int i = 0; i < fields.length; i++) {
            JPanel o = new JPanel(new BorderLayout(0, 4));
            o.setOpaque(false);

            JLabel nhan = new JLabel(NHAN[i]);
            nhan.setFont(new Font("Arial", Font.BOLD, 12));
            nhan.setForeground(new Color(87, 100, 117));

            fields[i] = new JTextField();
            fields[i].setEditable(choSua);
            fields[i].setBackground(Color.WHITE);
            fields[i].setForeground(CHU);

            fields[i].setFont(
                new Font("Arial", Font.PLAIN, 13)
            );

            fields[i].setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(VIEN),
                    new EmptyBorder(6, 8, 6, 8)
                )
            );

            nhan.setLabelFor(fields[i]);

            o.add(nhan, BorderLayout.NORTH);
            o.add(fields[i], BorderLayout.CENTER);

            form.add(o);
        }

        return form;
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
                        "Tìm kiếm theo mã hoặc tên dịch vụ...",
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

    private void hienThiThongTinDichVu() {
        int dong = tableDichVu.getSelectedRow();

        btnSua.setEnabled(dong >= 0);
        btnXoa.setEnabled(dong >= 0);

        if (dong < 0) {
            for (JTextField field : thongTin) {
                field.setText("");
            }

            return;
        }

        int dongModel =
            tableDichVu.convertRowIndexToModel(dong);

        String[] dv = danhSachDichVu.get(dongModel);

        // Thứ tự ô thông tin khác thứ tự cột trong bảng.
        int[] viTri = {0, 1, 3, 4, 2, 5};

        for (int i = 0; i < thongTin.length; i++) {
            thongTin[i].setText(dv[viTri[i]]);
        }
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

    private void locDichVu() {
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

                    String[] dv = danhSachDichVu.get(
                        entry.getIdentifier()
                    );

                    boolean dungTuKhoa = chuanHoa(
                        dv[0] + " " + dv[1]
                    ).contains(tuKhoa);

                    boolean dungTrangThai =
                        "Tất cả".equals(trangThai)
                        || dv[5].equals(trangThai);

                    return dungTuKhoa && dungTrangThai;
                }
            }
        );

        if (tableDichVu.getSelectedRow() < 0
                && tableDichVu.getRowCount() > 0) {

            tableDichVu.setRowSelectionInterval(0, 0);
        }

        hienThiThongTinDichVu();
    }

    private void capNhatBang(int dongModel) {
        model.setRowCount(0);

        for (String[] dv : danhSachDichVu) {
            model.addRow(dv);
        }

        txtTimKiem.setText("");
        cboTrangThai.setSelectedIndex(0);

        if (dongModel >= 0
                && dongModel < model.getRowCount()) {

            int dongView =
                tableDichVu.convertRowIndexToView(dongModel);

            if (dongView >= 0) {
                tableDichVu.setRowSelectionInterval(
                    dongView, dongView
                );
            }
        }

        hienThiThongTinDichVu();
    }

    private void moHopThoaiDichVu(boolean sua) {
        int dong = tableDichVu.getSelectedRow();

        if (sua && dong < 0) {
            return;
        }

        int dongModel = sua
            ? tableDichVu.convertRowIndexToModel(dong)
            : -1;

        JTextField[] fields = new JTextField[6];
        JPanel form = taoForm(fields, true);

        fields[5].setEditable(false);

        JPanel hopThoai = new JPanel(
            new BorderLayout(0, 12)
        );

        hopThoai.add(form, BorderLayout.CENTER);

        JComboBox<String> trangThai =
            new JComboBox<>(TRANG_THAI);

        JPanel pTrangThai = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );

        pTrangThai.add(new JLabel("Chọn trạng thái:"));
        pTrangThai.add(trangThai);

        hopThoai.add(pTrangThai, BorderLayout.SOUTH);

        trangThai.addActionListener(e ->
            fields[5].setText(
                (String) trangThai.getSelectedItem()
            )
        );

        if (sua) {
            String[] dv = danhSachDichVu.get(dongModel);
            int[] viTri = {0, 1, 3, 4, 2, 5};

            for (int i = 0; i < fields.length; i++) {
                fields[i].setText(dv[viTri[i]]);
            }

            fields[0].setEditable(false);
            trangThai.setSelectedItem(dv[5]);

        } else {
            fields[4].setText("Lần");
            fields[5].setText(TRANG_THAI[0]);
        }

        while (JOptionPane.showConfirmDialog(
                this,
                hopThoai,
                sua ? "Sửa dịch vụ" : "Thêm dịch vụ",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            ) == JOptionPane.OK_OPTION) {

            String[] nhap = new String[6];
            boolean dayDu = true;

            for (int i = 0; i < fields.length; i++) {
                nhap[i] = fields[i].getText().trim();
                dayDu &= !nhap[i].isEmpty();
            }

            if (!dayDu) {
                JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập đầy đủ thông tin dịch vụ."
                );

                continue;
            }

            nhap[0] = nhap[0].toUpperCase(Locale.ROOT);

            boolean trung = false;

            for (int i = 0; i < danhSachDichVu.size(); i++) {
                if (i != dongModel
                        && danhSachDichVu.get(i)[0]
                            .equalsIgnoreCase(nhap[0])) {

                    trung = true;
                }
            }

            if (trung) {
                JOptionPane.showMessageDialog(
                    this,
                    "Mã dịch vụ đã tồn tại."
                );

                continue;
            }

            long gia;
            int phut;

            try {
                gia = Long.parseLong(
                    nhap[2].replaceAll("[.,\\sđĐ]", "")
                );

                phut = Integer.parseInt(
                    nhap[3].replace("phút", "").trim()
                );

                if (gia < 0 || phut <= 0) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Đơn giá phải là số nguyên không âm; "
                    + "thời gian phải là số phút lớn hơn 0."
                );

                continue;
            }

            String[] dv = {
                nhap[0],
                nhap[1],
                nhap[4],
                String.format(
                    Locale.US, "%,d", gia
                ).replace(',', '.') + " đ",
                phut + " phút",
                (String) trangThai.getSelectedItem()
            };

            if (sua) {
                danhSachDichVu.set(dongModel, dv);

            } else {
                danhSachDichVu.add(dv);
                dongModel = danhSachDichVu.size() - 1;
            }

            capNhatBang(dongModel);
            break;
        }
    }

    private void xoaDichVu() {
        int dong = tableDichVu.getSelectedRow();

        if (dong < 0) {
            return;
        }

        int dongModel =
            tableDichVu.convertRowIndexToModel(dong);

        String[] dv = danhSachDichVu.get(dongModel);

        int chon = JOptionPane.showConfirmDialog(
            this,
            "Xóa dịch vụ " + dv[0] + " - " + dv[1] + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (chon == JOptionPane.YES_OPTION) {
            danhSachDichVu.remove(dongModel);

            capNhatBang(
                Math.min(
                    dongModel, danhSachDichVu.size() - 1
                )
            );
        }
    }

    // ==================== CỬA SỔ CHẠY ĐỘC LẬP ====================
    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame(
            "Quản lý dịch vụ - Night Invader"
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
            new QuanLyDichVuFrame().taoCuaSo().setVisible(true)
        );
    }
}