
package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon {

    public static final String CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String DA_THANH_TOAN = "Đã thanh toán";
    public static final String DA_HUY = "Đã hủy";

    private String maHoaDon;
    private String maKhachHang;
    private String maNhanVien;
    private String maPhuongTien;
    private String maPhieuSuaChua;
    private LocalDateTime ngayLap;
    private String trangThai;

    private String tenKhachHang;
    private String soDienThoaiKhach;
    private String diaChiKhach;
    private String tenNhanVien;
    private String bienSo;
    private String loaiXe;

    private BigDecimal tienDichVu;
    private BigDecimal tienPhuTung;
    private BigDecimal giamGia;
    private BigDecimal vat;
    private BigDecimal tongTien;
    private BigDecimal daThanhToan;

    private List<ChiTietHoaDon> details;

    public HoaDon() {
        this.ngayLap = LocalDateTime.now();
        this.trangThai = CHUA_THANH_TOAN;

        this.tienDichVu = BigDecimal.ZERO;
        this.tienPhuTung = BigDecimal.ZERO;
        this.giamGia = BigDecimal.ZERO;
        this.vat = BigDecimal.ZERO;
        this.tongTien = BigDecimal.ZERO;
        this.daThanhToan = BigDecimal.ZERO;

        this.details = new ArrayList<>();
    }

    public HoaDon(String maHoaDon, String maKhachHang, String maNhanVien, String maPhuongTien, String maPhieuSuaChua, LocalDateTime ngayLap, String trangThai) {
        this();

        this.maHoaDon = maHoaDon;
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.maPhuongTien = maPhuongTien;
        this.maPhieuSuaChua = maPhieuSuaChua;
        this.ngayLap = ngayLap;
        setTrangThai(trangThai);
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSoDienThoaiKhach() {
        return soDienThoaiKhach;
    }

    public void setSoDienThoaiKhach(String soDienThoaiKhach) {
        this.soDienThoaiKhach = soDienThoaiKhach;
    }

    public String getDiaChiKhach() {
        return diaChiKhach;
    }

    public void setDiaChiKhach(String diaChiKhach) {
        this.diaChiKhach = diaChiKhach;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getMaPhuongTien() {
        return maPhuongTien;
    }

    public void setMaPhuongTien(String maPhuongTien) {
        this.maPhuongTien = maPhuongTien;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
    }

    public String getLoaiXe() {
        return loaiXe;
    }

    public void setLoaiXe(String loaiXe) {
        this.loaiXe = loaiXe;
    }

    public String getMaPhieuSuaChua() {
        return maPhieuSuaChua;
    }

    public void setMaPhieuSuaChua(String maPhieuSuaChua) {
        this.maPhieuSuaChua = maPhieuSuaChua;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        if (!CHUA_THANH_TOAN.equals(trangThai)
                && !DA_THANH_TOAN.equals(trangThai)
                && !DA_HUY.equals(trangThai)) {
            throw new IllegalArgumentException(
                "Trạng thái hóa đơn không hợp lệ: " + trangThai
            );
        }

        this.trangThai = trangThai;
    }

    public BigDecimal getTienDichVu() {
        return zeroIfNull(tienDichVu);
    }

    public void setTienDichVu(BigDecimal tienDichVu) {
        this.tienDichVu = zeroIfNull(tienDichVu);
    }

    public BigDecimal getTienPhuTung() {
        return zeroIfNull(tienPhuTung);
    }

    public void setTienPhuTung(BigDecimal tienPhuTung) {
        this.tienPhuTung = zeroIfNull(tienPhuTung);
    }

    public BigDecimal getGiamGia() {
        return zeroIfNull(giamGia);
    }

    public void setGiamGia(BigDecimal giamGia) {
        this.giamGia = zeroIfNull(giamGia);
    }

    public BigDecimal getVat() {
        return zeroIfNull(vat);
    }

    public void setVat(BigDecimal vat) {
        this.vat = zeroIfNull(vat);
    }

    public BigDecimal getTongTien() {
        return zeroIfNull(tongTien);
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = zeroIfNull(tongTien);
    }

    public BigDecimal getDaThanhToan() {
        return zeroIfNull(daThanhToan);
    }

    public void setDaThanhToan(BigDecimal daThanhToan) {
        this.daThanhToan = zeroIfNull(daThanhToan);
    }

    public List<ChiTietHoaDon> getDetails() {
        return details;
    }

    public void setDetails(List<ChiTietHoaDon> details) {
        this.details = details == null
                ? new ArrayList<>()
                : new ArrayList<>(details);
    }

    @Override
    public String toString() {
        return maHoaDon + " - " + trangThai;
    }
}
