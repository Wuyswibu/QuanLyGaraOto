package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class TiepNhanXeFrame extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final Color CHU = new Color(35, 41, 51);
    private static final Color CHU_PHU = new Color(112, 124, 139);
    private static final Color VIEN = new Color(216, 222, 230);
    private static final Color DO = new Color(188, 25, 58);

    private static final DateTimeFormatter DINH_DANG = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    // Thông tin khách hàng
    private final JTextField txtMaKH = new JTextField();
    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtSDT = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtDiaChi = new JTextField();

    private final JButton btnTimKH = taoNut("Tìm khách", false);

    // Thông tin xe
    private final JTextField txtMaXe = new JTextField();
    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtMauXe = new JTextField();

    private final JButton btnTimXe = taoNut("Tìm xe", false);

    // Thông tin tiếp nhận
    private final JTextField txtMaTiepNhan = new JTextField();
    private final JTextField txtNhanVien = new JTextField("NV002 • Nguyễn Thị Lan");
    private final JTextField txtNgayNhan = new JTextField();
    private final JTextField txtHenTra = new JTextField();
    private final JTextField txtSoKm = new JTextField();
    private final JTextField txtTrangThai = new JTextField();

    private final JComboBox<String> cboNhienLieu = new JComboBox<>(new String[]{"-- Chọn --", "Cạn", "1/4", "1/2", "3/4", "Đầy"});

    private final JTextArea txtTinhTrang = new JTextArea(3, 20);
    private final JTextArea txtYeuCau = new JTextArea(3, 20);
    private final JTextArea txtGhiChu = new JTextArea(3, 20);

    // Tra cứu danh sách phiếu
    private final JTextField txtTimKiem = new JTextField();
    private final JComboBox<String> cboLocTrangThai = new JComboBox<>( new String[]{"Tất cả", "Đã tiếp nhận", "Đang sửa chữa", "Đã giao xe"});

    private final JLabel lblSoLuong = new JLabel();

    private final JButton btnTim = taoNut("Tìm kiếm", false);
    private final JButton btnMoi = taoNut("Làm mới", false);
    private final JButton btnLuu = taoNut("Lưu tiếp nhận", true);

    private final DefaultTableModel model = new DefaultTableModel(new String[]{
                "Mã tiếp nhận", "Biển số", "Chủ xe",
                "Số điện thoại", "Ngày tiếp nhận",
                "Hẹn trả", "Trạng thái"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> boLoc = new TableRowSorter<>(model);

    private final ArrayList<KhachMau> danhSachKH = new ArrayList<>();
    private final ArrayList<XeMau> danhSachXe = new ArrayList<>();
    private final ArrayList<PhieuMau> danhSachPhieu = new ArrayList<>();

    private boolean dangXemPhieu;
    private boolean dangLamMoi;
    private int soKH = 3;
    private int soXe = 3;
    private int soPhieu = 3;

    public TiepNhanXeFrame() {
        setLayout(new BorderLayout());
        setBackground(new Color(216, 220, 228));
        setBorder(new EmptyBorder(18, 45, 15, 45));

        txtMaKH.setEditable(false);
        txtMaXe.setEditable(false);
        txtMaTiepNhan.setEditable(false);
        txtNhanVien.setEditable(false);
        txtTrangThai.setEditable(false);

        JPanel khung = new JPanel(new BorderLayout());
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createLineBorder(VIEN));

        JLabel tieuDe = new JLabel("LẬP PHIẾU TIẾP NHẬN XE");
        tieuDe.setOpaque(true);
        tieuDe.setBackground(new Color(246, 247, 250));
        tieuDe.setForeground(CHU);
        tieuDe.setFont(new Font("Arial", Font.BOLD, 16));
        tieuDe.setBorder(new EmptyBorder(10, 16, 10, 16));
        khung.add(tieuDe, BorderLayout.NORTH);

        JPanel noiDung = new JPanel(new BorderLayout(0, 12));
        noiDung.setBackground(Color.WHITE);
        noiDung.setBorder(new EmptyBorder(10, 16, 12, 16));

        JPanel cacForm = new JPanel();
        cacForm.setLayout(new BoxLayout(cacForm, BoxLayout.Y_AXIS));
        cacForm.setBackground(Color.WHITE);

        JLabel moTa = new JLabel(
                "Tìm hồ sơ đã có hoặc nhập khách hàng và xe mới ngay tại đây.");
        moTa.setForeground(CHU_PHU);
        moTa.setFont(new Font("Arial", Font.PLAIN, 13));
        moTa.setAlignmentX(Component.LEFT_ALIGNMENT);
        cacForm.add(moTa);
        cacForm.add(Box.createVerticalStrut(10));

        // ==================== KHÁCH HÀNG ====================

        JPanel formKH = taoNhom("1. Thông tin khách hàng", 2, 3);

        themO(formKH, "Số điện thoại", ghepNut(txtSDT, btnTimKH));
        themO(formKH, "Mã khách hàng", txtMaKH);
        themO(formKH, "Họ tên", txtHoTen);
        themO(formKH, "Email", txtEmail);
        themO(formKH, "Địa chỉ", txtDiaChi);

        JLabel huongDanKH = new JLabel(
                "Khách mới: nhập thông tin, mã tự sinh khi lưu.");
        huongDanKH.setForeground(CHU_PHU);
        huongDanKH.setFont(new Font("Arial", Font.ITALIC, 12));
        formKH.add(huongDanKH);

        cacForm.add(formKH);
        cacForm.add(Box.createVerticalStrut(8));

        // ==================== XE ====================

        JPanel formXe = taoNhom("2. Thông tin xe", 1, 3);

        themO(formXe, "Biển số", ghepNut(txtBienSo, btnTimXe));
        themO(formXe, "Mã phương tiện", txtMaXe);
        themO(formXe, "Mẫu xe", txtMauXe);

        cacForm.add(formXe);
        cacForm.add(Box.createVerticalStrut(8));

        // ==================== TIẾP NHẬN ====================

        JPanel formTN = taoNhom("3. Thông tin tiếp nhận", 2, 4);

        themO(formTN, "Mã tiếp nhận", txtMaTiepNhan);
        themO(formTN, "Nhân viên tiếp nhận", txtNhanVien);
        themO(formTN, "Ngày nhận (dd/MM/yyyy HH:mm)", txtNgayNhan);
        themO(formTN, "Hẹn trả (dd/MM/yyyy HH:mm)", txtHenTra);
        themO(formTN, "Số km đã đi", txtSoKm);
        themO(formTN, "Mức nhiên liệu", cboNhienLieu);
        themO(formTN, "Trạng thái phiếu", txtTrangThai);

        JPanel oTrong = new JPanel();
        oTrong.setOpaque(false);
        formTN.add(oTrong);

        cacForm.add(formTN);
        cacForm.add(Box.createVerticalStrut(8));

        JPanel ghiNhan = new JPanel(new GridLayout(1, 3, 16, 0));
        ghiNhan.setOpaque(false);
        ghiNhan.setAlignmentX(Component.LEFT_ALIGNMENT);

        themVanBan(ghiNhan, "Tình trạng ban đầu", txtTinhTrang);
        themVanBan(ghiNhan, "Yêu cầu khách hàng", txtYeuCau);
        themVanBan(ghiNhan, "Ghi chú", txtGhiChu);

        cacForm.add(ghiNhan);

        JScrollPane cuonForm = new JScrollPane(cacForm);
        cuonForm.setBorder(BorderFactory.createEmptyBorder());
        cuonForm.getVerticalScrollBar().setUnitIncrement(16);
        cuonForm.setPreferredSize(new Dimension(1100, 405));

        noiDung.add(cuonForm, BorderLayout.NORTH);

        // ==================== DANH SÁCH ====================

        JPanel danhSach = new JPanel(new BorderLayout(0, 8));
        danhSach.setOpaque(false);

        JPanel thanhTim = new JPanel(new BorderLayout(12, 0));
        thanhTim.setBackground(new Color(237, 241, 245));
        thanhTim.setBorder(new EmptyBorder(6, 10, 6, 10));

        JPanel tim = new JPanel(new BorderLayout(8, 0));
        tim.setOpaque(false);
        tim.add(new JLabel("Tìm phiếu, biển số, khách hàng:"),
                BorderLayout.WEST);
        tim.add(txtTimKiem, BorderLayout.CENTER);
        tim.add(btnTim, BorderLayout.EAST);

        JPanel loc = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 5, 0));
        loc.setOpaque(false);
        loc.add(new JLabel("Trạng thái:"));
        loc.add(cboLocTrangThai);

        thanhTim.add(tim, BorderLayout.CENTER);
        thanhTim.add(loc, BorderLayout.EAST);

        danhSach.add(thanhTim, BorderLayout.NORTH);

        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setRowSorter(boLoc);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(53, 105, 139));
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(
                new Color(237, 241, 245));
        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(
                Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable bang, Object value, boolean selected,
                    boolean focus, int row, int column) {
                super.getTableCellRendererComponent(
                        bang, value, selected, focus, row, column);
                setBorder(new EmptyBorder(0, 8, 0, 8));

                if (!selected) {
                    setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(247, 248, 250));
                    setForeground(CHU);
                }

                return this;
            }
        });

        JScrollPane cuonBang = new JScrollPane(table);
        cuonBang.setBorder(BorderFactory.createLineBorder(VIEN));
        cuonBang.getViewport().setBackground(Color.WHITE);
        danhSach.add(cuonBang, BorderLayout.CENTER);

        lblSoLuong.setForeground(CHU_PHU);
        danhSach.add(lblSoLuong, BorderLayout.SOUTH);

        noiDung.add(danhSach, BorderLayout.CENTER);

        JPanel thaoTac = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thaoTac.setOpaque(false);
        thaoTac.add(btnMoi);
        thaoTac.add(btnLuu);

        noiDung.add(thaoTac, BorderLayout.SOUTH);
        khung.add(noiDung, BorderLayout.CENTER);
        add(khung, BorderLayout.CENTER);

        btnTimKH.addActionListener(this);
        txtSDT.addActionListener(this);
        btnTimXe.addActionListener(this);
        txtBienSo.addActionListener(this);
        btnMoi.addActionListener(this);
        btnLuu.addActionListener(this);
        btnTim.addActionListener(this);
        txtTimKiem.addActionListener(this);
        cboLocTrangThai.addActionListener(this);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || dangLamMoi) {
                return;
            }

            int dong = table.getSelectedRow();
            if (dong < 0) {
                return;
            }

            PhieuMau p = danhSachPhieu.get(
                    table.convertRowIndexToModel(dong));

            dangXemPhieu = true;

            txtMaKH.setText(p.khach.ma);
            txtHoTen.setText(p.khach.hoTen);
            txtSDT.setText(p.khach.sdt);
            txtEmail.setText(p.khach.email);
            txtDiaChi.setText(p.khach.diaChi);

            txtMaXe.setText(p.xe.ma);
            txtBienSo.setText(p.xe.bienSo);
            txtMauXe.setText(p.xe.mauXe);

            txtMaTiepNhan.setText(p.ma);
            txtNgayNhan.setText(p.ngayNhan);
            txtHenTra.setText(p.henTra);
            txtSoKm.setText(p.soKm);
            cboNhienLieu.setSelectedItem(p.nhienLieu);
            txtTinhTrang.setText(p.tinhTrang);
            txtYeuCau.setText(p.yeuCau);
            txtGhiChu.setText(p.ghiChu);
            txtTrangThai.setText(p.trangThai);

            btnLuu.setEnabled(false);
            btnTimKH.setEnabled(false);
            btnTimXe.setEnabled(false);
        });

        taoDuLieuMau();
        btnMoi.doClick();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object nguon = e.getSource();

        if (nguon == btnMoi) {
            dangLamMoi = true;

            table.clearSelection();
            boLoc.setRowFilter(null);
            txtTimKiem.setText("");
            cboLocTrangThai.setSelectedIndex(0);

            txtMaKH.setText("");
            txtHoTen.setText("");
            txtSDT.setText("");
            txtEmail.setText("");
            txtDiaChi.setText("");

            txtMaXe.setText("");
            txtBienSo.setText("");
            txtMauXe.setText("");

            txtMaTiepNhan.setText("");
            txtNgayNhan.setText(
                    LocalDateTime.now().format(DINH_DANG));
            txtHenTra.setText("");
            txtSoKm.setText("");
            cboNhienLieu.setSelectedIndex(0);
            txtTinhTrang.setText("");
            txtYeuCau.setText("");
            txtGhiChu.setText("");
            txtTrangThai.setText("Đã tiếp nhận");

            dangXemPhieu = false;
            btnLuu.setEnabled(true);
            btnTimKH.setEnabled(true);
            btnTimXe.setEnabled(true);

            lblSoLuong.setText(
                    "Hiển thị: " + table.getRowCount()
                            + " / " + model.getRowCount() + " phiếu");

            dangLamMoi = false;
            txtSDT.requestFocusInWindow();

        } else if (nguon == btnTimKH || nguon == txtSDT) {
            if (dangXemPhieu) {
                return;
            }

            String sdt = txtSDT.getText().trim();

            if (sdt.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Nhập số điện thoại để tìm khách hàng.");
                return;
            }

            ArrayList<KhachMau> ketQua = new ArrayList<>();

            for (KhachMau kh : danhSachKH) {
                if (kh.sdt.equals(sdt)) {
                    ketQua.add(kh);
                }
            }

            if (ketQua.isEmpty()) {
                txtMaKH.setText("");
                txtHoTen.setText("");
                txtEmail.setText("");
                txtDiaChi.setText("");
                txtMaXe.setText("");

                JOptionPane.showMessageDialog(
                        this,
                        "Chưa có khách hàng này."
                                + "\nNhập họ tên và thông tin khách mới.");
                txtHoTen.requestFocusInWindow();
                return;
            }

            KhachMau kh = ketQua.get(0);

            if (ketQua.size() > 1) {
                String[] luaChon = new String[ketQua.size()];

                for (int i = 0; i < ketQua.size(); i++) {
                    luaChon[i] = ketQua.get(i).ma
                            + " • " + ketQua.get(i).hoTen;
                }

                String chon = (String) JOptionPane.showInputDialog(
                        this, "Chọn khách hàng:", "Kết quả tìm",
                        JOptionPane.QUESTION_MESSAGE,
                        null, luaChon, luaChon[0]);

                if (chon == null) {
                    return;
                }

                for (int i = 0; i < luaChon.length; i++) {
                    if (luaChon[i].equals(chon)) {
                        kh = ketQua.get(i);
                        break;
                    }
                }
            }

            txtMaKH.setText(kh.ma);
            txtHoTen.setText(kh.hoTen);
            txtSDT.setText(kh.sdt);
            txtEmail.setText(kh.email);
            txtDiaChi.setText(kh.diaChi);
            txtMaXe.setText("");

        } else if (nguon == btnTimXe || nguon == txtBienSo) {
            if (dangXemPhieu) {
                return;
            }

            String bienSo = chuanHoaBienSo(txtBienSo.getText());

            if (bienSo.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Nhập biển số để tìm xe.");
                return;
            }

            XeMau xeTim = null;

            for (XeMau xe : danhSachXe) {
                if (chuanHoaBienSo(xe.bienSo).equals(bienSo)) {
                    xeTim = xe;
                    break;
                }
            }

            if (xeTim == null) {
                txtMaXe.setText("");
                txtMauXe.setText("");

                JOptionPane.showMessageDialog(
                        this,
                        "Chưa có xe này. Nhập mẫu xe để tiếp nhận xe mới.");
                txtMauXe.requestFocusInWindow();
                return;
            }

            if (!txtMaKH.getText().isEmpty()
                    && !txtMaKH.getText().equals(xeTim.maKH)) {
                int chon = JOptionPane.showConfirmDialog(
                        this,
                        "Xe thuộc khách hàng khác."
                                + "\nBạn có muốn hiển thị chủ xe đã lưu?",
                        "Đối chiếu chủ xe",
                        JOptionPane.YES_NO_OPTION);

                if (chon != JOptionPane.YES_OPTION) {
                    txtMaXe.setText("");
                    return;
                }
            }

            txtMaXe.setText(xeTim.ma);
            txtBienSo.setText(xeTim.bienSo);
            txtMauXe.setText(xeTim.mauXe);

            for (KhachMau kh : danhSachKH) {
                if (kh.ma.equals(xeTim.maKH)) {
                    txtMaKH.setText(kh.ma);
                    txtHoTen.setText(kh.hoTen);
                    txtSDT.setText(kh.sdt);
                    txtEmail.setText(kh.email);
                    txtDiaChi.setText(kh.diaChi);
                    break;
                }
            }

        } else if (nguon == btnTim
                || nguon == txtTimKiem
                || nguon == cboLocTrangThai) {
            if (dangLamMoi) {
                return;
            }

            String tuKhoa = chuanHoa(txtTimKiem.getText().trim());
            String trangThai =
                    (String) cboLocTrangThai.getSelectedItem();

            boLoc.setRowFilter(
                    new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(
                        Entry<? extends DefaultTableModel,
                                ? extends Integer> entry) {
                    boolean dungTrangThai =
                            "Tất cả".equals(trangThai)
                            || entry.getStringValue(6)
                                    .equals(trangThai);

                    String noiDung = "";
                    for (int i = 0; i < 4; i++) {
                        noiDung += " " + entry.getStringValue(i);
                    }

                    return dungTrangThai
                            && chuanHoa(noiDung).contains(tuKhoa);
                }
            });

            lblSoLuong.setText(
                    "Hiển thị: " + table.getRowCount()
                            + " / " + model.getRowCount() + " phiếu");

        } else if (nguon == btnLuu) {
            if (dangXemPhieu || !validData()) {
                return;
            }

            KhachMau khach = null;
            XeMau xe = null;

            String maKhach = txtMaKH.getText().trim();
            String bienSo = chuanHoaBienSo(txtBienSo.getText());

            for (KhachMau kh : danhSachKH) {
                if (kh.ma.equals(maKhach)) {
                    khach = kh;
                    break;
                }
            }

            for (XeMau x : danhSachXe) {
                if (chuanHoaBienSo(x.bienSo).equals(bienSo)) {
                    xe = x;
                    break;
                }
            }

            if (khach != null
                    && !khach.sdt.equals(txtSDT.getText().trim())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Số điện thoại đã thay đổi."
                                + "\nHãy bấm Tìm khách để xác định lại hồ sơ.");
                return;
            }

            if (khach == null) {
                for (KhachMau kh : danhSachKH) {
                    if (kh.sdt.equals(txtSDT.getText().trim())) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Số điện thoại đã có trong dữ liệu mẫu."
                                        + "\nBấm Tìm khách để chọn hồ sơ.");
                        return;
                    }
                }
            }

            if (xe != null
                    && (khach == null || !xe.maKH.equals(khach.ma))) {
                JOptionPane.showMessageDialog(
                        this,
                        "Xe đã có và không thuộc khách hàng đang nhập."
                                + "\nBấm Tìm xe để đối chiếu chủ xe.");
                return;
            }

            if (xe != null) {
                for (PhieuMau p : danhSachPhieu) {
                    if (p.xe.ma.equals(xe.ma)
                            && !"Đã giao xe".equals(p.trangThai)) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Xe đang có phiếu chưa hoàn tất: " + p.ma);
                        return;
                    }
                }
            }

            if (khach == null) {
                khach = new KhachMau(
                        String.format("KH%03d", ++soKH),
                        txtHoTen.getText().trim(),
                        txtSDT.getText().trim(),
                        txtEmail.getText().trim(),
                        txtDiaChi.getText().trim());

                danhSachKH.add(khach);
            }

            if (xe == null) {
                xe = new XeMau(
                        String.format("PT%03d", ++soXe),
                        khach.ma,
                        txtBienSo.getText().trim()
                                .toUpperCase(Locale.ROOT),
                        txtMauXe.getText().trim());

                danhSachXe.add(xe);
            }

            PhieuMau phieu = new PhieuMau(
                    String.format("PTN%03d", ++soPhieu),
                    khach,
                    xe,
                    txtNgayNhan.getText().trim(),
                    txtHenTra.getText().trim(),
                    txtSoKm.getText().trim(),
                    (String) cboNhienLieu.getSelectedItem(),
                    txtTinhTrang.getText().trim(),
                    txtYeuCau.getText().trim(),
                    txtGhiChu.getText().trim(),
                    "Đã tiếp nhận");

            danhSachPhieu.add(phieu);

            model.addRow(new Object[]{
                phieu.ma,
                xe.bienSo,
                khach.hoTen,
                khach.sdt,
                phieu.ngayNhan,
                phieu.henTra,
                phieu.trangThai
            });

            JOptionPane.showMessageDialog(
                    this,
                    "Đã thêm phiếu " + phieu.ma
                            + " vào dữ liệu ảo."
                            + "\nChưa lưu vào CSDL.");

            btnMoi.doClick();
        }
    }

    private boolean validData() {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        if (hoTen.isEmpty() || hoTen.length() > 120) {
            JOptionPane.showMessageDialog(
                    this, "Họ tên không được rỗng, tối đa 120 ký tự.");
            txtHoTen.requestFocusInWindow();
            return false;
        }

        if (!sdt.matches("[0-9]{10,15}")) {
            JOptionPane.showMessageDialog(
                    this, "Số điện thoại phải gồm 10 đến 15 chữ số.");
            txtSDT.requestFocusInWindow();
            return false;
        }

        if (!email.isEmpty()
                && (email.length() > 254
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))) {
            JOptionPane.showMessageDialog(
                    this, "Email không hợp lệ.");
            txtEmail.requestFocusInWindow();
            return false;
        }

        if (txtDiaChi.getText().trim().length() > 255) {
            JOptionPane.showMessageDialog(
                    this, "Địa chỉ tối đa 255 ký tự.");
            txtDiaChi.requestFocusInWindow();
            return false;
        }

        String bienSo = txtBienSo.getText().trim();

        if (chuanHoaBienSo(bienSo).isEmpty()
                || bienSo.length() > 20) {
            JOptionPane.showMessageDialog(
                    this, "Biển số không được rỗng, tối đa 20 ký tự.");
            txtBienSo.requestFocusInWindow();
            return false;
        }

        String mau = txtMauXe.getText().trim();

        if (mau.isEmpty() || mau.length() > 100) {
            JOptionPane.showMessageDialog(
                    this, "Mẫu xe không được rỗng, tối đa 100 ký tự.");
            txtMauXe.requestFocusInWindow();
            return false;
        }

        LocalDateTime ngayNhan;

        try {
            ngayNhan = LocalDateTime.parse(
                    txtNgayNhan.getText().trim(), DINH_DANG);

            if (ngayNhan.isAfter(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(
                        this, "Ngày tiếp nhận không được ở tương lai.");
                txtNgayNhan.requestFocusInWindow();
                return false;
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ngày nhận phải đúng dạng dd/MM/yyyy HH:mm."
                            + "\nVí dụ: 18/10/2026 08:30");
            txtNgayNhan.requestFocusInWindow();
            return false;
        }

        if (!txtHenTra.getText().trim().isEmpty()) {
            try {
                LocalDateTime henTra = LocalDateTime.parse(
                        txtHenTra.getText().trim(), DINH_DANG);

                if (henTra.isBefore(ngayNhan)) {
                    JOptionPane.showMessageDialog(
                            this, "Hẹn trả không được trước ngày nhận.");
                    txtHenTra.requestFocusInWindow();
                    return false;
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Hẹn trả phải đúng dạng dd/MM/yyyy HH:mm.");
                txtHenTra.requestFocusInWindow();
                return false;
            }
        }

        try {
            int km = Integer.parseInt(txtSoKm.getText().trim());

            if (km < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Số km phải là số nguyên không âm.");
            txtSoKm.requestFocusInWindow();
            return false;
        }

        if (cboNhienLieu.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(
                    this, "Vui lòng chọn mức nhiên liệu.");
            return false;
        }

        String tinhTrang = txtTinhTrang.getText().trim();
        if (tinhTrang.isEmpty() || tinhTrang.length() > 2000) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tình trạng ban đầu không được rỗng,"
                            + " tối đa 2000 ký tự.");
            txtTinhTrang.requestFocusInWindow();
            return false;
        }

        String yeuCau = txtYeuCau.getText().trim();
        if (yeuCau.isEmpty() || yeuCau.length() > 2000) {
            JOptionPane.showMessageDialog(
                    this,
                    "Yêu cầu khách hàng không được rỗng,"
                            + " tối đa 2000 ký tự.");
            txtYeuCau.requestFocusInWindow();
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

    private void taoDuLieuMau() {
        KhachMau kh1 = new KhachMau(
                "KH001", "Nguyễn Minh An", "0901234567",
                "an@gmail.com", "Gò Vấp, TP.HCM");

        KhachMau kh2 = new KhachMau(
                "KH002", "Trần Thị Bình", "0912345678",
                "binh@gmail.com", "Bình Thạnh, TP.HCM");

        KhachMau kh3 = new KhachMau(
                "KH003", "Lê Quốc Cường", "0923456789",
                "", "Thủ Đức, TP.HCM");

        danhSachKH.add(kh1);
        danhSachKH.add(kh2);
        danhSachKH.add(kh3);

        XeMau xe1 = new XeMau(
                "PT001", "KH001", "51H-123.45", "Toyota Vios");
        XeMau xe2 = new XeMau(
                "PT002", "KH002", "51K-678.90", "Honda City");
        XeMau xe3 = new XeMau(
                "PT003", "KH001", "50H-456.78", "Mazda 3");

        danhSachXe.add(xe1);
        danhSachXe.add(xe2);
        danhSachXe.add(xe3);

        danhSachPhieu.add(new PhieuMau(
                "PTN001", kh1, xe1,
                "15/10/2026 08:15", "15/10/2026 11:30",
                "40250", "1/2",
                "Xước nhẹ cản sau, phanh có tiếng kêu.",
                "Bảo dưỡng và kiểm tra phanh.",
                "Khách đã nhận xe.", "Đã giao xe"));

        danhSachPhieu.add(new PhieuMau(
                "PTN002", kh2, xe2,
                "17/10/2026 13:40", "19/10/2026 10:00",
                "28500", "3/4",
                "Điều hòa làm lạnh yếu.",
                "Kiểm tra điều hòa.",
                "Liên hệ trước khi thay phụ tùng.", "Đang sửa chữa"));

        danhSachPhieu.add(new PhieuMau(
                "PTN003", kh1, xe3,
                "18/10/2026 15:20", "19/10/2026 09:30",
                "53000", "1/4",
                "Đèn cảnh báo động cơ sáng.",
                "Kiểm tra lỗi động cơ.",
                "", "Đã tiếp nhận"));

        for (PhieuMau p : danhSachPhieu) {
            model.addRow(new Object[]{
                p.ma, p.xe.bienSo, p.khach.hoTen,
                p.khach.sdt, p.ngayNhan, p.henTra, p.trangThai
            });
        }
    }

    private JPanel taoNhom(String ten, int dong, int cot) {
        JPanel panel = new JPanel(
                new GridLayout(dong, cot, 16, 8));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(VIEN),
                ten,
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                CHU));
        return panel;
    }

    private void themO(
            JPanel panel, String ten, JComponent field) {
        JPanel o = new JPanel(new BorderLayout(0, 4));
        o.setOpaque(false);

        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("Arial", Font.BOLD, 12));
        nhan.setForeground(new Color(87, 100, 117));

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(CHU);
        field.setPreferredSize(new Dimension(150, 30));

        o.add(nhan, BorderLayout.NORTH);
        o.add(field, BorderLayout.CENTER);
        panel.add(o);
    }

    private JPanel ghepNut(JTextField field, JButton nut) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        panel.add(field, BorderLayout.CENTER);
        panel.add(nut, BorderLayout.EAST);
        return panel;
    }

    private void themVanBan(
            JPanel panel, String ten, JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 13));
        area.setBorder(new EmptyBorder(5, 7, 5, 7));

        JScrollPane cuon = new JScrollPane(area);
        cuon.setBorder(BorderFactory.createLineBorder(VIEN));
        themO(panel, ten, cuon);
        cuon.setPreferredSize(new Dimension(200, 65));
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
                new EmptyBorder(7, 12, 7, 12)));
        return nut;
    }

    private static String chuanHoa(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT);
    }

    private static String chuanHoaBienSo(String s) {
        return s.replaceAll("[\\s.\\-]", "")
                .toUpperCase(Locale.ROOT);
    }

    // Các lớp dữ liệu tạm dùng riêng cho giao diện mẫu.
    private static class KhachMau {
        String ma, hoTen, sdt, email, diaChi;

        KhachMau(String ma, String hoTen, String sdt,
                String email, String diaChi) {
            this.ma = ma;
            this.hoTen = hoTen;
            this.sdt = sdt;
            this.email = email;
            this.diaChi = diaChi;
        }
    }

    private static class XeMau {
        String ma, maKH, bienSo, mauXe;

        XeMau(String ma, String maKH,
                String bienSo, String mauXe) {
            this.ma = ma;
            this.maKH = maKH;
            this.bienSo = bienSo;
            this.mauXe = mauXe;
        }
    }

    private static class PhieuMau {
        String ma, ngayNhan, henTra, soKm, nhienLieu;
        String tinhTrang, yeuCau, ghiChu, trangThai;
        KhachMau khach;
        XeMau xe;

        PhieuMau(String ma, KhachMau khach, XeMau xe,
                String ngayNhan, String henTra,
                String soKm, String nhienLieu,
                String tinhTrang, String yeuCau,
                String ghiChu, String trangThai) {
            this.ma = ma;
            this.khach = khach;
            this.xe = xe;
            this.ngayNhan = ngayNhan;
            this.henTra = henTra;
            this.soKm = soKm;
            this.nhienLieu = nhienLieu;
            this.tinhTrang = tinhTrang;
            this.yeuCau = yeuCau;
            this.ghiChu = ghiChu;
            this.trangThai = trangThai;
        }
    }

    // ==================== MENU, HEADER, FOOTER ====================

    public JFrame taoCuaSo() {
        JFrame cuaSo = new JFrame(
                "Tiếp nhận xe - Night Invader");
        cuaSo.setSize(1340, 960);
        cuaSo.setLocationRelativeTo(null);
        cuaSo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cuaSo.setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Font menuFont = new Font("Arial", Font.BOLD, 16);

        JMenu logo = new JMenu("NIGHT INVADER");
        logo.setFont(new Font("Arial", Font.BOLD, 15));
        logo.setForeground(new Color(210, 30, 30));
        logo.setMargin(new Insets(5, 10, 5, 10));
        menuBar.add(logo);

        String[] tenMenu = {
            "Hệ thống", "Khách hàng ", "Xe ", "Dịch vụ ",
            "Phụ tùng", "Kho hàng ", "Hóa đơn ",
            "Thống kê ", "Nhân viên "
        };

        String[][] cacItem = {
            {
                "Tài khoản cá nhân", "Đổi mật khẩu",
                "-", "Đăng xuất"
            },
            {
                "Quản lý khách hàng"
            },
            {
                "Quản lý xe", "Tra cứu xe", "Lịch sử sửa chữa"
            },
            {
                "Quản lý dịch vụ", "Thêm dịch vụ",
                "Sửa dịch vụ", "Xóa dịch vụ", "-",
                "Lập phiếu tiếp nhận xe",
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

        for (int i = 0; i < tenMenu.length; i++) {
            JMenu menu = taoMenuDropdown(tenMenu[i], cacItem[i]);
            menu.setFont(menuFont);
            menu.setMargin(new Insets(5, 10, 5, 10));
            menuBar.add(menu);
        }

        JMenu taiKhoan = new JMenu("Xin chào, Chủ Gara");
        taiKhoan.setFont(menuFont);
        taiKhoan.setMargin(new Insets(5, 10, 5, 10));

        ImageIcon iconUser =
                loadIcon("/img/icon_nguoi.jpg", 20, 20);
        if (iconUser != null) {
            taiKhoan.setIcon(iconUser);
        }

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(taiKhoan);
        cuaSo.setJMenuBar(menuBar);

        JPanel toolbar = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(220, 224, 230)));

        toolbar.add(taoNutNhanh(
                "Tiếp nhận xe", "F2",
                loadIcon("/img/icon_xe.png", 22, 22)));
        toolbar.add(taoNutNhanh(
                "Tình trạng sửa xe", "F5",
                loadIcon("/img/icon-vector.png", 22, 22)));
        toolbar.add(taoNutNhanh(
                "Lập hóa đơn", "F3",
                loadIcon("/img/icon_hoadon.png", 22, 22)));
        toolbar.add(taoNutNhanh(
                "Nhập kho", "F4",
                loadIcon("/img/icon_nhapkho.jpg", 22, 22)));

        JPanel thongTin = new JPanel(new GridLayout(2, 1));
        thongTin.setBackground(Color.WHITE);

        JLabel ngay = new JLabel(
                "Thứ Ba, 06/10/2026", JLabel.RIGHT);
        ngay.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel chiNhanh = new JLabel(
                "Chi nhánh: Gara chính", JLabel.RIGHT);
        chiNhanh.setFont(new Font("Arial", Font.PLAIN, 11));
        chiNhanh.setForeground(Color.GRAY);

        thongTin.add(ngay);
        thongTin.add(chiNhanh);

        JPanel header = new JPanel(new BorderLayout());
        header.add(toolbar, BorderLayout.CENTER);
        header.add(thongTin, BorderLayout.EAST);
        header.setBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 15));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(245, 247, 250));
        footer.setBorder(
                BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel trangThai = new JLabel(
                "Đang hoạt động | Vai trò: Chủ Gara"
                        + " | Chi nhánh: Gara chính");
        trangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        trangThai.setForeground(Color.DARK_GRAY);

        JLabel banQuyen = new JLabel("NIGHT INVADER • Gara ô tô");
        banQuyen.setFont(new Font("Arial", Font.PLAIN, 12));
        banQuyen.setForeground(Color.GRAY);

        footer.add(trangThai, BorderLayout.WEST);
        footer.add(banQuyen, BorderLayout.EAST);

        cuaSo.add(header, BorderLayout.NORTH);
        cuaSo.add(this, BorderLayout.CENTER);
        cuaSo.add(footer, BorderLayout.SOUTH);

        return cuaSo;
    }

    private ImageIcon loadIcon(
            String path, int width, int height) {
        java.net.URL url = getClass().getResource(path);

        if (url == null) {
            return null;
        }

        ImageIcon icon = new ImageIcon(url);
        Image anh = icon.getImage().getScaledInstance(
                width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(anh);
    }

    private JMenu taoMenuDropdown(
            String tenMenu, String[] cacItem) {
        JMenu menu = new JMenu(tenMenu);

        for (String ten : cacItem) {
            if ("-".equals(ten)) {
                menu.addSeparator();
            } else {
                JMenuItem item = new JMenuItem(ten);
                item.addActionListener(e ->
                        JOptionPane.showMessageDialog(
                                this, "Đang mở chức năng: " + ten));
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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        btn.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Đang mở nhanh chức năng: "
                                + tieuDe + " (" + phimTat + ")"));

        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() ->
                new TiepNhanXeFrame().taoCuaSo().setVisible(true));
    }
}