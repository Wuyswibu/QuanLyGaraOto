package ui;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.LinkedHashMap;
import entity.HangMucDichVu;
import entity.PhieuSuaChua;
import service.TienDoSuaChuaService;
import service.TienDoSuaChuaService.ChiTiet;

/** Chọn mã PSC để cập nhật trạng thái cả phiếu; bảng dịch vụ chỉ để xem. */
public class CapNhatTTSuaXeUI extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color DO = new Color(180, 30, 40);
    private static final Color VIEN = new Color(218, 224, 230);
    private final TienDoSuaChuaService service;
    private final JTextField txtTimKiem = new JTextField(24);
    private final JTextField txtMaPhieu = new JTextField();
    private final JTextField txtMaTiepNhan = new JTextField();
    private final JTextField txtLoaiPhieu = new JTextField();
    private final JTextField txtChanDoan = new JTextField();
    private final JTextField txtTinhTrang = new JTextField();
    private final JTextField txtNgayLap = new JTextField();
    private final JComboBox<String> cboPhieu = new JComboBox<>();
    private final JComboBox<String> cboLocTrangThai = new JComboBox<>(new String[]{
        "Tất cả", "Chờ thực hiện", "Chưa bắt đầu", "Đang thực hiện", "Hoàn thành"
    });
    private final JButton btnTim = nut("Tìm kiếm", false);
    private final JButton btnTaiLai = nut("Tải lại", false);
    private final JButton btnXuatKho = nut("Yêu cầu xuất kho", false);
    private final JButton btnPhanCong = nut("Phân công kỹ thuật viên", false);
    private final JButton btnCapNhat = nut("Cập nhật trạng thái phiếu", true);
    private final JProgressBar tienDo = new JProgressBar(0, 100);
    private final JLabel thongBao = new JLabel("Chọn phiếu sửa chữa để xem các hạng mục.");
    private final DefaultTableModel model = new DefaultTableModel(new String[]{
        "STT", "Công việc", "Trạng thái", "Công dự kiến", "Công thực tế", "Phụ tùng", "Giá phụ tùng", "Kỹ thuật viên","Kết quả", "Ghi chú"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable bang = new JTable(model);
    private List<PhieuSuaChua> danhSachPhieu = new ArrayList<>();
    private final List<HangMucDichVu> hangMucHienThi = new ArrayList<>();
    private ChiTiet chiTiet;
    private boolean dangTai;
    private boolean dangDoPhieu;

    public CapNhatTTSuaXeUI() { this(new TienDoSuaChuaService()); }

    public CapNhatTTSuaXeUI(TienDoSuaChuaService service) {
        this.service = service;
        taoGiaoDien();
        SwingUtilities.invokeLater(() -> loadDuLieu(""));
    }

    private void taoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(new Color(225, 229, 235));
        setBorder(new EmptyBorder(18, 28, 18, 28));
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VIEN), new EmptyBorder(20, 22, 20, 22)));
        add(card);

        JPanel tren = new JPanel(new BorderLayout(0, 12));
        tren.setOpaque(false);
        JLabel tieuDe = new JLabel("CẬP NHẬT TIẾN ĐỘ SỬA CHỮA");
        tieuDe.setFont(new Font("Arial", Font.BOLD, 18));
        tren.add(tieuDe, BorderLayout.NORTH);

        JPanel tim = new JPanel(new BorderLayout(8, 0));
        tim.setOpaque(false);
        txtTimKiem.setToolTipText("Mã phiếu, mã tiếp nhận, biển số hoặc tên khách hàng; Enter để tìm.");
        JPanel nutTim = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        nutTim.setOpaque(false);
        nutTim.add(btnTim);
        nutTim.add(btnTaiLai);
        tim.add(txtTimKiem, BorderLayout.CENTER);
        tim.add(nutTim, BorderLayout.EAST);
        JPanel chon = new JPanel(new BorderLayout(8, 8));
        chon.setOpaque(false);
        chon.add(tim, BorderLayout.NORTH);
        chon.add(cboPhieu, BorderLayout.SOUTH);
        tren.add(chon, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 2, 22, 10));
        form.setOpaque(false);
        truong(form, "Phiếu sửa chữa", txtMaPhieu);
        truong(form, "Mã tiếp nhận", txtMaTiepNhan);
        truong(form, "Loại phiếu", txtLoaiPhieu);
        truong(form, "Ngày lập", txtNgayLap);
        truong(form, "Tình trạng phiếu", txtTinhTrang);
        truong(form, "Chẩn đoán", txtChanDoan);
        tren.add(form, BorderLayout.SOUTH);
        card.add(tren, BorderLayout.NORTH);

        JPanel giua = new JPanel(new BorderLayout(0, 10));
        giua.setOpaque(false);
        JPanel loc = new JPanel(new BorderLayout(16, 0));
        loc.setOpaque(false);
        tienDo.setStringPainted(true);
        tienDo.setString("Chưa chọn phiếu");
        tienDo.setForeground(new Color(45, 140, 95));
        loc.add(tienDo, BorderLayout.CENTER);
        loc.add(cboLocTrangThai, BorderLayout.EAST);
        giua.add(loc, BorderLayout.NORTH);
        bang.setRowHeight(34);
        bang.setFont(new Font("Arial", Font.PLAIN, 13));
        bang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        bang.getTableHeader().setReorderingAllowed(false);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bang.setAutoCreateRowSorter(true);
        bang.setFillsViewportHeight(true);
        JScrollPane cuon = new JScrollPane(bang);
        cuon.setBorder(BorderFactory.createLineBorder(VIEN));
        giua.add(cuon, BorderLayout.CENTER);
        card.add(giua, BorderLayout.CENTER);

        JPanel duoi = new JPanel(new BorderLayout(8, 8));
        duoi.setOpaque(false);
        thongBao.setForeground(new Color(95, 105, 120));
        duoi.add(thongBao, BorderLayout.NORTH);
        JPanel cacNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        cacNut.setOpaque(false);
        cacNut.add(btnPhanCong);
        cacNut.add(btnXuatKho);
        cacNut.add(btnCapNhat);

        duoi.add(cacNut, BorderLayout.SOUTH);
        card.add(duoi, BorderLayout.SOUTH);

        btnTim.addActionListener(e -> loadDuLieu(txtTimKiem.getText()));
        txtTimKiem.addActionListener(e -> loadDuLieu(txtTimKiem.getText()));
        btnTaiLai.addActionListener(e -> loadDuLieu(txtTimKiem.getText()));
        cboPhieu.addActionListener(e -> {
            int index = cboPhieu.getSelectedIndex();
            if (!dangDoPhieu && !dangTai && index >= 0 && index < danhSachPhieu.size()) {
                taiChiTiet(danhSachPhieu.get(index).getMaPhieuSuaChua());
            }
        });
        cboLocTrangThai.addActionListener(e -> doBang());
        bang.getSelectionModel().addListSelectionListener(e -> capNhatNut());
        btnCapNhat.addActionListener(e -> suaPhieu());

        btnXuatKho.addActionListener(e -> {
            Window cuaSo = SwingUtilities.getWindowAncestor(this);
            if (cuaSo instanceof GaraManHinhChinh) {
                ((GaraManHinhChinh) cuaSo).chuyenManHinh("XUAT_KHO");
            } else {
                JOptionPane.showMessageDialog(this, "Mở từ màn hình chính để chuyển sang xuất kho.");
            }
        });
        
        btnPhanCong.addActionListener(e -> {
            Window cuaSo = SwingUtilities.getWindowAncestor(this);

            if (cuaSo instanceof GaraManHinhChinh) {
                ((GaraManHinhChinh) cuaSo).chuyenManHinh("PHAN_CONG");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chạy từ màn hình chính để mở phân công kỹ thuật viên.");
            }
        });
        capNhatNut();
    }

    /** Giữ API cũ để màn hình chính có thể gọi với mã phiếu hoặc mã tiếp nhận. */
    public void loadDuLieu(String tuKhoa) {
        if (dangTai) return;
        String maCu = chiTiet == null ? null : chiTiet.phieu.getMaPhieuSuaChua();
        chayNen(() -> service.timPhieu(tuKhoa), ds -> {
            danhSachPhieu = ds;
            dangDoPhieu = true;
            cboPhieu.removeAllItems();
            int chon = 0;
            for (int i = 0; i < ds.size(); i++) {
                PhieuSuaChua p = ds.get(i);
                cboPhieu.addItem(p.getMaPhieuSuaChua() + " | " + p.getMaTiepNhan()
                        + " | " + p.getTrangThai());
                if (p.getMaPhieuSuaChua().equals(maCu)) chon = i;
            }
            if (!ds.isEmpty()) cboPhieu.setSelectedIndex(chon);
            dangDoPhieu = false;
            if (ds.isEmpty()) {
                hienThi(null);
                thongBao.setText("Không tìm thấy phiếu sửa chữa. Phiếu tiếp nhận cần được lập phiếu sửa chữa trước.");
            } else {
                taiChiTiet(ds.get(chon).getMaPhieuSuaChua());
            }
        });
    }

    private void taiChiTiet(String ma) {
        hienThi(null);
        chayNen(() -> service.layChiTiet(ma), this::hienThi);
    }

    private void hienThi(ChiTiet duLieu) {
        chiTiet = duLieu;
        PhieuSuaChua p = duLieu == null ? null : duLieu.phieu;
        txtMaPhieu.setText(p == null ? "" : p.getMaPhieuSuaChua());
        txtMaTiepNhan.setText(p == null ? "" : p.getMaTiepNhan());
        txtLoaiPhieu.setText(p == null ? "" : p.getLoaiPhieu());
        txtTinhTrang.setText(p == null ? "" : p.getTrangThai());
        txtChanDoan.setText(p == null ? "" : p.getChanDoan());
        txtChanDoan.setToolTipText(p == null ? null : p.getChanDoan());
        txtNgayLap.setText(p == null || p.getNgayLap() == null ? ""
                : p.getNgayLap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        int tong = duLieu == null ? 0 : duLieu.hangMuc.size();
        tienDo.setVisible(false);
        thongBao.setText(p == null ? "Chọn mã phiếu PSC để cập nhật trạng thái."
                : TienDoSuaChuaService.daDong(p.getTrangThai()) ? "Phiếu đã kết thúc; chỉ xem thông tin."
                : "Đang chọn " + p.getMaPhieuSuaChua() + " — cập nhật trạng thái cho toàn bộ phiếu ("
                        + tong + " hạng mục).");
        if (p != null) {
            int index = cboPhieu.getSelectedIndex();
            if (index >= 0 && index < danhSachPhieu.size()
                    && danhSachPhieu.get(index).getMaPhieuSuaChua().equals(p.getMaPhieuSuaChua())) {
                dangDoPhieu = true;
                cboPhieu.removeItemAt(index);
                cboPhieu.insertItemAt(p.getMaPhieuSuaChua() + " | " + p.getMaTiepNhan()
                        + " | " + p.getTrangThai(), index);
                cboPhieu.setSelectedIndex(index);
                dangDoPhieu = false;
            }
        }
        doBang();
    }

    private void doBang() {
        model.setRowCount(0);
        hangMucHienThi.clear();

        if (chiTiet != null) {
            List<String> tenPhuTung = new ArrayList<>();
            List<String> giaPhuTung = new ArrayList<>();

            for (TienDoSuaChuaService.PhuTungHienThi pt : chiTiet.phuTung) {
                tenPhuTung.add(pt.ten);
                giaPhuTung.add(tien(pt.donGia));
            }

            String phuTung = tenPhuTung.isEmpty()
                    ? "Chưa có"
                    : String.join("; ", tenPhuTung);

            String gia = giaPhuTung.isEmpty()
                    ? ""
                    : String.join("; ", giaPhuTung);

            String loc = (String) cboLocTrangThai.getSelectedItem();

            for (HangMucDichVu h : chiTiet.hangMuc) {
                if (!"Tất cả".equals(loc)
                        && !java.util.Objects.equals(h.getTrangThai(), loc)) {
                    continue;
                }

                hangMucHienThi.add(h);

                model.addRow(new Object[]{
                	    hangMucHienThi.size(),
                	    h.getTenHangMuc(),
                	    h.getTrangThai(),
                	    tien(h.getChiPhiDuKien()),
                	    tien(h.getChiPhiThucTe()),
                	    phuTung,
                	    gia,
                	    chiTiet.tenKyThuatVien,
                	    h.getKetQua(),
                	    h.getGhiChu()
                	});
            }
        }

        capNhatNut();
    }

    private void suaPhieu() {
        if (dangTai || chiTiet == null) {
            return;
        }
        String ma = chiTiet.phieu.getMaPhieuSuaChua();
        JComboBox<String> trangThai = new JComboBox<>(new String[]{
            "Chờ sửa chữa",
            "Đang sửa chữa",
            "Chờ kiểm tra",
            "Đã hoàn thành"
        });

        String cu = chiTiet.phieu.getTrangThai();
        if ("Chưa bắt đầu".equals(cu)) {
            cu = "Chờ sửa chữa";
        }
        trangThai.setSelectedItem(cu);
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 12));
        form.add(new JLabel("Phiếu sửa chữa: " + ma));
        form.add(new JLabel("Trạng thái hiện tại: " + chiTiet.phieu.getTrangThai()));
        form.add(trangThai);
        form.add(new JLabel("Áp dụng cho toàn bộ hạng mục trong phiếu."));

        int luaChon = JOptionPane.showConfirmDialog(this,form,"Cập nhật trạng thái " + ma,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);

        if (luaChon != JOptionPane.OK_OPTION) {
            return;
        }

        String moi = (String) trangThai.getSelectedItem();

        if (TienDoSuaChuaService.DA_HOAN_THANH.equals(moi)) {
            Map<String, String> tienCong = nhapTienCongThucTe();

            if (tienCong == null) {
                return;
            }

            int xacNhan = JOptionPane.showConfirmDialog( this,"Xác nhận hoàn thành phiếu " + ma + " và lưu tiền công thực tế?\n" + "Sau khi hoàn thành, phiếu chỉ được xem.", "Xác nhận hoàn thành", JOptionPane.YES_NO_OPTION);

            if (xacNhan != JOptionPane.YES_OPTION) {
                return;
            }

            chayNen(() -> {
                service.hoanThanhPhieu(ma, tienCong);
                return true;
            }, ok -> sauKhiLuu(
                    ma, "Đã hoàn thành phiếu và lưu tiền công thực tế."));

            return;
        }

        chayNen(() -> {
            service.capNhatTrangThaiPhieu(ma, moi);
            return true;
        }, ok -> sauKhiLuu(
                ma, "Đã cập nhật phiếu " + ma + " thành: " + moi));
    }
    
    private Map<String, String> nhapTienCongThucTe() {
        if (chiTiet.hangMuc.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Phiếu chưa có hạng mục dịch vụ.");
            return null;
        }

        JPanel cacDong = new JPanel(new GridLayout(0, 2, 12, 10));

        Map<String, JTextField> oNhapTheoMa = new LinkedHashMap<>();

        for (HangMucDichVu h : chiTiet.hangMuc) {
            BigDecimal giaGoiY = h.getChiPhiThucTe();

            if (giaGoiY == null || giaGoiY.signum() == 0) {
                giaGoiY = h.getChiPhiDuKien();
            }

            JTextField oNhap = new JTextField(
                    giaGoiY == null
                            ? ""
                            : giaGoiY.stripTrailingZeros().toPlainString(),
                    15);

            cacDong.add(new JLabel(h.getTenHangMuc()));
            cacDong.add(oNhap);

            oNhapTheoMa.put(h.getMaHangMuc(), oNhap);
        }

        JPanel noiDung = new JPanel(new BorderLayout(0, 12));
        noiDung.add(new JLabel(
                "Kiểm tra tiền công thực tế (VNĐ), ví dụ: 150000."),
                BorderLayout.NORTH);

        JScrollPane cuon = new JScrollPane(cacDong);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.setPreferredSize(new Dimension(
                600,
                Math.min(350, chiTiet.hangMuc.size() * 42 + 15)));

        noiDung.add(cuon, BorderLayout.CENTER);

        int luaChon = JOptionPane.showConfirmDialog(
                this,
                noiDung,
                "Xác nhận tiền công thực tế",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (luaChon != JOptionPane.OK_OPTION) {
            return null;
        }

        Map<String, String> ketQua = new LinkedHashMap<>();

        for (Map.Entry<String, JTextField> entry : oNhapTheoMa.entrySet()) {
            ketQua.put(entry.getKey(), entry.getValue().getText());
        }

        return ketQua;
    }

    private void sauKhiLuu(String ma, String tin) {
        JOptionPane.showMessageDialog(this, tin);
        // Vô hiệu hóa bản chi tiết cũ; tải lại lỗi thì người dùng bấm Tải lại.
        hienThi(null);
        taiChiTiet(ma);
    }

    private <T> void chayNen(Callable<T> viec, Consumer<T> thanhCong) {
        if (dangTai) return;
        dangTai = true;
        capNhatNut();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return viec.call(); }
            @Override protected void done() {
                dangTai = false;
                setCursor(Cursor.getDefaultCursor());
                try { thanhCong.accept(get()); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                catch (ExecutionException ex) {
                    Throwable loi = ex.getCause();
                    loi.printStackTrace();
                    JOptionPane.showMessageDialog(CapNhatTTSuaXeUI.this, loi.getMessage(),
                            "Không thực hiện được", JOptionPane.ERROR_MESSAGE);
                } finally { capNhatNut(); }
            }
        }.execute();
    }

    private void capNhatNut() {
        boolean coPhieu = chiTiet != null;
        boolean duocSua = coPhieu && !TienDoSuaChuaService.daDong(chiTiet.phieu.getTrangThai());
        btnTim.setEnabled(!dangTai);
        btnTaiLai.setEnabled(!dangTai);
        txtTimKiem.setEnabled(!dangTai);
        cboPhieu.setEnabled(!dangTai && !danhSachPhieu.isEmpty());
        cboLocTrangThai.setEnabled(!dangTai);
        bang.setEnabled(!dangTai);
        btnCapNhat.setEnabled(!dangTai && duocSua);
        btnXuatKho.setEnabled(!dangTai && duocSua);

    }

    private static String tien(BigDecimal so) {
        return so == null
                ? ""
                : String.format(
                        Locale.forLanguageTag("vi-VN"), "%,.0f đ", so);
    }

    private static void truong(JPanel form, String nhan, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(new JLabel(nhan), BorderLayout.NORTH);
        field.setEditable(false);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(180, 32));
        field.setMargin(new Insets(4, 8, 4, 8));
        p.add(field, BorderLayout.CENTER);
        form.add(p);
    }

    private static JButton nut(String ten, boolean chinh) {
        JButton b = new JButton(ten);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBackground(chinh ? DO : Color.WHITE);
        b.setForeground(chinh ? Color.WHITE : new Color(40, 45, 55));
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(chinh ? DO : VIEN),
                new EmptyBorder(9, 14, 9, 14)));
        return b;
    }
}
