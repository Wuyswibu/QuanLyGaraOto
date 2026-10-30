package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.KhachHang;
import entity.Xe;

/** SQL hỗ trợ lập phiếu. Kết nối do Service sở hữu, DAO không đóng kết nối. */
public class TiepNhanHoSoDao {
    private final Connection c;
    public TiepNhanHoSoDao(Connection c) { this.c = c; }
    private PreparedStatement ps(String sql, String... values) throws SQLException {
        PreparedStatement p = c.prepareStatement(sql);
        p.setQueryTimeout(20);
        for (int i = 0; i < values.length; i++) p.setString(i + 1, values[i]);
        return p;
    }
    private KhachHang kh(ResultSet r) throws SQLException {
        return new KhachHang(r.getString("maKhachHang"), r.getString("hoTen"),
            r.getString("soDienThoai"), r.getString("email"), r.getString("diaChi"), r.getBoolean("trangThai"));
    }
    private Xe xe(ResultSet r) throws SQLException {
        return new Xe(r.getString("maPhuongTien"), r.getString("maKhachHang"),
            r.getString("bienSo"), r.getString("mauXe"), r.getString("trangThai"));
    }
    public KhachHang khachTheoMa(String ma, boolean khoa) throws SQLException {
        try (PreparedStatement p = ps("SELECT * FROM dbo.KhachHang "
                + (khoa ? "WITH (UPDLOCK,HOLDLOCK) " : "") + "WHERE maKhachHang=?", ma);
             ResultSet r = p.executeQuery()) { return r.next() ? kh(r) : null; }
    }
    public KhachHang khachTheoSo(String so, boolean khoa) throws SQLException {
        try (PreparedStatement p = ps("SELECT * FROM dbo.KhachHang "
                + (khoa ? "WITH (UPDLOCK,HOLDLOCK) " : "") + "WHERE soDienThoai=?", so);
             ResultSet r = p.executeQuery()) {
            if (!r.next()) return null;
            KhachHang k = kh(r);
            if (r.next()) throw new SQLException("Có nhiều hồ sơ trùng số điện thoại. Cần xử lý dữ liệu trước.");
            return k;
        }
    }
    public Xe xeTheoMa(String ma, boolean khoa) throws SQLException {
        try (PreparedStatement p = ps("SELECT * FROM dbo.PhuongTien "
                + (khoa ? "WITH (UPDLOCK,HOLDLOCK) " : "") + "WHERE maPhuongTien=?", ma);
             ResultSet r = p.executeQuery()) { return r.next() ? xe(r) : null; }
    }
    public Xe xeTheoBienSo(String bienSo, boolean khoa) throws SQLException {
        try (PreparedStatement p = ps("SELECT * FROM dbo.PhuongTien "
                + (khoa ? "WITH (UPDLOCK,HOLDLOCK) " : "")
                + "WHERE UPPER(REPLACE(REPLACE(REPLACE(bienSo,' ',''),'-',''),'.',''))=?", bienSo);
             ResultSet r = p.executeQuery()) {
            if (!r.next()) return null;
            Xe x = xe(r);
            if (r.next()) throw new SQLException("Có nhiều xe trùng biển số chuẩn hóa. Cần xử lý dữ liệu trước.");
            return x;
        }
    }
    public List<Xe> xeCuaKhach(String maKH) throws SQLException {
        List<Xe> ds = new ArrayList<>();
        try (PreparedStatement p = ps("SELECT * FROM dbo.PhuongTien WHERE maKhachHang=? ORDER BY bienSo", maKH);
             ResultSet r = p.executeQuery()) { while (r.next()) ds.add(xe(r)); }
        return ds;
    }
    public List<String> mauXe() throws SQLException {
        java.util.TreeMap<String,String> ds = new java.util.TreeMap<>();
        try (PreparedStatement p = ps("SELECT DISTINCT mauXe FROM dbo.PhuongTien"); ResultSet r = p.executeQuery()) {
            while (r.next()) {
                String s = r.getString(1);
                if (s != null && !s.trim().isEmpty()) {
                    s = s.trim().replaceAll("\\s+", " ");
                    ds.putIfAbsent(s.toLowerCase(java.util.Locale.ROOT), s);
                }
            }
        }
        return new ArrayList<>(ds.values());
    }
    /** Chỉ gọi trong transaction. Khóa bảng bảo vệ cấp mã MAX+1 của luồng này. */
    public String taoMa(String loai) throws SQLException {
        String bang, cot, dau;
        if ("KH".equals(loai)) { bang="KhachHang"; cot="maKhachHang"; dau="KH"; }
        else if ("PT".equals(loai)) { bang="PhuongTien"; cot="maPhuongTien"; dau="PT"; }
        else if ("PTN".equals(loai)) { bang="PhieuTiepNhan"; cot="maTiepNhan"; dau="PTN"; }
        else throw new IllegalArgumentException("Loại mã không hợp lệ.");
        try (PreparedStatement p = ps("SELECT MAX(TRY_CONVERT(BIGINT,SUBSTRING(" + cot + ","
                + (dau.length()+1) + ",27))) FROM dbo." + bang + " WITH (TABLOCKX,HOLDLOCK) WHERE " + cot + " LIKE ?", dau+"%");
             ResultSet r = p.executeQuery()) {
            r.next(); long n = Math.addExact(r.getLong(1), 1);
            return String.format(java.util.Locale.ROOT, dau+"%03d", n);
        }
    }
    public void themKhach(KhachHang k) throws SQLException {
        try (PreparedStatement p = ps("INSERT INTO dbo.KhachHang "
                + "(maKhachHang,hoTen,soDienThoai,email,diaChi,trangThai) VALUES (?,?,?,?,?,1)")) {
            p.setString(1,k.getMaKhachHang()); p.setNString(2,k.getHoTen());
            p.setString(3,k.getSoDienThoai()); p.setString(4,k.getEmail()); p.setNString(5,k.getDiaChi());
            if (p.executeUpdate()!=1) throw new SQLException("Không thêm được khách hàng.");
        }
    }
    public void themXe(Xe x) throws SQLException {
        try (PreparedStatement p = ps("INSERT INTO dbo.PhuongTien "
                + "(maPhuongTien,maKhachHang,bienSo,mauXe,trangThai) VALUES (?,?,?,?,?)")) {
            p.setString(1,x.getMaPhuongTien()); p.setString(2,x.getMaKhachHang());
            p.setString(3,x.getBienSo()); p.setNString(4,x.getMauXe()); p.setNString(5,x.getTrangThai());
            if (p.executeUpdate()!=1) throw new SQLException("Không thêm được xe.");
        }
    }
    public boolean nhanVienTonTai(String ma) throws SQLException {
        try (PreparedStatement p=ps("SELECT 1 FROM dbo.NhanVien WHERE maNhanVien=?",ma);
             ResultSet r=p.executeQuery()) { return r.next(); }
    }
    public String phieuDangMo(String maXe) throws SQLException {
        // Mọi trạng thái chưa được xác nhận kết thúc đều được coi là đang mở.
        try (PreparedStatement p=ps("SELECT TOP 1 maTiepNhan FROM dbo.PhieuTiepNhan WITH (UPDLOCK,HOLDLOCK) "
                + "WHERE maPhuongTien=? AND trangThai NOT IN (N'Đã giao xe',N'Đã hủy') ORDER BY ngayGioTiepNhan DESC",maXe);
             ResultSet r=p.executeQuery()) { return r.next()?r.getString(1):null; }
    }
    public Integer odoGanNhat(String maXe) throws SQLException {
        try (PreparedStatement p=ps("SELECT TOP 1 odo FROM dbo.PhieuTiepNhan WHERE maPhuongTien=? "
                + "AND trangThai<>N'Đã hủy' ORDER BY ngayGioTiepNhan DESC,maTiepNhan DESC",maXe);
             ResultSet r=p.executeQuery()) { return r.next()?r.getInt(1):null; }
    }
    
}
