package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GaraDangNhap extends JFrame {

    // Khai báo các thành phần giao diện chính
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JLabel lblQuenMatKhau;

    public GaraDangNhap() {
        // Thiết lập tiêu đề cửa sổ ứng dụng
        setTitle("Hệ thống Quản lý Gara Ô Tô - Night Invader");
        
        setSize(1000, 600);
        
        // Căn giữa cửa sổ trên màn hình
        setLocationRelativeTo(null);
        
        // Thiết lập hành vi khi đóng cửa sổ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Khởi tạo các thành phần giao diện
        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        // Sử dụng BorderLayout để chia giao diện thành 2 phần: Trái và Phải
        setLayout(new BorderLayout());

        // ==================== PHẦN BÊN TRÁI: LOGO & THƯƠNG HIỆU ====================
        JPanel panelTrai = new JPanel();
        panelTrai.setBackground(Color.WHITE);
        panelTrai.setPreferredSize(new Dimension(470, 240));
        panelTrai.setLayout(new GridBagLayout()); // Căn giữa nội dung bên trái

        JPanel noiDungTrai = new JPanel();
        noiDungTrai.setBackground(Color.WHITE);
        noiDungTrai.setLayout(new BoxLayout(noiDungTrai, BoxLayout.Y_AXIS));

        // Tải và hiển thị logo bên trái
        ImageIcon iconLogo = null;
        try {
            iconLogo = new ImageIcon(getClass().getResource("/img/logo.png"));
        } catch (Exception e) {
            System.out.println("Không tìm thấy file ảnh logo tại /img/logo.png");
        }
        
        JLabel lblLogo;
        if (iconLogo != null && iconLogo.getImage() != null) {
            Image anhDaChinhSua = iconLogo.getImage().getScaledInstance(350, 250, Image.SCALE_SMOOTH);
            lblLogo = new JLabel(new ImageIcon(anhDaChinhSua));
        } else {
            lblLogo = new JLabel("[LOGO NIGHT INVADER]");
            lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTieuDePhu = new JLabel("HỆ THỐNG QUẢN LÝ GARA Ô TÔ");
        lblTieuDePhu.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDePhu.setForeground(new Color(60, 60, 60));
        lblTieuDePhu.setAlignmentX(Component.CENTER_ALIGNMENT);

        noiDungTrai.add(lblLogo);
        noiDungTrai.add(Box.createRigidArea(new Dimension(0, 20)));
        noiDungTrai.add(lblTieuDePhu);

        panelTrai.add(noiDungTrai);

        // ==================== PHẦN BÊN PHẢI: FORM ĐĂNG NHẬP ====================
        JPanel panelPhai = new JPanel();
        panelPhai.setBackground(new Color(250, 235, 235)); // Màu nền hồng nhạt nhẹ nhàng
        panelPhai.setLayout(new GridBagLayout());

        // Khung chứa form đăng nhập
        JPanel khungForm = new JPanel();
        khungForm.setBackground(new Color(253, 242, 242));
        khungForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 120, 120), 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        khungForm.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề form: ĐĂNG NHẬP
        JLabel lblTieuDeDangNhap = new JLabel("ĐĂNG NHẬP", JLabel.CENTER);
        lblTieuDeDangNhap.setFont(new Font("Arial", Font.BOLD, 26));
        lblTieuDeDangNhap.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        khungForm.add(lblTieuDeDangNhap, gbc);

        // Nhãn Tên đăng nhập
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        khungForm.add(lblUser, gbc);

        // --- Ô NHẬP TÊN ĐĂNG NHẬP KÈM ICON ---
        JPanel panelUserWrapper = new JPanel(new BorderLayout());
        panelUserWrapper.setBackground(Color.WHITE);
        panelUserWrapper.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        
        JLabel lblIconUser = new JLabel();
        try {
            ImageIcon iconUser = new ImageIcon(getClass().getResource("/img/User_box_light.png"));
            if (iconUser.getImage() != null) {
                Image scaledUser = iconUser.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                lblIconUser.setIcon(new ImageIcon(scaledUser));
            }
        } catch (Exception e) {
        }
        lblIconUser.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        
        txtTenDangNhap = new JTextField(20);
        txtTenDangNhap.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTenDangNhap.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtTenDangNhap.setPreferredSize(new Dimension(280, 38));
        
        panelUserWrapper.add(lblIconUser, BorderLayout.WEST);
        panelUserWrapper.add(txtTenDangNhap, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        khungForm.add(panelUserWrapper, gbc);

        // Nhãn Mật khẩu
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        khungForm.add(lblPass, gbc);

        // --- Ô NHẬP MẬT KHẨU KÈM ICON ---
        JPanel panelPassWrapper = new JPanel(new BorderLayout());
        panelPassWrapper.setBackground(Color.WHITE);
        panelPassWrapper.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        
        JLabel lblIconPass = new JLabel();
        try {
        	ImageIcon iconLock = new ImageIcon(getClass().getResource("/img/icon_mk.png"));
        	if (iconLock.getImage() != null) {
                Image scaledLock = iconLock.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                lblIconPass.setIcon(new ImageIcon(scaledLock));
            }
        } catch (Exception e) {
        }
        lblIconPass.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        
        txtMatKhau = new JPasswordField(20);
        txtMatKhau.setFont(new Font("Arial", Font.PLAIN, 14));
        txtMatKhau.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtMatKhau.setPreferredSize(new Dimension(280, 38));
        
        panelPassWrapper.add(lblIconPass, BorderLayout.WEST);
        panelPassWrapper.add(txtMatKhau, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        khungForm.add(panelPassWrapper, gbc);

        // Dòng Quên mật khẩu?
        lblQuenMatKhau = new JLabel("Quên mật khẩu?");
        lblQuenMatKhau.setFont(new Font("Arial", Font.ITALIC, 12));
        lblQuenMatKhau.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        khungForm.add(lblQuenMatKhau, gbc);

     // Nút Đăng Nhập
        btnDangNhap = new JButton("Đăng Nhập");
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnDangNhap.setForeground(new Color(20, 110, 60));     
        btnDangNhap.setBackground(new Color(225, 247, 235));   
        btnDangNhap.setFocusPainted(false);
        
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNut.setOpaque(false);
        panelNut.add(btnDangNhap);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        khungForm.add(panelNut, gbc);

        panelPhai.add(khungForm);

        // Thêm các panel vào Frame
        add(panelTrai, BorderLayout.WEST);
        add(panelPhai, BorderLayout.CENTER);

        // Xử lý sự kiện đăng nhập mẫu
        btnDangNhap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tenDangNhap = txtTenDangNhap.getText().trim();
                String matKhau = new String(txtMatKhau.getPassword());

                if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
                    JOptionPane.showMessageDialog(GaraDangNhap.this,
                            "Vui lòng điền đầy đủ tên đăng nhập và mật khẩu!",
                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(GaraDangNhap.this,
                            "Đăng nhập thành công vào hệ thống!\nXin chào nhân viên: " + tenDangNhap,
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GaraDangNhap().setVisible(true);
            }
        });
    }
}