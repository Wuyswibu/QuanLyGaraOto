package service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import connectDB.ConnectDB;
import dao.ChiTietHoaDonDao;
import dao.HoaDonDao;
import dao.KhachHangDao;
import dao.NhanVienDao;
import dao.PhieuSuaChuaDao;
import dao.XeDao;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuSuaChua;
import entity.Xe;

public class HoaDonService {
    private final HoaDonDao hoaDonDao;
    private final ChiTietHoaDonDao ctHoaDonDao;
    private final PhieuSuaChuaDao phieuSuaChuaDao;
    private final KhachHangDao khachHangDao;
    private final NhanVienDao nhanVienDao;
    private final XeDao xeDao;

    public HoaDonService() {
        this.hoaDonDao = new HoaDonDao();
        this.ctHoaDonDao = new ChiTietHoaDonDao();
        this.phieuSuaChuaDao = new PhieuSuaChuaDao();
        this.khachHangDao = new KhachHangDao();
        this.nhanVienDao = new NhanVienDao();
        this.xeDao = new XeDao();
    }

    // ==================== 1. CÁC HÀM LẤY DANH MỤC ====================
    public ArrayList<PhieuSuaChua> layDanhSachPhieuSuaChua() throws SQLException {
        return phieuSuaChuaDao.layDanhSachPhieuSuaChua();
    }

    public ArrayList<KhachHang> layDanhSachKhachHang() throws SQLException {
        return khachHangDao.layDanhSachKhachHang();
    }

    public ArrayList<Xe> layDanhSachXe() throws SQLException {
        return xeDao.layDanhSachXe();
    }

    public ArrayList<NhanVien> layDanhSachNhanVien() throws SQLException {
        return nhanVienDao.layDanhSachNhanVien();
    }

    public KhachHang timKhachHangTheoMa(String maKH) throws SQLException {
        return khachHangDao.timTheoMa(maKH);
    }

    public NhanVien timNhanVienTheoMa(String maNV) throws SQLException {
        return nhanVienDao.timTheoMa(maNV);
    }

    // ==================== 2. CÁC HÀM TRUY VẤN & TRA CỨU HÓA ĐƠN ====================
    public String taoMaHoaDonMoi() throws SQLException {
        return hoaDonDao.taoMaHoaDon();
    }

    public HoaDon timHoaDonTheoMa(String maHoaDon) throws SQLException {
        return hoaDonDao.timTheoMa(maHoaDon);
    }

    public HoaDon timHoaDonTheoMaPhieuSuaChua(String maPSC) throws SQLException {
        return hoaDonDao.timTheoMaPhieuSuaChua(maPSC);
    }

    public ArrayList<HoaDon> layDanhSachHoaDonTheoLoc(String trangThaiLoc) throws SQLException {
        if (trangThaiLoc == null || trangThaiLoc.equals("Tất cả")) {
            return hoaDonDao.layDanhSachHoaDon();
        }
        return hoaDonDao.timTheoTrangThai(trangThaiLoc);
    }

    public ArrayList<HoaDon> timKiemHoaDon(String tuKhoa) throws SQLException {
        return hoaDonDao.timKiem(tuKhoa);
    }

    /**
     * Tra cứu kết hợp cả từ khóa và bộ lọc trạng thái (dùng cho màn hình Tra Cứu Hóa Đơn)
     */
    public ArrayList<HoaDon> traCuuHoaDonKetHop(String tuKhoa, String trangThaiChon, String placeholder) throws SQLException {
        if (tuKhoa == null || tuKhoa.trim().isEmpty() || tuKhoa.equals(placeholder)) {
            return layDanhSachHoaDonTheoLoc(trangThaiChon);
        }
        ArrayList<HoaDon> dsTim = hoaDonDao.timKiem(tuKhoa.trim());
        if (trangThaiChon == null || trangThaiChon.equals("Tất cả")) {
            return dsTim;
        }
        ArrayList<HoaDon> dsKetQua = new ArrayList<>();
        for (HoaDon hd : dsTim) {
            if (trangThaiChon.equals(hd.getTrangThai())) {
                dsKetQua.add(hd);
            }
        }
        return dsKetQua;
    }

    public ArrayList<ChiTietHoaDon> layChiTietHoaDon(String maHoaDon) throws SQLException {
        return ctHoaDonDao.timTheoMaHoaDon(maHoaDon);
    }

    // ==================== 3. CÁC HÀM NGHIỆP VỤ LẬP / CẬP NHẬT / THANH TOÁN ====================
    public boolean themHoaDonMoi(HoaDon hdMoi) throws SQLException {
        return hoaDonDao.themHoaDon(hdMoi);
    }

    /**
     * Cập nhật thông tin hóa đơn và trạng thái hóa đơn đã tồn tại
     */
    public boolean capNhatHoaDon(HoaDon hdTonTai, String maPSC, String maKH, String maPT, String maNV, BigDecimal vat, String trangThaiMoi) throws SQLException {
        LocalDateTime ngayLapGoc = hdTonTai.getNgayLap() != null ? hdTonTai.getNgayLap() : LocalDateTime.now();
        HoaDon hdSua = new HoaDon(hdTonTai.getMaHoaDon(), maPSC, maKH, maPT, maNV, ngayLapGoc, vat, hdTonTai.getTrangThai());
        hdSua.setVat(vat);
        
        boolean kqSua = hoaDonDao.suaHoaDon(hdSua);
        boolean kqTrangThai = true;
        if (!hdTonTai.getTrangThai().equals(trangThaiMoi)) {
            kqTrangThai = hoaDonDao.capNhatTrangThai(hdTonTai.getMaHoaDon(), hdTonTai.getTrangThai(), trangThaiMoi);
        }
        return kqSua || kqTrangThai;
    }

    public boolean xacNhanThanhToan(String maHoaDon, String trangThaiHienTai) throws SQLException {
        return hoaDonDao.capNhatTrangThai(maHoaDon, trangThaiHienTai, HoaDon.DA_THANH_TOAN);
    }

    /**
     * Tự động lấy [maKhachHang, maPhuongTien, maNhanVien] khi chọn Mã phiếu sửa chữa
     */
    public String[] layThongTinLienKetTuPhieuSuaChua(String maPSC) {
        String sql = "SELECT p.maKhachHang, p.maPhuongTien, t.maNhanVien "
                + "FROM dbo.PhieuSuaChua s "
                + "JOIN dbo.PhieuTiepNhan t ON s.maTiepNhan = t.maTiepNhan "
                + "JOIN dbo.PhuongTien p ON t.maPhuongTien = p.maPhuongTien "
                + "WHERE s.maPhieuSuaChua = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maPSC);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            rs.getString("maKhachHang"),
                            rs.getString("maPhuongTien"),
                            rs.getString("maNhanVien")
                    };
                }
            }
        } catch (SQLException e) {
            // Bỏ qua nếu bảng PhieuTiepNhan không có cột maNhanVien
        }
        return null;
    }

    // ==================== 4. CÁC HÀM TÍNH TIỀN & HỖ TRỢ TRUY VẤN PHỤ ====================
    public BigDecimal tinhTamTinh(String maHoaDon, String maPSC) {
        BigDecimal tamTinh = BigDecimal.ZERO;
        try {
            ArrayList<ChiTietHoaDon> dsCT = ctHoaDonDao.timTheoMaHoaDon(maHoaDon);
            for (ChiTietHoaDon ct : dsCT) {
                BigDecimal sl = new BigDecimal(ct.getSoLuong());
                BigDecimal dg = ct.getDonGia() != null ? ct.getDonGia() : BigDecimal.ZERO;
                BigDecimal gg = ct.getGiamGia() != null ? ct.getGiamGia() : BigDecimal.ZERO;
                tamTinh = tamTinh.add(dg.multiply(sl).subtract(gg));
            }
            if (tamTinh.compareTo(BigDecimal.ZERO) == 0 && maPSC != null && !maPSC.isEmpty()) {
                String sql = "SELECT ISNULL(SUM(donGia), 0) AS tongPT FROM dbo.PhuTung WHERE maPhieuSuaChua = ?";
                try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
                    ps.setString(1, maPSC);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getBigDecimal("tongPT") != null) {
                            tamTinh = tamTinh.add(rs.getBigDecimal("tongPT"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tamTinh;
    }

    public BigDecimal tinhTongTienHoaDon(String maHoaDon, String maPSC, BigDecimal vat) {
        BigDecimal tamTinh = tinhTamTinh(maHoaDon, maPSC);
        return tinhTongTienTuTamTinh(tamTinh, vat);
    }

    public BigDecimal tinhTongTienTuTamTinh(BigDecimal tamTinh, BigDecimal vat) {
        if (tamTinh == null) tamTinh = BigDecimal.ZERO;
        if (vat == null || vat.compareTo(BigDecimal.ZERO) < 0) vat = BigDecimal.ZERO;
        BigDecimal tienVat = tamTinh.multiply(vat).divide(new BigDecimal("100"));
        return tamTinh.add(tienVat);
    }

    public Map<String, BigDecimal> layTatCaVatTuDB() {
        Map<String, BigDecimal> map = new HashMap<>();
        String sql = "SELECT maHoaDon, vat FROM dbo.HoaDon";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("maHoaDon"), rs.getBigDecimal("vat") != null ? rs.getBigDecimal("vat") : BigDecimal.ZERO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String layTenPhuTungTuDB(String maPhuTung) {
        String sql = "SELECT tenPhuTung FROM dbo.PhuTung WHERE maPhuTung = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maPhuTung);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tenPhuTung");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maPhuTung;
    }
}