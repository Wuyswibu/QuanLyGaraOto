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

import service.PhieuXuatKhoService;
import service.PhuTungService;
import dao.ChiTietPhieuXuatDao;
import dao.NhanVienDao;
import entity.ChiTietPhieuXuat;
import entity.NhanVien;
import entity.PhieuXuatKho;
import entity.PhuTung;

public class GaraLapPhieuXuatKho extends JPanel {
    private static final long serialVersionUID = 1L;

    // Component Form
    private JTextField txtMaPhieuXuat;
    private JTextField txtNgayXuat;
    private JComboBox<String> cboLyDoXuat;
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
    private JTable tablePhieuXuat;
    private DefaultTableModel tableModel;

    // Service & DAO
    private final PhieuXuatKhoService phieuXuatKhoService = new PhieuXuatKhoService();
    private final PhuTungService phuTungService = new PhuTungService();
    private final ChiTietPhieuXuatDao ctPhieuXuatDao = new ChiTietPhieuXuatDao();
    private final NhanVienDao nhanVienDao = new NhanVienDao();

    private final Map<String, PhuTung> mapPhuTung = new HashMap<>();
    private boolean dangCapNhatCombo = false;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã phiếu xuất, lý do hoặc ghi chú (Nhấn Enter)...";

    public GaraLapPhieuXuatKho() {
        khoiTaoGiaoDien();
        napDuLieuComboBox();
        taiDanhSachPhieuXuatLenBang("Tất cả");
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
        JLabel lblTieuDeCard = new JLabel("LẬP PHIẾU XUẤT KHO");
        lblTieuDeCard.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDeCard.setForeground(new Color(30, 35, 45));
        panelCardTop.add(lblTieuDeCard, BorderLayout.WEST);

        // Form
        JLabel lblMoTa = new JLabel("Lập phiếu xuất phụ tùng sử dụng cho phiếu sửa chữa hoặc bảo hành. Tồn kho sẽ tự động giảm.");
        lblMoTa.setFont(new Font("Arial", Font.PLAIN, 13));
        lblMoTa.setForeground(Color.GRAY);
        lblMoTa.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        txtMaPhieuXuat = new JTextField();
        txtMaPhieuXuat.setFont(new Font("Arial", Font.BOLD, 13));
        txtMaPhieuXuat.setEditable(false);

        txtNgayXuat = new JTextField("Tự động ghi nhận khi lưu");
        txtNgayXuat.setEditable(false);
        txtNgayXuat.setBackground(new Color(245, 247, 250));

        cboLyDoXuat = new JComboBox<>(new String[]{"Xuất sửa chữa", "Xuất bảo dưỡng", "Xuất bảo hành", "Xuất thanh lý phụ tùng hỏng"});
        cboTrangThai = new JComboBox<>(new String[]{"Đã xuất kho", "Chờ duyệt", "Đã hủy"});
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
        panelGridFields.add(taoOnhapLieu("Mã phiếu xuất", txtMaPhieuXuat));
        panelGridFields.add(taoOnhapLieu("Ngày xuất (Tự động)", txtNgayXuat));
        panelGridFields.add(taoOCombo("Lý do xuất kho", cboLyDoXuat));
        panelGridFields.add(taoOCombo("Phụ tùng xuất", cboPhuTung));
        panelGridFields.add(taoOnhapLieu("Số lượng xuất", txtSoLuong));
        panelGridFields.add(taoOnhapLieu("Đơn giá xuất (VNĐ)", txtDonGia));
        panelGridFields.add(taoOCombo("Nhân viên xuất", cboNhanVienKho));
        panelGridFields.add(taoOnhapLieu("Ghi chú / Mã phiếu sửa chữa", txtGhiChu));
        panelGridFields.add(taoOnhapLieu("Tổng giá trị xuất", txtTongGiaTri));

        JPanel panelFieldsContainer = new JPanel(new BorderLayout());
        panelFieldsContainer.setBackground(Color.WHITE);
        panelFieldsContainer.add(lblMoTa, BorderLayout.NORTH);
        panelFieldsContainer.add(panelGridFields, BorderLayout.CENTER);

        // Thanh lọc & tìm kiếm
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
                timKiemPhieuXuatLenBang(tuKhoa);
            } else {
                taiDanhSachPhieuXuatLenBang(cboLocTrangThai.getSelectedItem().toString());
            }
        });

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlFilterRight.setOpaque(false);
        pnlFilterRight.add(new JLabel("Trạng thái:"));
        cboLocTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã xuất kho", "Chờ duyệt", "Đã hủy"});
        cboLocTrangThai.setFont(new Font("Arial", Font.PLAIN, 12));
        cboLocTrangThai.addActionListener(e -> {
            if (cboLocTrangThai.getSelectedItem() != null) {
                taiDanhSachPhieuXuatLenBang(cboLocTrangThai.getSelectedItem().toString());
            }
        });
        pnlFilterRight.add(cboLocTrangThai);
        panelSearchFilter.add(txtTimKiemBang, BorderLayout.CENTER);
        panelSearchFilter.add(pnlFilterRight, BorderLayout.EAST);

        // Bảng danh sách phiếu xuất
        String[] columnNames = {"Mã Phiếu Xuất", "Ngày Xuất", "Lý Do Xuất", "Phụ Tùng Xuất", "Tổng SL", "Tổng Giá Trị", "Trạng Thái"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablePhieuXuat = new JTable(tableModel);
        tablePhieuXuat.setRowHeight(34);
        tablePhieuXuat.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablePhieuXuat.setSelectionBackground(new Color(40, 100, 150));
        tablePhieuXuat.setSelectionForeground(Color.WHITE);

        tablePhieuXuat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablePhieuXuat.getSelectedRow();
                if (row >= 0) {
                    hienThiPhieuXuatLenForm(tableModel.getValueAt(row, 0).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablePhieuXuat);
        JPanel panelTableSection = new JPanel(new BorderLayout());
        panelTableSection.setBackground(Color.WHITE);
        panelTableSection.add(panelSearchFilter, BorderLayout.NORTH);
        panelTableSection.add(scrollPane, BorderLayout.CENTER);

        // Nút bấm
        JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBottomButtons.setBackground(Color.WHITE);

        JButton btnTaoMoi = taoNutTrang("Tạo phiếu mới");
        JButton btnXemChiTiet = taoNutTrang("Xem chi tiết phụ tùng");
        JButton btnXacNhan = taoNutDo("Xác nhận xuất kho");

        btnTaoMoi.addActionListener(e -> {
            tablePhieuXuat.clearSelection();
            lamMoiFormLapPhieu();
        });
        btnXemChiTiet.addActionListener(e -> xemChiTietPhieuXuatPopup());
        btnXacNhan.addActionListener(e -> luuPhieuXuatQuaService());

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

    private void taiDanhSachPhieuXuatLenBang(String trangThaiLoc) {
        try {
            ArrayList<PhieuXuatKho> dsPhieu = phieuXuatKhoService.locTheoTrangThai(trangThaiLoc);
            doDuLieuPhieuXuatVaoBang(dsPhieu);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải phiếu xuất: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemPhieuXuatLenBang(String tuKhoa) {
        try {
            ArrayList<PhieuXuatKho> dsPhieu = phieuXuatKhoService.timKiem(tuKhoa);
            doDuLieuPhieuXuatVaoBang(dsPhieu);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void doDuLieuPhieuXuatVaoBang(ArrayList<PhieuXuatKho> dsPhieu) throws SQLException {
        tableModel.setRowCount(0);
        for (PhieuXuatKho pxk : dsPhieu) {
            ArrayList<ChiTietPhieuXuat> dsCT = ctPhieuXuatDao.timTheoMaPhieuXuat(pxk.getMaPhieuXuat());
            int tongSL = 0;
            BigDecimal tongTien = BigDecimal.ZERO;
            StringBuilder tomTat = new StringBuilder();

            for (ChiTietPhieuXuat ct : dsCT) {
                tongSL += ct.getSoLuong();
                tongTien = tongTien.add(ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong())));
                PhuTung pt = mapPhuTung.get(ct.getMaPhuTung());
                String tenPT = pt != null ? pt.getTenPhuTung() : ct.getMaPhuTung();
                if (tomTat.length() > 0) tomTat.append(", ");
                tomTat.append(tenPT).append(" (x").append(ct.getSoLuong()).append(")");
            }

            String ngayXuatStr = pxk.getNgayXuat() != null ? pxk.getNgayXuat().format(dtf) : "";
            tableModel.addRow(new Object[]{
                    pxk.getMaPhieuXuat(),
                    ngayXuatStr,
                    pxk.getLyDoXuat(),
                    tomTat.length() == 0 ? "(Chưa có chi tiết)" : tomTat.toString(),
                    tongSL,
                    String.format("%,.0f đ", tongTien),
                    pxk.getTrangThai()
            });
        }
    }

    private void lamMoiFormLapPhieu() {
        try {
            txtMaPhieuXuat.setText(phieuXuatKhoService.taoMaTuDong());
            txtNgayXuat.setText("Tự động ghi nhận khi lưu");
            txtSoLuong.setText("1");
            txtGhiChu.setText("");
            if (cboLyDoXuat.getItemCount() > 0) cboLyDoXuat.setSelectedIndex(0);
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

    private void hienThiPhieuXuatLenForm(String maPhieuXuat) {
        try {
            PhieuXuatKho pxk = phieuXuatKhoService.timTheoMa(maPhieuXuat);
            if (pxk == null) return;

            dangCapNhatCombo = true;
            txtMaPhieuXuat.setText(pxk.getMaPhieuXuat());
            txtNgayXuat.setText(pxk.getNgayXuat() != null ? pxk.getNgayXuat().format(dtf) : "");
            cboLyDoXuat.setSelectedItem(pxk.getLyDoXuat());
            cboTrangThai.setSelectedItem(pxk.getTrangThai());
            txtGhiChu.setText(pxk.getGhiChu() != null ? pxk.getGhiChu() : "");

            ArrayList<ChiTietPhieuXuat> dsCT = ctPhieuXuatDao.timTheoMaPhieuXuat(maPhieuXuat);
            BigDecimal tongGiaTri = BigDecimal.ZERO;
            for (ChiTietPhieuXuat ct : dsCT) {
                tongGiaTri = tongGiaTri.add(ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong())));
            }

            if (!dsCT.isEmpty()) {
                ChiTietPhieuXuat ctDau = dsCT.get(0);
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
     * XỬ LÝ LƯU PHIẾU XUẤT VÀ GIẢM TỒN KHO THÔNG QUA SERVICE
     */
    private void luuPhieuXuatQuaService() {
    try {
        String maPX = txtMaPhieuXuat.getText().trim();

        // 1. Kiểm tra xem phiếu này đã được lưu trước đó chưa
        if (phieuXuatKhoService.timTheoMa(maPX) != null) {
            JOptionPane.showMessageDialog(
                this,
                "Phiếu xuất [" + maPX + "] này đã được lưu trong hệ thống rồi nè!\n"
                + "Nếu bạn muốn xuất thêm phiếu khác, hãy nhấn nút 'Tạo phiếu mới' trước nha!",
                "Nhắc nhở nhẹ nhàng",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String lyDo = cboLyDoXuat.getSelectedItem().toString();
        String trangThai = cboTrangThai.getSelectedItem().toString();
        String maPT = layMaTuCombo(cboPhuTung);
        int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
        BigDecimal donGia = parseSoTien(txtDonGia.getText().trim());
        String ghiChu = txtGhiChu.getText().trim();

        PhieuXuatKho pxk = new PhieuXuatKho(maPX, LocalDateTime.now(), lyDo, trangThai, ghiChu.isEmpty() ? null : ghiChu);
        ArrayList<ChiTietPhieuXuat> dsCT = new ArrayList<>();
        dsCT.add(new ChiTietPhieuXuat(maPX, maPT, soLuong, donGia));

        boolean thanhCong = phieuXuatKhoService.lapPhieuXuatKho(pxk, dsCT);

        if (thanhCong) {
            JOptionPane.showMessageDialog(
                this, 
                "Tuyệt vời! Đã lập phiếu xuất kho " + maPX + " và trừ tồn kho thành công rồi nhé!", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE
            );
            napDuLieuComboBox();
            taiDanhSachPhieuXuatLenBang("Tất cả");
            lamMoiFormLapPhieu();
        } else {
            JOptionPane.showMessageDialog(
                this, 
                "Hệ thống chưa lưu được phiếu xuất kho này, bạn kiểm tra lại giúp tớ nhé!", 
                "Ối, có lỗi nhỏ", 
                JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(
            this, 
            "Số lượng và đơn giá phải là số hợp lệ nha!", 
            "Nhập liệu chưa chuẩn nè", 
            JOptionPane.WARNING_MESSAGE
        );
    } catch (Exception ex) {
        // Việt hóa thông báo lỗi SQL nếu có phát sinh khác
        String thongBao = ex.getMessage();
        if (thongBao != null && thongBao.contains("PRIMARY KEY")) {
            thongBao = "Mã phiếu xuất này đã tồn tại mất rồi! Bạn bấm 'Tạo phiếu mới' để lấy mã mới nhé";
        }
        JOptionPane.showMessageDialog(
            this, 
            thongBao, 
            "Thông báo từ kho hàng", 
            JOptionPane.WARNING_MESSAGE
        );
    }
}

   private void xemChiTietPhieuXuatPopup() {
        String maPX = txtMaPhieuXuat.getText().trim();
        try {
            PhieuXuatKho pxk = phieuXuatKhoService.timTheoMa(maPX);
            if (pxk == null) {
                JOptionPane.showMessageDialog(this, 
                        "Vui lòng chọn một phiếu xuất từ bảng dưới để xem chi tiết nhé!", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ArrayList<ChiTietPhieuXuat> dsCT = ctPhieuXuatDao.timTheoMaPhieuXuat(maPX);
            String[] cols = {"Mã Phụ Tùng", "Tên Phụ Tùng", "Đơn Vị Tính", "Số Lượng Xuất", "Đơn Giá", "Thành Tiền"};
            DefaultTableModel modelCT = new DefaultTableModel(null, cols) {
                private static final long serialVersionUID = 1L;
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };

            BigDecimal tongGiaTri = BigDecimal.ZERO;
            for (ChiTietPhieuXuat ct : dsCT) {
                PhuTung pt = mapPhuTung.get(ct.getMaPhuTung());
                String tenPT = pt != null ? pt.getTenPhuTung() : ct.getMaPhuTung();
                String dvt = pt != null ? pt.getDonViTinh() : "Cái";
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));
                tongGiaTri = tongGiaTri.add(thanhTien);

                modelCT.addRow(new Object[]{
                        ct.getMaPhuTung(), 
                        tenPT, 
                        dvt, 
                        ct.getSoLuong(),
                        String.format("%,.0f đ", ct.getDonGia()),
                        String.format("%,.0f đ", thanhTien)
                });
            }

            JTable tblCT = new JTable(modelCT);
            tblCT.setRowHeight(28);
            tblCT.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            tblCT.getTableHeader().setBackground(new Color(240, 243, 246));
            tblCT.setFont(new Font("Arial", Font.PLAIN, 12));
            tblCT.setSelectionBackground(new Color(40, 100, 150));
            tblCT.setSelectionForeground(Color.WHITE);

            JScrollPane sp = new JScrollPane(tblCT);
            sp.setPreferredSize(new Dimension(720, 220));

            // Panel thông tin tổng quát hiển thị Lý Do Xuất
            JPanel pnlThongTinChung = new JPanel(new GridLayout(2, 2, 10, 6));
            pnlThongTinChung.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
            pnlThongTinChung.setBackground(new Color(248, 249, 251));

            JLabel lblMa = new JLabel(" Mã phiếu: " + pxk.getMaPhieuXuat());
            lblMa.setFont(new Font("Arial", Font.BOLD, 13));

            String ngayXuatStr = pxk.getNgayXuat() != null ? pxk.getNgayXuat().format(dtf) : "Chưa xác định";
            JLabel lblNgay = new JLabel(" Ngày xuất: " + ngayXuatStr);
            lblNgay.setFont(new Font("Arial", Font.PLAIN, 12));

            // Hiển thị nổi bật Lý do xuất
            String lyDo = pxk.getLyDoXuat() != null && !pxk.getLyDoXuat().trim().isEmpty() 
                    ? pxk.getLyDoXuat() : "Không ghi rõ";
            JLabel lblLyDo = new JLabel(" Lý do xuất: " + lyDo);
            lblLyDo.setFont(new Font("Arial", Font.BOLD, 13));
            lblLyDo.setForeground(new Color(180, 30, 40));

            JLabel lblTongTien = new JLabel(String.format("• Tổng tiền: %,.0f đ", tongGiaTri));
            lblTongTien.setFont(new Font("Arial", Font.BOLD, 13));
            lblTongTien.setForeground(new Color(40, 100, 150));

            pnlThongTinChung.add(lblMa);
            pnlThongTinChung.add(lblLyDo);
            pnlThongTinChung.add(lblNgay);
            pnlThongTinChung.add(lblTongTien);

            // Container chứa cả panel thông tin và bảng phụ tùng
            JPanel pnlContainer = new JPanel(new BorderLayout(0, 10));
            pnlContainer.add(pnlThongTinChung, BorderLayout.NORTH);
            pnlContainer.add(sp, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(
                    this, 
                    pnlContainer, 
                    "Chi tiết phiếu xuất kho: " + maPX, 
                    JOptionPane.PLAIN_MESSAGE
            );

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin chi tiết: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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