package ui;

import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import entity.NhanVien;

import javax.swing.*;

public class GaraManHinhChinh extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color RED = new Color(190, 22, 58);
    private static final Color BACKGROUND = new Color(240, 243, 246);
    private static final Color BORDER = new Color(220, 224, 230);

    // Vùng chứa các màn hình, giống pnlContent trong QLCF_N7.
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelNoiDung = new JPanel(cardLayout);
    
    private XeUI pnlQuanLyXe;
    private LapPhieuTiepNhanUI pnlTiepNhan;

    public GaraManHinhChinh() {
        setTitle("Hệ thống Quản lý Gara Ô Tô - Night Invader");
        setSize(1340, 840);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        khoiTaoGiaoDien();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    public GaraManHinhChinh(NhanVien nhanVienDangNhap) {
        this();

        if (nhanVienDangNhap == null) {
            throw new IllegalArgumentException(
                    "Chưa có thông tin nhân viên đăng nhập."
            );
        }

        pnlTiepNhan.setNguoiTiepNhan(
                nhanVienDangNhap.getMaNhanVien(),
                nhanVienDangNhap.getHoTen()
        );
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        setJMenuBar(taoThanhMenu());

        // Đăng ký Trang chủ vào CardLayout.
        themManHinh("TRANG_CHU", taoTrangChu());

        
         KhachHangUI panelKhachHang = new KhachHangUI();
         themManHinh("KHACH_HANG", panelKhachHang);
         
         QuanLyPhuTungFrame panelPhuTung = new QuanLyPhuTungFrame();
         themManHinh("PHU_TUNG", panelPhuTung);
         
         themManHinh("TAI_KHOAN", new GaraTaiKhoanCaNhan());
         
         themManHinh("DOI_MAT_KHAU", new GaraDoiMatKhau());
         
         themManHinh("NHAP_KHO", new GaraLapPhieuNhapKho());
         
         themManHinh("XUAT_KHO", new GaraLapPhieuXuatKho());
         
         themManHinh("PHAN_CONG", new GaraPhanCongKTV());
         
         themManHinh("TRA_CUU_HOA_DON", new GaraTraCuuHoaDon());
         
         pnlTiepNhan = new LapPhieuTiepNhanUI();
         themManHinh("TIEP_NHAN", pnlTiepNhan);
         
         pnlQuanLyXe = new XeUI();
         themManHinh("XE", pnlQuanLyXe);
         themManHinh("DICH_VU", new QuanLyDichVuFrame());
         themManHinh("LICH_SU", new LichSuSuaChuaFrame());
         themManHinh("SUA_CHUA", new LapPhieuSuaChuaFrame());
         themManHinh("TINH_TRANG_SUA_XE", new CapNhatTTSuaXeUI());
         themManHinh("DS_PHIEU_TIEP_NHAN", new PhieuTiepNhanUI());
         
         themManHinh("THONG_KE_DOANH_THU", new GaraThongKe(0));
         themManHinh("THONG_KE_TON_KHO", new GaraThongKe(1));
         themManHinh("PHU_TUNG_BAN_CHAY", new GaraThongKe(2));
         themManHinh("PHU_TUNG_BAN_CHAM", new GaraThongKe(3));
         
//         CapNhatTTSuaXeUI panelTinhTrang = new CapNhatTTSuaXeUI();
//         themManHinh("TINH_TRANG_SUA_XE", panelTinhTrang);
         
         themManHinh("LAP_HOA_DON", new GaraLapHoaDon());
         
         themManHinh("NHAN_SU", new GaraNhanVien(0));
         
         themManHinh("THONG_KE_DOANH_THU", new GaraThongKe());
         themManHinh("THONG_KE_TON_KHO", new GaraThongKeTonKho());
         themManHinh("PHU_TUNG_BAN_CHAY", new GaraPhuTungBanChay());
         themManHinh("PHU_TUNG_BAN_CHAM", new GaraPhuTungBanCham());
         
        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(panelNoiDung, BorderLayout.CENTER);
        add(taoFooter(), BorderLayout.SOUTH);

        hienThiTrangChu();
    }

    // ==================== QUẢN LÝ MÀN HÌNH ====================

    public void themManHinh(String maManHinh, JPanel panel) {
        // Đặt tên để kiểm tra màn hình đã được thêm chưa.
        panel.setName(maManHinh);
        panelNoiDung.add(panel, maManHinh);
        panelNoiDung.revalidate();
        panelNoiDung.repaint();
    }

    public void chuyenManHinh(String maManHinh) {
        // Các màn hình chưa chuyển thành JPanel sẽ báo rõ,
        // tránh bấm menu mà không thấy phản hồi.
        for (Component component : panelNoiDung.getComponents()) {
            if (maManHinh.equals(component.getName())) {
                cardLayout.show(panelNoiDung, maManHinh);
                return;
            }
        }

        JOptionPane.showMessageDialog(
            this,
            "Màn hình này chưa được gắn vào cửa sổ chính.",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void hienThiTrangChu() {
        cardLayout.show(panelNoiDung, "TRANG_CHU");
    }

    // ==================== THANH MENU ====================

    private JMenuBar taoThanhMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        JMenu menuLogo = new JMenu("NIGHT INVADER");
        menuLogo.setFont(new Font("Arial", Font.BOLD, 15));
        menuLogo.setForeground(RED);
        menuLogo.setMargin(new Insets(5, 10, 5, 10));
        menuBar.add(menuLogo);

        String[] tenCacMenu = {
            "Hệ thống", "Khách hàng", "Xe", "Dịch vụ",
            "Phụ tùng", "Kho hàng", "Hóa đơn",
            "Thống kê", "Nhân viên"
        };

        String[][] cacItemMenu = {
            {
                "Màn hình chính", "-",
                "Tài khoản cá nhân", "Đổi mật khẩu",
                "-", "Đăng xuất"
            },
            {
                "Quản lý khách hàng"
            },
            {
                "Quản lý xe", "Lịch sử sửa chữa"
            },
            {
                "Quản lý dịch vụ",
                
                "-",
                "Lập phiếu tiếp nhận xe",
                "Danh sách phiếu tiếp nhận",
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

        for (int i = 0; i < tenCacMenu.length; i++) {
            JMenu menu = taoMenuDropdown(
                tenCacMenu[i], cacItemMenu[i]
            );
            menuBar.add(menu);
        }

        menuBar.add(Box.createHorizontalGlue());

        JMenu menuTaiKhoan = taoMenuDropdown(
            "Xin chào, Chủ Gara",
            new String[] {
                "Tài khoản cá nhân",
                "Đổi mật khẩu",
                "-",
                "Đăng xuất"
            }
        );

        ImageIcon iconUser = loadIcon("/img/icon_nguoi.jpg", 20, 20);

        if (iconUser != null) {
            menuTaiKhoan.setIcon(iconUser);
        }

        menuBar.add(menuTaiKhoan);

        return menuBar;
    }

    private JMenu taoMenuDropdown(String tenMenu, String[] cacItem) {
        JMenu menu = new JMenu(tenMenu);
        menu.setFont(new Font("Arial", Font.BOLD, 16));
        menu.setMargin(new Insets(5, 10, 5, 10));

        for (String itemName : cacItem) {
            if ("-".equals(itemName)) {
                menu.addSeparator();
                continue;
            }

            JMenuItem item = new JMenuItem(itemName);
            item.setFont(new Font("Arial", Font.PLAIN, 14));
            item.setBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 18)
            );

            item.addActionListener(e -> xuLyDieuHuong(itemName));

            menu.add(item);
        }

        return menu;
    }

    // ==================== ĐIỀU HƯỚNG CHUNG ====================

    private void xuLyDieuHuong(String tenChucNang) {
        switch (tenChucNang.trim()) {
            case "Màn hình chính":
                hienThiTrangChu();
                break;

            case "Khách hàng":
            case "Quản lý khách hàng":
                chuyenManHinh("KHACH_HANG");
                break;

            case "Xe":
            case "Quản lý xe":
                chuyenManHinh("XE");
                break;

            case "Tra cứu xe":
                chuyenManHinh("TRA_CUU_XE");
                break;

            case "Lịch sử sửa chữa":
                chuyenManHinh("LICH_SU");
                break;

            case "Dịch vụ":
            case "Quản lý dịch vụ":
            case "Thêm dịch vụ":
            case "Sửa dịch vụ":
            case "Xóa dịch vụ":
                // Trước mắt cùng mở giao diện dịch vụ.
                chuyenManHinh("DICH_VU");
                break;

            case "Tiếp nhận xe":
            case "Lập phiếu tiếp nhận xe":
                chuyenManHinh("TIEP_NHAN");
                break;
            case "Danh sách phiếu tiếp nhận":
                chuyenManHinh("DS_PHIEU_TIEP_NHAN");
                break;
            case "Lập phiếu sửa chữa / Báo giá":
                chuyenManHinh("SUA_CHUA");
                break;

            case "Tình trạng sửa xe":
            case "Cập nhật tình trạng sửa xe":
                chuyenManHinh("TINH_TRANG_SUA_XE");
                break;

            case "Phụ tùng":
            case "Quản lý phụ tùng":
                chuyenManHinh("PHU_TUNG");
                break;

            case "Kho hàng":
            case "Nhập kho":
            case "Lập phiếu nhập kho":
                chuyenManHinh("NHAP_KHO");
                break;

            case "Lập phiếu xuất kho":
                chuyenManHinh("XUAT_KHO");
                break;

            case "Hóa đơn":
            case "Lập hóa đơn":
                chuyenManHinh("LAP_HOA_DON");
                break;

            case "Tra cứu hóa đơn":
                chuyenManHinh("TRA_CUU_HOA_DON");
                break;

            case "Thống kê":
            case "Thống kê doanh thu":
                chuyenManHinh("THONG_KE_DOANH_THU");
                break;

            case "Thống kê tồn kho":
                chuyenManHinh("THONG_KE_TON_KHO");
                break;

            case "Phụ tùng bán chạy":
                chuyenManHinh("PHU_TUNG_BAN_CHAY");
                break;

            case "Phụ tùng bán chậm":
                chuyenManHinh("PHU_TUNG_BAN_CHAM");
                break;

            case "Nhân viên":
            case "Quản lý nhân sự":
                chuyenManHinh("NHAN_SU");
                break;

            case "Phân công kỹ thuật viên":
                chuyenManHinh("PHAN_CONG");
                break;

            case "Hệ thống":
            case "Tài khoản cá nhân":
                chuyenManHinh("TAI_KHOAN");
                break;

            case "Đổi mật khẩu":
                chuyenManHinh("DOI_MAT_KHAU");
                break;

            case "Đăng xuất":
                JOptionPane.showMessageDialog(
                    this,
                    "Chưa gắn màn hình đăng nhập để đăng xuất.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
                );
                break;

            default:
                JOptionPane.showMessageDialog(
                    this,
                    "Chưa nối chức năng: " + tenChucNang,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
                );
                break;
        }
    }

    // ==================== THANH CÔNG CỤ NHANH ====================

    private JPanel taoThanhCongCu() {
        JPanel panelToolbar = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 15, 10)
        );
        panelToolbar.setBackground(Color.WHITE);

        panelToolbar.add(taoNutNhanh(
            "Tiếp nhận xe", "F2",
            loadIcon("/img/icon_xe.png", 22, 22)
        ));

        panelToolbar.add(taoNutNhanh(
            "Tình trạng sửa xe", "F5",
            loadIcon("/img/icon-vector.png", 22, 22)
        ));

        panelToolbar.add(taoNutNhanh(
            "Lập hóa đơn", "F3",
            loadIcon("/img/icon_hoadon.png", 22, 22)
        ));

        panelToolbar.add(taoNutNhanh(
            "Nhập kho", "F4",
            loadIcon("/img/icon_nhapkho.jpg", 22, 22)
        ));

        JPanel panelThongTinPhu = new JPanel(new GridLayout(2, 1));
        panelThongTinPhu.setBackground(Color.WHITE);

        DateTimeFormatter dinhDang = DateTimeFormatter.ofPattern(
            "EEEE, dd/MM/yyyy",
            Locale.forLanguageTag("vi-VN")
        );

        JLabel lblNgay = new JLabel(
            LocalDate.now().format(dinhDang),
            SwingConstants.RIGHT
        );
        lblNgay.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel lblChiNhanh = new JLabel(
            "Chi nhánh: Gara chính",
            SwingConstants.RIGHT
        );
        lblChiNhanh.setFont(new Font("Arial", Font.PLAIN, 11));
        lblChiNhanh.setForeground(Color.GRAY);

        panelThongTinPhu.add(lblNgay);
        panelThongTinPhu.add(lblChiNhanh);

        JPanel panelTopContainer = new JPanel(new BorderLayout());
        panelTopContainer.setBackground(Color.WHITE);
        panelTopContainer.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(0, 10, 0, 15)
            )
        );

        panelTopContainer.add(panelToolbar, BorderLayout.CENTER);
        panelTopContainer.add(panelThongTinPhu, BorderLayout.EAST);

        return panelTopContainer;
    }

    private JButton taoNutNhanh(
            String tieuDe, String phimTat, ImageIcon icon) {

        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 0));

        if (icon != null) {
            btn.add(new JLabel(icon), BorderLayout.WEST);
        }

        JPanel pText = new JPanel(new GridLayout(2, 1));
        pText.setOpaque(false);

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblPhimTat = new JLabel(phimTat);
        lblPhimTat.setFont(new Font("Arial", Font.PLAIN, 11));
        lblPhimTat.setForeground(Color.GRAY);

        pText.add(lblTieuDe);
        pText.add(lblPhimTat);

        btn.add(pText, BorderLayout.CENTER);

        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        btn.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(200, 205, 210)
                ),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            )
        );

        btn.addActionListener(e -> xuLyDieuHuong(tieuDe));

        // Đăng ký phím F2, F3, F4, F5 trên cửa sổ chính.
        String actionKey = "QUICK_" + phimTat;

        getRootPane()
            .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(phimTat), actionKey);

        getRootPane().getActionMap().put(
            actionKey,
            new AbstractAction() {
                @Override
                public void actionPerformed(
                        java.awt.event.ActionEvent e) {
                    xuLyDieuHuong(tieuDe);
                }
            }
        );

        return btn;
    }

    // ==================== TRANG CHỦ ====================

    private JPanel taoTrangChu() {
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(BACKGROUND);

        JPanel panelTieuDeContainer = new JPanel(new BorderLayout());
        panelTieuDeContainer.setOpaque(false);
        panelTieuDeContainer.setBorder(
            BorderFactory.createEmptyBorder(20, 30, 15, 30)
        );

        JPanel panelTieuDeText = new JPanel(new GridLayout(2, 1));
        panelTieuDeText.setOpaque(false);

        JLabel lblManHinhChinh = new JLabel("MÀN HÌNH CHÍNH");
        lblManHinhChinh.setFont(new Font("Arial", Font.BOLD, 22));
        lblManHinhChinh.setForeground(new Color(30, 35, 45));

        JLabel lblHuongDan = new JLabel(
            "Chọn công việc bạn muốn thực hiện"
        );
        lblHuongDan.setFont(new Font("Arial", Font.PLAIN, 13));
        lblHuongDan.setForeground(Color.GRAY);

        panelTieuDeText.add(lblManHinhChinh);
        panelTieuDeText.add(lblHuongDan);

        // Giữ ô tìm chức năng của giao diện cũ.
        JTextField txtTimKiem = new JTextField(20);
        txtTimKiem.setToolTipText("Tìm chức năng...");
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem.setPreferredSize(new Dimension(240, 32));

        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.setBorder(
            BorderFactory.createLineBorder(
                new Color(200, 205, 210)
            )
        );
        searchWrapper.add(txtTimKiem, BorderLayout.CENTER);

        JLabel lblSearch = new JLabel(
            loadIcon("/img/icon_tim.png", 16, 16)
        );
        lblSearch.setBorder(
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        );
        searchWrapper.add(lblSearch, BorderLayout.EAST);

        JPanel panelTimKiem = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 0, 0)
        );
        panelTimKiem.setOpaque(false);
        panelTimKiem.add(searchWrapper);

        panelTieuDeContainer.add(
            panelTieuDeText, BorderLayout.WEST
        );
        panelTieuDeContainer.add(
            panelTimKiem, BorderLayout.EAST
        );

        JPanel panelGridThe = new JPanel(
            new GridLayout(3, 3, 20, 20)
        );
        panelGridThe.setOpaque(false);
        panelGridThe.setBorder(
            BorderFactory.createEmptyBorder(0, 30, 30, 30)
        );

        panelGridThe.add(taoNutChucNang(
            "Khách hàng",
            "Quản lý khách hàng\nThêm, sửa thông tin khách hàng",
            loadIcon("/img/icon_khachhang.png", 32, 32)
        ));

        panelGridThe.add(taoNutChucNang(
            "Xe",
            "Quản lý xe - Tra cứu xe\nXem lịch sử sửa chữa theo biển số",
            loadIcon("/img/icon_xe.png", 32, 32)
        ));

        panelGridThe.add(taoNutChucNang(
            "Dịch vụ",
            "Thêm - Sửa - Xóa dịch vụ\nLập phiếu tiếp nhận xe - Lập báo giá",
            loadIcon("/img/icon-vector.png", 32, 32)
        ));

        panelGridThe.add(taoNutChucNang(
            "Phụ tùng",
            "Quản lý danh mục phụ tùng\nThêm, sửa - Ngừng kinh doanh",
            loadIcon("/img/icon_nhapkho.jpg", 32, 32)
        ));

        panelGridThe.add(taoNutChucNang(
            "Kho hàng",
            "Lập phiếu nhập kho\nLập phiếu xuất kho",
            loadIcon("/img/icon_nhapkho.jpg", 32, 32)
        ));

        panelGridThe.add(taoNutChucNang(
            "Hóa đơn",
            "Lập hóa đơn và ghi nhận thanh toán\nTra cứu - In hóa đơn",
            loadIcon("/img/icon_hoadon.png", 32, 32)
        ));

        panelGridThe.add(taoNutChucNang(
            "Thống kê",
            "Doanh thu - Tồn kho\nPhụ tùng bán chạy - Bán chậm",
            loadIcon("/img/icon_thongke.png", 32, 32)
        ));

        panelGridThe.add(taoNutChucNang(
            "Nhân viên",
            "Quản lý nhân sự\nPhân công kỹ thuật viên",
            loadIcon("/img/icon_nguoi.jpg", 32, 32)
        ));

        panelGridThe.add(taoNutChucNang(
            "Hệ thống",
            "Tài khoản cá nhân - Đổi mật khẩu\nĐăng xuất",
            loadIcon("/img/icon_caidat.png", 32, 32)
        ));

        panelCenter.add(panelTieuDeContainer, BorderLayout.NORTH);
        panelCenter.add(panelGridThe, BorderLayout.CENTER);

        return panelCenter;
    }

    private JButton taoNutChucNang(
            String tenTieuDe, String moTa, ImageIcon icon) {

        JButton btnCard = new JButton();
        btnCard.setLayout(new BorderLayout(5, 5));
        btnCard.setBackground(Color.WHITE);
        btnCard.setFocusPainted(false);
        btnCard.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        btnCard.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            )
        );

        if (icon != null) {
            btnCard.add(
                new JLabel(icon, SwingConstants.CENTER),
                BorderLayout.NORTH
            );
        }

        JPanel panelText = new JPanel();
        panelText.setLayout(
            new BoxLayout(panelText, BoxLayout.Y_AXIS)
        );
        panelText.setOpaque(false);

        JLabel lblTieuDe = new JLabel(
            tenTieuDe.toUpperCase(Locale.forLanguageTag("vi-VN"))
        );
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 17));
        lblTieuDe.setForeground(new Color(30, 35, 45));
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelText.add(Box.createRigidArea(new Dimension(0, 5)));
        panelText.add(lblTieuDe);
        panelText.add(Box.createRigidArea(new Dimension(0, 6)));

        for (String line : moTa.split("\n")) {
            JLabel lblLine = new JLabel(line);
            lblLine.setFont(new Font("Arial", Font.PLAIN, 12));
            lblLine.setForeground(Color.GRAY);
            lblLine.setAlignmentX(Component.CENTER_ALIGNMENT);

            panelText.add(lblLine);
            panelText.add(
                Box.createRigidArea(new Dimension(0, 2))
            );
        }

        btnCard.add(panelText, BorderLayout.CENTER);
        btnCard.addActionListener(e -> xuLyDieuHuong(tenTieuDe));

        return btnCard;
    }

    // ==================== FOOTER ====================

    private JPanel taoFooter() {
        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBackground(new Color(245, 247, 250));
        panelFooter.setBorder(
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        );

        JLabel lblTrangThai = new JLabel(
            "Đang hoạt động | Vai trò: Chủ Gara | Chi nhánh: Gara chính"
        );
        lblTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTrangThai.setForeground(Color.DARK_GRAY);

        JLabel lblBanQuyen = new JLabel("NIGHT INVADER • Gara ô tô");
        lblBanQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        lblBanQuyen.setForeground(Color.GRAY);

        panelFooter.add(lblTrangThai, BorderLayout.WEST);
        panelFooter.add(lblBanQuyen, BorderLayout.EAST);

        return panelFooter;
    }

    // ==================== ICON ====================

    private ImageIcon loadIcon(String path, int width, int height) {
        URL resource = getClass().getResource(path);

        if (resource == null) {
            return null;
        }

        ImageIcon icon = new ImageIcon(resource);
        Image image = icon.getImage().getScaledInstance(
            width, height, Image.SCALE_SMOOTH
        );

        return new ImageIcon(image);
    }
    public void moXeCuaKhachHang(String maKhachHang) {
        pnlQuanLyXe.hienThiXeCuaKhachHang(maKhachHang);
        chuyenManHinh("XE");
    }

    // ==================== CHẠY THỬ MÀN HÌNH CHÍNH ====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            new GaraManHinhChinh().setVisible(true);
        });
    }
}