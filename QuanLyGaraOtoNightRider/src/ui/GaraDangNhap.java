package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import entity.NhanVien;
import service.DangNhapService;

public class GaraDangNhap extends JFrame {

    private static final long serialVersionUID = 1L;

    private final DangNhapService dangNhapService = new DangNhapService();

    private final JTextField txtTenDangNhap = new JTextField(20);
    private final JPasswordField txtMatKhau = new JPasswordField(20);
    private final JButton btnDangNhap = new JButton("Đăng nhập");

    private boolean dangXuLy;

    public GaraDangNhap() {
        setTitle("Hệ thống Quản lý Gara Ô Tô - Night Invader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setMinimumSize(new Dimension(950, 550));
        setLocationRelativeTo(null);

        khoiTaoGiaoDien();
        ganSuKien();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());

        add(taoPanelThuongHieu(), BorderLayout.WEST);
        add(taoPanelDangNhap(), BorderLayout.CENTER);

        getRootPane().setDefaultButton(btnDangNhap);
    }

    private JPanel taoPanelThuongHieu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(440, 0));

        JPanel noiDung = new JPanel();
        noiDung.setBackground(Color.WHITE);
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));

        ImageIcon logo = taiIcon("/img/logo.png", 350, 250);

        JLabel lblLogo;
        if (logo != null) {
            lblLogo = new JLabel(logo);
        } else {
            lblLogo = new JLabel("NIGHT INVADER");
            lblLogo.setFont(new Font("Arial", Font.BOLD, 28));
            lblLogo.setForeground(new Color(190, 20, 60));
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTieuDe = new JLabel("HỆ THỐNG QUẢN LÝ GARA Ô TÔ");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDe.setForeground(new Color(60, 60, 60));
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        noiDung.add(lblLogo);
        noiDung.add(Box.createRigidArea(new Dimension(0, 20)));
        noiDung.add(lblTieuDe);

        panel.add(noiDung);
        return panel;
    }

    private JPanel taoPanelDangNhap() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(250, 235, 235));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(253, 242, 242));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 120, 120)),
                BorderFactory.createEmptyBorder(30, 25, 30, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblTieuDe = new JLabel("ĐĂNG NHẬP", JLabel.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 26));

        gbc.gridy = 0;
        form.add(lblTieuDe, gbc);

        gbc.gridy = 1;
        form.add(taoNhan("Tên đăng nhập"), gbc);

        gbc.gridy = 2;
        form.add(taoONhap(txtTenDangNhap, "/img/User_box_light.png"), gbc);

        gbc.gridy = 3;
        form.add(taoNhan("Mật khẩu"), gbc);

        gbc.gridy = 4;
        form.add(taoONhap(txtMatKhau, "/img/icon_mk.png"), gbc);

        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 15));
        btnDangNhap.setForeground(new Color(20, 110, 60));
        btnDangNhap.setBackground(new Color(225, 247, 235));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setPreferredSize(new Dimension(300, 42));

        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        form.add(btnDangNhap, gbc);

        panel.add(form);
        return panel;
    }

    private JLabel taoNhan(String noiDung) {
        JLabel label = new JLabel(noiDung);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JPanel taoONhap(JTextField oNhap, String duongDanIcon) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(
                new Color(180, 180, 180)
        ));

        JLabel lblIcon = new JLabel();
        lblIcon.setIcon(taiIcon(duongDanIcon, 28, 28));
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        oNhap.setFont(new Font("Arial", Font.PLAIN, 15));
        oNhap.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        oNhap.setPreferredSize(new Dimension(260, 40));

        panel.add(lblIcon, BorderLayout.WEST);
        panel.add(oNhap, BorderLayout.CENTER);

        return panel;
    }

    private ImageIcon taiIcon(String duongDan, int rong, int cao) {
        URL url = getClass().getResource(duongDan);
        if (url == null) {
            return null;
        }

        ImageIcon icon = new ImageIcon(url);
        if (icon.getIconWidth() <= 0) {
            return null;
        }

        Image image = icon.getImage().getScaledInstance(
                rong, cao, Image.SCALE_SMOOTH
        );
        return new ImageIcon(image);
    }

    private void ganSuKien() {
        btnDangNhap.addActionListener(e -> dangNhap());
    }

    private void dangNhap() {
        if (dangXuLy) {
            return;
        }

        String tenDangNhap = txtTenDangNhap.getText().trim();

        char[] kyTuMatKhau = txtMatKhau.getPassword();
        String matKhau = new String(kyTuMatKhau);
        Arrays.fill(kyTuMatKhau, '\0');

        if (tenDangNhap.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập tên đăng nhập.",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE
            );
            txtTenDangNhap.requestFocusInWindow();
            return;
        }

        if (matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập mật khẩu.",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE
            );
            txtMatKhau.requestFocusInWindow();
            return;
        }

        datTrangThaiXuLy(true);

        SwingWorker<NhanVien, Void> worker = new SwingWorker<NhanVien, Void>() {
            @Override
            protected NhanVien doInBackground() throws Exception {
                return dangNhapService.dangNhap(tenDangNhap, matKhau);
            }

            @Override
            protected void done() {
                try {
                    NhanVien nhanVien = get();

                    if (nhanVien == null) {
                        hienLoi("Không lấy được thông tin nhân viên đăng nhập.");
                        return;
                    }

                    moManHinhChinh(nhanVien);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    hienLoi("Quá trình đăng nhập bị gián đoạn.");
                } catch (ExecutionException ex) {
                    Throwable loi = ex.getCause();
                    if (loi == null) {
                        loi = ex;
                    }
                    loi.printStackTrace();

                    if (loi instanceof IllegalArgumentException) {
                        hienLoi(loi.getMessage());
                    } else {
                        hienLoi(
                                "Không thể kiểm tra tài khoản.\n"
                                + "Xem lỗi chi tiết trong Console của Eclipse."
                        );
                    }

                    txtMatKhau.setText("");
                } finally {
                    datTrangThaiXuLy(false);
                }
            }
        };

        worker.execute();
    }

    private void moManHinhChinh(NhanVien nhanVien) {
        GaraManHinhChinh manHinhChinh = null;

        try {
            // Truyền nhân viên đăng nhập để phiếu tiếp nhận lấy mã NV.
            manHinhChinh = new GaraManHinhChinh(nhanVien);

            JOptionPane.showMessageDialog(
                    this,
                    "Đăng nhập thành công!\nXin chào: " + nhanVien.getHoTen(),
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );

            manHinhChinh.setVisible(true);
            txtMatKhau.setText("");
            dispose();
        } catch (RuntimeException ex) {
            if (manHinhChinh != null) {
                manHinhChinh.dispose();
            }

            ex.printStackTrace();
            hienLoi(
                    "Tài khoản hợp lệ nhưng không mở được màn hình chính.\n"
                    + "Xem lỗi chi tiết trong Console của Eclipse."
            );
        }
    }

    private void datTrangThaiXuLy(boolean xuLy) {
        dangXuLy = xuLy;

        txtTenDangNhap.setEnabled(!xuLy);
        txtMatKhau.setEnabled(!xuLy);
        btnDangNhap.setEnabled(!xuLy);
        btnDangNhap.setText(xuLy ? "Đang đăng nhập..." : "Đăng nhập");
    }

    private void hienLoi(String noiDung) {
        JOptionPane.showMessageDialog(
                this,
                noiDung,
                "Không thực hiện được",
                JOptionPane.ERROR_MESSAGE
        );
    }
}