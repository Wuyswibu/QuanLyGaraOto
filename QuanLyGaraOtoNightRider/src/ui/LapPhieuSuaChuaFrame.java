package ui;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import entity.DichVu;
import entity.HangMucDichVu;
import entity.KhachHang;
import entity.PhieuSuaChua;
import entity.PhieuTiepNhan;
import entity.Xe;
import service.PhieuSuaChuaService;
import service.PhieuSuaChuaService.DuLieuKhongHopLeException;

public class LapPhieuSuaChuaFrame extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    private static final DateTimeFormatter DINH_DANG = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    private final PhieuSuaChuaService phieuSuaChuaService = new PhieuSuaChuaService();

    private final ArrayList<PhieuTiepNhan> danhSachTiepNhan = new ArrayList<>();
    private final ArrayList<DichVu> danhSachDichVu = new ArrayList<>();
    private final ArrayList<HangMucDichVu> danhSachHangMuc = new ArrayList<>();

    private final Map<String, Xe> xeTheoMa = new LinkedHashMap<>();
    private final Map<String, KhachHang> khachHangTheoMa = new LinkedHashMap<>();
    private final Map<String, DichVu> dichVuTheoMa = new LinkedHashMap<>();

    private PhieuSuaChua phieuDangXem;
    private boolean dangHienThi;
    private int viTriTiepNhanCu;

// GIAO DIỆN

    private final JTextField txtMaPhieu = new JTextField();
    private final JComboBox<String> cboTiepNhan = new JComboBox<>();

    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtMauXe = new JTextField();
    private final JTextField txtKhachHang = new JTextField();
    private final JTextField txtNgayLap = new JTextField();
    private final JTextField txtTrangThai = new JTextField();

    private final JComboBox<String> cboLoaiPhieu = new JComboBox<>(
            new String[]{"Sửa chữa", "Bảo dưỡng", "Bảo hành"});

    private final JTextArea txtYeuCau = new JTextArea(3, 20);
    private final JTextArea txtChanDoan = new JTextArea(3, 20);
    private final JTextArea txtGhiChu = new JTextArea(3, 20);

    private final JButton btnThemHangMuc = taoNut("Thêm hạng mục", false);
    private final JButton btnSuaHangMuc = taoNut("Sửa hạng mục", false);
    private final JButton btnBoHangMuc = taoNut("Bỏ hạng mục", false);

    private final JButton btnLuu = taoNut("Lưu phiếu sửa chữa", true);
    private final JButton btnLamMoi = taoNut("Làm mới", false);
    private final JButton btnHuy = taoNut("Hủy", false);

    private final JLabel lblTong = new JLabel("Tổng chi phí dự kiến: 0 đ");

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                "Mã hạng mục", "Dịch vụ", "Tên hạng mục",
                "Mô tả", "Chi phí dự kiến", "Trạng thái"
            }, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == 4 ? BigDecimal.class : String.class;
        }
    };

    private final JTable table = new JTable(model);

    public LapPhieuSuaChuaFrame() {
        khoiTaoGiaoDien();
        dangKySuKien();
        xoaForm();

        try {
            taiDuLieuTuSQL();
        } catch (SQLException | IllegalArgumentException ex) {
            hienThiLoi(ex);
        }
    }

// KHỞI TẠO GIAO DIỆN

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(new Color(216, 220, 228));
        setBorder(new EmptyBorder(20, 55, 15, 55));

        txtMaPhieu.setEditable(false);
        txtBienSo.setEditable(false);
        txtMauXe.setEditable(false);
        txtKhachHang.setEditable(false);
        txtNgayLap.setEditable(false);
        txtTrangThai.setEditable(false);
        txtYeuCau.setEditable(false);

        cboTiepNhan.addItem("-- Chọn phiếu tiếp nhận --");

        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));

        JLabel tieuDe = new JLabel("LẬP PHIẾU SỬA CHỮA");
        tieuDe.setOpaque(true);
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setForeground(CHU);
        tieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        tieuDe.setBorder(new EmptyBorder(10, 16, 10, 16));

        khung.add(tieuDe, BorderLayout.NORTH);

        NoiDungCuon noiDung = new NoiDungCuon();
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));

        // THÔNG TIN PHIẾU

        JPanel phanTren = new JPanel(new BorderLayout(0, 12));
        phanTren.setOpaque(false);

        JLabel moTa = new JLabel( "Lập phiếu từ thông tin tiếp nhận; có thể bổ sung chẩn đoán và hạng mục sau.");
        moTa.setFont(new Font("Arial", Font.PLAIN, 13));
        moTa.setForeground(CHU_PHU);

        phanTren.add(moTa, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 24, 10));
        form.setOpaque(false);

        themO(form, "Mã phiếu sửa chữa", txtMaPhieu);
        themO(form, "Phiếu tiếp nhận", cboTiepNhan);
        themO(form, "Biển số", txtBienSo);
        themO(form, "Mẫu xe", txtMauXe);
        themO(form, "Khách hàng", txtKhachHang);
        themO(form, "Ngày lập", txtNgayLap);
        themO(form, "Loại phiếu", cboLoaiPhieu);
        themO(form, "Trạng thái", txtTrangThai);
        
        txtMaPhieu.setBackground(new Color(240, 240, 240));
        phanTren.add(form, BorderLayout.CENTER);

        JPanel ghiNhan = new JPanel(new GridLayout(1, 3, 16, 0));
        ghiNhan.setOpaque(false);

        themVanBan(ghiNhan, "Yêu cầu khách hàng", txtYeuCau);
        themVanBan(ghiNhan, "Chẩn đoán", txtChanDoan);
        themVanBan(ghiNhan, "Ghi chú", txtGhiChu);

        phanTren.add(ghiNhan, BorderLayout.SOUTH);
        noiDung.add(phanTren, BorderLayout.NORTH);

        // BẢNG HẠNG MỤC

        JPanel vungBang = new JPanel(new BorderLayout(0, 10));
        vungBang.setOpaque(false);

        JPanel thanhHangMuc = new JPanel(new BorderLayout());
        thanhHangMuc.setBackground(new Color(237, 241, 245));
        thanhHangMuc.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel lblHangMuc = new JLabel("HẠNG MỤC DỊCH VỤ DỰ KIẾN");
        lblHangMuc.setFont(new Font("Arial", Font.BOLD, 12));
        lblHangMuc.setForeground(CHU);

        JPanel nutHangMuc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        nutHangMuc.setOpaque(false);
        nutHangMuc.add(btnThemHangMuc);
        nutHangMuc.add(btnSuaHangMuc);
        nutHangMuc.add(btnBoHangMuc);

        thanhHangMuc.add(lblHangMuc, BorderLayout.WEST);
        thanhHangMuc.add(nutHangMuc, BorderLayout.EAST);
        vungBang.add(thanhHangMuc, BorderLayout.NORTH);

        table.setRowHeight(34);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(53, 105, 139));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);

        int[] rong = {120, 210, 210, 260, 150, 140};

        for (int i = 0; i < rong.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(rong[i]);
        }
        
        table.setPreferredScrollableViewportSize(new Dimension(1000, 280));
        JScrollPane cuonBang = new JScrollPane(table);
        cuonBang.setBorder(BorderFactory.createLineBorder(VIEN));
        cuonBang.getViewport().setBackground(Color.WHITE);

        vungBang.add(cuonBang, BorderLayout.CENTER);

        lblTong.setFont(new Font("Arial", Font.BOLD, 13));
        lblTong.setForeground(CHU);
        lblTong.setHorizontalAlignment(SwingConstants.RIGHT);

        vungBang.add(lblTong, BorderLayout.SOUTH);
        noiDung.add(vungBang, BorderLayout.CENTER);

        // CÁC NÚT CHỨC NĂNG

        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        thaoTac.add(btnHuy);
        thaoTac.add(btnLamMoi);
        thaoTac.add(btnLuu);

        JScrollPane cuonNoiDung = new JScrollPane(noiDung, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cuonNoiDung.setBorder(BorderFactory.createEmptyBorder());
        cuonNoiDung.getViewport().setBackground(Color.WHITE);
        cuonNoiDung.getVerticalScrollBar().setUnitIncrement(20);
        thaoTac.setBorder(new EmptyBorder(10, 18, 16, 18));
        khung.add(cuonNoiDung, BorderLayout.CENTER);
        khung.add(thaoTac, BorderLayout.SOUTH);
        add(khung, BorderLayout.CENTER);
        
    }

//  ĐĂNG KÝ SỰ KIỆN

    private void dangKySuKien() {
        cboTiepNhan.addActionListener(e -> chonPhieuTiepNhan());

        btnThemHangMuc.addActionListener(e -> nhapHangMuc(false));
        btnSuaHangMuc.addActionListener(e -> nhapHangMuc(true));
        btnBoHangMuc.addActionListener(e -> boHangMuc());

        btnLuu.addActionListener(e -> luuPhieuSuaChua());

        btnLamMoi.addActionListener(e -> {
            if (!xacNhanXoaNoiDung()) {
                return;
            }

            try {
                taiDuLieuTuSQL();
            } catch (SQLException | IllegalArgumentException ex) {
                hienThiLoi(ex);
            }
        });

        btnHuy.addActionListener(e -> {
            if (xacNhanXoaNoiDung()) {
                xoaForm();
                viTriTiepNhanCu = 0;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                capNhatNut();
            }
        });
    }

//  TẢI DỮ LIỆU SQL

    private void taiDuLieuTuSQL() throws SQLException {
        ArrayList<PhieuTiepNhan> tiepNhanMoi =
                phieuSuaChuaService.layDanhSachPhieuTiepNhan();

        ArrayList<Xe> xeMoi = phieuSuaChuaService.layDanhSachXe();

        ArrayList<KhachHang> khachHangMoi =
                phieuSuaChuaService.layDanhSachKhachHang();

        ArrayList<DichVu> dichVuMoi =
                phieuSuaChuaService.layDichVuDangApDung();

        dangHienThi = true;

        try {
            danhSachTiepNhan.clear();
            danhSachTiepNhan.addAll(tiepNhanMoi);

            danhSachDichVu.clear();
            danhSachDichVu.addAll(dichVuMoi);

            xeTheoMa.clear();
            for (Xe xe : xeMoi) {
                xeTheoMa.put(xe.getMaPhuongTien(), xe);
            }

            khachHangTheoMa.clear();
            for (KhachHang khachHang : khachHangMoi) {
                khachHangTheoMa.put(khachHang.getMaKhachHang(), khachHang);
            }

            dichVuTheoMa.clear();
            for (DichVu dichVu : dichVuMoi) {
                dichVuTheoMa.put(dichVu.getMaDichVu(), dichVu);
            }

            cboTiepNhan.removeAllItems();
            cboTiepNhan.addItem("-- Chọn phiếu tiếp nhận --");

            for (PhieuTiepNhan tiepNhan : danhSachTiepNhan) {
                Xe xe = xeTheoMa.get(tiepNhan.getMaPhuongTien());

                String bienSo = xe == null
                        ? tiepNhan.getMaPhuongTien()
                        : xe.getBienSo();

                cboTiepNhan.addItem(
                        tiepNhan.getMaTiepNhan()
                                + " • " + bienSo
                                + " • " + tiepNhan.getTrangThai());
            }

            xoaForm();
            viTriTiepNhanCu = 0;

        } finally {
            dangHienThi = false;
        }

        capNhatNut();
    }

//  CHỌN PHIẾU TIẾP NHẬN 

    private void chonPhieuTiepNhan() {
        if (dangHienThi) {
            return;
        }

        int viTriMoi = cboTiepNhan.getSelectedIndex();

        if (!xacNhanXoaNoiDung()) {
            dangHienThi = true;

            try {
                cboTiepNhan.setSelectedIndex(viTriTiepNhanCu);
            } finally {
                dangHienThi = false;
            }

            return;
        }

        if (viTriMoi <= 0) {
            xoaForm();
            viTriTiepNhanCu = 0;
            return;
        }

        PhieuTiepNhan tiepNhan = danhSachTiepNhan.get(viTriMoi - 1);

        try {
            PhieuSuaChua phieu = phieuSuaChuaService.timTheoMaTiepNhan(
                    tiepNhan.getMaTiepNhan());

            ArrayList<HangMucDichVu> hangMuc = new ArrayList<>();

            if (phieu != null) {
                hangMuc = phieuSuaChuaService.layHangMucTheoPhieu(
                        phieu.getMaPhieuSuaChua());
            }

            phieuDangXem = phieu;
            viTriTiepNhanCu = viTriMoi;

            danhSachHangMuc.clear();
            danhSachHangMuc.addAll(hangMuc);

            hienThiThongTinXe(tiepNhan);

            if (phieu != null) {
                txtMaPhieu.setText(phieu.getMaPhieuSuaChua());
                txtNgayLap.setText(phieu.getNgayLap() == null
                        ? "" : phieu.getNgayLap().format(DINH_DANG));
                txtTrangThai.setText(phieu.getTrangThai());
                cboLoaiPhieu.setSelectedItem(phieu.getLoaiPhieu());
                txtChanDoan.setText(phieu.getChanDoan());
                txtGhiChu.setText(phieu.getGhiChu());
            } else {
                txtMaPhieu.setText("");
                txtNgayLap.setText(LocalDateTime.now().format(DINH_DANG));
                txtTrangThai.setText(PhieuSuaChuaService.CHO_SUA_CHUA);
                cboLoaiPhieu.setSelectedIndex(0);
                txtChanDoan.setText("");
                txtGhiChu.setText("");
            }

            txtChanDoan.setCaretPosition(0);
            txtGhiChu.setCaretPosition(0);

            duaHangMucLenBang();

        } catch (SQLException | IllegalArgumentException ex) {
            dangHienThi = true;

            try {
                cboTiepNhan.setSelectedIndex(viTriTiepNhanCu);
            } finally {
                dangHienThi = false;
            }

            hienThiLoi(ex);
        }
    }

    private void hienThiThongTinXe(PhieuTiepNhan tiepNhan) {
        txtBienSo.setText("");
        txtMauXe.setText("");
        txtKhachHang.setText("");
        txtYeuCau.setText(tiepNhan.getYeuCauKhachHang());
        txtYeuCau.setCaretPosition(0);

        Xe xe = xeTheoMa.get(tiepNhan.getMaPhuongTien());

        if (xe == null) {
            return;
        }

        txtBienSo.setText(xe.getBienSo());
        txtMauXe.setText(xe.getMauXe());

        KhachHang khachHang = khachHangTheoMa.get(xe.getMaKhachHang());

        if (khachHang != null) {
            txtKhachHang.setText(
                    khachHang.getMaKhachHang() + " • " + khachHang.getHoTen());
        }
    }

//  THÊM VÀ SỬA HẠNG MỤC ĐANG NHẬP 

    private void nhapHangMuc(boolean sua) {
        if (layTiepNhanDangChon() == null || phieuDangXem != null) {
            return;
        }

        int dongModel = -1;

        if (sua) {
            int dong = table.getSelectedRow();

            if (dong < 0) {
                JOptionPane.showMessageDialog(this, "Chọn hạng mục cần sửa.");
                return;
            }

            dongModel = table.convertRowIndexToModel(dong);
        }

        ArrayList<DichVu> dichVuLuaChon;

        try {
            dichVuLuaChon = phieuSuaChuaService.layDichVuDangApDung();
        } catch (SQLException | IllegalArgumentException ex) {
            hienThiLoi(ex);
            return;
        }

        if (dichVuLuaChon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có dịch vụ đang áp dụng.");
            return;
        }

        JComboBox<String> cboDichVu = new JComboBox<>();

        for (DichVu dichVu : dichVuLuaChon) {
            cboDichVu.addItem(
                    dichVu.getMaDichVu() + " • " + dichVu.getTenDichVu());

            dichVuTheoMa.put(dichVu.getMaDichVu(), dichVu);
        }

        JTextField txtTen = new JTextField();
        JTextArea txtMoTa = new JTextArea(3, 20);
        JTextField txtChiPhi = new JTextField();

        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);

        if (sua) {
            HangMucDichVu hangMuc = danhSachHangMuc.get(dongModel);

            cboDichVu.setSelectedIndex(-1);

            for (int i = 0; i < dichVuLuaChon.size(); i++) {
                if (dichVuLuaChon.get(i).getMaDichVu().equals(hangMuc.getMaDichVu())) {
                    cboDichVu.setSelectedIndex(i);
                    break;
                }
            }

            txtTen.setText(hangMuc.getTenHangMuc());
            txtMoTa.setText(hangMuc.getMoTa());
            txtChiPhi.setText(hangMuc.getChiPhiDuKien().toPlainString());

        } else {
            DichVu dichVu = dichVuLuaChon.get(0);

            txtTen.setText(dichVu.getTenDichVu());
            txtMoTa.setText(dichVu.getMoTa());
            txtChiPhi.setText(dichVu.getDonGia().toPlainString());
        }

        cboDichVu.addActionListener(e -> {
            int viTri = cboDichVu.getSelectedIndex();

            if (viTri >= 0) {
                DichVu dichVu = dichVuLuaChon.get(viTri);

                txtTen.setText(dichVu.getTenDichVu());
                txtMoTa.setText(dichVu.getMoTa());
                txtChiPhi.setText(dichVu.getDonGia().toPlainString());
            }
        });

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Dịch vụ"));
        form.add(cboDichVu);
        form.add(new JLabel("Tên hạng mục"));
        form.add(txtTen);
        form.add(new JLabel("Mô tả"));
        form.add(new JScrollPane(txtMoTa));
        form.add(new JLabel("Chi phí dự kiến (ví dụ: 150000 hoặc 150000.50)"));
        form.add(txtChiPhi);

        while (true) {
            int chon = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    sua ? "Sửa hạng mục" : "Thêm hạng mục",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (chon != JOptionPane.OK_OPTION) {
                return;
            }

            int viTri = cboDichVu.getSelectedIndex();

            String maDichVu = viTri < 0
                    ? null
                    : dichVuLuaChon.get(viTri).getMaDichVu();

            try {
                HangMucDichVu hangMuc = phieuSuaChuaService.taoHangMucDuKien(
                        maDichVu,
                        txtTen.getText(),
                        txtMoTa.getText(),
                        txtChiPhi.getText());

                if (sua) {
                    danhSachHangMuc.set(dongModel, hangMuc);
                } else {
                    danhSachHangMuc.add(hangMuc);
                }

                duaHangMucLenBang();
                return;

            } catch (DuLieuKhongHopLeException ex) {
                hienThiLoi(ex);
            } catch (SQLException | IllegalArgumentException ex) {
                hienThiLoi(ex);
                return;
            }
        }
    }

//  BỎ HẠNG MỤC ĐANG NHẬP 

    private void boHangMuc() {
        if (phieuDangXem != null) {
            return;
        }

        int dong = table.getSelectedRow();

        if (dong < 0) {
            JOptionPane.showMessageDialog(this, "Chọn hạng mục cần bỏ.");
            return;
        }

        int chon = JOptionPane.showConfirmDialog(
                this,
                "Bỏ hạng mục đang chọn khỏi phiếu?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (chon != JOptionPane.YES_OPTION) {
            return;
        }

        int dongModel = table.convertRowIndexToModel(dong);
        danhSachHangMuc.remove(dongModel);

        duaHangMucLenBang();
    }

//  LƯU PHIẾU SỬA CHỮA 

    private void luuPhieuSuaChua() {
        if (phieuDangXem != null) {
            JOptionPane.showMessageDialog(this, "Phiếu này đã được lưu.");
            return;
        }

        PhieuTiepNhan tiepNhan = layTiepNhanDangChon();

        String maTiepNhan = tiepNhan == null
                ? null
                : tiepNhan.getMaTiepNhan();

        PhieuSuaChua phieuDaLuu;

        try {
            phieuDaLuu = phieuSuaChuaService.luuPhieuSuaChua(
                    maTiepNhan,
                    (String) cboLoaiPhieu.getSelectedItem(),
                    txtChanDoan.getText(),
                    txtGhiChu.getText(),
                    danhSachHangMuc);

        } catch (SQLException | IllegalArgumentException ex) {
            hienThiLoi(ex);
            return;
        }

        // Khóa chỉnh sửa ngay khi Service trả về lưu thành công.
        phieuDangXem = phieuDaLuu;
        txtMaPhieu.setText(phieuDaLuu.getMaPhieuSuaChua());
        txtNgayLap.setText(phieuDaLuu.getNgayLap().format(DINH_DANG));
        txtTrangThai.setText(phieuDaLuu.getTrangThai());
        capNhatNut();

        JOptionPane.showMessageDialog(
                this,
                "Lưu phiếu sửa chữa thành công: "
                        + phieuDaLuu.getMaPhieuSuaChua());

        try {
            ArrayList<HangMucDichVu> hangMucDaLuu =
                    phieuSuaChuaService.layHangMucTheoPhieu(
                            phieuDaLuu.getMaPhieuSuaChua());

            danhSachHangMuc.clear();
            danhSachHangMuc.addAll(hangMucDaLuu);

            duaHangMucLenBang();

        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Phiếu đã lưu nhưng chưa tải lại được hạng mục:\n"
                            + ex.getMessage(),
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

//  HIỂN THỊ BẢNG HẠNG MỤC 

    private void duaHangMucLenBang() {
        BigDecimal tong = phieuSuaChuaService.tinhTongChiPhiDuKien(
                danhSachHangMuc);

        table.clearSelection();
        model.setRowCount(0);

        for (HangMucDichVu hangMuc : danhSachHangMuc) {
            DichVu dichVu = dichVuTheoMa.get(hangMuc.getMaDichVu());

            String tenDichVu = dichVu == null
                    ? ""
                    : dichVu.getTenDichVu();

            model.addRow(new Object[]{
                hangMuc.getMaHangMuc() == null ? "" : hangMuc.getMaHangMuc(),
                tenDichVu,
                hangMuc.getTenHangMuc(),
                hangMuc.getMoTa(),
                String.format(Locale.forLanguageTag("vi-VN"),"%,.0f đ", hangMuc.getChiPhiDuKien()),
                hangMuc.getTrangThai()
            });
        }

        lblTong.setText("Tổng chi phí dự kiến: " + String.format(Locale.forLanguageTag("vi-VN"), "%,.2f đ", tong));

        capNhatNut();
    }

//  TRẠNG THÁI GIAO DIỆN 

    private PhieuTiepNhan layTiepNhanDangChon() {
        int viTri = cboTiepNhan.getSelectedIndex() - 1;

        if (viTri < 0 || viTri >= danhSachTiepNhan.size()) {
            return null;
        }

        return danhSachTiepNhan.get(viTri);
    }

    private void capNhatNut() {
        boolean duocNhap = layTiepNhanDangChon() != null
                && phieuDangXem == null;

        boolean daChonDong = table.getSelectedRow() >= 0;

        txtChanDoan.setEditable(duocNhap);
        txtGhiChu.setEditable(duocNhap);
        cboLoaiPhieu.setEnabled(duocNhap);

        btnThemHangMuc.setEnabled(duocNhap);
        btnSuaHangMuc.setEnabled(duocNhap && daChonDong);
        btnBoHangMuc.setEnabled(duocNhap && daChonDong);
        btnLuu.setEnabled(duocNhap && !danhSachHangMuc.isEmpty());
    }

    private boolean xacNhanXoaNoiDung() {
        if (phieuDangXem != null) {
            return true;
        }

        boolean coNoiDung = !txtChanDoan.getText().trim().isEmpty()
                || !txtGhiChu.getText().trim().isEmpty()
                || !danhSachHangMuc.isEmpty()
                || cboLoaiPhieu.getSelectedIndex() > 0;

        if (!coNoiDung) {
            return true;
        }

        return JOptionPane.showConfirmDialog(
                this,
                "Xóa nội dung đang nhập?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void xoaForm() {
        boolean trangThaiCu = dangHienThi;
        dangHienThi = true;

        try {
            phieuDangXem = null;
            danhSachHangMuc.clear();

            table.clearSelection();
            model.setRowCount(0);

            if (cboTiepNhan.getItemCount() > 0) {
                cboTiepNhan.setSelectedIndex(0);
            }

            cboLoaiPhieu.setSelectedIndex(0);

            txtMaPhieu.setText("");
            txtBienSo.setText("");
            txtMauXe.setText("");
            txtKhachHang.setText("");
            txtNgayLap.setText("");
            txtTrangThai.setText("");
            txtYeuCau.setText("");
            txtChanDoan.setText("");
            txtGhiChu.setText("");

            lblTong.setText("Tổng chi phí dự kiến: 0 đ");

        } finally {
            dangHienThi = trangThaiCu;
        }

        capNhatNut();
    }

    private void hienThiLoi(Exception ex) {
        String thongBao;

        if (ex instanceof DuLieuKhongHopLeException) {
            DuLieuKhongHopLeException loi = (DuLieuKhongHopLeException) ex;
            thongBao = String.join("\n", loi.getLoiTheoTruong().values());
        } else {
            ex.printStackTrace();
            thongBao = ex.getMessage();
        }

        JOptionPane.showMessageDialog(
                this,
                thongBao,
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
        field.setPreferredSize(new Dimension(200, 32));

        o.add(nhan, BorderLayout.NORTH);
        o.add(field, BorderLayout.CENTER);
        form.add(o);
    }

    private void themVanBan(JPanel form, String ten, JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 13));
        area.setBorder(new EmptyBorder(5, 7, 5, 7));

        JScrollPane cuon = new JScrollPane(area);
        cuon.setBorder(BorderFactory.createLineBorder(VIEN));

        themO(form, ten, cuon);
        cuon.setPreferredSize(new Dimension(200, 75));
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

    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame("LapPhieuSuaChuaFrame - NIGHT INVADER");
        cuaSo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cuaSo.setContentPane(this);
        cuaSo.setSize(1340, 900);
        cuaSo.setLocationRelativeTo(null);
        cuaSo.setExtendedState(JFrame.MAXIMIZED_BOTH);

        return cuaSo;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() ->
                new LapPhieuSuaChuaFrame().taoCuaSo().setVisible(true));
    }
    
    private static class NoiDungCuon extends JPanel implements Scrollable {
        public NoiDungCuon() {
            super(new BorderLayout(0, 16));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(
                Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(
                Rectangle visibleRect, int orientation, int direction) {
            return orientation == SwingConstants.VERTICAL
                    ? Math.max(20, visibleRect.height - 20)
                    : Math.max(20, visibleRect.width - 20);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent() != null
                    && getParent().getHeight() > getPreferredSize().height;
        }
    }
}