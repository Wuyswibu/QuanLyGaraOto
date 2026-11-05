package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import connectDB.ConnectDB;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuSuaChua;
import entity.Xe;

public class HoaDonDao {

	private Connection ketNoi() throws SQLException {
		Connection conn = ConnectDB.getConnection();

		if (conn == null || conn.isClosed()) {
			throw new SQLException("Chưa kết nối cơ sở dữ liệu.");
		}

		return conn;
	}

	private HoaDon docHoaDon(ResultSet rs) throws SQLException {
		Timestamp ngayLap = rs.getTimestamp("ngayLap");
		PhieuSuaChua phieuSuaChua = new PhieuSuaChua();
		phieuSuaChua.setMaPhieuSuaChua(rs.getString("maPhieuSuaChua"));
		KhachHang khachHang = new KhachHang();
		khachHang.setMaKhachHang(rs.getString("maKhachHang"));

		Xe xe = new Xe();
		xe.setMaPhuongTien(rs.getString("maPhuongTien"));
		NhanVien nhanVien = new NhanVien();
		nhanVien.setMaNhanVien(rs.getString("maNhanVien"));

		HoaDon hoaDon = new HoaDon(rs.getString("maHoaDon"), phieuSuaChua, khachHang, xe, nhanVien,
				ngayLap == null ? null : ngayLap.toLocalDateTime(), rs.getBigDecimal("vat"), rs.getString("trangThai"));

		// Gán VAT vì constructor HoaDon hiện tại
		hoaDon.setVat(rs.getBigDecimal("vat"));

		return hoaDon;
	}

	public ArrayList<HoaDon> layDanhSachHoaDon() throws SQLException {
		ArrayList<HoaDon> danhSach = new ArrayList<>();

		String sql = "SELECT * FROM dbo.HoaDon " + "ORDER BY ngayLap DESC, maHoaDon DESC";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				danhSach.add(docHoaDon(rs));
			}
		}

		return danhSach;
	}

	public HoaDon timTheoMa(String maHoaDon) throws SQLException {
		String sql = "SELECT * FROM dbo.HoaDon WHERE maHoaDon = ?";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setString(1, maHoaDon);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return docHoaDon(rs);
				}
			}
		}

		return null;
	}

	public HoaDon timTheoMaPhieuSuaChua(String maPhieuSuaChua) throws SQLException {
		String sql = "SELECT * FROM dbo.HoaDon " + "WHERE maPhieuSuaChua = ?";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setString(1, maPhieuSuaChua);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return docHoaDon(rs);
				}
			}
		}

		return null;
	}

	public ArrayList<HoaDon> timTheoMaKhachHang(String maKhachHang) throws SQLException {
		ArrayList<HoaDon> danhSach = new ArrayList<>();

		String sql = "SELECT * FROM dbo.HoaDon " + "WHERE maKhachHang = ? " + "ORDER BY ngayLap DESC, maHoaDon DESC";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setString(1, maKhachHang);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					danhSach.add(docHoaDon(rs));
				}
			}
		}

		return danhSach;
	}

	public ArrayList<HoaDon> timTheoMaPhuongTien(String maPhuongTien) throws SQLException {
		ArrayList<HoaDon> danhSach = new ArrayList<>();

		String sql = "SELECT * FROM dbo.HoaDon " + "WHERE maPhuongTien = ? " + "ORDER BY ngayLap DESC, maHoaDon DESC";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setString(1, maPhuongTien);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					danhSach.add(docHoaDon(rs));
				}
			}
		}

		return danhSach;
	}
	

	public ArrayList<HoaDon> timTheoTrangThai(String trangThai) throws SQLException {
		ArrayList<HoaDon> danhSach = new ArrayList<>();

		String sql = "SELECT * FROM dbo.HoaDon " + "WHERE trangThai = ? " + "ORDER BY ngayLap DESC, maHoaDon DESC";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setNString(1, trangThai);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					danhSach.add(docHoaDon(rs));
				}
			}
		}

		return danhSach;
	}

	public ArrayList<HoaDon> timKiem(String tuKhoa) throws SQLException {
		ArrayList<HoaDon> danhSach = new ArrayList<>();

		String sql = "SELECT h.* " + "FROM dbo.HoaDon h " + "JOIN dbo.KhachHang k "
				+ "ON h.maKhachHang = k.maKhachHang " + "JOIN dbo.PhuongTien p " + "ON h.maPhuongTien = p.maPhuongTien "
				+ "WHERE h.maHoaDon LIKE ? " + "OR h.maPhieuSuaChua LIKE ? " + "OR k.hoTen LIKE ? "
				+ "OR k.soDienThoai LIKE ? " + "OR p.bienSo LIKE ? " + "ORDER BY h.ngayLap DESC, h.maHoaDon DESC";

		String noiDung = "%" + tuKhoa.trim() + "%";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setString(1, noiDung);
			ps.setString(2, noiDung);
			ps.setNString(3, noiDung);
			ps.setString(4, noiDung);
			ps.setString(5, noiDung);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					danhSach.add(docHoaDon(rs));
				}
			}
		}

		return danhSach;
	}

	
	
	public String[] layThongTinLienKetTuPhieuSuaChua(String maPSC) throws SQLException {
        if (maPSC == null || maPSC.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT p.maKhachHang, p.maPhuongTien, t.maNhanVien "
                   + "FROM dbo.PhieuSuaChua s "
                   + "JOIN dbo.PhieuTiepNhan t ON s.maTiepNhan = t.maTiepNhan "
                   + "JOIN dbo.PhuongTien p ON t.maPhuongTien = p.maPhuongTien "
                   + "WHERE s.maPhieuSuaChua = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPSC.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                        rs.getString("maKhachHang"),
                        rs.getString("maPhuongTien"),
                        rs.getString("maNhanVien")
                    };
                }
            }
        }
        return null;
    }
	public String layTenPhuTungTuDB(String maPhuTung) throws SQLException {
        if (maPhuTung == null || maPhuTung.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT tenPhuTung FROM dbo.PhuTung WHERE maPhuTung = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPhuTung.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tenPhuTung");
                }
            }
        }
        return null;
    }
	
	
	public String taoMaHoaDon() throws SQLException {
		String sql = "SELECT MAX(TRY_CONVERT(BIGINT, " + "SUBSTRING(maHoaDon, 3, 28))) AS soLonNhat "
				+ "FROM dbo.HoaDon WHERE maHoaDon LIKE 'HD%'";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				long soMoi = rs.getLong("soLonNhat") + 1;

				return String.format(java.util.Locale.ROOT, "HD%04d", soMoi);
			}
		}

		return "HD0001";
	}

	public boolean themHoaDon(HoaDon hoaDon) throws SQLException {
		String sql = "INSERT INTO dbo.HoaDon " + "(maHoaDon, maPhieuSuaChua, maKhachHang, "
				+ "maPhuongTien, maNhanVien, ngayLap, vat, trangThai) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		String maPhieuSuaChua = hoaDon.getMaPhieuSuaChua().getMaPhieuSuaChua();
		String maKH = hoaDon.getMaKhachHang().getMaKhachHang();
		String maPhuongTien = hoaDon.getMaPhuongTien().getMaPhuongTien();
		String maNhanVien = hoaDon.getMaNhanVien().getMaNhanVien();
		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setString(1, hoaDon.getMaHoaDon());
			ps.setString(2, maPhieuSuaChua);
			ps.setString(3, maKH);
			ps.setString(4, maPhuongTien);
			ps.setString(5, maNhanVien);
			ps.setTimestamp(6, Timestamp.valueOf(hoaDon.getNgayLap()));
			ps.setBigDecimal(7, hoaDon.getVat());
			ps.setNString(8, hoaDon.getTrangThai());

			return ps.executeUpdate() > 0;
		}
	}

	public boolean suaHoaDon(HoaDon hoaDon) throws SQLException {
		String sql = "UPDATE dbo.HoaDon " + "SET maPhieuSuaChua = ?, maKhachHang = ?, "
				+ "maPhuongTien = ?, maNhanVien = ?, " + "ngayLap = ?, vat = ? "
				+ "WHERE maHoaDon = ? AND trangThai = ?";
		String maPhieuSuaChua = hoaDon.getMaPhieuSuaChua().getMaPhieuSuaChua();
		String maKH = hoaDon.getMaKhachHang().getMaKhachHang();
		String maPhuongTien = hoaDon.getMaPhuongTien().getMaPhuongTien();
		String maNhanVien = hoaDon.getMaNhanVien().getMaNhanVien();
		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setString(1, maPhieuSuaChua);
			ps.setString(2, maKH);
			ps.setString(3, maPhuongTien);
			ps.setString(4, maNhanVien);
			ps.setTimestamp(5, Timestamp.valueOf(hoaDon.getNgayLap()));
			ps.setBigDecimal(6, hoaDon.getVat());
			ps.setString(7, hoaDon.getMaHoaDon());
			ps.setNString(8, HoaDon.CHUA_THANH_TOAN);

			return ps.executeUpdate() > 0;
		}
	}

	public boolean capNhatTrangThai(String maHoaDon, String trangThaiCu, String trangThaiMoi) throws SQLException {
		if (!HoaDon.CHUA_THANH_TOAN.equals(trangThaiMoi) && !HoaDon.DA_THANH_TOAN.equals(trangThaiMoi)
				&& !HoaDon.DA_HUY.equals(trangThaiMoi)) {
			throw new IllegalArgumentException("Trạng thái hóa đơn không hợp lệ.");
		}

		String sql = "UPDATE dbo.HoaDon " + "SET trangThai = ? " + "WHERE maHoaDon = ? AND trangThai = ?";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
			ps.setNString(1, trangThaiMoi);
			ps.setString(2, maHoaDon);
			ps.setNString(3, trangThaiCu);

			return ps.executeUpdate() > 0;
		}
	}

	public java.util.Map<String, String> layTrangThaiBanGiao() throws SQLException {

		java.util.Map<String, String> ketQua = new java.util.HashMap<>();

		String sql = "SELECT h.maHoaDon, t.trangThai AS trangThaiTiepNhan " + "FROM dbo.HoaDon h "
				+ "LEFT JOIN dbo.PhieuSuaChua p " + "ON p.maPhieuSuaChua = h.maPhieuSuaChua "
				+ "LEFT JOIN dbo.PhieuTiepNhan t " + "ON t.maTiepNhan = p.maTiepNhan";

		try (PreparedStatement ps = ketNoi().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String trangThai = rs.getString("trangThaiTiepNhan");
				String hienThi;

				if ("Đã giao xe".equals(trangThai)) {
					hienThi = "Đã giao xe";
				} else if ("Đã hủy".equals(trangThai)) {
					hienThi = "Tiếp nhận đã hủy";
				} else if (trangThai == null) {
					hienThi = "Thiếu phiếu tiếp nhận";
				} else {
					hienThi = "Chưa giao xe";
				}

				ketQua.put(rs.getString("maHoaDon"), hienThi);
			}
		}

		return ketQua;
	}
	public boolean banGiaoXe(String maHoaDon, String maNhanVien) throws SQLException {
	    String sql = "UPDATE tn SET "
	            + "tn.trangThai = N'Đã giao xe', "
	            + "tn.ngayGioBanGiao = SYSDATETIME(), "
	            + "tn.maNhanVienBanGiao = ? "
	            + "FROM dbo.PhieuTiepNhan tn "
	            + "JOIN dbo.PhieuSuaChua sc ON sc.maTiepNhan = tn.maTiepNhan "
	            + "JOIN dbo.HoaDon hd ON hd.maPhieuSuaChua = sc.maPhieuSuaChua "
	            + "WHERE hd.maHoaDon = ? "
	            + "AND hd.trangThai = N'Đã thanh toán' "
	            + "AND sc.trangThai = N'Đã hoàn thành' "
	            + "AND tn.trangThai NOT IN (N'Đã giao xe', N'Đã hủy') "
	            + "AND tn.ngayGioBanGiao IS NULL";

	    try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
	        ps.setString(1, maNhanVien);
	        ps.setString(2, maHoaDon);

	        return ps.executeUpdate() > 0;
	    }
	}
	public BigDecimal tinhTienPhuTungDaXuat(String maPSC) throws SQLException {
        if (maPSC == null || maPSC.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String sql = "SELECT ISNULL(SUM(CAST(ct.soLuong AS DECIMAL(18,0)) * ct.donGia), 0) AS tongPhuTung "
                   + "FROM dbo.PhieuXuatKho x "
                   + "JOIN dbo.ChiTietPhieuXuat ct ON ct.maPhieuXuat = x.maPhieuXuat "
                   + "WHERE x.maPhieuSuaChua = ? "
                   + "AND x.trangThai IN (N'Đã xuất kho', N'Đã xác nhận')";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPSC.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal tong = rs.getBigDecimal("tongPhuTung");
                    return tong != null ? tong : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
	
	public Map<String, BigDecimal> layTatCaVatTuDB() throws SQLException {
        Map<String, BigDecimal> map = new HashMap<>();
        String sql = "SELECT maHoaDon, vat FROM dbo.HoaDon";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BigDecimal vat = rs.getBigDecimal("vat");
                map.put(rs.getString("maHoaDon"), vat != null ? vat : BigDecimal.ZERO);
            }
        }
        return map;
    }
	public ArrayList<Object[]> layDanhSachPhuTungXuatKho(String maPSC) throws SQLException {
		ArrayList<Object[]> list = new ArrayList<>();
		if (maPSC == null || maPSC.trim().isEmpty()) return list;

		String sql = "SELECT p.tenPhuTung, ct.soLuong, ct.donGia FROM dbo.PhieuXuatKho x "
				+ "JOIN dbo.ChiTietPhieuXuat ct ON ct.maPhieuXuat = x.maPhieuXuat "
				+ "JOIN dbo.PhuTung p ON p.maPhuTung = ct.maPhuTung "
				+ "WHERE x.maPhieuSuaChua = ? AND x.trangThai IN (N'Đã xuất kho', N'Đã xác nhận') "
				+ "ORDER BY x.maPhieuXuat, p.maPhuTung";

		try (Connection conn = ketNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, maPSC.trim());
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(new Object[] {
						rs.getString("tenPhuTung"),
						rs.getInt("soLuong"),
						rs.getBigDecimal("donGia")
					});
				}
			}
		}
		return list;
	}

	
	public BigDecimal tinhTienCongThucTe(String maPSC) throws SQLException {
        if (maPSC == null || maPSC.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String sql = "SELECT ISNULL(SUM(chiPhiThucTe), 0) AS tongCong "
                   + "FROM dbo.HangMucDichVu "
                   + "WHERE maPhieuSuaChua = ?";

        try (PreparedStatement ps = ketNoi().prepareStatement(sql)) {
            ps.setString(1, maPSC.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal tong = rs.getBigDecimal("tongCong");
                    return tong != null ? tong : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
}