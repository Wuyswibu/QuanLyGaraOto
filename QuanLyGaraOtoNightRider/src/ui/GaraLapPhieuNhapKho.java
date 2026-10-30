package ui;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import service.*;
import dao.ChiTietPhieuNhapDao;
import dao.NhanVienDao;
import entity.ChiTietPhieuNhap;
import entity.NhanVien;
import entity.PhieuNhapKho;
import entity.PhuTung;

public class GaraLapPhieuNhapKho extends JPanel {
    private static final long serialVersionUID = 1L;

    // Các thành phần Form
    private JTextField txtMaPhieuNhap;
    private JTextField txtNgayNhap;
    private JComboBox<String> cboTrangThai;
    private JComboBox<String> cboPhuTung;
    private JTextField txtSoLuong;
    private JTextField txtDonGia;
    private JComboBox<String> cboNhanVienKho;
    private JTextField txtGhiChu;
    private JTextField txtTongGiaTri;

    // Bảng và tìm kiếm
    private JTextField txtTimKiemBang;
    private JComboBox<String> cboLocTrangThai;
    private JTable tablePhieuNhap;
    private DefaultTableModel tableModel;

    // SỬ DỤNG SERVICE THAY VÌ GỌI DAO TRỰC TIẾP TẠI UI
    private final PhieuNhapKhoService phieuNhapKhoService = new PhieuNhapKhoService();
    private final PhuTungService phuTungService = new PhuTungService();
    private final ChiTietPhieuNhapDao ctPhieuNhapDao = new ChiTietPhieuNhapDao();
    private final NhanVienDao nhanVienDao = new NhanVienDao();

    private final Map<String, PhuTung> mapPhuTung = new HashMap<>();
    private boolean dangCapNhatCombo = false;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo mã phiếu nhập hoặc ghi chú (Nhấn Enter)...";

    public GaraLapPhieuNhapKho() {
        khoiTaoGiaoDien();
        napDuLieuComboBox();
        taiDanhSachPhieuNhapLenBang("Tất cả");
        lamMoiFormLapPhieu();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBackground(new Color(225, 230, 235));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(20, 70, 20, 70));

        JPanel panelCardChinh = new JPanel(new BorderLayout());
        panelCardChinh.setBackground(Color.WHITE);
        panelCardChinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(20, 30, 15, 30)
        ));

        // Tiêu đề
        JPanel panelCardTop = new JPanel(new BorderLayout());
        panelCardTop.setBackground(Color.WHITE);
        JLabel lblTieuDeCard = new JLabel("LẬP PHIẾU NHẬP KHO");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));
        panelCardTop.add(lblTieuDeCard, BorderLayout.WEST);

        // Form
        JLabel lblMoTa = new JLabel("Điền thông tin nhập phụ tùng bên dưới hoặc chọn phiếu từ bảng để xem.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        txtMaPhieuNhap = new JTextField();
        txtMaPhieuNhap.setFont(new Font("Arial", Font.BOLD, 13));
        txtNgayNhap = new JTextField("Tự động ghi nhận khi lưu");
        txtNgayNhap.setEditable(false);
        txtNgayNhap.setBackground(new Color(245, 247, 250));

        cboTrangThai = new JComboBox<>(new String[]{"Đã nhập kho", "Chờ xử lý", "Đã hủy"});
        cboPhuTung = new JComboBox<>();
        txtSoLuong = new JTextField("1");
        txtDonGia = new JTextField("0");
        cboNhanVienKho = new JComboBox<>();
        txtGhiChu = new JTextField();
        txtTongGiaTri = new JTextField("0 đ");
        txtTongGiaTri.setEditable(false);
        txtTongGiaTri.setBackground(new Color(245, 247, 250));
        txtTongGiaTri.setForeground(new Color(180, 30, 40));
        txtTongGiaTri.setFont(new Font("Arial", Font.BOLD, 14));

        cboPhuTung.addActionListener(e -> {
            if (!dangCapNhatCombo && cboPhuTung.getSelectedItem() != null) {
                String maPT = layMaTuCombo(cboPhuTung);
                PhuTung pt = mapPhuTung.get(maPT);
                if (pt != null && pt.getDonGia() != null) {
                    txtDonGia.setText(pt.getDonGia().stripTrailingZeros().toPlainString());
                    tinhTamThanhTienTrenForm();
                }
            }
        });

        KeyAdapter tinhTienListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                tinhTamThanhTienTrenForm();
            }
        };
        txtSoLuong.addKeyListener(tinhTienListener);
        txtDonGia.addKeyListener(tinhTienListener);

        JPanel panelGridFields = new JPanel(new GridLayout(3, 3, 25, 10));
        panelGridFields.setBackground(Color.WHITE);
        panelGridFields.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        panelGridFields.add(taoOnhapLieu("Mã phiếu nhập", txtMaPhieuNhap));
        panelGridFields.add(taoOnhapLieu("Ngày nhập (Tự động)", txtNgayNhap));
        panelGridFields.add(taoOCombo("Trạng thái", cboTrangThai));
        panelGridFields.add(taoOCombo("Phụ tùng nhập", cboPhuTung));
        panelGridFields.add(taoOnhapLieu("Số lượng nhập", txtSoLuong));
        panelGridFields.add(taoOnhapLieu("Đơn giá nhập (VNĐ)", txtDonGia));
        panelGridFields.add(taoOCombo("Nhân viên kho", cboNhanVienKho));
        panelGridFields.add(taoOnhapLieu("Ghi chú / Nhà cung cấp", txtGhiChu));
        panelGridFields.add(taoOnhapLieu("Tổng giá trị phiếu nhập", txtTongGiaTri));

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);
        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // Thanh lọc và tìm kiếm
        JPanel panelSearchFilter = new JPanel(new BorderLayout(15, 0));
        panelSearchFilter.setBackground(new Color(240, 243, 246));
        panelSearchFilter.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        txtTimKiemBang = new JTextField(PLACEHOLDER_TIM_KIEM);
        txtTimKiemBang.setFont(new Font("Arial", Font.PLAIN, 12));
        txtTimKiemBang.setForeground(Color.GRAY);
        txtTimKiemBang.setBorder(null);
        txtTimKiemBang.setOpaque(false);

        txtTimKiemBang.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtTimKiemBang.getText().equals(PLACEHOLDER_TIM_KIEM)) {
                    txtTimKiemBang.setText("");
                    txtTimKiemBang.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtTimKiemBang.getText().trim().isEmpty()) {
                    txtTimKiemBang.setText(PLACEHOLDER_TIM_KIEM);
                    txtTimKiemBang.setForeground(Color.GRAY);
                }
            }
        });

        txtTimKiemBang.addActionListener(e -> {
            String tuKhoa = txtTimKiemBang.getText().trim();
            if (!tuKhoa.isEmpty() && !tuKhoa.equals(PLACEHOLDER_TIM_KIEM)) {
                timKiemPhieuNhapLenBang(tuKhoa);
            } else {
                taiDanhSachPhieuNhapLenBang(cboLocTrangThai.getSelectedItem().toString());
            }
        });

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlFilterRight.setOpaque(false);
        pnlFilterRight.add(new JLabel("Trạng thái:"));
        cboLocTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã nhập kho", "Chờ xử lý", "Đã hủy"});
        cboLocTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        cboLocTrangThai.addActionListener(e -> {
            if (cboLocTrangThai.getSelectedItem() != null) {
                taiDanhSachPhieuNhapLenBang(cboLocTrangThai.getSelectedItem().toString());
            }
        });
        pnlFilterRight.add(cboLocTrangThai);
        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(pnlFilterRight, BorderLayout.EAST);

        // Bảng
        String[] columnNames = {"Mã Phiếu Nhập", "Ngày Nhập", "Phụ Tùng Nhập", "Tổng SL", "Tổng Giá Trị", "Ghi Chú", "Trạng Thái"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablePhieuNhap = new JTable(tableModel);
        tablePhieuNhap.setRowHeight(34);
        tablePhieuNhap.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablePhieuNhap.setSelectionBackground(new Color(40, 100, 150));
        tablePhieuNhap.setSelectionForeground(Color.WHITE);

        tablePhieuNhap.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablePhieuNhap.getSelectedRow();
                if (row >= 0) {
                    hienThiPhieuNhapLenForm(tableModel.getValueAt(row, 0).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablePhieuNhap);
        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // Nút chức năng
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnTaoMoi = taoNutTrang("Tạo phiếu mới");
        JButton btnXemChiTiet = taoNutTrang("Xem chi tiết phụ tùng");
        JButton btnXacNhan = taoNutDo("Xác nhận nhập kho");

        btnTaoMoi.addActionListener(e -> {
            tablePhieuNhap.clearSelection();
            lamMoiFormLapPhieu();
        });
        btnXemChiTiet.addActionListener(e -> xemChiTietPhieuNhapPopup());
        btnXacNhan.addActionListener(e -> luuPhieuNhapQuaService());

        panelBottomButtons.add(btnTaoMoi);
        panelBottomButtons.add(btnXemChiTiet);
        panelBottomButtons.add(btnXacNhan);

        panelCardChinh.add(panelCardTop, BorderLayout.NORTH);
        JPanel panelCardBody = new JPanel(new BorderLayout(0, 8));
        panelCardBody.setBackground(Color.WHITE);
        panelCardBody.add(panelFieldsContainer, BorderLayout.NORTH);
        panelCardBody.add(panelTableSection, BorderLayout.CENTER);
        panelCardBody.add(panelBottomButtons, BorderLayout.SOUTH);
        panelCardChinh.add(panelCardBody, BorderLayout.CENTER);

        panelCenter.add(panelCardChinh, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);
    }

    private void napDuLieuComboBox() {
        try {
            dangCapNhatCombo = true;
            cboPhuTung.removeAllItems();
            cboNhanVienKho.removeAllItems();
            mapPhuTung.clear();

            for (PhuTung pt : phuTungService.layDanhSachPhuTung()) {
                mapPhuTung.put(pt.getMaPhuTung(), pt);
                cboPhuTung.addItem(pt.getMaPhuTung() + " - " + pt.getTenPhuTung() + " (Tồn: " + pt.getSoLuongTon() + ")");
            }

            for (NhanVien nv : nhanVienDao.layDanhSachNhanVien()) {
                cboNhanVienKho.addItem(nv.getMaNhanVien() + " - " + nv.getHoTen());
            }
            dangCapNhatCombo = false;
        } catch (SQLException e) {
            dangCapNhatCombo = false;
            e.printStackTrace();
        }
    }

    private void taiDanhSachPhieuNhapLenBang(String trangThaiLoc) {
        try {
            ArrayList<PhieuNhapKho> dsPhieu = phieuNhapKhoService.locTheoTrangThai(trangThaiLoc);
            doDuLieuPhieuNhapVaoBang(dsPhieu);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải phiếu nhập: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemPhieuNhapLenBang(String tuKhoa) {
        try {
            ArrayList<PhieuNhapKho> dsPhieu = phieuNhapKhoService.timKiem(tuKhoa);
            doDuLieuPhieuNhapVaoBang(dsPhieu);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void doDuLieuPhieuNhapVaoBang(ArrayList<PhieuNhapKho> dsPhieu) throws SQLException {
        tableModel.setRowCount(0);
        for (PhieuNhapKho pnk : dsPhieu) {
            ArrayList<ChiTietPhieuNhap> dsCT = ctPhieuNhapDao.timTheoMaPhieuNhap(pnk.getMaPhieuNhap());
            int tongSL = 0;
            BigDecimal tongTien = BigDecimal.ZERO;
            StringBuilder tomTat = new StringBuilder();

            for (ChiTietPhieuNhap ct : dsCT) {
                tongSL += ct.getSoLuong();
                tongTien = tongTien.add(ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong())));
                PhuTung pt = mapPhuTung.get(ct.getMaPhuTung());
                String tenPT = pt != null ? pt.getTenPhuTung() : ct.getMaPhuTung();
                if (tomTat.length() > 0) tomTat.append(", ");
                tomTat.append(tenPT).append(" (x").append(ct.getSoLuong()).append(")");
            }

            String ngayNhapStr = pnk.getNgayNhap() != null ? pnk.getNgayNhap().format(dtf) : "";
            tableModel.addRow(new Object[]{
                    pnk.getMaPhieuNhap(),
                    ngayNhapStr,
                    tomTat.length() == 0 ? "(Chưa có chi tiết)" : tomTat.toString(),
                    tongSL,
                    String.format("%,.0f đ", tongTien),
                    pnk.getGhiChu() != null ? pnk.getGhiChu() : "",
                    pnk.getTrangThai()
            });
        }
    }

    private void lamMoiFormLapPhieu() {
        try {
            txtMaPhieuNhap.setText(phieuNhapKhoService.taoMaTuDong());
            txtMaPhieuNhap.setEditable(false);
            txtNgayNhap.setText("Tự động ghi nhận khi lưu");
            txtSoLuong.setText("1");
            txtGhiChu.setText("");
            if (cboTrangThai.getItemCount() > 0) cboTrangThai.setSelectedIndex(0);
            if (cboPhuTung.getItemCount() > 0) {
                cboPhuTung.setSelectedIndex(0);
                String maPT = layMaTuCombo(cboPhuTung);
                PhuTung pt = mapPhuTung.get(maPT);
                if (pt != null && pt.getDonGia() != null) {
                    txtDonGia.setText(pt.getDonGia().stripTrailingZeros().toPlainString());
                }
            }
            tinhTamThanhTienTrenForm();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hienThiPhieuNhapLenForm(String maPhieuNhap) {
        try {
            PhieuNhapKho pnk = phieuNhapKhoService.timTheoMa(maPhieuNhap);
            if (pnk == null) return;

            dangCapNhatCombo = true;
            txtMaPhieuNhap.setText(pnk.getMaPhieuNhap());
            txtMaPhieuNhap.setEditable(false);
            txtNgayNhap.setText(pnk.getNgayNhap() != null ? pnk.getNgayNhap().format(dtf) : "");
            cboTrangThai.setSelectedItem(pnk.getTrangThai());
            txtGhiChu.setText(pnk.getGhiChu() != null ? pnk.getGhiChu() : "");

            ArrayList<ChiTietPhieuNhap> dsCT = ctPhieuNhapDao.timTheoMaPhieuNhap(maPhieuNhap);
            BigDecimal tongGiaTri = BigDecimal.ZERO;
            for (ChiTietPhieuNhap ct : dsCT) {
                tongGiaTri = tongGiaTri.add(ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong())));
            }

            if (!dsCT.isEmpty()) {
                ChiTietPhieuNhap ctDau = dsCT.get(0);
                chonItemComboTheoMa(cboPhuTung, ctDau.getMaPhuTung());
                txtSoLuong.setText(String.valueOf(ctDau.getSoLuong()));
                txtDonGia.setText(ctDau.getDonGia().stripTrailingZeros().toPlainString());
            }
            txtTongGiaTri.setText(String.format("%,.0f đ", tongGiaTri));
            dangCapNhatCombo = false;
        } catch (SQLException e) {
            dangCapNhatCombo = false;
            e.printStackTrace();
        }
    }

    /**
     * GỌI NGHIỆP VỤ NHẬP KHO QUA PHIEUNHAPKHOSERVICE
     */
    private void luuPhieuNhapQuaService() {
        try {
            String maPN = txtMaPhieuNhap.getText().trim();
            String trangThai = cboTrangThai.getSelectedItem().toString();
            String maPT = layMaTuCombo(cboPhuTung);
            int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            BigDecimal donGia = parseSoTien(txtDonGia.getText().trim());
            String ghiChu = txtGhiChu.getText().trim();

            PhieuNhapKho pnk = new PhieuNhapKho(maPN, LocalDateTime.now(), trangThai, ghiChu.isEmpty() ? null : ghiChu);
            ArrayList<ChiTietPhieuNhap> dsCT = new ArrayList<>();
            dsCT.add(new ChiTietPhieuNhap(maPN, maPT, soLuong, donGia));

            boolean thanhCong = phieuNhapKhoService.lapPhieuNhapKho(pnk, dsCT);

            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Đã lập phiếu nhập kho " + maPN + " và cập nhật tồn kho thành công!");
                napDuLieuComboBox();
                taiDanhSachPhieuNhapLenBang("Tất cả");
                lamMoiFormLapPhieu();
            } else {
                JOptionPane.showMessageDialog(this, "Lập phiếu nhập không thành công!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng và đơn giá phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xemChiTietPhieuNhapPopup() {
        String maPN = txtMaPhieuNhap.getText().trim();
        try {
            PhieuNhapKho pnk = phieuNhapKhoService.timTheoMa(maPN);
            if (pnk == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu nhập từ bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ArrayList<ChiTietPhieuNhap> dsCT = ctPhieuNhapDao.timTheoMaPhieuNhap(maPN);
            String[] cols = {"Mã Phụ Tùng", "Tên Phụ Tùng", "Đơn Vị Tính", "Số Lượng Nhập", "Đơn Giá", "Thành Tiền"};
            DefaultTableModel modelCT = new DefaultTableModel(null, cols) {
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };

            for (ChiTietPhieuNhap ct : dsCT) {
                PhuTung pt = mapPhuTung.get(ct.getMaPhuTung());
                String tenPT = pt != null ? pt.getTenPhuTung() : ct.getMaPhuTung();
                String dvt = pt != null ? pt.getDonViTinh() : "Cái";
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));
                modelCT.addRow(new Object[]{
                        ct.getMaPhuTung(), tenPT, dvt, ct.getSoLuong(),
                        String.format("%,.0f đ", ct.getDonGia()),
                        String.format("%,.0f đ", thanhTien)
                });
            }

            JTable tblCT = new JTable(modelCT);
            tblCT.setRowHeight(28);
            JScrollPane sp = new JScrollPane(tblCT);
            sp.setPreferredSize(new Dimension(680, 240));
            JOptionPane.showMessageDialog(this, sp, "Chi tiết phiếu nhập: " + maPN, JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tinhTamThanhTienTrenForm() {
        try {
            int sl = Integer.parseInt(txtSoLuong.getText().trim());
            BigDecimal dg = parseSoTien(txtDonGia.getText().trim());
            if (sl < 0) sl = 0;
            txtTongGiaTri.setText(String.format("%,.0f đ", dg.multiply(new BigDecimal(sl))));
        } catch (Exception e) {
            txtTongGiaTri.setText("0 đ");
        }
    }

    private BigDecimal parseSoTien(String text) {
        String clean = text.replaceAll("[^0-9.]", "");
        return clean.isEmpty() ? BigDecimal.ZERO : new BigDecimal(clean);
    }

    private String layMaTuCombo(JComboBox<String> combo) {
        if (combo.getSelectedItem() == null) return "";
        return combo.getSelectedItem().toString().split(" - ")[0].trim();
    }

    private void chonItemComboTheoMa(JComboBox<String> combo, String maCanChon) {
        if (maCanChon == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).startsWith(maCanChon + " -") || combo.getItemAt(i).equals(maCanChon)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private JPanel taoOnhapLieu(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 85, 95));
        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(0, 32));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoOCombo(String labelText, JComboBox<String> comboBox) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 85, 95));
        comboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(0, 32));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
        return panel;
    }

    private JButton taoNutTrang(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton taoNutDo(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(new Color(180, 30, 40));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}