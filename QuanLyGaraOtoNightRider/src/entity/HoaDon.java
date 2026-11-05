package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDon {
    public static final String CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String DA_THANH_TOAN = "Đã thanh toán";
    public static final String DA_HUY = "Đã hủy";

    private String maHoaDon;
    private PhieuSuaChua maPhieuSuaChua;
    private KhachHang maKhachHang;
    private Xe maPhuongTien;
    private NhanVien maNhanVien;
    private LocalDateTime ngayLap;
    private BigDecimal vat;
    private String trangThai;

    public HoaDon() {
        this.ngayLap = LocalDateTime.now();
        this.trangThai = CHUA_THANH_TOAN;
    }

    public HoaDon(String maHoaDon, PhieuSuaChua maPhieuSuaChua, KhachHang maKhachHang, Xe maPhuongTien, NhanVien maNhanVien, LocalDateTime ngayLap, BigDecimal vat, String trangThai) {
        this.maHoaDon = maHoaDon;
        this.maPhieuSuaChua = maPhieuSuaChua;
        this.maKhachHang = maKhachHang;
        this.maPhuongTien = maPhuongTien;
        this.maNhanVien = maNhanVien;
        this.ngayLap = ngayLap;
        setTrangThai(trangThai);
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty() || maHoaDon.trim().length() > 30) {
            throw new IllegalArgumentException("Mã hóa đơn không được rỗng và tối đa 30 ký tự.");
        }
        this.maHoaDon = maHoaDon.trim();
    }

    public PhieuSuaChua getMaPhieuSuaChua() {
        return maPhieuSuaChua;
    }

    public void setMaPhieuSuaChua(PhieuSuaChua maPhieuSuaChua) {
    	String ma = maPhieuSuaChua.getMaPhieuSuaChua();
        if (ma == null || ma.trim().isEmpty() || ma.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phiếu sửa chữa không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhieuSuaChua = maPhieuSuaChua;
    }

    public KhachHang getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(KhachHang maKhachHang) {
    	String ma = maKhachHang.getMaKhachHang();
        if (ma == null || ma.trim().isEmpty() || ma.trim().length() > 30) {
            throw new IllegalArgumentException("Mã khách hàng không được rỗng và tối đa 30 ký tự.");
        }
        this.maKhachHang = maKhachHang;
    }

    public Xe getMaPhuongTien() {
        return maPhuongTien;
    }

    public void setMaPhuongTien(Xe maPhuongTien) {
    	String ma = maPhuongTien.getMaPhuongTien();
        if (ma == null || ma.trim().isEmpty() || ma.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phương tiện không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhuongTien = maPhuongTien;
    }

    public NhanVien getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(NhanVien maNhanVien) {
    	String ma = maNhanVien.getMaNhanVien();
        if (ma == null || ma.trim().isEmpty() || ma.trim().length() > 30) {
            throw new IllegalArgumentException("Mã nhân viên không được rỗng và tối đa 30 ký tự.");
        }
        this.maNhanVien = maNhanVien;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        if (ngayLap == null) {
            throw new IllegalArgumentException("Ngày lập không được để trống.");
        }

        if (ngayLap.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày lập không được ở tương lai.");
        }

        this.ngayLap = ngayLap;
    }

    public String getTrangThai() {
        return trangThai;
    }
    
    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        if (vat == null) {
            throw new IllegalArgumentException(
                    "VAT không được để trống.");
        }

        if (vat.compareTo(BigDecimal.ZERO) < 0
                || vat.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(
                    "VAT phải từ 0 đến 100.");
        }

        if (vat.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException(
                    "VAT tối đa 2 chữ số thập phân.");
        }

        this.vat = vat;
    }
    
    public void setTrangThai(String trangThai) {
        if (trangThai == null) {
            throw new IllegalArgumentException("Trạng thái hóa đơn không được để trống.");
        }

        String tt = trangThai.trim();

        if (!CHUA_THANH_TOAN.equals(tt) && !DA_THANH_TOAN.equals(tt) && !DA_HUY.equals(tt)) {
            throw new IllegalArgumentException("Trạng thái hóa đơn phải là Chưa thanh toán, Đã thanh toán hoặc Đã hủy.");
        }
        this.trangThai = tt;
    }

	@Override
	public String toString() {
		return "HoaDon [maHoaDon=" + maHoaDon + ", maPhieuSuaChua=" + maPhieuSuaChua + ", maKhachHang=" + maKhachHang
				+ ", maPhuongTien=" + maPhuongTien + ", maNhanVien=" + maNhanVien + ", ngayLap=" + ngayLap + ", vat="
				+ vat + ", trangThai=" + trangThai + "]";
	}

    
    
}