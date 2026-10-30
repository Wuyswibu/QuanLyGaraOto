package service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import connectDB.ConnectDB;
import dao.DichVuDao;
import dao.HangMucDichVuDao;
import dao.KhachHangDao;
import dao.PhieuSuaChuaDao;
import dao.PhieuTiepNhanDao;
import dao.XeDao;
import entity.DichVu;
import entity.HangMucDichVu;
import entity.KhachHang;
import entity.PhieuSuaChua;
import entity.PhieuTiepNhan;
import entity.Xe;

public class PhieuSuaChuaService {
    public static final String MA_PHIEU_SUA_CHUA = "maPhieuSuaChua";
    public static final String MA_TIEP_NHAN = "maTiepNhan";
    public static final String LOAI_PHIEU = "loaiPhieu";
    public static final String CHAN_DOAN = "chanDoan";
    public static final String GHI_CHU = "ghiChu";

    public static final String MA_DICH_VU = "maDichVu";
    public static final String TEN_HANG_MUC = "tenHangMuc";
    public static final String MO_TA = "moTa";
    public static final String CHI_PHI_DU_KIEN = "chiPhiDuKien";
    public static final String DANH_SACH_HANG_MUC = "danhSachHangMuc";

    public static final String DA_TIEP_NHAN = "Đã tiếp nhận";
    public static final String CHO_SUA_CHUA = "Chờ sửa chữa";
    public static final String CHO_THUC_HIEN = "Chờ thực hiện";

    private static final Pattern MAU_CHI_PHI = Pattern.compile("^[0-9]+(?:\\.[0-9]{1,2})?$");

    private static final BigDecimal CHI_PHI_TOI_DA = new BigDecimal("9999999999999999.99");

    private final PhieuSuaChuaDao phieuSuaChuaDao = new PhieuSuaChuaDao();
    private final HangMucDichVuDao hangMucDichVuDao = new HangMucDichVuDao();
    private final PhieuTiepNhanDao phieuTiepNhanDao = new PhieuTiepNhanDao();
    private final DichVuDao dichVuDao = new DichVuDao();
    private final XeDao xeDao = new XeDao();
    private final KhachHangDao khachHangDao = new KhachHangDao();

// CHUẨN HÓA DỮ LIỆU

    private String chuanHoaChuoi(String giaTri) {
        return giaTri == null ? "" : giaTri.trim();
    }

    private String chuoiHoacNull(String giaTri) {
        String chuoi = chuanHoaChuoi(giaTri);
        return chuoi.isEmpty() ? null : chuoi;
    }

// KẾT NỐI DATABASE
    
    private Connection damBaoKetNoi() throws SQLException {
        synchronized (ConnectDB.class) {
            Connection conn = ConnectDB.getConnection();

            if (conn == null || conn.isClosed()) {
                conn = ConnectDB.getInstance().connect();
            }

            if (conn == null || conn.isClosed()) {
                throw new SQLException("Không thể kết nối cơ sở dữ liệu.");
            }

            return conn;
        }
    }

// KIỂM TRA THÔNG TIN PHIẾU

    public Map<String, String> kiemTraDuLieu(String maTiepNhan, String loaiPhieu, String chanDoan, String ghiChu) {
        Map<String, String> loi = new LinkedHashMap<>();

        String ma = chuanHoaChuoi(maTiepNhan);
        String loai = chuanHoaChuoi(loaiPhieu);

        if (ma.isEmpty()) {
            loi.put(MA_TIEP_NHAN, "Vui lòng chọn phiếu tiếp nhận.");
        } else if (ma.length() > 30) {
            loi.put(MA_TIEP_NHAN, "Mã tiếp nhận tối đa 30 ký tự.");
        }

        if (!"Sửa chữa".equals(loai) && !"Bảo dưỡng".equals(loai) && !"Bảo hành".equals(loai)) {
            loi.put(LOAI_PHIEU, "Vui lòng chọn loại phiếu hợp lệ.");
        }

        if (chuanHoaChuoi(chanDoan).length() > 2000) {
            loi.put(CHAN_DOAN, "Chẩn đoán tối đa 2000 ký tự.");
        }

        if (chuanHoaChuoi(ghiChu).length() > 1000) {
            loi.put(GHI_CHU, "Ghi chú tối đa 1000 ký tự.");
        }

        return loi;
    }

    public Map<String, String> kiemTraHangMuc(String maDichVu, String tenHangMuc, String moTa, String chiPhiDuKien) {
        Map<String, String> loi = new LinkedHashMap<>();

        String ma = chuanHoaChuoi(maDichVu);
        String ten = chuanHoaChuoi(tenHangMuc);
        String chiPhi = chuanHoaChuoi(chiPhiDuKien);

        if (ma.isEmpty()) {
            loi.put(MA_DICH_VU, "Vui lòng chọn dịch vụ.");
        } else if (ma.length() > 30) {
            loi.put(MA_DICH_VU, "Mã dịch vụ tối đa 30 ký tự.");
        }

        if (ten.isEmpty()) {
            loi.put(TEN_HANG_MUC, "Vui lòng nhập tên hạng mục.");
        } else if (ten.length() > 150) {
            loi.put(TEN_HANG_MUC, "Tên hạng mục tối đa 150 ký tự.");
        }

        if (chuanHoaChuoi(moTa).length() > 2000) {
            loi.put(MO_TA, "Mô tả tối đa 2000 ký tự.");
        }

        if (chiPhi.isEmpty()) {
            loi.put(CHI_PHI_DU_KIEN, "Vui lòng nhập chi phí dự kiến.");
        } else if (!MAU_CHI_PHI.matcher(chiPhi).matches()) {
            loi.put(CHI_PHI_DU_KIEN,
                    "Chi phí phải là số không âm, tối đa 2 chữ số thập phân. "
                            + "Ví dụ: 150000 hoặc 150000.50.");
        } else {
            try {
                BigDecimal giaTri = new BigDecimal(chiPhi);

                if (giaTri.compareTo(CHI_PHI_TOI_DA) > 0) {
                    loi.put(CHI_PHI_DU_KIEN,
                            "Chi phí vượt giới hạn DECIMAL(18,2).");
                }
            } catch (NumberFormatException ex) {
                loi.put(CHI_PHI_DU_KIEN, "Chi phí dự kiến không hợp lệ.");
            }
        }

        return loi;
    }

    private String kiemTraMa(String giaTri, String truong, String tenTruong) {
        String ma = chuanHoaChuoi(giaTri);

        if (ma.isEmpty() || ma.length() > 30) {
            throw taoLoi(truong, tenTruong + " không được rỗng và tối đa 30 ký tự.");
        }

        return ma;
    }

    private DuLieuKhongHopLeException taoLoi(String truong, String thongBao) {
        return new DuLieuKhongHopLeException(
                Collections.singletonMap(truong, thongBao));
    }

// LẤY DỮ LIỆU HIỂN THỊ

    public ArrayList<PhieuSuaChua> layDanhSachPhieuSuaChua() throws SQLException {
        damBaoKetNoi();
        return phieuSuaChuaDao.layDanhSachPhieuSuaChua();
    }

    public ArrayList<PhieuTiepNhan> layDanhSachPhieuTiepNhan() throws SQLException {
        damBaoKetNoi();
        return phieuTiepNhanDao.layDanhSachPhieuTiepNhan();
    }

    public ArrayList<Xe> layDanhSachXe() throws SQLException {
        damBaoKetNoi();
        return xeDao.layDanhSachXe();
    }

    public ArrayList<KhachHang> layDanhSachKhachHang() throws SQLException {
        damBaoKetNoi();
        return khachHangDao.layDanhSachKhachHang();
    }

    public ArrayList<DichVu> layDichVuDangApDung() throws SQLException {
        damBaoKetNoi();

        ArrayList<DichVu> ketQua = new ArrayList<>();

        for (DichVu dichVu : dichVuDao.layDanhSachDichVu()) {
            if (dichVu.isTrangThai()) {
                ketQua.add(dichVu);
            }
        }

        return ketQua;
    }

    public PhieuSuaChua timTheoMa(String maPhieuSuaChua) throws SQLException {
        String ma = kiemTraMa(
                maPhieuSuaChua, MA_PHIEU_SUA_CHUA, "Mã phiếu sửa chữa");

        damBaoKetNoi();

        return phieuSuaChuaDao.timTheoMa(ma);
    }

    public PhieuSuaChua timTheoMaTiepNhan(String maTiepNhan) throws SQLException {
        String ma = kiemTraMa(
                maTiepNhan, MA_TIEP_NHAN, "Mã tiếp nhận");

        damBaoKetNoi();

        return phieuSuaChuaDao.timTheoMaTiepNhan(ma);
    }

    public ArrayList<HangMucDichVu> layHangMucTheoPhieu(
            String maPhieuSuaChua) throws SQLException {
        String ma = kiemTraMa(
                maPhieuSuaChua, MA_PHIEU_SUA_CHUA, "Mã phiếu sửa chữa");

        damBaoKetNoi();

        return hangMucDichVuDao.timTheoMaPhieuSuaChua(ma);
    }

    public ArrayList<PhieuSuaChua> timKiem(String tuKhoa) throws SQLException {
        String noiDung = chuanHoaChuoi(tuKhoa);

        damBaoKetNoi();

        if (noiDung.isEmpty()) {
            return phieuSuaChuaDao.layDanhSachPhieuSuaChua();
        }

        return phieuSuaChuaDao.timKiem(noiDung);
    }

// HẠNG MỤC ĐANG NHẬP

    public HangMucDichVu taoHangMucDuKien(
            String maDichVu, String tenHangMuc,
            String moTa, String chiPhiDuKien) throws SQLException {
        HangMucDichVu hangMuc = taoHangMucHopLe(
                maDichVu, tenHangMuc, moTa, chiPhiDuKien);

        damBaoKetNoi();
        kiemTraDichVuDangApDung(hangMuc.getMaDichVu());

        return hangMuc;
    }

    private HangMucDichVu taoHangMucHopLe(
            String maDichVu, String tenHangMuc,
            String moTa, String chiPhiDuKien) {
        Map<String, String> loi = kiemTraHangMuc(
                maDichVu, tenHangMuc, moTa, chiPhiDuKien);

        if (!loi.isEmpty()) {
            throw new DuLieuKhongHopLeException(loi);
        }

        HangMucDichVu hangMuc = new HangMucDichVu();
        hangMuc.setMaDichVu(chuanHoaChuoi(maDichVu));
        hangMuc.setTenHangMuc(chuanHoaChuoi(tenHangMuc));
        hangMuc.setMoTa(chuoiHoacNull(moTa));
        hangMuc.setTrangThai(CHO_THUC_HIEN);
        hangMuc.setChiPhiDuKien(
                new BigDecimal(chuanHoaChuoi(chiPhiDuKien)));
        hangMuc.setChiPhiThucTe(BigDecimal.ZERO);

        return hangMuc;
    }

    private void kiemTraDichVuDangApDung(String maDichVu) throws SQLException {
        DichVu dichVu = dichVuDao.timTheoMa(maDichVu);

        if (dichVu == null) {
            throw taoLoi(MA_DICH_VU,
                    "Dịch vụ " + maDichVu + " không còn tồn tại.");
        }

        if (!dichVu.isTrangThai()) {
            throw taoLoi(MA_DICH_VU,
                    "Dịch vụ " + maDichVu + " đã ngừng áp dụng.");
        }
    }

    public BigDecimal tinhTongChiPhiDuKien(List<HangMucDichVu> danhSach) {
        if (danhSach == null) {
            throw taoLoi(DANH_SACH_HANG_MUC,
                    "Danh sách hạng mục không được null.");
        }

        BigDecimal tong = BigDecimal.ZERO;

        for (HangMucDichVu hangMuc : danhSach) {
            if (hangMuc == null || hangMuc.getChiPhiDuKien() == null) {
                throw taoLoi(CHI_PHI_DU_KIEN,
                        "Hạng mục thiếu chi phí dự kiến.");
            }

            BigDecimal chiPhi = hangMuc.getChiPhiDuKien();

            if (chiPhi.signum() < 0
                    || chiPhi.stripTrailingZeros().scale() > 2
                    || chiPhi.compareTo(CHI_PHI_TOI_DA) > 0) {
                throw taoLoi(CHI_PHI_DU_KIEN,
                        "Chi phí hạng mục không hợp lệ.");
            }

            tong = tong.add(chiPhi);
        }

        return tong;
    }

    private ArrayList<HangMucDichVu> saoChepHangMuc(
            List<HangMucDichVu> danhSach) {
        if (danhSach == null) {
            throw taoLoi(DANH_SACH_HANG_MUC,
                    "Danh sách hạng mục không được null.");
        }

        ArrayList<HangMucDichVu> ketQua = new ArrayList<>();

        for (int i = 0; i < danhSach.size(); i++) {
            HangMucDichVu hangMuc = danhSach.get(i);

            if (hangMuc == null) {
                throw taoLoi(DANH_SACH_HANG_MUC,
                        "Hạng mục thứ " + (i + 1) + " không hợp lệ.");
            }

            if (!chuanHoaChuoi(hangMuc.getMaHangMuc()).isEmpty()
                    || !chuanHoaChuoi(hangMuc.getMaPhieuSuaChua()).isEmpty()) {
                throw taoLoi(DANH_SACH_HANG_MUC,
                        "Chỉ sử dụng hạng mục chưa lưu khi lập phiếu mới.");
            }

            String chiPhi = hangMuc.getChiPhiDuKien() == null
                    ? ""
                    : hangMuc.getChiPhiDuKien().toPlainString();

            ketQua.add(taoHangMucHopLe(
                    hangMuc.getMaDichVu(),
                    hangMuc.getTenHangMuc(),
                    hangMuc.getMoTa(),
                    chiPhi));
        }

        return ketQua;
    }

// LƯU PHIẾU VÀ HẠNG MỤC

    public PhieuSuaChua luuPhieuSuaChua(
            String maTiepNhan, String loaiPhieu,
            String chanDoan, String ghiChu,
            List<HangMucDichVu> danhSachHangMuc) throws SQLException {
        Map<String, String> loi = kiemTraDuLieu(
                maTiepNhan, loaiPhieu, chanDoan, ghiChu);

        if (!loi.isEmpty()) {
            throw new DuLieuKhongHopLeException(loi);
        }

        ArrayList<HangMucDichVu> hangMucCanLuu = saoChepHangMuc(
                danhSachHangMuc);

        synchronized (ConnectDB.class) {
            Connection conn = damBaoKetNoi();

            if (!conn.getAutoCommit()) {
                throw new SQLException(
                        "Kết nối đang có giao dịch khác chưa kết thúc.");
            }

            conn.setAutoCommit(false);

            boolean daKetThucGiaoDich = false;
            Throwable loiGoc = null;

            try {
                String maTiepNhanHopLe = chuanHoaChuoi(maTiepNhan);

                PhieuTiepNhan tiepNhan = phieuTiepNhanDao.timTheoMa(
                        maTiepNhanHopLe);

                if (tiepNhan == null) {
                    throw taoLoi(MA_TIEP_NHAN,
                            "Phiếu tiếp nhận không còn tồn tại.");
                }

                if (!DA_TIEP_NHAN.equals(tiepNhan.getTrangThai())) {
                    throw taoLoi(MA_TIEP_NHAN,
                            "Chỉ lập phiếu sửa chữa cho phiếu có trạng thái Đã tiếp nhận.");
                }

                PhieuSuaChua phieuCu = phieuSuaChuaDao.timTheoMaTiepNhan(
                        maTiepNhanHopLe);

                if (phieuCu != null) {
                    throw taoLoi(MA_TIEP_NHAN,
                            "Phiếu tiếp nhận đã có phiếu sửa chữa: "
                                    + phieuCu.getMaPhieuSuaChua());
                }

                for (HangMucDichVu hangMuc : hangMucCanLuu) {
                    kiemTraDichVuDangApDung(hangMuc.getMaDichVu());
                }

                PhieuSuaChua phieu = new PhieuSuaChua();
                phieu.setMaPhieuSuaChua(
                        phieuSuaChuaDao.taoMaPhieuSuaChua());
                phieu.setMaTiepNhan(maTiepNhanHopLe);
                phieu.setNgayLap(LocalDateTime.now());
                phieu.setLoaiPhieu(chuanHoaChuoi(loaiPhieu));
                phieu.setChanDoan(chuoiHoacNull(chanDoan));
                phieu.setGhiChu(chuoiHoacNull(ghiChu));
                phieu.setTrangThai(CHO_SUA_CHUA);

                if (!phieuSuaChuaDao.themPhieuSuaChua(phieu)) {
                    throw new SQLException("Không lưu được phiếu sửa chữa.");
                }

                for (HangMucDichVu hangMuc : hangMucCanLuu) {
                    hangMuc.setMaHangMuc(hangMucDichVuDao.taoMaHangMuc());
                    hangMuc.setMaPhieuSuaChua(phieu.getMaPhieuSuaChua());

                    if (!hangMucDichVuDao.themHangMuc(hangMuc)) {
                        throw new SQLException(
                                "Không lưu được hạng mục "
                                        + hangMuc.getTenHangMuc() + ".");
                    }
                }

                conn.commit();
                daKetThucGiaoDich = true;

                return phieu;

            } catch (SQLException | RuntimeException ex) {
                loiGoc = ex;

                try {
                    conn.rollback();
                    daKetThucGiaoDich = true;
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }

                throw ex;

            } finally {
                if (daKetThucGiaoDich) {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException restoreError) {
                        if (loiGoc != null) {
                            loiGoc.addSuppressed(restoreError);
                        } else {
                            throw new SQLException(
                                    "Phiếu đã lưu nhưng không khôi phục được kết nối. "
                                            + "Hãy tải lại dữ liệu trước khi thao tác tiếp.",
                                    restoreError);
                        }
                    }
                }
            }
        }
    }


    public static class DuLieuKhongHopLeException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;

        private final Map<String, String> loiTheoTruong;

        public DuLieuKhongHopLeException(Map<String, String> loiTheoTruong) {
            super("Thông tin phiếu sửa chữa chưa hợp lệ.");

            this.loiTheoTruong = Collections.unmodifiableMap(
                    new LinkedHashMap<>(loiTheoTruong));
        }

        public Map<String, String> getLoiTheoTruong() {
            return loiTheoTruong;
        }
    }
}