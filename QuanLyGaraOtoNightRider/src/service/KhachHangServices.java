package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import connectDB.ConnectDB;
import dao.KhachHangDao;
import entity.KhachHang;

/** Nghiệp vụ khách hàng. Không phụ thuộc thành phần giao diện Swing. */
public class KhachHangServices {
    public static final String MA_KHACH_HANG = "maKhachHang";
    public static final String HO_TEN = "hoTen";
    public static final String SO_DIEN_THOAI = "soDienThoai";
    public static final String EMAIL = "email";
    public static final String DIA_CHI = "diaChi";

    private static final Pattern MAU_HO_TEN = Pattern.compile(
        "^\\p{L}[\\p{L}\\p{M}]*(?: +\\p{L}[\\p{L}\\p{M}]*)+$"
    );
    private static final Pattern MAU_DIEN_THOAI = Pattern.compile(
        "^0[35789][0-9]{8}$"
    );
    private static final Pattern MAU_EMAIL = Pattern.compile(
        "^[A-Za-z0-9_%+\\-]+(?:\\.[A-Za-z0-9_%+\\-]+)*@"
        + "(?:[A-Za-z0-9](?:[A-Za-z0-9\\-]*[A-Za-z0-9])?\\.)+"
        + "[A-Za-z]{2,63}$"
    );

    private final KhachHangDao khachHangDao;

    public KhachHangServices() {
        this(new KhachHangDao());
    }

    public KhachHangServices(KhachHangDao khachHangDao) {
        this.khachHangDao = Objects.requireNonNull(khachHangDao);
    }

    private String chuanHoaChuoi(String giaTri) {
        return giaTri == null ? "" : giaTri.trim();
    }

    private String chuanHoaHoTen(String hoTen) {
        return Normalizer.normalize(
            chuanHoaChuoi(hoTen), Normalizer.Form.NFC
        ).replaceAll("[\\p{Z}\\s]+", " ").trim();
    }

    private String chuanHoaDienThoai(String soDienThoai) {
        String so = chuanHoaChuoi(soDienThoai);
        return so.startsWith("+84") ? "0" + so.substring(3) : so;
    }

    /** Chỉ kiểm tra định dạng; lỗi trả về theo tên trường để UI hiển thị. */
    public Map<String, String> kiemTraDuLieu(
            String hoTen, String soDienThoai, String email, String diaChi) {
        Map<String, String> loi = new LinkedHashMap<>();
        String ten = chuanHoaHoTen(hoTen);
        String so = chuanHoaDienThoai(soDienThoai);
        String mail = chuanHoaChuoi(email);

        if (ten.isEmpty()) {
            loi.put(HO_TEN, "Vui lòng nhập họ tên.");
        } else if (ten.length() > 120) {
            loi.put(HO_TEN, "Họ tên tối đa 120 ký tự.");
        } else if (!MAU_HO_TEN.matcher(ten).matches()) {
            loi.put(HO_TEN, "Tên phải có ít nhất 2 từ, chỉ chứa chữ, viết hoa chữ cái đầu.");
        }

        if (so.isEmpty()) {
            loi.put(SO_DIEN_THOAI, "Vui lòng nhập số điện thoại.");
        } else if (!MAU_DIEN_THOAI.matcher(so).matches()) {
            loi.put(SO_DIEN_THOAI, "Nhập 10 số, đầu 03/05/07/08/09.");
        }

        if (!mail.isEmpty()) {
            if (mail.length() > 254) {
                loi.put(EMAIL, "Email tối đa 254 ký tự.");
            } else if (!emailHopLe(mail)) {
                loi.put(EMAIL, "Email chưa đúng, ví dụ ten@gmail.com.");
            }
        }

        if (chuanHoaChuoi(diaChi).length() > 255) {
            loi.put(DIA_CHI, "Địa chỉ tối đa 255 ký tự.");
        }
        return loi;
    }

    private boolean emailHopLe(String email) {
        if (email.indexOf('@') > 64 || !MAU_EMAIL.matcher(email).matches()) {
            return false;
        }
        String tenMien = email.substring(email.indexOf('@') + 1);
        for (String phan : tenMien.split("\\.")) {
            if (phan.length() > 63) {
                return false;
            }
        }
        return true;
    }

    private KhachHang taoKhachHangHopLe(
            String hoTen, String soDienThoai, String email,
            String diaChi, boolean trangThai) {
        Map<String, String> loi = kiemTraDuLieu(
            hoTen, soDienThoai, email, diaChi
        );
        if (!loi.isEmpty()) {
            throw new DuLieuKhongHopLeException(loi);
        }

        KhachHang kh = new KhachHang();
        kh.setHoTen(chuanHoaHoTen(hoTen));
        kh.setSoDienThoai(chuanHoaDienThoai(soDienThoai));
        kh.setEmail(chuanHoaChuoi(email));
        kh.setDiaChi(chuanHoaChuoi(diaChi));
        kh.setTrangThai(trangThai);
        return kh;
    }

    private String kiemTraMa(String maKhachHang) {
        String ma = chuanHoaChuoi(maKhachHang).toUpperCase(Locale.ROOT);
        if (ma.isEmpty() || ma.length() > 30) {
            throw taoLoi(MA_KHACH_HANG, "Vui lòng chọn khách hàng hợp lệ.");
        }
        return ma;
    }

    private DuLieuKhongHopLeException taoLoi(String truong, String noiDung) {
        return new DuLieuKhongHopLeException(
            Collections.singletonMap(truong, noiDung)
        );
    }

    private void damBaoKetNoi() throws SQLException {
        // Main vẫn nên kết nối trước. Đồng bộ bước mở lại kết nối dùng chung.
        synchronized (ConnectDB.class) {
            Connection conn = ConnectDB.getConnection();
            if (conn == null || conn.isClosed()) {
                ConnectDB.getInstance().connect();
                System.out.println("Kết nối database thành công!");
            }
        }
    }

    /** Kiểm tra cả hồ sơ đang hoạt động và ngừng hoạt động. */
    private void kiemTraTrungDienThoai(String soDienThoai, String maBoQua)
            throws SQLException {
        for (KhachHang kh : khachHangDao.timTheoSoDienThoai(soDienThoai)) {
            if (maBoQua == null || !maBoQua.equalsIgnoreCase(kh.getMaKhachHang())) {
                throw taoLoi(SO_DIEN_THOAI,
                    "Số điện thoại đã thuộc khách hàng " + kh.getMaKhachHang() + ".");
            }
        }
    }

    public boolean themKhachHang(
            String hoTen, String soDienThoai, String email, String diaChi)
            throws SQLException {
        KhachHang kh = taoKhachHangHopLe(
            hoTen, soDienThoai, email, diaChi, true
        );
        damBaoKetNoi();
        kiemTraTrungDienThoai(kh.getSoDienThoai(), null);
        kh.setMaKhachHang(khachHangDao.taoMaKhachHang());
        try {
            return khachHangDao.themKhachHang(kh);
        } catch (SQLException ex) {
            xuLyLoiTrungKhoa(ex, kh.getSoDienThoai(), null);
            throw ex;
        }
    }

    public boolean suaKhachHang(
            String maKhachHang, String hoTen, String soDienThoai,
            String email, String diaChi, boolean trangThai) throws SQLException {
        String ma = kiemTraMa(maKhachHang);
        KhachHang kh = taoKhachHangHopLe(
            hoTen, soDienThoai, email, diaChi, trangThai
        );
        kh.setMaKhachHang(ma);
        damBaoKetNoi();
        kiemTraTrungDienThoai(kh.getSoDienThoai(), ma);
        try {
            return khachHangDao.suaKhachHang(kh);
        } catch (SQLException ex) {
            xuLyLoiTrungKhoa(ex, kh.getSoDienThoai(), ma);
            throw ex;
        }
    }

    // Nếu UNIQUE chặn số trùng do hai máy cùng lưu, trả lỗi về ô điện thoại.
    // Không coi mọi lỗi trùng khóa (ví dụ mã KH) là trùng điện thoại.
    private void xuLyLoiTrungKhoa(SQLException ex, String so, String maBoQua)
            throws SQLException {
        if (ex.getErrorCode() == 2601 || ex.getErrorCode() == 2627) {
            try {
                kiemTraTrungDienThoai(so, maBoQua);
            } catch (SQLException loiKiemTra) {
                ex.addSuppressed(loiKiemTra);
            }
        }
    }

    /** Xóa mềm: giữ hồ sơ, xe và lịch sử. */
    public boolean ngungSuDungKhachHang(String maKhachHang) throws SQLException {
        String ma = kiemTraMa(maKhachHang);
        damBaoKetNoi();
        return khachHangDao.capNhatTrangThai(ma, false);
    }

    public ArrayList<KhachHang> timKiem(String tuKhoa, Boolean trangThai)
            throws SQLException {
        String noiDung = chuanHoaChuoi(tuKhoa);
        damBaoKetNoi();
        ArrayList<KhachHang> dsKhachHang;
        if (noiDung.isEmpty()) {
            dsKhachHang = khachHangDao.layDanhSachKhachHang();
        } else {
            KhachHang kh = khachHangDao.timTheoMa(noiDung.toUpperCase(Locale.ROOT));
            if (kh != null) {
                dsKhachHang = new ArrayList<>();
                dsKhachHang.add(kh);
            } else {
                dsKhachHang = khachHangDao.timTheoSoDienThoai(
                    chuanHoaDienThoai(noiDung)
                );
            }
        }
        if (trangThai != null) {
            dsKhachHang.removeIf(kh -> kh.isTrangThai() != trangThai.booleanValue());
        }
        return dsKhachHang;
    }

    /** UI lấy danh sách lỗi để hiển thị, service không mở hộp thoại. */
    public static class DuLieuKhongHopLeException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;
        private final Map<String, String> loiTheoTruong;

        public DuLieuKhongHopLeException(Map<String, String> loiTheoTruong) {
            super("Thông tin khách hàng chưa hợp lệ.");
            this.loiTheoTruong = Collections.unmodifiableMap(
                new LinkedHashMap<>(loiTheoTruong)
            );
        }

        public Map<String, String> getLoiTheoTruong() {
            return loiTheoTruong;
        }
    }
}
