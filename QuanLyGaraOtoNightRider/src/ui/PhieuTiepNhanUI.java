package ui;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import entity.PhieuTiepNhan;
import service.PhieuTiepNhanService;

/** Danh sách phiếu: dùng độc lập ở menu hoặc nhúng trong tab tiếp nhận. */
public class PhieuTiepNhanUI extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color LINE = new Color(220, 224, 230);
    private final PhieuTiepNhanService service;
    private final List<JButton> buttons = new ArrayList<>();
    private boolean busy;
    private final JTextField tuKhoa = new JTextField();
    private final JComboBox<String> locTrangThai = new JComboBox<>(new String[]{"Tất cả"});
    private final JLabel soLuong = new JLabel("Chọn tab danh sách để tải dữ liệu từ SQL.");
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Mã tiếp nhận", "Mã phương tiện", "Nhân viên", "Ngày tiếp nhận", "Hẹn trả", "Trạng thái"}, 0){
        private static final long serialVersionUID = 1L;
        
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private List<PhieuTiepNhan> ds = new ArrayList<>();

    public PhieuTiepNhanUI() {
        this(new PhieuTiepNhanService());
    }

    public PhieuTiepNhanUI(PhieuTiepNhanService service) {
        this.service = Objects.requireNonNull(service);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 14, 10, 14));
        add(taoTrangDanhSach());
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tuKhoa.addActionListener(e -> taiDanhSach());
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                taiDanhSach();
            }
        });
    }

    private JPanel taoTrangDanhSach() {
        JPanel page = card("DANH SÁCH PHIẾU TIẾP NHẬN", new BorderLayout(0, 10));
        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.setOpaque(false);
        tuKhoa.setToolTipText("Tìm theo mã phiếu, biển số, tên khách hoặc điện thoại");
        search.add(new JLabel("Tìm phiếu:"), BorderLayout.WEST);
        search.add(tuKhoa);
        JPanel tools = flow();
        tools.add(locTrangThai);
        tools.add(button("Tìm kiếm", this::taiDanhSach));
        tools.add(button("Tải lại", ()->{
            tuKhoa.setText("");
            locTrangThai.setSelectedIndex(0);
            taiDanhSach();
        }));
        search.add(tools, BorderLayout.EAST);
        page.add(search, BorderLayout.NORTH);
        JScrollPane bang = new JScrollPane(table);
        bang.setColumnHeaderView(table.getTableHeader());
        page.add(bang);
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(soLuong);
        footer.add(button("Xem chi tiết phiếu", this::xemChiTiet), BorderLayout.EAST);
        page.add(footer, BorderLayout.SOUTH);
        return page;
    }

    public void taiDanhSach() {
        String q = tuKhoa.getText();
        String state = String.valueOf(locTrangThai.getSelectedItem());
        run(()->service.danhSach(q, state), (list)->{
            ds = list;
            model.setRowCount(0);
            java.util.TreeSet<String> states = new java.util.TreeSet<>();
            for (PhieuTiepNhan p : list) {
                states.add(p.getTrangThai());
                model.addRow(new Object[]{p.getMaTiepNhan(), p.getMaPhuongTien(), p.getMaNhanVien(), fmt(p.getNgayGioTiepNhan()), fmt(p.getNgayGioHenTra()), p.getTrangThai()});
            }
            for (int i = 1; i < locTrangThai.getItemCount(); i++) states.add(locTrangThai.getItemAt(i));
            locTrangThai.removeAllItems();
            locTrangThai.addItem("Tất cả");
            for (String s : states) locTrangThai.addItem(s);
            locTrangThai.setSelectedItem(state);
            soLuong.setText("Hiển thị: " + list.size() + " phiếu");
        });
    }

    private static String fmt(java.time.LocalDateTime d) {
        return d == null ? "" : d.format(PhieuTiepNhanService.NGAY);
    }

    private void xemChiTiet() {
        int v = table.getSelectedRow();
        if (v < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một phiếu để xem.");
            return;
        }
        PhieuTiepNhan p = ds.get(table.convertRowIndexToModel(v));
        JPanel detail = new JPanel(new BorderLayout(0, 10));
        JTextArea info = area(7);
        info.setEditable(false);
        info.setText("Phiếu: " + p.getMaTiepNhan() + "\nPhương tiện: " + p.getMaPhuongTien() + " • Nhân viên: " + p.getMaNhanVien() + "\nNgày nhận: " + fmt(p.getNgayGioTiepNhan()) + "\nHẹn trả: " + fmt(p.getNgayGioHenTra()) + "\nSố km: " + p.getOdo() + " • Nhiên liệu: " + p.getMucNhienLieu() + "\nTrạng thái: " + p.getTrangThai());
        detail.add(info, BorderLayout.NORTH);
        JTextArea notes = area(16);
        notes.setEditable(false);
        notes.setText("HIỆN TRẠNG BAN ĐẦU\n" + p.getTinhTrangBanDau() + "\n\nYÊU CẦU KHÁCH HÀNG\n" + p.getYeuCauKhachHang() + "\n\nGHI CHÚ\n" + PhieuTiepNhanService.trim(p.getGhiChu()));
        notes.setCaretPosition(0);
        detail.add(new JScrollPane(notes));
        detail.setPreferredSize(new Dimension(680, 510));
        JOptionPane.showMessageDialog(this, detail, "Chi tiết phiếu tiếp nhận", JOptionPane.PLAIN_MESSAGE);
    }

    private JPanel card(String title, LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LINE), title, 0, 0, new Font("SansSerif", Font.BOLD, 13)), new EmptyBorder(9, 12, 9, 12)));
        return p;
    }

    private static JPanel flow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        p.setOpaque(false);
        return p;
    }

    private JButton button(String title, Runnable run) {
        JButton b = new JButton(title);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(Math.max(105, b.getPreferredSize().width), 30));
        b.addActionListener((e)->{
            if (!busy) run.run();
        });
        buttons.add(b);
        return b;
    }

    private static JTextArea area(int rows) {
        JTextArea a = new JTextArea(rows, 30);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setFont(new Font("SansSerif", Font.PLAIN, 13));
        a.setMargin(new Insets(9, 10, 9, 10));
        return a;
    }

    private <T> void run(Callable<T> task, Consumer<T> success) {
        if (busy) return;
        busy = true;
        buttons.forEach(b -> b.setEnabled(false));
        tuKhoa.setEnabled(false);
        locTrangThai.setEnabled(false);
        soLuong.setText("Đang tải danh sách…");
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                try {
                    success.accept(get());
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    soLuong.setText("Tải thất bại; dữ liệu đang hiển thị có thể đã cũ.");
                    JOptionPane.showMessageDialog(PhieuTiepNhanUI.this,
                            cause.getMessage(), "Không tải được danh sách", JOptionPane.ERROR_MESSAGE);
                } finally {
                    busy = false;
                    buttons.forEach(b -> b.setEnabled(true));
                    tuKhoa.setEnabled(true);
                    locTrangThai.setEnabled(true);
                }
            }
        }.execute();
    }
}
