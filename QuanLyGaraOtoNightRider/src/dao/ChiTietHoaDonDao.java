package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.PhuTung;

public class ChiTietHoaDonDao {

	private Connection ketNoi() throws SQLException {
		Connection conn = ConnectDB.getConnection();

		if (conn == null || conn.isClosed()) {
			throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
		}

		return conn;
	}

	private ChiTietHoaDon docChiTiet(ResultSet rs) throws SQLException {
		HoaDon hoaDon = new HoaDon();
		hoaDon.setMaHoaDon(rs.getString("maHoaDon"));

		PhuTung phuTung = new PhuTung();
		phuTung.setMaPhuTung(rs.getString("maPhuTung"));

		return new ChiTietHoaDon(hoaDon, phuTung, rs.getInt("soLuong"), rs.getBigDecimal("donGia"),
				rs.getBigDecimal("giamGia"));
	}

	public ArrayList<ChiTietHoaDon> layDanhSachChiTiet() throws SQLException {
		ArrayList<ChiTietHoaDon> danhSach = new ArrayList<>();

		String sql = "SELECT maHoaDon, maPhuTung, soLuong, " + "donGia, giamGia " + "FROM dbo.CTHoaDon "
				+ "ORDER BY maHoaDon, maPhuTung";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				danhSach.add(docChiTiet(rs));
			}
		}

		return danhSach;
	}

	public ArrayList<ChiTietHoaDon> timTheoMaHoaDon(String maHoaDon) throws SQLException {
		ArrayList<ChiTietHoaDon> danhSach = new ArrayList<>();

		String sql = "SELECT maHoaDon, maPhuTung, soLuong, " + "donGia, giamGia " + "FROM dbo.CTHoaDon "
				+ "WHERE maHoaDon = ? ORDER BY maPhuTung";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setString(1, maHoaDon);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					danhSach.add(docChiTiet(rs));
				}
			}
		}

		return danhSach;
	}

	public ChiTietHoaDon timTheoKhoa(String maHoaDon, String maPhuTung) throws SQLException {
		String sql = "SELECT maHoaDon, maPhuTung, soLuong, " + "donGia, giamGia " + "FROM dbo.CTHoaDon "
				+ "WHERE maHoaDon = ? AND maPhuTung = ?";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setString(1, maHoaDon);
			ps.setString(2, maPhuTung);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return docChiTiet(rs);
				}
			}
		}

		return null;
	}

	public boolean themChiTiet(ChiTietHoaDon chiTiet) throws SQLException {
		String sql = "INSERT INTO dbo.CTHoaDon " + "(maHoaDon, maPhuTung, soLuong, donGia, giamGia) "
				+ "SELECT ?, ?, ?, ?, ? " + "WHERE EXISTS (SELECT 1 FROM dbo.HoaDon "
				+ "WHERE maHoaDon = ? AND trangThai = ?)";
		String maHoaDon = chiTiet.getMaHoaDon().getMaHoaDon();
		String maPhuTung = chiTiet.getMaPhuTung().getMaPhuTung();

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {

			ps.setString(1, maHoaDon);
			ps.setString(2, maPhuTung);
			ps.setInt(3, chiTiet.getSoLuong());
			ps.setBigDecimal(4, chiTiet.getDonGia());
			ps.setBigDecimal(5, chiTiet.getGiamGia());
			ps.setString(6, chiTiet.getMaHoaDon().getMaHoaDon());
			ps.setNString(7, entity.HoaDon.CHUA_THANH_TOAN);

			return ps.executeUpdate() > 0;
		}
	}

	public boolean suaChiTiet(ChiTietHoaDon chiTiet) throws SQLException {
		String sql = "UPDATE c " + "SET c.soLuong = ?, c.donGia = ?, c.giamGia = ? " + "FROM dbo.CTHoaDon c "
				+ "JOIN dbo.HoaDon h ON c.maHoaDon = h.maHoaDon " + "WHERE c.maHoaDon = ? AND c.maPhuTung = ? "
				+ "AND h.trangThai = ?";
		String maHoaDon = chiTiet.getMaHoaDon().getMaHoaDon();
		String maPhuTung = chiTiet.getMaPhuTung().getMaPhuTung();
		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setInt(1, chiTiet.getSoLuong());
			ps.setBigDecimal(2, chiTiet.getDonGia());
			ps.setBigDecimal(3, chiTiet.getGiamGia());
			ps.setString(4, maHoaDon);
			ps.setString(5, maPhuTung);
			ps.setNString(6, entity.HoaDon.CHUA_THANH_TOAN);

			return ps.executeUpdate() > 0;
		}
	}

	public boolean xoaChiTiet(String maHoaDon, String maPhuTung) throws SQLException {
		String sql = "DELETE c " + "FROM dbo.CTHoaDon c " + "JOIN dbo.HoaDon h ON c.maHoaDon = h.maHoaDon "
				+ "WHERE c.maHoaDon = ? AND c.maPhuTung = ? " + "AND h.trangThai = ?";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setString(1, maHoaDon);
			ps.setString(2, maPhuTung);
			ps.setNString(3, entity.HoaDon.CHUA_THANH_TOAN);

			return ps.executeUpdate() > 0;
		}
	}
}