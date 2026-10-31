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

import entity.HangMucDichVu;
import entity.PhieuSuaChua;
import service.TienDoSuaChuaService;
import service.TienDoSuaChuaService.ChiTiet;

/** Giao diện cập nhật tiến độ từ hạng mục thật trong SQL. */
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
        "Tất cả", "Chưa bắt đầu", "Đang thực hiện", "Hoàn thành"
    });
    private final JButton btnTim = nut("Tìm kiếm", false);
    private final JButton btnTaiLai = nut("Tải lại", false);
    private final JButton btnXuatKho = nut("Yêu cầu xuất kho", false);
    private final JButton btnCapNhat = nut("Cập nhật hạng mục", false);
    private final JButton btnHoanThanh = nut("Hoàn thành kiểm tra", true);
    private final JProgressBar tienDo = new JProgressBar(0, 100);
    private final JLabel thongBao = new JLabel("Chọn phiếu sửa chữa để xem các hạng mục.");
    private final DefaultTableModel model = new DefaultTableModel(new String[]{
        "Mã hạng mục", "Công việc", "Trạng thái", "Công dự kiến", "Công thực tế", "Kết quả", "Ghi chú"
    }, 0) {
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
        cacNut.add(btnXuatKho);
        cacNut.add(btnCapNhat);
        cacNut.add(btnHoanThanh);
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
        btnCapNhat.addActionListener(e -> suaHangMuc());
        btnHoanThanh.addActionListener(e -> hoanThanh());
        btnXuatKho.addActionListener(e -> {
            Window cuaSo = SwingUtilities.getWindowAncestor(this);
            if (cuaSo instanceof GaraManHinhChinh) {
                ((GaraManHinhChinh) cuaSo).chuyenManHinh("XUAT_KHO");
            } else {
                JOptionPane.showMessageDialog(this, "Mở từ màn hình chính để chuyển sang xuất kho.");
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
        int xong = duLieu == null ? 0 : TienDoSuaChuaService.soHoanThanh(duLieu.hangMuc);
        tienDo.setValue(tong == 0 ? 0 : xong * 100 / tong);
        tienDo.setString(xong + "/" + tong + " hạng mục hoàn thành");
        thongBao.setText(p == null ? "Chưa chọn phiếu."
                : tong == 0 ? "Phiếu chưa có hạng mục dịch vụ. Cần bổ sung tại chức năng lập phiếu sửa chữa."
                : TienDoSuaChuaService.daDong(p.getTrangThai()) ? "Phiếu đã kết thúc; chỉ xem thông tin."
                : xong == tong ? "Đã xong các hạng mục. Kiểm tra xe trước khi bấm Hoàn thành kiểm tra."
                : "Chọn một hạng mục để cập nhật trạng thái, kết quả và ghi chú.");
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
            String loc = (String) cboLocTrangThai.getSelectedItem();
            for (HangMucDichVu h : chiTiet.hangMuc) {
                if (!"Tất cả".equals(loc) && !h.getTrangThai().equals(loc)) continue;
                hangMucHienThi.add(h);
                model.addRow(new Object[]{h.getMaHangMuc(), h.getTenHangMuc(), h.getTrangThai(),
                    tien(h.getChiPhiDuKien()), tien(h.getChiPhiThucTe()), h.getKetQua(), h.getGhiChu()});
            }
        }
        capNhatNut();
    }

    private HangMucDichVu hangMucChon() {
        int row = bang.getSelectedRow();
        return row < 0 ? null : hangMucHienThi.get(bang.convertRowIndexToModel(row));
    }

    private void suaHangMuc() {
        HangMucDichVu h = hangMucChon();
        if (dangTai || h == null || chiTiet == null) return;
        String maPhieu = chiTiet.phieu.getMaPhieuSuaChua();
        JComboBox<String> trangThai = new JComboBox<>(new String[]{
            "Chưa bắt đầu", "Đang thực hiện", "Hoàn thành"
        });
        trangThai.setSelectedItem(h.getTrangThai());
        JTextArea ketQua = new JTextArea(h.getKetQua(), 5, 40);
        JTextArea ghiChu = new JTextArea(h.getGhiChu(), 3, 40);
        ketQua.setLineWrap(true);
        ketQua.setWrapStyleWord(true);
        ghiChu.setLineWrap(true);
        ghiChu.setWrapStyleWord(true);
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(new JLabel(h.getMaHangMuc() + " - " + h.getTenHangMuc()));
        form.add(Box.createVerticalStrut(10));
        form.add(new JLabel("Trạng thái"));
        form.add(trangThai);
        form.add(new JLabel("Kết quả (tối đa 2000 ký tự; bắt buộc khi hoàn thành)"));
        form.add(new JScrollPane(ketQua));
        form.add(new JLabel("Ghi chú (tối đa 1000 ký tự)"));
        form.add(new JScrollPane(ghiChu));
        while (JOptionPane.showConfirmDialog(this, form, "Cập nhật hạng mục",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            String tt = (String) trangThai.getSelectedItem();
            if (ketQua.getText().trim().length() > 2000 || ghiChu.getText().trim().length() > 1000
                    || ("Hoàn thành".equals(tt) && ketQua.getText().trim().isEmpty())) {
                JOptionPane.showMessageDialog(this, "Kiểm tra độ dài và nhập kết quả khi hoàn thành.");
                continue;
            }
            String kq = ketQua.getText();
            String gc = ghiChu.getText();
            chayNen(() -> {
                service.capNhatHangMuc(maPhieu, h.getMaHangMuc(), tt, kq, gc);
                return true;
            }, ok -> sauKhiLuu(maPhieu, "Đã lưu tiến độ hạng mục vào SQL."));
            break;
        }
    }

    private void hoanThanh() {
        if (dangTai || chiTiet == null) return;
        String ma = chiTiet.phieu.getMaPhieuSuaChua();
        if (JOptionPane.showConfirmDialog(this,
                "Xác nhận đã kiểm tra xe đạt yêu cầu?\nPhiếu " + ma + " sẽ chuyển sang Đã hoàn thành.",
                "Hoàn thành kiểm tra", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        chayNen(() -> { service.hoanThanhKiemTra(ma); return true; },
                ok -> sauKhiLuu(ma, "Phiếu sửa chữa đã hoàn thành. Tiếp tục lập hóa đơn và giao xe."));
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
        btnCapNhat.setEnabled(!dangTai && duocSua && bang.getSelectedRow() >= 0);
        btnXuatKho.setEnabled(!dangTai && duocSua);
        btnHoanThanh.setEnabled(!dangTai && duocSua && !chiTiet.hangMuc.isEmpty()
                && TienDoSuaChuaService.soHoanThanh(chiTiet.hangMuc) == chiTiet.hangMuc.size());
    }

    private static String tien(BigDecimal so) {
        return so == null ? "" : NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(so);
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
