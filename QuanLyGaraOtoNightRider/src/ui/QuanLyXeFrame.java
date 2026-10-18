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

public class QuanLyXeFrame extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    private static final String[] NHAN = {
        "Biển số", "Chủ xe",
        "Hãng / dòng xe", "Năm sản xuất",
        "Số VIN", "Số km"
    };

    private static final String[] TRANG_THAI = {
        "Đang sửa", "Chờ báo giá", "Đã bàn giao"
    };

    // Biển số, chủ xe, dòng xe, năm, VIN, số km, tình trạng.
    private final List<String[]> danhSachXe = new ArrayList<>();

    private final JTextField[] thongTin = new JTextField[6];
    private final JTextField txtTimKiem = new JTextField();

    private final JComboBox<String> cboTrangThai = new JComboBox<>(
        new String[] {
            "Tất cả", "Đang sửa", "Chờ báo giá", "Đã bàn giao"
        }
    );

    private final DefaultTableModel model = new DefaultTableModel(
        new String[] {
            "Biển số", "Chủ xe", "Dòng xe",
            "Năm", "Số km", "Tình trạng"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tableXe = new JTable(model);

    private final TableRowSorter<DefaultTableModel> boLoc =
        new TableRowSorter<>(model);

    private final JButton btnSuaXe = taoNut("Sửa xe", false);

    public QuanLyXeFrame() {
        this(null);
    }

    public QuanLyXeFrame(Runnable veManHinhChinh) {
        setLayout(new BorderLayout());
        setBackground(new Color(216, 220, 228));
        setBorder(new EmptyBorder(20, 65, 15, 65));

        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));

        // ==================== TIÊU ĐỀ ====================
        JPanel tieuDe = new JPanel(new BorderLayout());
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setBorder(new EmptyBorder(8, 16, 8, 16));

        JLabel lblTieuDe = new JLabel("QUẢN LÝ XE");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTieuDe.setForeground(CHU);

        tieuDe.add(lblTieuDe, BorderLayout.WEST);

        if (veManHinhChinh != null) {
            JButton btnTrangChu = taoNut("Màn hình chính", false);

            btnTrangChu.addActionListener(
                e -> veManHinhChinh.run()
            );

            tieuDe.add(btnTrangChu, BorderLayout.EAST);
        }

        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 25));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));

        // ==================== THÔNG TIN XE ====================
        JPanel dau = new JPanel(new BorderLayout(0, 12));
        dau.setOpaque(false);

        JLabel moTa = new JLabel(
            "Mỗi biển số gắn với chủ xe và lịch sử sửa chữa."
        );

        moTa.setForeground(CHU_PHU);
        moTa.setFont(new Font("Arial", Font.PLAIN, 13));

        dau.add(moTa, BorderLayout.NORTH);
        dau.add(taoForm(thongTin, false), BorderLayout.CENTER);

        noiDung.add(dau, BorderLayout.NORTH);

        // ==================== TÌM KIẾM ====================
        JPanel vungBang = new JPanel(new BorderLayout(0, 12));
        vungBang.setOpaque(false);

        JPanel thanhLoc = new JPanel(new BorderLayout(24, 0));
        thanhLoc.setBackground(new Color(237, 241, 245));
        thanhLoc.setBorder(new EmptyBorder(6, 10, 6, 10));

        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));

        txtTimKiem.setBorder(
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        );

        txtTimKiem.setToolTipText(
            "Tìm theo mã chủ xe, tên, biển số hoặc dòng xe"
        );

        JLabel lblTim = new JLabel(
            "Tìm kiếm theo mã, tên hoặc biển số..."
        );

        lblTim.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTim.setForeground(CHU_PHU);

        JPanel tim = new JPanel(new BorderLayout(10, 0));
        tim.setOpaque(false);

        tim.add(lblTim, BorderLayout.WEST);
        tim.add(txtTimKiem, BorderLayout.CENTER);

        thanhLoc.add(tim, BorderLayout.CENTER);

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

        // ==================== BẢNG XE ====================
        tableXe.setFont(new Font("Arial", Font.PLAIN, 13));
        tableXe.setRowHeight(36);

        tableXe.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        tableXe.setRowSorter(boLoc);
        tableXe.setShowGrid(false);
        tableXe.setIntercellSpacing(new Dimension(0, 0));

        tableXe.setSelectionBackground(
            new Color(53, 105, 139)
        );

        tableXe.setSelectionForeground(Color.WHITE);

        tableXe.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 12)
        );

        tableXe.getTableHeader().setBackground(
            new Color(237, 241, 245)
        );

        tableXe.getTableHeader().setPreferredSize(
            new Dimension(0, 30)
        );

        tableXe.getTableHeader().setReorderingAllowed(false);

        tableXe.setDefaultRenderer(
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

        int[] doRong = {160, 220, 200, 100, 160, 190};

        for (int i = 0; i < doRong.length; i++) {
            tableXe.getColumnModel()
                   .getColumn(i)
                   .setPreferredWidth(doRong[i]);
        }

        JScrollPane cuon = new JScrollPane(tableXe);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(Color.WHITE);

        vungBang.add(cuon, BorderLayout.CENTER);
        noiDung.add(vungBang, BorderLayout.CENTER);

        // ==================== NÚT THAO TÁC ====================
        JPanel thaoTac = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        thaoTac.setOpaque(false);

        JButton btnThemXe = taoNut("Thêm xe", false);
        JButton btnTraCuu = taoNut("Tra cứu xe", true);
        btnTraCuu.setContentAreaFilled(false);
        btnTraCuu.setOpaque(true);
        btnTraCuu.setBackground(new Color(188, 25, 58));
        btnTraCuu.setForeground(Color.WHITE);
        
        btnThemXe.addActionListener(
            e -> moHopThoaiXe(false)
        );

        btnSuaXe.addActionListener(
            e -> moHopThoaiXe(true)
        );

        btnTraCuu.addActionListener(
            e -> chuyenDenTraCuu()
        );

        thaoTac.add(btnThemXe);
        thaoTac.add(btnSuaXe);
        thaoTac.add(btnTraCuu);

        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);

        add(khung, BorderLayout.CENTER);

        // ==================== SỰ KIỆN ====================
        tableXe.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiXeDuocChon();
            }
        });

        txtTimKiem.getDocument().addDocumentListener(
            new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    locXe();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    locXe();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    locXe();
                }
            }
        );

        cboTrangThai.addActionListener(e -> locXe());

        // ==================== DỮ LIỆU MẪU ====================
        danhSachXe.add(new String[] {
            "51H-123.45",
            "KH001 • Nguyễn Minh An",
            "Toyota Vios",
            "2021",
            "RL4B29F3XM100001",
            "40.250",
            "Đang sửa"
        });

        danhSachXe.add(new String[] {
            "59A-678.90",
            "KH002 • Lê Hoàng Nam",
            "Honda City",
            "2020",
            "MRHGM6650LP100002",
            "62.100",
            "Chờ báo giá"
        });

        danhSachXe.add(new String[] {
            "51K-246.80",
            "KH003 • Võ Thu Hà",
            "Mazda 3",
            "2022",
            "RN2BP2H7XM100003",
            "28.400",
            "Đã bàn giao"
        });

        capNhatBang(0);
    }

    private JPanel taoForm(JTextField[] fields, boolean choSua) {
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

    private static JButton taoNut(String ten, boolean chinh) {
        JButton nut = new JButton(ten);

        nut.setFont(new Font("Arial", Font.BOLD, 12));
        nut.setBackground(chinh ? DO : Color.WHITE);
        nut.setForeground(chinh ? Color.WHITE : CHU);
        nut.setOpaque(true);
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

    private void hienThiXeDuocChon() {
        int dong = tableXe.getSelectedRow();

        btnSuaXe.setEnabled(dong >= 0);

        if (dong < 0) {
            for (JTextField field : thongTin) {
                field.setText("");
            }

            return;
        }

        int dongModel = tableXe.convertRowIndexToModel(dong);
        String[] xe = danhSachXe.get(dongModel);

        for (int i = 0; i < thongTin.length; i++) {
            thongTin[i].setText(
                xe[i] + (i == 5 ? " km" : "")
            );
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

    private void locXe() {
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

                    String[] xe = danhSachXe.get(
                        entry.getIdentifier()
                    );

                    boolean dungTrangThai =
                        "Tất cả".equals(trangThai)
                        || xe[6].equals(trangThai);

                    boolean dungTuKhoa = chuanHoa(
                        String.join(" ", xe)
                    ).contains(tuKhoa);

                    return dungTrangThai && dungTuKhoa;
                }
            }
        );

        if (tableXe.getSelectedRow() < 0
                && tableXe.getRowCount() > 0) {

            tableXe.setRowSelectionInterval(0, 0);
        }

        hienThiXeDuocChon();
    }

    public void chuyenDenTraCuu() {
        txtTimKiem.requestFocusInWindow();
        txtTimKiem.selectAll();
    }

    private void capNhatBang(int dongModel) {
        model.setRowCount(0);

        for (String[] xe : danhSachXe) {
            String chuXe = xe[1];

            if (chuXe.contains(" • ")) {
                chuXe = chuXe.substring(
                    chuXe.indexOf(" • ") + 3
                );
            }

            model.addRow(new Object[] {
                xe[0],
                chuXe,
                xe[2],
                xe[3],
                xe[5],
                xe[6]
            });
        }

        int dongView = tableXe.convertRowIndexToView(dongModel);

        if (dongView >= 0) {
            tableXe.setRowSelectionInterval(dongView, dongView);
        }

        hienThiXeDuocChon();
    }

    private void moHopThoaiXe(boolean sua) {
        if (sua && tableXe.getSelectedRow() < 0) {
            return;
        }

        int dongModel = sua
            ? tableXe.convertRowIndexToModel(
                tableXe.getSelectedRow()
            )
            : -1;

        JTextField[] fields = new JTextField[6];

        JPanel noiDung = new JPanel(
            new BorderLayout(0, 12)
        );

        noiDung.add(
            taoForm(fields, true),
            BorderLayout.CENTER
        );

        JComboBox<String> trangThai =
            new JComboBox<>(TRANG_THAI);

        JPanel dongTrangThai = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );

        dongTrangThai.add(new JLabel("Tình trạng:"));
        dongTrangThai.add(trangThai);

        noiDung.add(dongTrangThai, BorderLayout.SOUTH);

        if (sua) {
            String[] xe = danhSachXe.get(dongModel);

            for (int i = 0; i < fields.length; i++) {
                fields[i].setText(xe[i]);
            }

            trangThai.setSelectedItem(xe[6]);
        }

        while (JOptionPane.showConfirmDialog(
                this,
                noiDung,
                sua ? "Sửa xe" : "Thêm xe",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            ) == JOptionPane.OK_OPTION) {

            String[] xe = new String[7];
            boolean dayDu = true;

            for (int i = 0; i < fields.length; i++) {
                xe[i] = fields[i].getText().trim();
                dayDu &= !xe[i].isEmpty();
            }

            if (!dayDu) {
                JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập đầy đủ thông tin xe."
                );

                continue;
            }

            try {
                int nam = Integer.parseInt(xe[3]);

                long km = Long.parseLong(
                    xe[5].replace(".", "").replace(",", "")
                );

                if (nam < 1886
                        || nam > java.time.Year.now().getValue() + 1
                        || km < 0) {

                    throw new NumberFormatException();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Năm sản xuất phải hợp lệ; "
                    + "số km phải là số nguyên không âm."
                );

                continue;
            }

            boolean trung = false;

            for (int i = 0; i < danhSachXe.size(); i++) {
                if (i != dongModel
                        && danhSachXe.get(i)[0]
                            .equalsIgnoreCase(xe[0])) {

                    trung = true;
                }
            }

            if (trung) {
                JOptionPane.showMessageDialog(
                    this,
                    "Biển số đã tồn tại trong danh sách."
                );

                continue;
            }

            xe[6] = (String) trangThai.getSelectedItem();

            if (sua) {
                danhSachXe.set(dongModel, xe);

            } else {
                danhSachXe.add(xe);
                dongModel = danhSachXe.size() - 1;
            }

            txtTimKiem.setText("");
            cboTrangThai.setSelectedIndex(0);

            capNhatBang(dongModel);
            break;
        }
    }

    // ==================== CỬA SỔ CHẠY ĐỘC LẬP ====================
    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame("Quản lý xe - Night Invader");

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

        // Gắn các thành phần vào cửa sổ.
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
            new QuanLyXeFrame().taoCuaSo().setVisible(true)
        );
    }
}