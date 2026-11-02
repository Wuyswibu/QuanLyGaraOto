package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import connectDB.ConnectDB;
import entity.NhanVien;

public class PhanQuyenService {

    public static final String QUAN_LY = "PQ001";
    public static final String TIEP_NHAN = "PQ002";
    public static final String KY_THUAT = "PQ003";
    public static final String KHO = "PQ004";
    public static final String THU_NGAN = "PQ005";

    private final String maQuyen;

    public PhanQuyenService(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException(
                    "Chưa có nhân viên đăng nhập.");
        }

        this.maQuyen = nhanVien.getMaPhanQuyen();
    }

    public boolean laQuanLy() {
        return QUAN_LY.equals(maQuyen);
    }

    public boolean laTiepNhan() {
        return TIEP_NHAN.equals(maQuyen);
    }

    public boolean laKyThuatVien() {
        return KY_THUAT.equals(maQuyen);
    }

    public boolean laKho() {
        return KHO.equals(maQuyen);
    }

    public boolean laThuNgan() {
        return THU_NGAN.equals(maQuyen);
    }

    private boolean thuoc(String ma, String... danhSach) {
        return Arrays.asList(danhSach).contains(ma);
    }

    public boolean duocMoManHinh(String ma) {
    	if ("LICH_CUA_TOI".equals(ma)) {
    	        return true;
    	    }
        if (thuoc(ma, "TRANG_CHU", "TAI_KHOAN", "DOI_MAT_KHAU")) {
            return true;
        }

        // Danh sách màn hình đã được định nghĩa quyền.
        Set<String> manHinhHopLe = new HashSet<>(Arrays.asList(
                "KHACH_HANG", "XE", "DICH_VU",
                "TIEP_NHAN", "DS_PHIEU_TIEP_NHAN", "SUA_CHUA",
                "TINH_TRANG_SUA_XE", "LICH_SU",
                "PHU_TUNG", "NHAP_KHO", "XUAT_KHO",
                "LAP_HOA_DON", "NHAN_SU",
                "PHAN_CONG", "PHAN_CONG_CA",
                "THONG_KE_DOANH_THU", "THONG_KE_TON_KHO",
                "PHU_TUNG_BAN_CHAY", "PHU_TUNG_BAN_CHAM"
        ));

        if (!manHinhHopLe.contains(ma)) {
            return false;
        }

        if (laQuanLy()) {
            return true;
        }

        if (laTiepNhan()) {
            return thuoc(ma,
                    "KHACH_HANG", "XE",
                    "TIEP_NHAN", "DS_PHIEU_TIEP_NHAN",
                    "SUA_CHUA", "LICH_SU",
                    "LAP_HOA_DON");
        }

        if (laKyThuatVien()) {
            return thuoc(ma,
            		"DS_PHIEU_TIEP_NHAN",
                    "SUA_CHUA",
                    "TINH_TRANG_SUA_XE");
        }

        if (laKho()) {
            return thuoc(ma,
                    "PHU_TUNG", "NHAP_KHO",
                    "XUAT_KHO", "THONG_KE_TON_KHO");
        }

        if (laThuNgan()) {
            return thuoc(ma, "DS_PHIEU_TIEP_NHAN","LAP_HOA_DON", "LICH_SU");
        }

        return false;
    }

    public boolean duocThaoTac(String thaoTac) {
        switch (thaoTac) {
            case "LAP_HOA_DON":
            case "THANH_TOAN":
            case "HUY_HOA_DON":
            case "IN_HOA_DON":
                return laQuanLy() || laThuNgan();

            case "BAN_GIAO":
                return laQuanLy() || laTiepNhan() || laThuNgan();

            case "GUI_YEU_CAU_PHU_TUNG":
            case "CAP_NHAT_TIEN_DO":
                return laQuanLy() || laKyThuatVien();

            case "NHAP_KHO":
            case "XUAT_KHO":
                return laQuanLy() || laKho();

            case "PHAN_CONG_KTV":
            case "PHAN_CONG_CA":
            case "QUAN_LY_NHAN_VIEN":
            case "PHAN_QUYEN":
                return laQuanLy();

            default:
                return false;
        }
    }

    // Đọc vai trò thật trong SQL, không dựa vào lời chào giao diện.
    public static void napQuyenDangNhap(NhanVien nhanVien)
            throws SQLException {

        Connection c = ConnectDB.getConnection();

        if (c == null || c.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        String sql =
                "SELECT q.maPhanQuyen, q.tenChuc, q.choPhep "
                + "FROM dbo.NhanVien n "
                + "JOIN dbo.PhanQuyen q "
                + "ON q.maPhanQuyen = n.maPhanQuyen "
                + "WHERE n.maNhanVien = ?";

        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, nhanVien.getMaNhanVien());

            try (ResultSet r = p.executeQuery()) {
                if (!r.next() || !r.getBoolean("choPhep")) {
                    throw new IllegalArgumentException(
                            "Vai trò của nhân viên chưa được cấp quyền.");
                }

                String ma = r.getString("maPhanQuyen");

                if (!Arrays.asList(
                        QUAN_LY, TIEP_NHAN, KY_THUAT, KHO, THU_NGAN
                ).contains(ma)) {
                    throw new IllegalArgumentException(
                            "Vai trò chưa được cấu hình quyền: " + ma);
                }

                nhanVien.setMaPhanQuyen(ma);
                nhanVien.setTenChuc(r.getString("tenChuc"));
            }
        }
    }
}