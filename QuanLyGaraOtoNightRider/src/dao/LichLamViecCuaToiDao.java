package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;

public class LichLamViecCuaToiDao {

    private Connection ketNoi() throws SQLException {
        Connection c = ConnectDB.getConnection();

        if (c == null || c.isClosed()) {
            throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
        }

        return c;
    }

    public List<Object[]> layCaTheoNhanVien(String maNhanVien)
            throws SQLException {

        String sql =
                "SELECT pc.ngayLamViec, ca.tenCa, "
              + "ca.gioBatDau, ca.gioKetThuc, "
              + "pc.trangThai, pc.ghiChu "
              + "FROM dbo.PhanCongCa pc "
              + "JOIN dbo.CaLamViec ca ON ca.maCa = pc.maCa "
              + "WHERE pc.maNhanVien = ? "
              + "ORDER BY pc.ngayLamViec DESC, ca.gioBatDau";

        List<Object[]> ds = new ArrayList<>();
        Connection c = ketNoi();

        synchronized (c) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, maNhanVien);
                ps.setQueryTimeout(20);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ds.add(new Object[] {
                            rs.getDate("ngayLamViec"),
                            rs.getString("tenCa"),
                            rs.getTime("gioBatDau"),
                            rs.getTime("gioKetThuc"),
                            rs.getString("trangThai"),
                            rs.getString("ghiChu")
                        });
                    }
                }
            }
        }

        return ds;
    }

    public List<Object[]> layPhieuTheoKyThuatVien(String maNhanVien)
            throws SQLException {

        String sql =
                "SELECT p.maPhieuSuaChua, p.maTiepNhan, "
              + "xe.bienSo, p.ngayLap, p.loaiPhieu, p.trangThai "
              + "FROM dbo.PhieuSuaChua p "
              + "JOIN dbo.PhieuTiepNhan tn "
              + "ON tn.maTiepNhan = p.maTiepNhan "
              + "JOIN dbo.PhuongTien xe "
              + "ON xe.maPhuongTien = tn.maPhuongTien "
              + "WHERE p.maKyThuatVien = ? "
              + "ORDER BY p.ngayLap DESC, p.maPhieuSuaChua DESC";

        List<Object[]> ds = new ArrayList<>();
        Connection c = ketNoi();

        synchronized (c) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, maNhanVien);
                ps.setQueryTimeout(20);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ds.add(new Object[] {
                            rs.getString("maPhieuSuaChua"),
                            rs.getString("maTiepNhan"),
                            rs.getString("bienSo"),
                            rs.getTimestamp("ngayLap"),
                            rs.getString("loaiPhieu"),
                            rs.getString("trangThai")
                        });
                    }
                }
            }
        }

        return ds;
    }
}