package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import entity.NhanVien;
import service.LichLamViecCuaToiService;

public class LichLamViecCuaToiUI extends JPanel {

    private static final long serialVersionUID = 1L;

    private final LichLamViecCuaToiService service;

    private final DefaultTableModel modelCa = taoModel(
            "Ngày làm việc", "Ca làm việc", "Giờ bắt đầu",
            "Giờ kết thúc", "Trạng thái", "Ghi chú");

    private final DefaultTableModel modelPhieu = taoModel(
            "Mã phiếu sửa chữa", "Mã tiếp nhận", "Biển số",
            "Ngày lập", "Loại phiếu", "Trạng thái");

    private final JButton btnLamMoi = new JButton("Làm mới");
    private final JLabel lblThongBao = new JLabel(" ");

    private boolean dangTai;

    public LichLamViecCuaToiUI(NhanVien nguoiDangNhap) {
        service = new LichLamViecCuaToiService(nguoiDangNhap);

        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(240, 243, 246));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblTieuDe = new JLabel(
                "CA TRỰC VÀ CÔNG VIỆC CỦA TÔI");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel lblNhanVien = new JLabel(
                service.getThongTinNhanVien());

        JPanel pTieuDe = new JPanel(new GridLayout(2, 1, 0, 6));
        pTieuDe.setOpaque(false);
        pTieuDe.add(lblTieuDe);
        pTieuDe.add(lblNhanVien);

        btnLamMoi.setUI(
                new javax.swing.plaf.basic.BasicButtonUI());
        btnLamMoi.setBackground(new Color(190, 22, 58));
        btnLamMoi.setForeground(Color.WHITE);
        btnLamMoi.setOpaque(true);
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setBorder(
                BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLamMoi.addActionListener(e -> taiDuLieu());

        JPanel pDau = new JPanel(new BorderLayout(12, 0));
        pDau.setOpaque(false);
        pDau.add(pTieuDe, BorderLayout.CENTER);
        pDau.add(btnLamMoi, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 14));

        tabs.addTab("Ca trực của tôi",
                new JScrollPane(taoBang(modelCa)));

        tabs.addTab("Phiếu sửa chữa được phân công",
                new JScrollPane(taoBang(modelPhieu)));

        add(pDau, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(lblThongBao, BorderLayout.SOUTH);
    }

    private static DefaultTableModel taoModel(String... cot) {
        return new DefaultTableModel(cot, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable taoBang(DefaultTableModel model) {
        JTable bang = new JTable(model);

        bang.setRowHeight(32);
        bang.setFont(new Font("Arial", Font.PLAIN, 14));
        bang.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14));
        bang.setFillsViewportHeight(true);
        bang.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        return bang;
    }

    public void taiDuLieu() {
        if (dangTai) {
            return;
        }

        dangTai = true;
        btnLamMoi.setEnabled(false);
        lblThongBao.setText("Đang tải lịch làm việc...");

        new SwingWorker<DuLieu, Void>() {

            @Override
            protected DuLieu doInBackground() throws Exception {
                return new DuLieu(
                        service.layCaCuaToi(),
                        service.layPhieuCuaToi());
            }

            @Override
            protected void done() {
                try {
                    DuLieu duLieu = get();

                    modelCa.setRowCount(0);
                    modelPhieu.setRowCount(0);

                    DateTimeFormatter ngay =
                            DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    DateTimeFormatter gio =
                            DateTimeFormatter.ofPattern("HH:mm");

                    DateTimeFormatter ngayGio =
                            DateTimeFormatter.ofPattern(
                                    "dd/MM/yyyy HH:mm");

                    for (Object[] dong : duLieu.ca) {
                        Object[] hienThi = dong.clone();

                        if (dong[0] != null) {
                            hienThi[0] = ((java.sql.Date) dong[0])
                                    .toLocalDate().format(ngay);
                        }

                        if (dong[2] != null) {
                            hienThi[2] = ((java.sql.Time) dong[2])
                                    .toLocalTime().format(gio);
                        }

                        if (dong[3] != null) {
                            hienThi[3] = ((java.sql.Time) dong[3])
                                    .toLocalTime().format(gio);
                        }

                        modelCa.addRow(hienThi);
                    }

                    for (Object[] dong : duLieu.phieu) {
                        Object[] hienThi = dong.clone();

                        if (dong[3] != null) {
                            hienThi[3] =
                                    ((java.sql.Timestamp) dong[3])
                                    .toLocalDateTime().format(ngayGio);
                        }

                        modelPhieu.addRow(hienThi);
                    }

                    lblThongBao.setText(
                            "Ca trực: " + duLieu.ca.size()
                            + " | Phiếu được phân công: "
                            + duLieu.phieu.size()
                            + (duLieu.phieu.isEmpty()
                                ? " — Chưa có phiếu sửa chữa được giao."
                                : ""));

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    lblThongBao.setText("Đã ngắt tải dữ liệu.");

                } catch (ExecutionException ex) {
                    modelCa.setRowCount(0);
                    modelPhieu.setRowCount(0);
                    lblThongBao.setText("Không tải được lịch làm việc.");

                    Throwable loi = ex.getCause();

                    JOptionPane.showMessageDialog(
                            LichLamViecCuaToiUI.this,
                            loi == null
                                    ? ex.getMessage()
                                    : loi.getMessage(),
                            "Lỗi tải lịch làm việc",
                            JOptionPane.ERROR_MESSAGE);

                } finally {
                    dangTai = false;
                    btnLamMoi.setEnabled(true);
                }
            }
        }.execute();
    }

    private static class DuLieu {

        final List<Object[]> ca;
        final List<Object[]> phieu;

        DuLieu(List<Object[]> ca, List<Object[]> phieu) {
            this.ca = ca;
            this.phieu = phieu;
        }
    }
}