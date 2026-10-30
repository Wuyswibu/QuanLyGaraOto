package service;

import java.sql.SQLException;

import dao.NhanVienDao;
import dao.TaiKhoanDao;
import entity.NhanVien;
import entity.TaiKhoan;

public class DangNhapService {

    private final TaiKhoanDao taiKhoanDao = new TaiKhoanDao();
    private final NhanVienDao nhanVienDao = new NhanVienDao();

    public NhanVien dangNhap(String tenDangNhap, String matKhau)
            throws SQLException {

        String ten = tenDangNhap == null ? "" : tenDangNhap.trim();

        if (ten.isEmpty() || matKhau == null || matKhau.isEmpty()) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu."
            );
        }

        TaiKhoan taiKhoan = taiKhoanDao.timTheoTenDangNhap(ten);

        // So sánh đúng mật khẩu, không trim và không bỏ qua hoa/thường.
        if (taiKhoan == null
                || !matKhau.equals(taiKhoan.getMatKhau())) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập hoặc mật khẩu không đúng."
            );
        }

        if (!TaiKhoan.HOAT_DONG.equals(taiKhoan.getTrangThai())) {
            throw new IllegalArgumentException("Tài khoản đã bị khóa.");
        }

        NhanVien nhanVien = nhanVienDao.timTheoMa(
                taiKhoan.getMaNhanVien()
        );

        if (nhanVien == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy nhân viên liên kết với tài khoản."
            );
        }

        if (!"Đang làm".equals(nhanVien.getTrangThai())) {
            throw new IllegalArgumentException(
                    "Nhân viên đã ngừng làm việc, không thể đăng nhập."
            );
        }

        return nhanVien;
    }
}