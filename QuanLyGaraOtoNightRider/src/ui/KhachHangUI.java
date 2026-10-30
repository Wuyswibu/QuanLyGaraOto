package ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import entity.KhachHang;
import service.KhachHangServices;
import service.KhachHangServices.DuLieuKhongHopLeException;

/** Giao diện khách hàng: hiển thị, sự kiện và gọi service ở luồng nền. */
public final class KhachHangUI extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color RED = new Color(190, 22, 58);
    private static final Color BACKGROUND = new Color(240, 243, 246);
    private static final Color BORDER = new Color(220, 224, 230);
    private final JTextField txtMaKhachHang = new JTextField();
    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtSoDienThoai = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtDiaChi = new JTextField();
    private final JTextField txtTimKiem = new JTextField();
    private final JLabel lblLoiHoTen = new JLabel(" ");
    private final JLabel lblLoiDienThoai = new JLabel(" ");
    private final JLabel lblLoiEmail = new JLabel(" ");
    private final JLabel lblLoiDiaChi = new JLabel(" ");
    private final JLabel lblSoLuong = new JLabel("Chưa tải dữ liệu");
    private final JComboBox<String> cboTrangThai = new JComboBox<>(
        new String[]{"Đang hoạt động", "Ngừng hoạt động"}
    );
    private final JComboBox<String> cboLocTrangThai = new JComboBox<>(
        new String[]{"Tất cả", "Đang hoạt động", "Ngừng hoạt động"}
    );
    private final JButton btnTimKiem = new JButton("Tìm kiếm");
    private final JButton btnTrangChu = new JButton("Về màn hình chính");
    private final JButton btnNhapMoi = new JButton("Nhập khách mới");
    private final JButton btnThem = new JButton("Thêm khách hàng");
    private final JButton btnSua = new JButton("Sửa thông tin");
    private final JButton btnNgungSuDung = new JButton("Ngừng sử dụng");
    private final JButton btnXemXe = new JButton("Xem xe của khách");
    private final KhachHangServices khachHangServices = new KhachHangServices();
    private ArrayList<KhachHang> dsKhachHang = new ArrayList<>();
    private boolean dangXuLy;
    private final DefaultTableModel mdlKhachHang = new DefaultTableModel(
        new String[]{
            "Mã KH", "Khách hàng", "Điện thoại",
            "Email", "Địa chỉ", "Trạng thái"
        }, 0
    ) {
        private static final long serialVersionUID = 1L;
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    };
    private final JTable tblKhachHang = new JTable(mdlKhachHang);
    public KhachHangUI() {
        khoiTaoGiaoDien();
        dangKySuKien();
        loadKhachHangFromSQL();
    }

    // ==================== GIAO DIỆN ====================
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JPanel pnlNoiDung = new JPanel(new BorderLayout(0, 10));
        pnlNoiDung.setBackground(Color.WHITE);
        pnlNoiDung.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        pnlNoiDung.add(taoFormKhachHang(), BorderLayout.NORTH);
        pnlNoiDung.add(taoBangKhachHang(), BorderLayout.CENTER);
        pnlNoiDung.add(taoThanhNut(), BorderLayout.SOUTH);
        add(pnlNoiDung, BorderLayout.CENTER);
    }

    private JPanel taoFormKhachHang() {
        JPanel pnlKhuVuc = new JPanel(new BorderLayout(0, 12));
        pnlKhuVuc.setOpaque(false);
        JLabel lblTieuDe = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDe.setForeground(new Color(30, 35, 45));
        pnlKhuVuc.add(lblTieuDe, BorderLayout.NORTH);
        JPanel pnlForm = new JPanel(new GridLayout(2, 3, 12, 8));
        pnlForm.setOpaque(false);
        txtMaKhachHang.setEditable(false);
        // Hàng 1.
        pnlForm.add(taoOThongTin(
            "Mã khách hàng", txtMaKhachHang, new JLabel(" ")
        ));
        pnlForm.add(taoOThongTin("Họ tên *", txtHoTen, lblLoiHoTen));
        pnlForm.add(taoOThongTin("Điện thoại *", txtSoDienThoai, lblLoiDienThoai));
        // Hàng 2.
        pnlForm.add(taoOThongTin("Email", txtEmail, lblLoiEmail));
        pnlForm.add(taoOThongTin("Địa chỉ", txtDiaChi, lblLoiDiaChi));
        pnlForm.add(taoOThongTin(
            "Trạng thái", cboTrangThai, new JLabel(" ")
        ));
        pnlKhuVuc.add(pnlForm, BorderLayout.CENTER);
        return pnlKhuVuc;
    }

    private JPanel taoOThongTin(
            String tieuDe, JComponent cmpNhapLieu, JLabel lblLoi) {
        JPanel pnl = new JPanel(new BorderLayout(0, 3));
        pnl.setOpaque(false);
        JLabel lblTenTruong = new JLabel(tieuDe);
        lblTenTruong.setFont(new Font("Arial", Font.PLAIN, 13));
        cmpNhapLieu.setFont(new Font("Arial", Font.PLAIN, 13));
        cmpNhapLieu.setPreferredSize(new Dimension(200, 30));
        lblLoi.setForeground(Color.RED);
        lblLoi.setFont(new Font("Arial", Font.PLAIN, 11));
        lblLoi.setPreferredSize(new Dimension(0, 18));
        pnl.add(lblTenTruong, BorderLayout.NORTH);
        pnl.add(cmpNhapLieu, BorderLayout.CENTER);
        pnl.add(lblLoi, BorderLayout.SOUTH);
        return pnl;
    }

    private JPanel taoBangKhachHang() {
        JPanel pnlKhuVuc = new JPanel(new BorderLayout(0, 6));
        pnlKhuVuc.setOpaque(false);
        JPanel pnlThanhCongCu = new JPanel(new BorderLayout(15, 0));
        pnlThanhCongCu.setBackground(new Color(237, 240, 244));
        pnlThanhCongCu.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        JPanel pnlTimKiem = new JPanel(new BorderLayout(8, 0));
        pnlTimKiem.setOpaque(false);
        pnlTimKiem.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);
        txtTimKiem.setPreferredSize(new Dimension(300, 28));
        txtTimKiem.setToolTipText(
            "Nhập đầy đủ mã khách hàng hoặc số điện thoại; để trống để xem tất cả"
        );
        btnTimKiem.setContentAreaFilled(false);
        btnTimKiem.setOpaque(true);
        btnTimKiem.setBackground(RED);
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFocusPainted(false);
        btnTimKiem.setBorderPainted(false);
        pnlTimKiem.add(txtTimKiem, BorderLayout.CENTER);
        pnlTimKiem.add(btnTimKiem, BorderLayout.EAST);
        JPanel pnlBoLoc = new JPanel(new BorderLayout(8, 0));
        pnlBoLoc.setOpaque(false);
        pnlBoLoc.add(new JLabel("Trạng thái:"), BorderLayout.WEST);
        pnlBoLoc.add(cboLocTrangThai, BorderLayout.CENTER);
        pnlThanhCongCu.add(pnlTimKiem, BorderLayout.CENTER);
        pnlThanhCongCu.add(pnlBoLoc, BorderLayout.EAST);
        pnlKhuVuc.add(pnlThanhCongCu, BorderLayout.NORTH);
        tblKhachHang.setFont(new Font("Arial", Font.PLAIN, 13));
        tblKhachHang.setRowHeight(28);
        tblKhachHang.setPreferredScrollableViewportSize(
            new Dimension(1100, 8 * 28)
        );
        tblKhachHang.setFillsViewportHeight(true);
        tblKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKhachHang.setAutoCreateRowSorter(true);
        tblKhachHang.setSelectionBackground(new Color(49, 106, 140));
        tblKhachHang.setSelectionForeground(Color.WHITE);
        tblKhachHang.setShowVerticalLines(false);
        tblKhachHang.setGridColor(BORDER);
        tblKhachHang.getTableHeader().setReorderingAllowed(false);
        tblKhachHang.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );
        tblKhachHang.getTableHeader().setPreferredSize(
            new Dimension(0, 28)
        );
        JScrollPane scrKhachHang = new JScrollPane(tblKhachHang);
        scrKhachHang.getViewport().setBackground(Color.WHITE);
        scrKhachHang.setBorder(BorderFactory.createLineBorder(BORDER));
        pnlKhuVuc.add(scrKhachHang, BorderLayout.CENTER);
        lblSoLuong.setForeground(Color.GRAY);
        pnlKhuVuc.add(lblSoLuong, BorderLayout.SOUTH);
        return pnlKhuVuc;
    }

    private JPanel taoThanhNut() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnl.setOpaque(false);
        btnTrangChu.setContentAreaFilled(false);
        btnTrangChu.setOpaque(true);
        btnTrangChu.setBackground(RED);
        btnTrangChu.setForeground(Color.WHITE);
        btnTrangChu.setFocusPainted(false);
        btnTrangChu.setBorderPainted(false);
        btnXemXe.setContentAreaFilled(false);
        btnXemXe.setOpaque(true);
        btnXemXe.setBackground(Color.BLUE);
        btnXemXe.setForeground(Color.WHITE);
        btnXemXe.setFocusPainted(false);
        btnXemXe.setBorderPainted(false);
        btnNgungSuDung.setContentAreaFilled(false);
        btnNgungSuDung.setOpaque(true);
        btnNgungSuDung.setBackground(RED);
        btnNgungSuDung.setForeground(Color.WHITE);
        btnNgungSuDung.setFocusPainted(false);
        btnNgungSuDung.setBorderPainted(false);
        btnTrangChu.setPreferredSize(new Dimension(165, 30));
        btnNhapMoi.setPreferredSize(new Dimension(150, 30));
        btnThem.setPreferredSize(new Dimension(150, 30));
        btnSua.setPreferredSize(new Dimension(140, 30));
        btnNgungSuDung.setPreferredSize(new Dimension(150, 30));
        btnXemXe.setPreferredSize(new Dimension(165, 30));
        pnl.add(btnTrangChu);
        pnl.add(btnNhapMoi);
        pnl.add(btnThem);
        pnl.add(btnSua);
        pnl.add(btnNgungSuDung);
        pnl.add(btnXemXe);
        return pnl;
    }

    // ==================== SỰ KIỆN ====================
    private void dangKySuKien() {
        btnTimKiem.addActionListener(event -> timKiemKhachHang());
        txtTimKiem.addActionListener(event -> timKiemKhachHang());
        cboLocTrangThai.addActionListener(event -> timKiemKhachHang());
        btnNhapMoi.addActionListener(event -> {
            if (!dangXuLy) {
                xoaForm();
                txtHoTen.requestFocusInWindow();
            }
        });
        btnThem.addActionListener(event -> themKhachHang());
        btnSua.addActionListener(event -> suaKhachHang());
        btnNgungSuDung.addActionListener(event -> ngungSuDungKhachHang());
        tblKhachHang.getSelectionModel().addListSelectionListener(event -> {
            if (dangXuLy || event.getValueIsAdjusting()) {
                return;
            }
            int dongHienThi = tblKhachHang.getSelectedRow();
            if (dongHienThi >= 0 && dongHienThi < tblKhachHang.getRowCount()) {
                int dongDuLieu = tblKhachHang.convertRowIndexToModel(dongHienThi);
                hienThiKhachHang(dongDuLieu);
            }
        });
        btnTrangChu.addActionListener(event -> {
            GaraManHinhChinh main = layManHinhChinh();
            if (main != null) {
                main.hienThiTrangChu();
            }
        });
        btnXemXe.addActionListener(event -> {
            if (txtMaKhachHang.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng trước khi xem xe.");
                return;
            }
            GaraManHinhChinh main = layManHinhChinh();
            if (main != null) {
                main.moXeCuaKhachHang(
                    txtMaKhachHang.getText().trim()
                );
            }
        });
    }

    // ==================== HIỂN THỊ LỖI TỪ SERVICE ====================

    private void xoaThongBaoLoi() {
        lblLoiHoTen.setText(" ");
        lblLoiDienThoai.setText(" ");
        lblLoiEmail.setText(" ");
        lblLoiDiaChi.setText(" ");
    }

    private boolean kiemTraForm() {
        Map<String, String> loi = khachHangServices.kiemTraDuLieu(
            txtHoTen.getText(), txtSoDienThoai.getText(),
            txtEmail.getText(), txtDiaChi.getText()
        );
        hienThiLoiNhap(loi);
        return loi.isEmpty();
    }

    private void hienThiLoiNhap(Map<String, String> loi) {
        lblLoiHoTen.setText(thongBaoTruong(loi, KhachHangServices.HO_TEN));
        lblLoiDienThoai.setText(thongBaoTruong(loi, KhachHangServices.SO_DIEN_THOAI));
        lblLoiEmail.setText(thongBaoTruong(loi, KhachHangServices.EMAIL));
        lblLoiDiaChi.setText(thongBaoTruong(loi, KhachHangServices.DIA_CHI));

        JComponent cmpLoi = null;
        if (loi.containsKey(KhachHangServices.HO_TEN)) {
            cmpLoi = txtHoTen;
        } else if (loi.containsKey(KhachHangServices.SO_DIEN_THOAI)) {
            cmpLoi = txtSoDienThoai;
        } else if (loi.containsKey(KhachHangServices.EMAIL)) {
            cmpLoi = txtEmail;
        } else if (loi.containsKey(KhachHangServices.DIA_CHI)) {
            cmpLoi = txtDiaChi;
        }
        if (cmpLoi != null) {
            final JComponent cmpCanChon = cmpLoi;
            SwingUtilities.invokeLater(() -> cmpCanChon.requestFocusInWindow());
        }
        if (loi.containsKey(KhachHangServices.MA_KHACH_HANG)) {
            JOptionPane.showMessageDialog(this, loi.get(KhachHangServices.MA_KHACH_HANG));
        }
    }

    private String thongBaoTruong(Map<String, String> loi, String truong) {
        return loi.containsKey(truong) ? "(*) " + loi.get(truong) : " ";
    }

    // ==================== THAO TÁC GIAO DIỆN ====================

    private void themKhachHang() {
        if (dangXuLy) {
            return;
        }
        if (!txtMaKhachHang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Bấm Nhập khách mới, nhập thông tin rồi bấm Thêm."
            );
            return;
        }
        luuKhachHang(true);
    }

    private void suaKhachHang() {
        if (dangXuLy) {
            return;
        }
        if (txtMaKhachHang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn khách hàng trong bảng trước khi sửa.");
            return;
        }
        luuKhachHang(false);
    }

    private void luuKhachHang(boolean themMoi) {
        if (!kiemTraForm()) {
            return;
        }

        // Đọc dữ liệu Swing trên EDT, trước khi chạy service ở luồng nền.
        final String ma = txtMaKhachHang.getText().trim();
        final String hoTen = txtHoTen.getText();
        final String soDienThoai = txtSoDienThoai.getText();
        final String email = txtEmail.getText();
        final String diaChi = txtDiaChi.getText();
        final boolean trangThai = cboTrangThai.getSelectedIndex() == 0;

        if (!themMoi && JOptionPane.showConfirmDialog(
                this, "Lưu thay đổi cho khách hàng " + ma + "?",
                "Xác nhận sửa", JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION) {
            return;
        }

        thucHienGhiDuLieu(() -> {
            if (themMoi) {
                return khachHangServices.themKhachHang(hoTen, soDienThoai, email, diaChi);
            }
            return khachHangServices.suaKhachHang(
                ma, hoTen, soDienThoai, email, diaChi, trangThai
            );
        }, themMoi ? "Thêm khách hàng thành công!" : "Sửa khách hàng thành công!");
    }

    private void ngungSuDungKhachHang() {
        if (dangXuLy) {
            return;
        }
        final String ma = txtMaKhachHang.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn khách hàng trước khi ngừng sử dụng.");
            return;
        }
        int chon = JOptionPane.showConfirmDialog(
            this, "Ngừng sử dụng hồ sơ khách hàng " + ma + "?"
                + "\nThông tin xe và lịch sử vẫn được giữ lại.",
            "Xác nhận ngừng sử dụng", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (chon == JOptionPane.YES_OPTION) {
            xoaThongBaoLoi();
            thucHienGhiDuLieu(
                () -> khachHangServices.ngungSuDungKhachHang(ma),
                "Đã chuyển hồ sơ sang trạng thái ngừng hoạt động."
            );
        }
    }

    private void thucHienGhiDuLieu(
            Callable<Boolean> thaoTac, String thongBaoThanhCong) {
        if (dangXuLy) {
            return;
        }
        setDangXuLy(true);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return thaoTac.call();
            }
            @Override
            protected void done() {
                boolean thanhCong = false;
                try {
                    thanhCong = get();
                    if (thanhCong) {
                        JOptionPane.showMessageDialog(
                            KhachHangUI.this, thongBaoThanhCong
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                            KhachHangUI.this,
                            "Không có dữ liệu được thay đổi."
                                + "\nHãy tải lại danh sách và kiểm tra."
                        );
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    Throwable loi = ex.getCause();
                    if (loi instanceof DuLieuKhongHopLeException) {
                        hienThiLoiNhap(((DuLieuKhongHopLeException) loi).getLoiTheoTruong());
                    } else {
                        baoLoiSQL("Không lưu được dữ liệu", loi);
                    }
                } finally {
                    setDangXuLy(false);
                }
                if (thanhCong) {
                    taiKhachHang(txtTimKiem.getText().trim(), false);
                }
            }
        }.execute();
    }

    // ==================== TẢI VÀ TÌM KIẾM ====================

    public void loadKhachHangFromSQL() {
        if (dangXuLy) {
            return;
        }
        txtTimKiem.setText("");
        taiKhachHang("", false);
    }

    private void timKiemKhachHang() {
        if (!dangXuLy) {
            taiKhachHang(txtTimKiem.getText().trim(), true);
        }
    }

    private void taiKhachHang(
            String tuKhoa, boolean thongBaoKhongTimThay) {
        if (dangXuLy) {
            return;
        }
        final String noiDung = tuKhoa.trim();
        final int trangThaiLoc = cboLocTrangThai.getSelectedIndex();
        final Boolean trangThai = trangThaiLoc == 0 ? null : trangThaiLoc == 1;
        setDangXuLy(true);
        lblSoLuong.setText("Đang tải khách hàng...");
        new SwingWorker<ArrayList<KhachHang>, Void>() {
            @Override
            protected ArrayList<KhachHang> doInBackground() throws Exception {
                return khachHangServices.timKiem(noiDung, trangThai);
            }

            @Override
            protected void done() {
                boolean thanhCong = false;
                try {
                    ArrayList<KhachHang> ketQua = get();
                    // Cập nhật bảng trên EDT, bỏ chọn trước khi xóa dòng.
                    tblKhachHang.clearSelection();
                    mdlKhachHang.setRowCount(0);
                    dsKhachHang = ketQua;
                    xoaForm();
                    for (KhachHang kh : dsKhachHang) {
                        mdlKhachHang.addRow(new Object[]{
                            kh.getMaKhachHang(),
                            kh.getHoTen(),
                            kh.getSoDienThoai(),
                            kh.getEmail(),
                            kh.getDiaChi(),
                            kh.isTrangThai()
                                ? "Đang hoạt động"
                                : "Ngừng hoạt động"
                        });
                    }
                    lblSoLuong.setText(
                        "Kết quả: " + dsKhachHang.size()
                            + " khách hàng"
                    );
                    thanhCong = true;
                    if (thongBaoKhongTimThay
                            && !noiDung.isEmpty()
                            && ketQua.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            KhachHangUI.this,
                            "Không tìm thấy khách hàng phù hợp."
                                + "\nKiểm tra mã, số điện thoại và bộ lọc trạng thái."
                        );
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    lblSoLuong.setText("Đã ngắt tải dữ liệu");
                } catch (ExecutionException ex) {
                    lblSoLuong.setText("Không tải được dữ liệu");
                    baoLoiSQL("Không tải được khách hàng", ex.getCause());
                } finally {
                    setDangXuLy(false);
                }
                if (thanhCong && tblKhachHang.getRowCount() > 0) {
                    tblKhachHang.setRowSelectionInterval(0, 0);
                }
            }
        }.execute();
    }

    // ==================== HIỂN THỊ VÀ TRẠNG THÁI UI ====================
    private void setDangXuLy(boolean dangXuLy) {
        this.dangXuLy = dangXuLy;
        btnTimKiem.setEnabled(!dangXuLy);
        txtTimKiem.setEnabled(!dangXuLy);
        cboLocTrangThai.setEnabled(!dangXuLy);
        btnNhapMoi.setEnabled(!dangXuLy);
        btnThem.setEnabled(!dangXuLy);
        btnSua.setEnabled(!dangXuLy);
        btnNgungSuDung.setEnabled(!dangXuLy);
        btnTrangChu.setEnabled(!dangXuLy);
        btnXemXe.setEnabled(!dangXuLy);
        txtHoTen.setEnabled(!dangXuLy);
        txtSoDienThoai.setEnabled(!dangXuLy);
        txtEmail.setEnabled(!dangXuLy);
        txtDiaChi.setEnabled(!dangXuLy);
        cboTrangThai.setEnabled(!dangXuLy);
        tblKhachHang.setEnabled(!dangXuLy);
        tblKhachHang.getTableHeader().setEnabled(!dangXuLy);
    }

    private void hienThiKhachHang(int dongDuLieu) {
        if (dongDuLieu < 0 || dongDuLieu >= dsKhachHang.size()) {
            return;
        }
        xoaThongBaoLoi();
        KhachHang kh = dsKhachHang.get(dongDuLieu);
        txtMaKhachHang.setText(kh.getMaKhachHang());
        txtHoTen.setText(kh.getHoTen());
        txtSoDienThoai.setText(kh.getSoDienThoai());
        txtEmail.setText(kh.getEmail() == null ? "" : kh.getEmail());
        txtDiaChi.setText(kh.getDiaChi() == null ? "" : kh.getDiaChi());
        cboTrangThai.setSelectedIndex(kh.isTrangThai() ? 0 : 1);
    }

    private void xoaForm() {
        xoaThongBaoLoi();
        tblKhachHang.clearSelection();
        txtMaKhachHang.setText("");
        txtHoTen.setText("");
        txtSoDienThoai.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        cboTrangThai.setSelectedIndex(0);
    }

    private void baoLoiSQL(String noiDung, Throwable loi) {
        loi.printStackTrace();
        JOptionPane.showMessageDialog(
            this,
            noiDung + ":\n" + loi.getMessage(),
            "Lỗi",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private GaraManHinhChinh layManHinhChinh() {
        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        if (cuaSo instanceof GaraManHinhChinh) {
            return (GaraManHinhChinh) cuaSo;
        }
        JOptionPane.showMessageDialog(
            this,
            "Hãy mở giao diện từ GaraManHinhChinh để điều hướng."
        );
        return null;
    }
    // Chạy riêng để kiểm tra giao diện.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frmKiemTra = new JFrame("Quản lý khách hàng");
            frmKiemTra.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frmKiemTra.setContentPane(new KhachHangUI());
            frmKiemTra.setSize(1340, 840);
            frmKiemTra.setLocationRelativeTo(null);
            frmKiemTra.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frmKiemTra.setVisible(true);
        });
    }
}
