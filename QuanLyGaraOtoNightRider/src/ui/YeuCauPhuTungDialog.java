package ui;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.PhuTungDao;
import entity.PhuTung;
import service.YeuCauPhuTungService;

public class YeuCauPhuTungDialog {

    private static DefaultTableModel model(String... cot) {
        return new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static String layMaNhanVien(Component parent) {
        Window cuaSo = SwingUtilities.getWindowAncestor(parent);

        if (!(cuaSo instanceof GaraManHinhChinh)) {
            throw new IllegalArgumentException(
                    "Vui lòng mở chức năng từ màn hình chính.");
        }

        GaraManHinhChinh main = (GaraManHinhChinh) cuaSo;

        if (main.getNhanVienDangNhap() == null) {
            throw new IllegalArgumentException(
                    "Không xác định được nhân viên đăng nhập.");
        }

        return main.getNhanVienDangNhap().getMaNhanVien();
    }

    public static void gui(Component parent, String maPSC) {
        try {
            String maNV = layMaNhanVien(parent);

            java.util.List<PhuTung> phuTung =
                    new PhuTungDao().layDanhSachPhuTung();

            if (phuTung.isEmpty()) {
                throw new IllegalArgumentException(
                        "Danh mục chưa có phụ tùng.");
            }

            JComboBox<String> cbo = new JComboBox<>();

            for (PhuTung p : phuTung) {
                cbo.addItem(p.getMaPhuTung() + " - " + p.getTenPhuTung());
            }

            JSpinner soLuong = new JSpinner(
                    new SpinnerNumberModel(1, 1, 100000, 1));

            JButton them = new JButton("Thêm / Đổi số lượng");
            JButton bo = new JButton("Bỏ dòng");

            DefaultTableModel m = model(
                    "Mã phụ tùng", "Tên phụ tùng", "Số lượng");

            JTable bang = new JTable(m);
            bang.setRowHeight(30);

            them.addActionListener(e -> {
                try {
                    soLuong.commitEdit();
                    int index = cbo.getSelectedIndex();

                    if (index < 0) return;

                    PhuTung p = phuTung.get(index);
                    int sl = ((Number) soLuong.getValue()).intValue();

                    for (int i = 0; i < m.getRowCount(); i++) {
                        if (p.getMaPhuTung().equals(m.getValueAt(i, 0))) {
                            m.setValueAt(sl, i, 2);
                            return;
                        }
                    }

                    m.addRow(new Object[]{
                        p.getMaPhuTung(), p.getTenPhuTung(), sl
                    });

                } catch (java.text.ParseException ex) {
                    JOptionPane.showMessageDialog(
                            parent, "Số lượng không hợp lệ.");
                }
            });

            bo.addActionListener(e -> {
                int row = bang.getSelectedRow();
                if (row >= 0) m.removeRow(row);
            });

            JPanel tren = new JPanel(new BorderLayout(8, 8));
            tren.add(cbo, BorderLayout.CENTER);

            JPanel nut = new JPanel();
            nut.add(new JLabel("Số lượng:"));
            nut.add(soLuong);
            nut.add(them);
            nut.add(bo);

            tren.add(nut, BorderLayout.SOUTH);

            JTextField ghiChu = new JTextField();
            ghiChu.setBorder(BorderFactory.createTitledBorder("Ghi chú"));

            JPanel form = new JPanel(new BorderLayout(8, 8));
            form.add(tren, BorderLayout.NORTH);
            form.add(new JScrollPane(bang), BorderLayout.CENTER);
            form.add(ghiChu, BorderLayout.SOUTH);
            form.setPreferredSize(new Dimension(750, 350));

            while (JOptionPane.showOptionDialog(
                    parent, form, "Yêu cầu phụ tùng — " + maPSC,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null,
                    new String[]{"Gửi yêu cầu", "Đóng"},
                    "Gửi yêu cầu") == 0) {

                try {
                    Map<String, Integer> ds = new LinkedHashMap<>();

                    for (int i = 0; i < m.getRowCount(); i++) {
                        ds.put(m.getValueAt(i, 0).toString(),
                                ((Number) m.getValueAt(i, 2)).intValue());
                    }

                    long ma = new YeuCauPhuTungService().guiYeuCau(
                            maPSC, maNV, ds, ghiChu.getText());

                    JOptionPane.showMessageDialog(parent,
                            "Đã gửi yêu cầu #" + ma
                            + ". Trạng thái: Chờ xử lý.\nChưa trừ tồn kho.");
                    return;

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent, ex.getMessage());
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, ex.getMessage());
        }
    }

    public static boolean xuLy(Component parent) {
        try {
            String maNV = layMaNhanVien(parent);
            YeuCauPhuTungService service = new YeuCauPhuTungService();

            DefaultTableModel m = model(
                    "Mã yêu cầu", "Phiếu sửa chữa",
                    "Người yêu cầu", "Ngày yêu cầu", "Ghi chú");

            for (Object[] dong : service.layYeuCauCho(maNV)) {
                m.addRow(dong);
            }

            if (m.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        parent, "Không có yêu cầu đang chờ.");
                return false;
            }

            JTable bang = new JTable(m);
            bang.setRowHeight(32);
            bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            bang.setRowSelectionInterval(0, 0);

            JScrollPane cuon = new JScrollPane(bang);
            cuon.setPreferredSize(new Dimension(850, 330));

            int chon = JOptionPane.showOptionDialog(
                    parent, cuon, "Yêu cầu phụ tùng đang chờ",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null,
                    new String[]{"Xem và xuất yêu cầu", "Đóng"},
                    "Xem và xuất yêu cầu");

            if (chon != 0 || bang.getSelectedRow() < 0) {
                return false;
            }

            long ma = ((Number) m.getValueAt(
                    bang.getSelectedRow(), 0)).longValue();

            DefaultTableModel ct = model(
                    "Mã phụ tùng", "Tên phụ tùng",
                    "Số lượng yêu cầu", "Tồn hiện tại");

            for (Object[] dong : service.layChiTiet(ma)) {
                ct.addRow(dong);
            }

            JTable bangCT = new JTable(ct);
            bangCT.setRowHeight(32);

            JScrollPane cuonCT = new JScrollPane(bangCT);
            cuonCT.setPreferredSize(new Dimension(700, 280));

            int xacNhan = JOptionPane.showOptionDialog(
                    parent, cuonCT, "Chi tiết yêu cầu #" + ma,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null,
                    new String[]{"Xác nhận xuất toàn bộ", "Đóng"},
                    "Đóng");

            if (xacNhan != 0) return false;

            String maPX = service.xacNhanXuat(ma, maNV);

            JOptionPane.showMessageDialog(parent,
                    "Đã xuất yêu cầu #" + ma
                    + "\nPhiếu xuất: " + maPX);

            return true;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent,
                    ex.getMessage(), "Không xử lý được",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}