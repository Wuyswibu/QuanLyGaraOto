package entity;

import java.time.LocalDateTime;

public class HoaDon {
    private String maHoaDon;
    private String maPhieuSuaChua;
    private String maKhachHang;
    private String maPhuongTien;
    private String maNhanVien;
    private LocalDateTime ngayLap;
    private String trangThai;

    public HoaDon() {
        this.ngayLap = LocalDateTime.now();
    }

    public HoaDon(String maHoaDon, String maPhieuSuaChua, String maKhachHang, String maPhuongTien, String maNhanVien, LocalDateTime ngayLap, String trangThai) {
        setMaHoaDon(maHoaDon);
        setMaPhieuSuaChua(maPhieuSuaChua);
        setMaKhachHang(maKhachHang);
        setMaPhuongTien(maPhuongTien);
        setMaNhanVien(maNhanVien);
        setNgayLap(ngayLap);
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

    public String getMaPhieuSuaChua() {
        return maPhieuSuaChua;
    }

    public void setMaPhieuSuaChua(String maPhieuSuaChua) {
        if (maPhieuSuaChua == null || maPhieuSuaChua.trim().isEmpty() || maPhieuSuaChua.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phiếu sửa chữa không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhieuSuaChua = maPhieuSuaChua.trim();
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty() || maKhachHang.trim().length() > 30) {
            throw new IllegalArgumentException("Mã khách hàng không được rỗng và tối đa 30 ký tự.");
        }
        this.maKhachHang = maKhachHang.trim();
    }

    public String getMaPhuongTien() {
        return maPhuongTien;
    }

    public void setMaPhuongTien(String maPhuongTien) {
        if (maPhuongTien == null || maPhuongTien.trim().isEmpty() || maPhuongTien.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phương tiện không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhuongTien = maPhuongTien.trim();
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty() || maNhanVien.trim().length() > 30) {
            throw new IllegalArgumentException("Mã nhân viên không được rỗng và tối đa 30 ký tự.");
        }
        this.maNhanVien = maNhanVien.trim();
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

    public void setTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty() || trangThai.trim().length() > 40) {
            throw new IllegalArgumentException("Trạng thái không được rỗng và tối đa 40 ký tự.");
        }
        this.trangThai = trangThai.trim();
    }

    @Override
    public String toString() {
        return "HoaDon [maHoaDon=" + maHoaDon
                + ", maPhieuSuaChua=" + maPhieuSuaChua
                + ", maKhachHang=" + maKhachHang
                + ", maPhuongTien=" + maPhuongTien
                + ", maNhanVien=" + maNhanVien
                + ", ngayLap=" + ngayLap
                + ", trangThai=" + trangThai + "]";
    }
}