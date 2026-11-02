package service;

import java.math.BigDecimal;
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
import dao.DichVuDao;
import entity.DichVu;

public class DichVuService {
    public static final String MA_DICH_VU = "maDichVu";
    public static final String TEN_DICH_VU = "tenDichVu";
    public static final String MO_TA = "moTa";
    public static final String DON_GIA = "donGia";
    
    private static final Pattern MAU_DON_GIA = Pattern.compile("^[0-9]+(?:\\.[0-9]{1,2})?$");
    private static final BigDecimal DON_GIA_TOI_DA = new BigDecimal("9999999999999999.99");
    private final DichVuDao dichVuDao;

    public DichVuService() {
        this(new DichVuDao());
    }

    public DichVuService(DichVuDao dichVuDao) {
        this.dichVuDao = Objects.requireNonNull(
                dichVuDao, "DichVuDao không được null.");
    }

    private String chuanHoaChuoi(String giaTri) {
        return giaTri == null ? "" : giaTri.trim();
    }

    private String chuanHoaTen(String tenDichVu) {
        return Normalizer.normalize(
                chuanHoaChuoi(tenDichVu), Normalizer.Form.NFC)
                .replaceAll("[\\p{Z}\\s]+", " ")
                .trim();
    }

    private String chuanHoaMoTa(String moTa) {
        String noiDung = chuanHoaChuoi(moTa);
        return noiDung.isEmpty() ? null : noiDung;
    }

    private String chuanHoaTimKiem(String giaTri) {
        return Normalizer.normalize(chuanHoaTen(giaTri), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT);
    }

// KIỂM TRA DỮ LIỆU
    
    public Map<String, String> kiemTraDuLieu(String tenDichVu, String moTa, String donGia) {
        Map<String, String> loi = new LinkedHashMap<>();

        String ten = chuanHoaTen(tenDichVu);
        String noiDungMoTa = chuanHoaChuoi(moTa);
        String gia = chuanHoaChuoi(donGia);

        if (ten.isEmpty()) {
            loi.put(TEN_DICH_VU, "Vui lòng nhập tên dịch vụ.");
        } else if (ten.length() > 150) {
            loi.put(TEN_DICH_VU, "Tên dịch vụ tối đa 150 ký tự.");
        }

        if (noiDungMoTa.length() > 1000) {
            loi.put(MO_TA, "Mô tả tối đa 1000 ký tự.");
        }

        if (gia.isEmpty()) {
            loi.put(DON_GIA, "Vui lòng nhập đơn giá.");
        } else if (!MAU_DON_GIA.matcher(gia).matches()) {
            loi.put(DON_GIA, "Nhập số không âm, tối đa 2 chữ số thập phân; ví dụ 150000 hoặc 150000.50. Không nhập dấu phân cách hàng nghìn.");
        } else {
            try {
                BigDecimal giaTri = new BigDecimal(gia);

                if (giaTri.compareTo(DON_GIA_TOI_DA) > 0) {
                    loi.put(DON_GIA, "Đơn giá vượt giới hạn DECIMAL(18,2).");
                }
            } catch (NumberFormatException ex) {
                loi.put(DON_GIA, "Đơn giá không hợp lệ.");
            }
        }

        return loi;
    }

    private DichVu taoDichVuHopLe(String tenDichVu, String moTa, String donGia, boolean trangThai) {
        Map<String, String> loi = kiemTraDuLieu(
                tenDichVu, moTa, donGia);

        if (!loi.isEmpty()) {
            throw new DuLieuKhongHopLeException(loi);
        }

        DichVu dichVu = new DichVu();
        dichVu.setTenDichVu(chuanHoaTen(tenDichVu));
        dichVu.setMoTa(chuanHoaMoTa(moTa));
        dichVu.setDonGia(new BigDecimal(chuanHoaChuoi(donGia)));
        dichVu.setTrangThai(trangThai);

        return dichVu;
    }

    private String kiemTraMa(String maDichVu) {
        String ma = chuanHoaChuoi(maDichVu);

        if (ma.isEmpty()) {
            throw taoLoi(MA_DICH_VU, "Vui lòng chọn dịch vụ.");
        }

        if (ma.length() > 30) {
            throw taoLoi(MA_DICH_VU, "Mã dịch vụ tối đa 30 ký tự.");
        }

        return ma;
    }

    private DuLieuKhongHopLeException taoLoi(String truong, String noiDung) {
        return new DuLieuKhongHopLeException(
                Collections.singletonMap(truong, noiDung));
    }
    
// KẾT NỐI DATABASE 

    private void damBaoKetNoi() throws SQLException {
        synchronized (ConnectDB.class) {
            Connection conn = ConnectDB.getConnection();

            if (conn == null || conn.isClosed()) {
                ConnectDB.getInstance().connect();
            }
        }
    }

// LẤY DANH SÁCH 

    public ArrayList<DichVu> layDanhSachDichVu() throws SQLException {
        damBaoKetNoi();
        return dichVuDao.layDanhSachDichVu();
    }

// TÌM THEO MÃ 
    
    public DichVu timTheoMa(String maDichVu) throws SQLException {
        String ma = kiemTraMa(maDichVu);

        damBaoKetNoi();

        return dichVuDao.timTheoMa(ma);
    }

// XỬ LÝ NGHIỆP VỤ

    public boolean themDichVu(String tenDichVu, String moTa, String donGia, boolean trangThai) throws SQLException {
        DichVu dichVu = taoDichVuHopLe(
                tenDichVu, moTa, donGia, trangThai);

        damBaoKetNoi();

        dichVu.setMaDichVu(dichVuDao.taoMaDichVu());

        return dichVuDao.themDichVu(dichVu);
    }

    public boolean suaDichVu(String maDichVu, String tenDichVu, String moTa, String donGia, boolean trangThai) throws SQLException {
        String ma = kiemTraMa(maDichVu);

        DichVu dichVu = taoDichVuHopLe(
                tenDichVu, moTa, donGia, trangThai);

        damBaoKetNoi();

        if (dichVuDao.timTheoMa(ma) == null) {
            throw taoLoi(MA_DICH_VU, "Dịch vụ không còn tồn tại. Hãy tải lại danh sách.");
        }

        dichVu.setMaDichVu(ma);

        return dichVuDao.suaDichVu(dichVu);
    }

    public ArrayList<DichVu> timKiem(String tuKhoa) throws SQLException {
        return timKiem(tuKhoa, null);
    }

    public ArrayList<DichVu> timKiem(String tuKhoa, Boolean trangThai) throws SQLException {
        String noiDungTim = chuanHoaTimKiem(tuKhoa);

        damBaoKetNoi();

        ArrayList<DichVu> ketQua = new ArrayList<>();

        for (DichVu dichVu : dichVuDao.layDanhSachDichVu()) {
            String noiDung = chuanHoaTimKiem(
                    dichVu.getMaDichVu() + " " + dichVu.getTenDichVu());

            boolean dungTuKhoa = noiDung.contains(noiDungTim);
            boolean dungTrangThai = trangThai == null
                    || dichVu.isTrangThai() == trangThai.booleanValue();

            if (dungTuKhoa && dungTrangThai) {
                ketQua.add(dichVu);
            }
        }

        return ketQua;
    }

// NGOẠI LỆ DỮ LIỆU

    public static class DuLieuKhongHopLeException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;

        private final Map<String, String> loiTheoTruong;

        public DuLieuKhongHopLeException(Map<String, String> loiTheoTruong) {
            super("Thông tin dịch vụ chưa hợp lệ.");

            this.loiTheoTruong = Collections.unmodifiableMap(
                    new LinkedHashMap<>(loiTheoTruong));
        }

        public Map<String, String> getLoiTheoTruong() {
            return loiTheoTruong;
        }
    }
}