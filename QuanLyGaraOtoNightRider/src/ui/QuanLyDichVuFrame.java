package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import dao.DichVuDao;
import entity.DichVu;

public class QuanLyDichVuFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    // ==================== DAO VÀ DỮ LIỆU ====================

    private final DichVuDao dichVuDao = new DichVuDao();
    private final ArrayList<DichVu> danhSachDichVu = new ArrayList<>();

    // ==================== CÁC THÀNH PHẦN GIAO DIỆN ====================

    private final JTextField txtMaDichVu = new JTextField();
    private final JTextField txtTenDichVu = new JTextField();
    private final JTextField txtDonGia = new JTextField();

    private final JComboBox<String> cboTrangThai = new JComboBox<>(
            new String[]{"Hoạt động", "Ngừng hoạt động"});

    private final JTextArea txtMoTa = new JTextArea(4, 20);
    private final JTextField txtTimKiem = new JTextField(25);

    private final JButton btnTimKiem = taoNut("Tìm kiếm", true);
    private final JButton btnThem = taoNut("Thêm", true);
    private final JButton btnSua = taoNut("Sửa", false);
    private final JButton btnNgungHoatDong = taoNut("Ngừng hoạt động", false);
    private final JButton btnLamMoi = taoNut("Làm mới", false);

    private final JLabel lblSoDichVu = new JLabel("Số dịch vụ: 0");

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã dịch vụ", "Tên dịch vụ", "Mô tả", "Đơn giá", "Trạng thái"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == 3 ? BigDecimal.class : String.class;
        }
    };

    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    // ==================== CONSTRUCTOR ====================

    public QuanLyDichVuFrame() {
        setTitle("Quản lý dịch vụ - Night Invader");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        khoiTaoGiaoDien();

        try {
            taiDuLieuTuDatabase();
        } catch (SQLException | IllegalArgumentException ex) {
            baoLoi("Không tải được danh sách dịch vụ.", ex);
        }
    }

    // ==================== KHỞI TẠO GIAO DIỆN ====================

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        // 1. MENU BAR

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(new EmptyBorder(10, 15, 10, 15));

        Font menuFont = new Font("Arial", Font.BOLD, 16);

        JMenu logo = new JMenu("NIGHT INVADER");
        logo.setFont(new Font("Arial", Font.BOLD, 15));
        logo.setForeground(new Color(210, 30, 30));
        logo.setMargin(new Insets(5, 10, 5, 10));
        menuBar.add(logo);

        String[] tenMenu = {
            "Hệ thống", "Khách hàng ", "Xe ", "Dịch vụ ",
            "Phụ tùng", "Kho hàng ", "Hóa đơn ", "Thống kê ", "Nhân viên "
        };

        String[][] cacItem = {
            {"Tài khoản cá nhân", "Đổi mật khẩu", "-", "Đăng xuất"},
            {"Quản lý khách hàng"},
            {"Quản lý xe", "Tra cứu xe", "Lịch sử sửa chữa"},
            {
                "Quản lý dịch vụ", "Thêm dịch vụ", "Sửa dịch vụ", "Xóa dịch vụ",
                "-", "Lập phiếu tiếp nhận xe", "Lập phiếu sửa chữa / Báo giá",
                "Cập nhật tình trạng sửa xe"
            },
            {"Quản lý phụ tùng"},
            {"Lập phiếu nhập kho", "Lập phiếu xuất kho"},
            {"Lập hóa đơn", "Tra cứu hóa đơn"},
            {
                "Thống kê doanh thu", "Thống kê tồn kho",
                "Phụ tùng bán chạy", "Phụ tùng bán chậm"
            },
            {"Quản lý nhân sự", "Phân công kỹ thuật viên"}
        };

        for (int i = 0; i < tenMenu.length; i++) {
            JMenu menu = taoMenuDropdown(tenMenu[i], cacItem[i]);
            menu.setFont(menuFont);
            menu.setMargin(new Insets(5, 10, 5, 10));

            if (i == 3) {
                menu.setForeground(DO);
                menu.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, DO));
            }

            menuBar.add(menu);
        }

        JMenu taiKhoan = new JMenu("Xin chào, Chủ Gara");
        taiKhoan.setFont(menuFont);
        taiKhoan.setMargin(new Insets(5, 10, 5, 10));
        taiKhoan.setIcon(loadIcon("/img/icon_nguoi.jpg", 20, 20));

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(taiKhoan);
        setJMenuBar(menuBar);

        // 2. THANH CÔNG CỤ NHANH

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 224, 230)));

        toolbar.add(taoNutNhanh("Tiếp nhận xe", "F2", loadIcon("/img/icon_xe.png", 22, 22)));
        toolbar.add(taoNutNhanh("Tình trạng sửa xe", "F5", loadIcon("/img/icon-vector.png", 22, 22)));
        toolbar.add(taoNutNhanh("Lập hóa đơn", "F3", loadIcon("/img/icon_hoadon.png", 22, 22)));
        toolbar.add(taoNutNhanh("Nhập kho", "F4", loadIcon("/img/icon_nhapkho.jpg", 22, 22)));

        JPanel thongTinPhu = new JPanel(new GridLayout(2, 1));
        thongTinPhu.setBackground(Color.WHITE);

        DateTimeFormatter dinhDangNgay = DateTimeFormatter.ofPattern(
                "EEEE, dd/MM/uuuu", Locale.forLanguageTag("vi-VN"));

        JLabel ngay = new JLabel(LocalDate.now().format(dinhDangNgay), JLabel.RIGHT);
        ngay.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel chiNhanh = new JLabel("Chi nhánh: Gara chính", JLabel.RIGHT);
        chiNhanh.setFont(new Font("Arial", Font.PLAIN, 11));
        chiNhanh.setForeground(Color.GRAY);

        thongTinPhu.add(ngay);
        thongTinPhu.add(chiNhanh);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(0, 10, 0, 15));
        header.add(toolbar, BorderLayout.CENTER);
        header.add(thongTinPhu, BorderLayout.EAST);

        // 3. NỘI DUNG QUẢN LÝ DỊCH VỤ

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(216, 220, 228));
        panelCenter.setBorder(new EmptyBorder(20, 55, 15, 55));

        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));

        JLabel tieuDe = new JLabel("QUẢN LÝ DỊCH VỤ");
        tieuDe.setOpaque(true);
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setForeground(CHU);
        tieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        tieuDe.setBorder(new EmptyBorder(10, 16, 10, 16));
        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 16));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(16, 18, 16, 18));

        // FORM THÔNG TIN

        JPanel phanTren = new JPanel(new BorderLayout(0, 12));
        phanTren.setOpaque(false);

        JPanel form = new JPanel(new GridLayout(2, 2, 24, 12));
        form.setOpaque(false);

        themO(form, "Mã dịch vụ", txtMaDichVu);
        themO(form, "Tên dịch vụ", txtTenDichVu);
        themO(form, "Đơn giá (VNĐ)", txtDonGia);
        themO(form, "Trạng thái", cboTrangThai);

        txtMaDichVu.setEditable(false);

        phanTren.add(form, BorderLayout.CENTER);

        JPanel panelMoTa = new JPanel(new BorderLayout(0, 4));
        panelMoTa.setOpaque(false);

        JLabel lblMoTa = new JLabel("Mô tả");
        lblMoTa.setFont(new Font("Arial", Font.BOLD, 12));
        lblMoTa.setForeground(new Color(87, 100, 117));

        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        txtMoTa.setForeground(CHU);
        txtMoTa.setBackground(Color.WHITE);
        txtMoTa.setBorder(new EmptyBorder(5, 7, 5, 7));

        JScrollPane cuonMoTa = new JScrollPane(txtMoTa);
        cuonMoTa.setPreferredSize(new Dimension(200, 90));
        cuonMoTa.setBorder(BorderFactory.createLineBorder(VIEN));

        panelMoTa.add(lblMoTa, BorderLayout.NORTH);
        panelMoTa.add(cuonMoTa, BorderLayout.CENTER);

        phanTren.add(panelMoTa, BorderLayout.SOUTH);
        noiDung.add(phanTren, BorderLayout.NORTH);

        // DANH SÁCH DỊCH VỤ

        JPanel panelDanhSach = new JPanel(new BorderLayout(0, 10));
        panelDanhSach.setBackground(Color.WHITE);

        JPanel thanhDanhSach = new JPanel(new BorderLayout(10, 0));
        thanhDanhSach.setBackground(new Color(237, 241, 245));
        thanhDanhSach.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel lblDanhSach = new JLabel("DANH SÁCH DỊCH VỤ");
        lblDanhSach.setFont(new Font("Arial", Font.BOLD, 12));
        lblDanhSach.setForeground(CHU);

        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelTimKiem.setOpaque(false);

        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem.setPreferredSize(new Dimension(250, 30));
        txtTimKiem.setToolTipText("Nhập mã hoặc tên dịch vụ");

        panelTimKiem.add(txtTimKiem);
        panelTimKiem.add(btnTimKiem);

        thanhDanhSach.add(lblDanhSach, BorderLayout.WEST);
        thanhDanhSach.add(panelTimKiem, BorderLayout.EAST);

        table.setRowSorter(sorter);
        table.setRowHeight(34);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(53, 105, 139));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(VIEN);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);

        int[] rong = {120, 250, 380, 150, 160};
        for (int i = 0; i < rong.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
        }

        JScrollPane cuonBang = new JScrollPane(table);
        cuonBang.setBorder(BorderFactory.createLineBorder(VIEN));
        cuonBang.getViewport().setBackground(Color.WHITE);

        lblSoDichVu.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSoDichVu.setForeground(CHU_PHU);

        panelDanhSach.add(thanhDanhSach, BorderLayout.NORTH);
        panelDanhSach.add(cuonBang, BorderLayout.CENTER);
        panelDanhSach.add(lblSoDichVu, BorderLayout.SOUTH);

        noiDung.add(panelDanhSach, BorderLayout.CENTER);

        // CÁC NÚT CHỨC NĂNG

        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        thaoTac.add(btnThem);
        thaoTac.add(btnSua);
        thaoTac.add(btnNgungHoatDong);
        thaoTac.add(btnLamMoi);

        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);
        panelCenter.add(khung, BorderLayout.CENTER);

        // 4. FOOTER

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(245, 247, 250));
        footer.setBorder(new EmptyBorder(8, 15, 8, 15));

        JLabel trangThai = new JLabel("Đang hoạt động | Vai trò: Chủ Gara | Chi nhánh: Gara chính");
        trangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        trangThai.setForeground(Color.DARK_GRAY);

        JLabel banQuyen = new JLabel("NIGHT INVADER • Gara ô tô");
        banQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        banQuyen.setForeground(Color.GRAY);

        footer.add(trangThai, BorderLayout.WEST);
        footer.add(banQuyen, BorderLayout.EAST);

        // GẮN VÀO FRAME

        add(header, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // ĐĂNG KÝ SỰ KIỆN

        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnNgungHoatDong.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnTimKiem.addActionListener(this);
        txtTimKiem.addActionListener(this);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiDichVuDangChon();
            }
        });

        xoaThongTin();
        capNhatTrangThaiForm();
    }

    // ==================== XỬ LÝ SỰ KIỆN ====================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();

        if (nguon == btnThem || nguon == btnSua) {
            boolean them = nguon == btnThem;
            DichVu dichVuDangChon = layDichVuDangChon();

            if (them && dichVuDangChon != null) {
                JOptionPane.showMessageDialog(this, "Bấm Làm mới trước khi nhập dịch vụ mới.");
                return;
            }

            if (!them && dichVuDangChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần sửa.");
                return;
            }

            if (!validData()) {
                return;
            }

            String maDaLuu;

            try {
                String ma = them
                        ? dichVuDao.taoMaDichVu()
                        : dichVuDangChon.getMaDichVu();

                DichVu dichVu = new DichVu();
                dichVu.setMaDichVu(ma);
                dichVu.setTenDichVu(txtTenDichVu.getText().trim());
                dichVu.setMoTa(txtMoTa.getText().trim().isEmpty() ? null : txtMoTa.getText().trim());
                dichVu.setDonGia(new BigDecimal(txtDonGia.getText().trim()));
                dichVu.setTrangThai(cboTrangThai.getSelectedIndex() == 0);

                boolean thanhCong;

                if (them) {
                    thanhCong = dichVuDao.themDichVu(dichVu);
                } else {
                    thanhCong = dichVuDao.suaDichVu(dichVu);
                }

                if (!thanhCong) {
                    JOptionPane.showMessageDialog(this, "Không lưu được dịch vụ.");
                    return;
                }

                maDaLuu = ma;

            } catch (SQLException | IllegalArgumentException ex) {
                baoLoi("Không lưu được dịch vụ.", ex);
                return;
            }

            JOptionPane.showMessageDialog(
                    this, them ? "Thêm dịch vụ thành công." : "Cập nhật dịch vụ thành công.");

            try {
                txtTimKiem.setText("");
                sorter.setRowFilter(null);
                taiDuLieuTuDatabase();
                chonDichVuTheoMa(maDaLuu);
            } catch (SQLException | IllegalArgumentException ex) {
                baoLoi("Đã lưu dịch vụ nhưng chưa tải lại được danh sách. Bấm Làm mới để thử lại.", ex);
            }

        } else if (nguon == btnNgungHoatDong) {
            DichVu dichVu = layDichVuDangChon();

            if (dichVu == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ.");
                return;
            }

            if (!dichVu.isTrangThai()) {
                JOptionPane.showMessageDialog(this, "Dịch vụ này đã ngừng hoạt động.");
                return;
            }

            int chon = JOptionPane.showConfirmDialog(
                    this,
                    "Ngừng hoạt động dịch vụ \"" + dichVu.getTenDichVu() + "\"?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);

            if (chon != JOptionPane.YES_OPTION) {
                return;
            }

            String ma = dichVu.getMaDichVu();

            try {
                if (!dichVuDao.capNhatTrangThai(ma, false)) {
                    JOptionPane.showMessageDialog(this, "Không cập nhật được trạng thái dịch vụ.");
                    return;
                }
            } catch (SQLException ex) {
                baoLoi("Không cập nhật được trạng thái dịch vụ.", ex);
                return;
            }

            JOptionPane.showMessageDialog(this, "Đã ngừng hoạt động dịch vụ.");

            try {
                taiDuLieuTuDatabase();
                chonDichVuTheoMa(ma);
            } catch (SQLException | IllegalArgumentException ex) {
                baoLoi("Đã cập nhật trạng thái nhưng chưa tải lại được danh sách.", ex);
            }

        } else if (nguon == btnLamMoi) {
            try {
                taiDuLieuTuDatabase();
                txtTimKiem.setText("");
                sorter.setRowFilter(null);
                table.clearSelection();
                xoaThongTin();
                capNhatTrangThaiForm();
                capNhatSoLuong();
                txtTenDichVu.requestFocusInWindow();
            } catch (SQLException | IllegalArgumentException ex) {
                baoLoi("Không tải lại được danh sách dịch vụ.", ex);
            }

        } else if (nguon == btnTimKiem || nguon == txtTimKiem) {
            String tuKhoa = txtTimKiem.getText().trim();

            table.clearSelection();

            if (tuKhoa.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(
                        "(?iu)" + Pattern.quote(tuKhoa), 0, 1));
            }

            xoaThongTin();
            capNhatTrangThaiForm();
            capNhatSoLuong();
        }
    }

    // ==================== KIỂM TRA DỮ LIỆU ====================

    private boolean validData() {
        String ten = txtTenDichVu.getText().trim();
        String moTa = txtMoTa.getText().trim();
        String donGia = txtDonGia.getText().trim();

        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên dịch vụ không được để trống.");
            txtTenDichVu.requestFocusInWindow();
            return false;
        }

        if (ten.length() > 150) {
            JOptionPane.showMessageDialog(this, "Tên dịch vụ tối đa 150 ký tự.");
            txtTenDichVu.requestFocusInWindow();
            return false;
        }

        if (moTa.length() > 1000) {
            JOptionPane.showMessageDialog(this, "Mô tả tối đa 1000 ký tự.");
            txtMoTa.requestFocusInWindow();
            return false;
        }

        if (donGia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đơn giá không được để trống.");
            txtDonGia.requestFocusInWindow();
            return false;
        }

        if (!donGia.matches("[0-9]+(\\.[0-9]{1,2})?")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Đơn giá phải là số không âm, tối đa 2 chữ số thập phân.\n"
                            + "Ví dụ: 150000 hoặc 150000.50, không nhập dấu phân cách hàng nghìn.");
            txtDonGia.requestFocusInWindow();
            return false;
        }

        BigDecimal gia = new BigDecimal(donGia);

        if (gia.compareTo(new BigDecimal("9999999999999999.99")) > 0) {
            JOptionPane.showMessageDialog(this, "Đơn giá vượt giới hạn DECIMAL(18,2).");
            txtDonGia.requestFocusInWindow();
            return false;
        }

        if (cboTrangThai.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái.");
            cboTrangThai.requestFocusInWindow();
            return false;
        }

        return true;
    }

    // ==================== TẢI DỮ LIỆU TỪ SQL ====================

    private void taiDuLieuTuDatabase() throws SQLException {
        ArrayList<DichVu> duLieu = dichVuDao.layDanhSachDichVu();

        table.clearSelection();
        model.setRowCount(0);

        danhSachDichVu.clear();
        danhSachDichVu.addAll(duLieu);

        for (DichVu dichVu : danhSachDichVu) {
            model.addRow(new Object[]{
                dichVu.getMaDichVu(),
                dichVu.getTenDichVu(),
                dichVu.getMoTa(),
                dichVu.getDonGia(),
                dichVu.isTrangThai() ? "Hoạt động" : "Ngừng hoạt động"
            });
        }

        xoaThongTin();
        capNhatSoLuong();
        capNhatTrangThaiForm();
    }

    // ==================== HIỂN THỊ DỮ LIỆU LÊN FORM ====================

    private DichVu layDichVuDangChon() {
        int dong = table.getSelectedRow();

        if (dong < 0) {
            return null;
        }

        int dongModel = table.convertRowIndexToModel(dong);

        if (dongModel >= danhSachDichVu.size()) {
            return null;
        }

        return danhSachDichVu.get(dongModel);
    }

    private void hienThiDichVuDangChon() {
        DichVu dichVu = layDichVuDangChon();

        if (dichVu == null) {
            xoaThongTin();
            capNhatTrangThaiForm();
            return;
        }

        txtMaDichVu.setText(dichVu.getMaDichVu());
        txtTenDichVu.setText(dichVu.getTenDichVu());
        txtDonGia.setText(dichVu.getDonGia() == null ? "" : dichVu.getDonGia().toPlainString());
        txtMoTa.setText(dichVu.getMoTa());
        cboTrangThai.setSelectedIndex(dichVu.isTrangThai() ? 0 : 1);
        txtMoTa.setCaretPosition(0);

        capNhatTrangThaiForm();
    }

    private void chonDichVuTheoMa(String ma) {
        for (int i = 0; i < danhSachDichVu.size(); i++) {
            if (danhSachDichVu.get(i).getMaDichVu().equals(ma)) {
                int dongHienThi = table.convertRowIndexToView(i);

                if (dongHienThi >= 0) {
                    table.setRowSelectionInterval(dongHienThi, dongHienThi);
                    table.scrollRectToVisible(table.getCellRect(dongHienThi, 0, true));
                }

                return;
            }
        }
    }

    // ==================== CÁC HÀM HỖ TRỢ ====================

    private void xoaThongTin() {
        txtMaDichVu.setText("");
        txtTenDichVu.setText("");
        txtDonGia.setText("");
        txtMoTa.setText("");
        cboTrangThai.setSelectedIndex(0);
    }

    private void capNhatTrangThaiForm() {
        DichVu dichVu = layDichVuDangChon();

        txtMaDichVu.setEditable(false);
        txtTenDichVu.setEditable(true);
        txtDonGia.setEditable(true);
        txtMoTa.setEditable(true);
        cboTrangThai.setEnabled(true);

        btnThem.setEnabled(dichVu == null);
        btnSua.setEnabled(dichVu != null);
        btnNgungHoatDong.setEnabled(dichVu != null && dichVu.isTrangThai());
    }

    private void capNhatSoLuong() {
        lblSoDichVu.setText(
                "Hiển thị: " + table.getRowCount() + " / " + danhSachDichVu.size() + " dịch vụ");
    }

    private void baoLoi(String noiDung, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                this,
                noiDung + "\n" + ex.getMessage(),
                "Thông báo",
                JOptionPane.ERROR_MESSAGE);
    }

    private void themO(JPanel form, String ten, JComponent field) {
        JPanel o = new JPanel(new BorderLayout(0, 4));
        o.setOpaque(false);

        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("Arial", Font.BOLD, 12));
        nhan.setForeground(new Color(87, 100, 117));

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(CHU);
        field.setPreferredSize(new Dimension(200, 34));

        if (field instanceof JTextField) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(VIEN),
                    new EmptyBorder(5, 7, 5, 7)));
        }

        o.add(nhan, BorderLayout.NORTH);
        o.add(field, BorderLayout.CENTER);
        form.add(o);
    }

    private static JButton taoNut(String ten, boolean chinh) {
        JButton nut = new JButton(ten);
        nut.setFont(new Font("Arial", Font.BOLD, 12));
        nut.setBackground(chinh ? DO : Color.WHITE);
        nut.setForeground(chinh ? Color.WHITE : CHU);
        nut.setContentAreaFilled(false);
        nut.setOpaque(true);
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(chinh ? DO : VIEN),
                new EmptyBorder(8, 14, 8, 14)));
        return nut;
    }

    // ==================== MENU VÀ THANH CÔNG CỤ ====================

    private ImageIcon loadIcon(String path, int width, int height) {
        java.net.URL url = getClass().getResource(path);

        if (url == null) {
            return null;
        }

        ImageIcon icon = new ImageIcon(url);
        Image anh = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(anh);
    }

    private JMenu taoMenuDropdown(String tenMenu, String[] cacItem) {
        JMenu menu = new JMenu(tenMenu);

        for (String ten : cacItem) {
            if ("-".equals(ten)) {
                menu.addSeparator();
            } else {
                menu.add(new JMenuItem(ten));
            }
        }

        return menu;
    }

    private JButton taoNutNhanh(String tieuDe, String phimTat, ImageIcon icon) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 0));

        if (icon != null) {
            btn.add(new JLabel(icon), BorderLayout.WEST);
        }

        JPanel pText = new JPanel(new GridLayout(2, 1));
        pText.setOpaque(false);

        JLabel lblTen = new JLabel(tieuDe);
        lblTen.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblPhim = new JLabel(phimTat);
        lblPhim.setFont(new Font("Arial", Font.PLAIN, 11));
        lblPhim.setForeground(Color.GRAY);

        pText.add(lblTen);
        pText.add(lblPhim);
        btn.add(pText, BorderLayout.CENTER);

        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                new EmptyBorder(6, 10, 6, 10)));

        return btn;
    }
}