package ui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import entity.PhieuSuaChua;
public class LapPhieuSuaChuaFrame extends JPanel
        implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);
    private static final DateTimeFormatter DINH_DANG =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");
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
    private final JButton btnThemHangMuc =
            taoNut("Thêm hạng mục", false);
    private final JButton btnSuaHangMuc =
            taoNut("Sửa hạng mục", false);
    private final JButton btnBoHangMuc =
            taoNut("Bỏ hạng mục", false);
    private final JButton btnLuu =
            taoNut("Lưu phiếu sửa chữa", true);
    private final JButton btnLamMoi =
            taoNut("Làm mới", false);
    private final JButton btnHuy =
            taoNut("Hủy", false);
    private final JLabel lblTong =
            new JLabel("Tổng chi phí dự kiến: 0 đ");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                "Mã hạng mục", "Dịch vụ", "Tên hạng mục",
                "Mô tả", "Chi phí dự kiến", "Trạng thái"
            }, 0) {
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
    private final ArrayList<TiepNhanMau> danhSachTiepNhan =
            new ArrayList<>();
    private final ArrayList<PhieuSuaChua> danhSachPhieu =
            new ArrayList<>();
    private final ArrayList<HangMucMau> hangMucDangNhap =
            new ArrayList<>();
    private final ArrayList<ArrayList<HangMucMau>> hangMucDaLuu =
            new ArrayList<>();
    private final String[] dichVuMau = {
        "DV001 • Thay dầu động cơ",
        "DV002 • Thay má phanh trước",
        "DV003 • Kiểm tra điều hòa",
        "DV004 • Cân bằng động bánh xe",
        "DV005 • Vệ sinh lọc gió"
    };
    private int soPhieu;
    private int soHangMuc;
    private boolean dangLamMoi;
    public LapPhieuSuaChuaFrame() {
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
        JPanel noiDung = new JPanel(new BorderLayout(0, 16));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(12, 18, 16, 18));
        // ==================== THÔNG TIN PHIẾU ====================
        JPanel phanTren = new JPanel(new BorderLayout(0, 12));
        phanTren.setOpaque(false);
        JLabel moTa = new JLabel(
                "Lập phiếu từ thông tin tiếp nhận; "
                        + "có thể bổ sung chẩn đoán và hạng mục sau.");
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
        phanTren.add(form, BorderLayout.CENTER);
        JPanel ghiNhan = new JPanel(new GridLayout(1, 3, 16, 0));
        ghiNhan.setOpaque(false);
        themVanBan(ghiNhan, "Yêu cầu khách hàng", txtYeuCau);
        themVanBan(ghiNhan, "Chẩn đoán", txtChanDoan);
        themVanBan(ghiNhan, "Ghi chú", txtGhiChu);
        phanTren.add(ghiNhan, BorderLayout.SOUTH);
        noiDung.add(phanTren, BorderLayout.NORTH);
        // ==================== HẠNG MỤC ====================
        JPanel vungBang = new JPanel(new BorderLayout(0, 10));
        vungBang.setOpaque(false);
        JPanel thanhHangMuc = new JPanel(new BorderLayout());
        thanhHangMuc.setBackground(new Color(237, 241, 245));
        thanhHangMuc.setBorder(new EmptyBorder(6, 10, 6, 10));
        JLabel lblHangMuc = new JLabel("HẠNG MỤC DỊCH VỤ DỰ KIẾN");
        lblHangMuc.setFont(new Font("Arial", Font.BOLD, 12));
        lblHangMuc.setForeground(CHU);
        JPanel nutHangMuc = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 6, 0));
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
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);
        int[] rong = {120, 210, 210, 260, 150, 140};
        for (int i = 0; i < rong.length; i++) {
            table.getColumnModel().getColumn(i)
                    .setPreferredWidth(rong[i]);
        }
        JScrollPane cuonBang = new JScrollPane(table);
        cuonBang.setBorder(BorderFactory.createLineBorder(VIEN));
        cuonBang.getViewport().setBackground(Color.WHITE);
        vungBang.add(cuonBang, BorderLayout.CENTER);
        lblTong.setFont(new Font("Arial", Font.BOLD, 13));
        lblTong.setForeground(CHU);
        lblTong.setHorizontalAlignment(SwingConstants.RIGHT);
        vungBang.add(lblTong, BorderLayout.SOUTH);
        noiDung.add(vungBang, BorderLayout.CENTER);
        JPanel thaoTac = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        thaoTac.add(btnHuy);
        thaoTac.add(btnLamMoi);
        thaoTac.add(btnLuu);
        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);
        add(khung, BorderLayout.CENTER);
        cboTiepNhan.addActionListener(this);
        btnThemHangMuc.addActionListener(this);
        btnSuaHangMuc.addActionListener(this);
        btnBoHangMuc.addActionListener(this);
        btnLuu.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnHuy.addActionListener(this);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean daChon = table.getSelectedRow() >= 0;
                btnSuaHangMuc.setEnabled(daChon);
                btnBoHangMuc.setEnabled(daChon);
            }
        });
        
        cboTiepNhan.addItem("-- Chọn phiếu tiếp nhận --");
        for (TiepNhanMau tn : danhSachTiepNhan) {
            cboTiepNhan.addItem(tn.ma + " • " + tn.bienSo);
        }
        btnLamMoi.doClick();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();
        if (nguon == cboTiepNhan) {
            if (dangLamMoi) {
                return;
            }
            int viTri = cboTiepNhan.getSelectedIndex() - 1;
            if (!hangMucDangNhap.isEmpty()
                    || !txtChanDoan.getText().trim().isEmpty()
                    || !txtGhiChu.getText().trim().isEmpty()) {
                int chon = JOptionPane.showConfirmDialog(
                        this,
                        "Đổi phiếu tiếp nhận sẽ xóa nội dung "
                                + "chẩn đoán, ghi chú và hạng mục đang nhập."
                                + "\nTiếp tục?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION);
                if (chon != JOptionPane.YES_OPTION) {
                    dangLamMoi = true;
                    cboTiepNhan.setSelectedIndex(
                            timViTriTiepNhanDangHienThi());
                    dangLamMoi = false;
                    return;
                }
            }
            hangMucDangNhap.clear();
            table.clearSelection();
            model.setRowCount(0);
            txtChanDoan.setText("");
            txtGhiChu.setText("");
            lblTong.setText("Tổng chi phí dự kiến: 0 đ");
            if (viTri >= 0) {
                TiepNhanMau tn = danhSachTiepNhan.get(viTri);
                txtBienSo.setText(tn.bienSo);
                txtMauXe.setText(tn.mauXe);
                txtKhachHang.setText(tn.khachHang);
                txtYeuCau.setText(tn.yeuCau);
            } else {
                txtBienSo.setText("");
                txtMauXe.setText("");
                txtKhachHang.setText("");
                txtYeuCau.setText("");
            }
        } else if (nguon == btnThemHangMuc
                || nguon == btnSuaHangMuc) {
            if (cboTiepNhan.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(
                        this, "Chọn phiếu tiếp nhận trước.");
                return;
            }
            boolean sua = nguon == btnSuaHangMuc;
            int dongModel = -1;
            if (sua) {
                int dong = table.getSelectedRow();
                if (dong < 0) {
                    JOptionPane.showMessageDialog(
                            this, "Chọn hạng mục cần sửa.");
                    return;
                }
                dongModel = table.convertRowIndexToModel(dong);
            }
            JComboBox<String> cboDichVu =
                    new JComboBox<>(dichVuMau);
            JTextField txtTen = new JTextField();
            JTextArea txtMoTa = new JTextArea(3, 20);
            JTextField txtChiPhi = new JTextField();
            txtMoTa.setLineWrap(true);
            txtMoTa.setWrapStyleWord(true);
            if (sua) {
                HangMucMau hm = hangMucDangNhap.get(dongModel);
                cboDichVu.setSelectedItem(hm.dichVu);
                txtTen.setText(hm.ten);
                txtMoTa.setText(hm.moTa);
                txtChiPhi.setText(hm.chiPhi.toPlainString());
            } else {
                txtTen.setText(dichVuMau[0].split(" • ", 2)[1]);
            }
            cboDichVu.addActionListener(ev -> {
                String chon =
                        (String) cboDichVu.getSelectedItem();
                if (chon != null) {
                    txtTen.setText(chon.split(" • ", 2)[1]);
                }
            });
            JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
            form.add(new JLabel("Dịch vụ"));
            form.add(cboDichVu);
            form.add(new JLabel("Tên hạng mục"));
            form.add(txtTen);
            form.add(new JLabel("Mô tả"));
            form.add(new JScrollPane(txtMoTa));
            form.add(new JLabel(
                    "Chi phí dự kiến (ví dụ: 150000 hoặc 150000.50)"));
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
                if (!validData(
                        txtTen.getText().trim(),
                        txtMoTa.getText().trim(),
                        txtChiPhi.getText().trim())) {
                    continue;
                }
                String ma = sua
                        ? hangMucDangNhap.get(dongModel).ma
                        : String.format(
                                Locale.ROOT,
                                "HMDV%03d", ++soHangMuc);
                HangMucMau hm = new HangMucMau(
                        ma,
                        (String) cboDichVu.getSelectedItem(),
                        txtTen.getText().trim(),
                        txtMoTa.getText().trim(),
                        new BigDecimal(txtChiPhi.getText().trim()),
                        "Chờ thực hiện");
                if (sua) {
                    hangMucDangNhap.set(dongModel, hm);
                } else {
                    hangMucDangNhap.add(hm);
                }
                table.clearSelection();
                model.setRowCount(0);
                BigDecimal tong = BigDecimal.ZERO;
                for (HangMucMau muc : hangMucDangNhap) {
                    model.addRow(new Object[]{
                        muc.ma, muc.dichVu, muc.ten,
                        muc.moTa, muc.chiPhi, muc.trangThai
                    });
                    tong = tong.add(muc.chiPhi);
                }
                lblTong.setText(
                        "Tổng chi phí dự kiến: "
                                + String.format(
                                        Locale.forLanguageTag("vi-VN"),
                                        "%,.2f đ", tong));
                break;
            }
        } else if (nguon == btnBoHangMuc) {
            int dong = table.getSelectedRow();
            if (dong < 0) {
                JOptionPane.showMessageDialog(
                        this, "Chọn hạng mục cần bỏ.");
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
            table.clearSelection();
            hangMucDangNhap.remove(dongModel);
            model.removeRow(dongModel);
            BigDecimal tong = BigDecimal.ZERO;
            for (HangMucMau hm : hangMucDangNhap) {
                tong = tong.add(hm.chiPhi);
            }
            lblTong.setText(
                    "Tổng chi phí dự kiến: "
                            + String.format(
                                    Locale.forLanguageTag("vi-VN"),
                                    "%,.2f đ", tong));
        } else if (nguon == btnLuu) {
            if (!validData()) {
                return;
            }
            TiepNhanMau tn = danhSachTiepNhan.get(
                    cboTiepNhan.getSelectedIndex() - 1);
            for (PhieuSuaChua p : danhSachPhieu) {
                if (p.getMaTiepNhan().equals(tn.ma)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Phiếu tiếp nhận này đã có phiếu sửa chữa: "
                                    + p.getMaPhieuSuaChua());
                    return;
                }
            }
            try {
                String ma = String.format(
                        Locale.ROOT, "PSC%03d", soPhieu + 1);
                LocalDateTime ngayLap = LocalDateTime.now();
                PhieuSuaChua phieu = new PhieuSuaChua(
                        ma,
                        tn.ma,
                        ngayLap,
                        txtChanDoan.getText().trim(),
                        (String) cboLoaiPhieu.getSelectedItem(),
                        "Chờ sửa chữa",
                        txtGhiChu.getText().trim());
                danhSachPhieu.add(phieu);
                hangMucDaLuu.add(
                        new ArrayList<>(hangMucDangNhap));
                soPhieu++;
                txtMaPhieu.setText(ma);
                txtNgayLap.setText(ngayLap.format(DINH_DANG));
                JOptionPane.showMessageDialog(
                        this,
                        "Đã tạo phiếu mẫu: " + ma
                                + "\nPhiếu tiếp nhận: " + tn.ma
                                + "\nSố hạng mục: "
                                + hangMucDangNhap.size()
                                + "\nDữ liệu chỉ lưu trong lần chạy,"
                                + " chưa ghi CSDL.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this, ex.getMessage());
            }
        } else if (nguon == btnLamMoi || nguon == btnHuy) {
            if (nguon == btnHuy) {
                int chon = JOptionPane.showConfirmDialog(
                        this,
                        "Xóa nội dung đang nhập?",
                        "Hủy lập phiếu",
                        JOptionPane.YES_NO_OPTION);
                if (chon != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            dangLamMoi = true;
            cboTiepNhan.setSelectedIndex(0);
            cboLoaiPhieu.setSelectedIndex(0);
            txtMaPhieu.setText("");
            txtBienSo.setText("");
            txtMauXe.setText("");
            txtKhachHang.setText("");
            txtNgayLap.setText(
                    LocalDateTime.now().format(DINH_DANG));
            txtTrangThai.setText("Chờ sửa chữa");
            txtYeuCau.setText("");
            txtChanDoan.setText("");
            txtGhiChu.setText("");
            table.clearSelection();
            hangMucDangNhap.clear();
            model.setRowCount(0);
            btnSuaHangMuc.setEnabled(false);
            btnBoHangMuc.setEnabled(false);
            lblTong.setText("Tổng chi phí dự kiến: 0 đ");
            dangLamMoi = false;
        }
    }
    private boolean validData() {
        if (cboTiepNhan.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(
                    this, "Vui lòng chọn phiếu tiếp nhận.");
            cboTiepNhan.requestFocusInWindow();
            return false;
        }
        if (cboLoaiPhieu.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(
                    this, "Vui lòng chọn loại phiếu.");
            return false;
        }
        if (txtChanDoan.getText().trim().length() > 2000) {
            JOptionPane.showMessageDialog(
                    this, "Chẩn đoán tối đa 2000 ký tự.");
            txtChanDoan.requestFocusInWindow();
            return false;
        }
        if (txtGhiChu.getText().trim().length() > 1000) {
            JOptionPane.showMessageDialog(
                    this, "Ghi chú tối đa 1000 ký tự.");
            txtGhiChu.requestFocusInWindow();
            return false;
        }
        return true;
    }
    private boolean validData(
            String ten, String moTa, String chiPhi) {
        if (ten.isEmpty() || ten.length() > 150) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tên hạng mục không được rỗng,"
                            + " tối đa 150 ký tự.");
            return false;
        }
        if (moTa.length() > 2000) {
            JOptionPane.showMessageDialog(
                    this, "Mô tả tối đa 2000 ký tự.");
            return false;
        }
        if (!chiPhi.matches("[0-9]+(\\.[0-9]{1,2})?")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Chi phí phải là số không âm,"
                            + " tối đa 2 chữ số thập phân."
                            + "\nKhông nhập dấu phân cách hàng nghìn.");
            return false;
        }
        BigDecimal gia = new BigDecimal(chiPhi);
        if (gia.compareTo(
                new BigDecimal("9999999999999999.99")) > 0) {
            JOptionPane.showMessageDialog(
                    this, "Chi phí vượt giới hạn DECIMAL(18,2).");
            return false;
        }
        return true;
    }
    private int timViTriTiepNhanDangHienThi() {
        for (int i = 0; i < danhSachTiepNhan.size(); i++) {
            if (danhSachTiepNhan.get(i).bienSo.equals(
                    txtBienSo.getText())) {
                return i + 1;
            }
        }
        return 0;
    }
    private void themO(
            JPanel form, String ten, JComponent field) {
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
    private void themVanBan(
            JPanel form, String ten, JTextArea area) {
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
        nut.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(chinh ? DO : VIEN),
                new EmptyBorder(8, 14, 8, 14)));
        return nut;
    }
    // ==================== DỮ LIỆU MẪU ====================
    private static class TiepNhanMau {
        String ma, bienSo, mauXe, khachHang, yeuCau;
        TiepNhanMau(String ma, String bienSo, String mauXe,
                String khachHang, String yeuCau) {
            this.ma = ma;
            this.bienSo = bienSo;
            this.mauXe = mauXe;
            this.khachHang = khachHang;
            this.yeuCau = yeuCau;
        }
    }
    private static class HangMucMau {
        String ma, dichVu, ten, moTa, trangThai;
        BigDecimal chiPhi;
        HangMucMau(String ma, String dichVu, String ten,
                String moTa, BigDecimal chiPhi,
                String trangThai) {
            this.ma = ma;
            this.dichVu = dichVu;
            this.ten = ten;
            this.moTa = moTa;
            this.chiPhi = chiPhi;
            this.trangThai = trangThai;
        }
    }
    // Cửa sổ để chạy thử riêng; khi gắn vào màn hình chính,
    // thêm trực tiếp panel, không gọi taoCuaSo().
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
}
