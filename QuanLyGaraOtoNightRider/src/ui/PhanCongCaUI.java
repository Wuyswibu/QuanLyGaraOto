package ui;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import entity.CaLamViec;
import entity.NhanVien;
import entity.PhanCongCa;
import service.PhanCongCaService;

public class PhanCongCaUI extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color DO = new Color(188, 25, 58);

    private final PhanCongCaService service =
            new PhanCongCaService();

    private final JComboBox<String> cboNhanVien =
            new JComboBox<>();

    private final JComboBox<String> cboCa =
            new JComboBox<>();

    private final JSpinner spNgay = new JSpinner(
            new SpinnerDateModel());

    private final JTextArea txtGhiChu = new JTextArea(3, 20);

    private final JTextField txtTimKiem = new JTextField();

    private final JCheckBox chkTheoNgay =
            new JCheckBox("Chỉ xem ngày đang chọn");

    private final JButton btnPhanCong = taoNut("Phân công", true);
    private final JButton btnHuy = taoNut("Hủy phân công", true);
    private final JButton btnLamMoi = taoNut("Làm mới", false);
    private final JButton btnTimKiem = taoNut("Tìm kiếm", false);

    private final JLabel lblSoLuong = new JLabel("Số phân công: 0");

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                "Mã phân công",
                "Ngày làm việc",
                "Mã nhân viên",
                "Họ tên",
                "Ca làm",
                "Giờ bắt đầu",
                "Giờ kết thúc",
                "Trạng thái",
                "Ghi chú"
            }, 0) {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    private final Map<String, NhanVien> mapNhanVien =
            new HashMap<>();

    private final Map<String, CaLamViec> mapCa =
            new HashMap<>();

    private ArrayList<PhanCongCa> danhSach =
            new ArrayList<>();

    private final DateTimeFormatter ngayFormat =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DateTimeFormatter gioFormat =
            DateTimeFormatter.ofPattern("HH:mm");

    public PhanCongCaUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(239, 243, 246));
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel khung = new JPanel(new BorderLayout(0, 15));
        khung.setBackground(Color.WHITE);
        khung.setBorder(new EmptyBorder(18, 20, 18, 20));

        khung.add(taoPhanTren(), BorderLayout.NORTH);
        khung.add(taoPhanBang(), BorderLayout.CENTER);

        lblSoLuong.setForeground(Color.GRAY);
        khung.add(lblSoLuong, BorderLayout.SOUTH);

        add(khung, BorderLayout.CENTER);

        ganSuKien();
        taiDuLieu();
    }

    private JPanel taoPhanTren() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JLabel tieuDe = new JLabel("PHÂN CÔNG CA LÀM VIỆC");
        tieuDe.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(tieuDe, BorderLayout.NORTH);

        spNgay.setEditor(new JSpinner.DateEditor(
                spNgay, "dd/MM/yyyy"));

        JPanel form = new JPanel(new GridLayout(1, 3, 15, 0));
        form.setOpaque(false);
        form.add(taoO("Ngày làm việc", spNgay));
        form.add(taoO("Nhân viên đang làm việc", cboNhanVien));
        form.add(taoO("Ca làm việc", cboCa));

        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setFont(new Font("Arial", Font.PLAIN, 14));
        txtGhiChu.setMargin(new Insets(6, 8, 6, 8));

        JPanel noiDung = new JPanel(new BorderLayout(0, 12));
        noiDung.setOpaque(false);
        noiDung.add(form, BorderLayout.NORTH);
        noiDung.add(
                taoO("Ghi chú — tối đa 255 ký tự",
                        new JScrollPane(txtGhiChu)),
                BorderLayout.CENTER);

        JPanel nut = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        nut.setOpaque(false);
        nut.add(btnLamMoi);
        nut.add(btnHuy);
        nut.add(btnPhanCong);

        noiDung.add(nut, BorderLayout.SOUTH);
        panel.add(noiDung, BorderLayout.CENTER);

        btnHuy.setEnabled(false);

        return panel;
    }

    private JPanel taoPhanBang() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel timKiem = new JPanel(new BorderLayout(8, 0));
        timKiem.setOpaque(false);

        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTimKiem.setPreferredSize(new Dimension(300, 36));
        txtTimKiem.setToolTipText(
                "Tìm theo mã phân công, mã hoặc tên nhân viên, ca làm");

        chkTheoNgay.setOpaque(false);

        JPanel benPhai = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        benPhai.setOpaque(false);
        benPhai.add(chkTheoNgay);
        benPhai.add(btnTimKiem);

        timKiem.add(txtTimKiem, BorderLayout.CENTER);
        timKiem.add(benPhai, BorderLayout.EAST);

        table.setRowHeight(36);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        panel.add(timKiem, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void ganSuKien() {
        btnPhanCong.addActionListener(e -> phanCong());
        btnHuy.addActionListener(e -> huyPhanCong());

        btnTimKiem.addActionListener(e -> taiDuLieu());
        txtTimKiem.addActionListener(e -> taiDuLieu());
        chkTheoNgay.addActionListener(e -> locBang());

        spNgay.addChangeListener(e -> {
            if (chkTheoNgay.isSelected()) {
                locBang();
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtGhiChu.setText("");
            txtTimKiem.setText("");
            chkTheoNgay.setSelected(false);
            spNgay.setValue(new Date());
            taiDuLieu();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();

                if (row < 0) {
                    btnHuy.setEnabled(false);
                    return;
                }

                int dong = table.convertRowIndexToModel(row);

                btnHuy.setEnabled(
                        PhanCongCaService.DA_PHAN_CONG.equals(
                                model.getValueAt(dong, 7)));
            }
        });
    }

    private void taiDuLieu() {
        try {
            String nvDangChon = layMa(cboNhanVien);
            String caDangChon = layMa(cboCa);

            ArrayList<NhanVien> nhanVienMoi =
                    service.layDanhSachNhanVien();

            ArrayList<CaLamViec> caMoi =
                    service.layDanhSachCa();

            ArrayList<PhanCongCa> phanCongMoi =
                    service.layDanhSach();

            mapNhanVien.clear();
            mapCa.clear();

            cboNhanVien.removeAllItems();
            cboCa.removeAllItems();

            for (NhanVien nv : nhanVienMoi) {
                mapNhanVien.put(nv.getMaNhanVien(), nv);

                if ("Đang làm".equals(nv.getTrangThai())) {
                    cboNhanVien.addItem(
                            nv.getMaNhanVien() + " - " + nv.getHoTen());
                }
            }

            for (CaLamViec ca : caMoi) {
                mapCa.put(ca.getMaCa(), ca);

                if (ca.isTrangThai()) {
                    cboCa.addItem(
                            ca.getMaCa() + " - " + ca.getTenCa()
                            + " (" + gio(ca.getGioBatDau())
                            + " - " + gio(ca.getGioKetThuc()) + ")");
                }
            }

            chonTheoMa(cboNhanVien, nvDangChon);
            chonTheoMa(cboCa, caDangChon);

            danhSach = phanCongMoi;
            locBang();

        } catch (SQLException ex) {
            hienLoi(ex);
        }
    }

    private void locBang() {
        String tuKhoa = txtTimKiem.getText()
                .trim().toLowerCase(java.util.Locale.ROOT);

        LocalDate ngay = layNgay();

        model.setRowCount(0);
        btnHuy.setEnabled(false);

        for (PhanCongCa pc : danhSach) {
            if (chkTheoNgay.isSelected()
                    && !ngay.equals(pc.getNgayLamViec())) {
                continue;
            }

            NhanVien nv = mapNhanVien.get(pc.getMaNhanVien());
            CaLamViec ca = mapCa.get(pc.getMaCa());

            String tenNV = nv == null ? "" : nv.getHoTen();
            String tenCa = ca == null ? pc.getMaCa() : ca.getTenCa();

            String noiDung = (
                    pc.getMaPhanCongCa() + " "
                    + pc.getMaNhanVien() + " "
                    + tenNV + " " + tenCa)
                    .toLowerCase(java.util.Locale.ROOT);

            if (!tuKhoa.isEmpty() && !noiDung.contains(tuKhoa)) {
                continue;
            }

            model.addRow(new Object[]{
                pc.getMaPhanCongCa(),
                pc.getNgayLamViec() == null
                        ? "" : pc.getNgayLamViec().format(ngayFormat),
                pc.getMaNhanVien(),
                tenNV,
                tenCa,
                ca == null ? "" : gio(ca.getGioBatDau()),
                ca == null ? "" : gio(ca.getGioKetThuc()),
                pc.getTrangThai(),
                pc.getGhiChu()
            });
        }

        lblSoLuong.setText("Số phân công: " + model.getRowCount());
    }

    private void phanCong() {
        try {
            spNgay.commitEdit();

            service.phanCong(
                    layMa(cboNhanVien),
                    layMa(cboCa),
                    layNgay(),
                    txtGhiChu.getText());

            JOptionPane.showMessageDialog(
                    this, "Phân công ca thành công!");

            txtGhiChu.setText("");
            taiDuLieu();

        } catch (java.text.ParseException ex) {
            JOptionPane.showMessageDialog(
                    this, "Ngày làm việc không hợp lệ.");
        } catch (SQLException | IllegalArgumentException ex) {
            hienLoi(ex);
        }
    }

    private void huyPhanCong() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this, "Vui lòng chọn phân công cần hủy.");
            return;
        }

        int dong = table.convertRowIndexToModel(row);
        String ma = model.getValueAt(dong, 0).toString();

        int chon = JOptionPane.showConfirmDialog(
                this,
                "Bạn muốn hủy phân công " + ma + "?",
                "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (chon != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            service.huyPhanCong(ma);
            JOptionPane.showMessageDialog(
                    this, "Đã hủy phân công.");
            taiDuLieu();

        } catch (SQLException | IllegalArgumentException ex) {
            hienLoi(ex);
        }
    }

    private LocalDate layNgay() {
        return ((Date) spNgay.getValue()).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private String layMa(JComboBox<String> combo) {
        Object item = combo.getSelectedItem();

        return item == null
                ? ""
                : item.toString().split(" - ", 2)[0];
    }

    private void chonTheoMa(JComboBox<String> combo, String ma) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).startsWith(ma + " - ")) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private String gio(java.time.LocalTime giaTri) {
        return giaTri == null ? "" : giaTri.format(gioFormat);
    }

    private JPanel taoO(String ten, JComponent thanhPhan) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel nhan = new JLabel(ten);
        nhan.setFont(new Font("Arial", Font.BOLD, 12));

        thanhPhan.setFont(new Font("Arial", Font.PLAIN, 14));

        if (!(thanhPhan instanceof JScrollPane)) {
            thanhPhan.setPreferredSize(new Dimension(200, 36));
        }

        panel.add(nhan, BorderLayout.NORTH);
        panel.add(thanhPhan, BorderLayout.CENTER);

        return panel;
    }

    private static JButton taoNut(String ten, boolean chinh) {
        JButton nut = new JButton(ten);

        nut.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        nut.setBackground(chinh ? DO : Color.WHITE);
        nut.setForeground(chinh ? Color.WHITE : Color.DARK_GRAY);
        nut.setFont(new Font("Arial", Font.BOLD, 13));
        nut.setOpaque(true);
        nut.setContentAreaFilled(true);
        nut.setFocusPainted(false);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        nut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        chinh ? DO : new Color(210, 215, 220)),
                new EmptyBorder(9, 16, 9, 16)));

        return nut;
    }

    private void hienLoi(Exception ex) {
        if (ex instanceof SQLException) {
            ex.printStackTrace();
        }

        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Không thực hiện được",
                JOptionPane.ERROR_MESSAGE);
    }
}