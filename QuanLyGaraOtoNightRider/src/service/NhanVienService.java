package service;

import connectDB.ConnectDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NhanVienService {

    // --- KIỂM TRA RÀNG BUỘC CHO TỪNG TRƯỜNG DỮ LIỆU ---
    public String kiemTraHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return "(*) Họ và tên không được để trống.";
        }
        return "";
    }

    public String kiemTraSoDienThoai(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) {
            return "(*) Số điện thoại không được để trống.";
        }
        if (!sdt.trim().matches("^0\\d{9}$")) {
            return "(*) SĐT phải bắt đầu từ 0 và đủ 10 số.";
        }
        return "";
    }

    public String kiemTraNgayVaoLam(String ngayVaoLam) {
        if (ngayVaoLam == null || ngayVaoLam.trim().isEmpty()) {
            return "(*) Ngày vào làm không được để trống.";
        }
        if (!ngayVaoLam.trim().matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")) {
            return "(*) Định dạng ngày phải là DD/MM/YYYY.";
        }
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(ngayVaoLam.trim(), dtf);
        } catch (Exception e) {
            return "(*) Ngày nhập vào không hợp lệ trong thực tế.";
        }
        return "";
    }

    // Kiểm tra tổng hợp trước khi thêm hoặc sửa
    public boolean kiemTraHopLeToanBo(String hoTen, String sdt, String ngayVaoLam) {
        return kiemTraHoTen(hoTen).isEmpty() 
            && kiemTraSoDienThoai(sdt).isEmpty() 
            && kiemTraNgayVaoLam(ngayVaoLam).isEmpty();
    }

    private String layMaPhanQuyen(String vaiTro) throws SQLException {
        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();
        String sql = "SELECT maPhanQuyen FROM dbo.PhanQuyen WHERE tenChuc = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vaiTro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("maPhanQuyen");
            }
        }
        return "PQ002";
    }

    public String taoMaNhanVienMoi() throws SQLException {
        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();
        String sql = "SELECT MAX(TRY_CONVERT(INT, SUBSTRING(maNhanVien, 3, 10))) AS maxMa FROM dbo.NhanVien WHERE maNhanVien LIKE 'NV%'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int max = rs.getInt("maxMa");
                if (!rs.wasNull()) return String.format("NV%03d", max + 1);
            }
        }
        return "NV001";
    }

    public boolean themNhanVien(String hoTen, String sdt, String vaiTro, String ngayVaoLamStr, String trangThai) throws Exception {
        if (!kiemTraHopLeToanBo(hoTen, sdt, ngayVaoLamStr)) {
            throw new Exception("Vui lòng kiểm tra lại các thông tin cảnh báo màu đỏ trên giao diện!");
        }

        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();

        String maNV = taoMaNhanVienMoi();
        String maPQ = layMaPhanQuyen(vaiTro);
        LocalDate ngayVaoLam = LocalDate.parse(ngayVaoLamStr.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String sql = "INSERT INTO dbo.NhanVien (maNhanVien, maPhanQuyen, hoTen, soDienThoai, ngayVaoLam, trangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setString(2, maPQ);
            ps.setString(3, hoTen.trim());
            ps.setString(4, sdt.trim());
            ps.setDate(5, Date.valueOf(ngayVaoLam));
            ps.setString(6, trangThai);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatNhanVien(String maNV, String hoTen, String sdt, String vaiTro, String ngayVaoLamStr, String trangThai) throws Exception {
        if (!kiemTraHopLeToanBo(hoTen, sdt, ngayVaoLamStr)) {
            throw new Exception("Vui lòng kiểm tra lại các thông tin cảnh báo màu đỏ trên giao diện!");
        }

        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();

        String maPQ = layMaPhanQuyen(vaiTro);
        LocalDate ngayVaoLam = LocalDate.parse(ngayVaoLamStr.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String sql = "UPDATE dbo.NhanVien SET hoTen = ?, soDienThoai = ?, maPhanQuyen = ?, ngayVaoLam = ?, trangThai = ? WHERE maNhanVien = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hoTen.trim());
            ps.setString(2, sdt.trim());
            ps.setString(3, maPQ);
            ps.setDate(4, Date.valueOf(ngayVaoLam));
            ps.setString(5, trangThai);
            ps.setString(6, maNV);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean khoaTaiKhoan(String maNV) throws SQLException {
        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();
        String sql = "UPDATE dbo.NhanVien SET trangThai = N'Đã nghỉ / Khóa' WHERE maNhanVien = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Object[]> locVaTimKiemNhanVien(String tuKhoa, String vaiTroLoc) throws SQLException {
        List<Object[]> danhSach = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null || conn.isClosed()) conn = ConnectDB.getInstance().connect();

        StringBuilder sql = new StringBuilder(
            "SELECT nv.maNhanVien, nv.hoTen, nv.soDienThoai, pq.tenChuc, nv.ngayVaoLam, nv.trangThai " +
            "FROM dbo.NhanVien nv LEFT JOIN dbo.PhanQuyen pq ON nv.maPhanQuyen = pq.maPhanQuyen WHERE 1=1 "
        );

        if (tuKhoa != null && !tuKhoa.trim().isEmpty() && !tuKhoa.equals(" Tìm kiếm nhân viên...")) {
            sql.append("AND (nv.maNhanVien LIKE ? OR nv.hoTen LIKE ? OR nv.soDienThoai LIKE ?) ");
        }
        if (vaiTroLoc != null && !vaiTroLoc.equals("Tất cả vai trò")) {
            sql.append("AND pq.tenChuc = ? ");
        }
        sql.append("ORDER BY nv.maNhanVien ASC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (tuKhoa != null && !tuKhoa.trim().isEmpty() && !tuKhoa.equals(" Tìm kiếm nhân viên...")) {
                String kw = "%" + tuKhoa.trim() + "%";
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
            }
            if (vaiTroLoc != null && !vaiTroLoc.equals("Tất cả vai trò")) {
                ps.setString(paramIndex++, vaiTroLoc);
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String vaiTro = rs.getString("tenChuc") != null ? rs.getString("tenChuc") : "Chưa phân quyền";
                    Date ngayVL = rs.getDate("ngayVaoLam");
                    String ngayVaoStr = (ngayVL != null) ? ngayVL.toLocalDate().format(dtf) : "";

                    danhSach.add(new Object[]{
                        rs.getString("maNhanVien"), rs.getString("hoTen"), rs.getString("soDienThoai"),
                        vaiTro, ngayVaoStr, rs.getString("trangThai")
                    });
                }
            }
        }
        return danhSach;
    }
}