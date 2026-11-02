package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import dao.XeDao;
import entity.Xe;

/** Xử lý dữ liệu xe; không phụ thuộc Swing. Main chịu trách nhiệm kết nối DB. */
public class XeService {
    private final XeDao xeDao;

    public XeService() { this(new XeDao()); }

    public XeService(XeDao xeDao) {
        if (xeDao == null) throw new IllegalArgumentException("DAO không được null.");
        this.xeDao = xeDao;
    }

    public DuLieuXe taiDuLieu() throws SQLException {
        return new DuLieuXe(xeDao.layDanhSachXe(), xeDao.layKhachHang());
    }

    public void themXe(String maKH, String bienSo, String mauXe, String trangThai) throws SQLException {
        Xe xe = kiemTra("", maKH, bienSo, mauXe, trangThai, false);
        xe.setMaPhuongTien(xeDao.taoMaXe());
        if (!xeDao.themXe(xe)) throw new SQLException("Thêm xe không thành công.");
    }

    public void suaXe(String maXe, String maKH, String bienSo, String mauXe, String trangThai)
            throws SQLException {
        Xe xe = kiemTra(maXe, maKH, bienSo, mauXe, trangThai, true);
        if (!xeDao.suaXe(xe)) throw new SQLException("Xe không còn tồn tại hoặc chưa cập nhật được.");
    }

    private Xe kiemTra(String maXe, String maKH, String bienSo, String mauXe,
            String trangThai, boolean sua) throws SQLException {
        maXe = trim(maXe);
        maKH = trim(maKH);
        bienSo = trim(bienSo).replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
        mauXe = trim(mauXe);
        if (sua && (!maXe.matches("PT[0-9]{3,}") || maXe.length() > 30))
            throw new IllegalArgumentException("Mã xe phải có dạng PT001, tối đa 30 ký tự.");
        if (bienSo.isEmpty() || bienSo.length() > 20)
            throw new IllegalArgumentException("Biển số không được rỗng và tối đa 20 ký tự.");
        if (mauXe.isEmpty() || mauXe.length() > 100)
            throw new IllegalArgumentException("Mẫu xe không được rỗng và tối đa 100 ký tự.");
        if (maKH.isEmpty()) throw new IllegalArgumentException("Vui lòng chọn chủ xe.");
        if (!"Hoạt động".equals(trangThai) && !"Ngừng theo dõi".equals(trangThai))
            throw new IllegalArgumentException("Trạng thái xe không hợp lệ.");
        if (!xeDao.layKhachHang().containsKey(maKH))
            throw new IllegalArgumentException("Khách hàng không còn tồn tại. Hãy tải lại dữ liệu.");
        if (xeDao.trungBienSo(bienSo, sua ? maXe : ""))
            throw new IllegalArgumentException("Biển số đã tồn tại.");
        return new Xe(maXe, maKH, bienSo, mauXe, trangThai);
    }

    /** Luôn kết hợp mã chủ xe chính xác với từ khóa và trạng thái. */
    public ArrayList<Xe> locXe(List<Xe> danhSach, Map<String, String> khachHang,
            String maKH, String tuKhoa, String trangThai) {
        ArrayList<Xe> ketQua = new ArrayList<>();
        String tu = trim(tuKhoa);
        for (Xe xe : danhSach) {
            if (maKH != null && !maKH.equals(xe.getMaKhachHang())) continue;
            if (trangThai != null && !"Tất cả".equals(trangThai)
                    && !trangThai.equals(xe.getTrangThai())) continue;
            if (tu.isEmpty() || tu.equalsIgnoreCase(xe.getMaPhuongTien())
                    || tu.equalsIgnoreCase(xe.getMaKhachHang())) ketQua.add(xe);
        }
        return ketQua;
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    /** Gợi ý từ mẫu xe đã lưu; bỏ trống, bỏ trùng khác hoa/thường và khoảng trắng. */
    public ArrayList<String> layMauXeDaCo(List<Xe> danhSach) {
        java.util.TreeMap<String, String> mauXe = new java.util.TreeMap<>();
        for (Xe xe : danhSach) {
            String mau = trim(xe.getMauXe()).replaceAll("\\s+", " ");
            if (!mau.isEmpty()) mauXe.putIfAbsent(mau.toLowerCase(Locale.ROOT), mau);
        }
        return new ArrayList<>(mauXe.values());
    }

    public static final class DuLieuXe {
        private final ArrayList<Xe> danhSachXe;
        private final LinkedHashMap<String, String> khachHang;
        public DuLieuXe(List<Xe> xe, Map<String, String> kh) {
            danhSachXe = new ArrayList<>(xe);
            khachHang = new LinkedHashMap<>(kh);
        }
        public ArrayList<Xe> getDanhSachXe() { return new ArrayList<>(danhSachXe); }
        public LinkedHashMap<String, String> getKhachHang() { return new LinkedHashMap<>(khachHang); }
    }
}
