package service;

import java.sql.SQLException;
import java.util.List;

import dao.LichLamViecCuaToiDao;
import entity.NhanVien;

public class LichLamViecCuaToiService {

    private final LichLamViecCuaToiDao dao;
    private final String maNhanVien;
    private final String tenNhanVien;

    public LichLamViecCuaToiService(NhanVien nguoiDangNhap) {
        if (nguoiDangNhap == null
                || nguoiDangNhap.getMaNhanVien() == null
                || nguoiDangNhap.getMaNhanVien().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Chưa xác định nhân viên đăng nhập.");
        }

        maNhanVien = nguoiDangNhap.getMaNhanVien().trim();
        tenNhanVien = nguoiDangNhap.getHoTen();
        dao = new LichLamViecCuaToiDao();
    }

    public String getThongTinNhanVien() {
        return maNhanVien + " - " + tenNhanVien;
    }

    public List<Object[]> layCaCuaToi() throws SQLException {
        return dao.layCaTheoNhanVien(maNhanVien);
    }

    public List<Object[]> layPhieuCuaToi() throws SQLException {
        return dao.layPhieuTheoKyThuatVien(maNhanVien);
    }
}