package entity;

import java.time.LocalDateTime;

public class PhuongThucThanhToan {
    private String maThanhToan;
    private String maHoaDon;
    private LocalDateTime ngayThanhToan;
    private String phuongThuc;
    private String maGiaoDich;
    private String trangThai;

    public PhuongThucThanhToan() {
        this.ngayThanhToan = LocalDateTime.now();
    }

    public PhuongThucThanhToan(String maThanhToan, String maHoaDon, LocalDateTime ngayThanhToan, String phuongThuc, String maGiaoDich, String trangThai) {
        setMaThanhToan(maThanhToan);
        setMaHoaDon(maHoaDon);
        setNgayThanhToan(ngayThanhToan);
        setPhuongThuc(phuongThuc);
        setMaGiaoDich(maGiaoDich);
        setTrangThai(trangThai);
    }

    public String getMaThanhToan() {
        return maThanhToan;
    }

    public void setMaThanhToan(String maThanhToan) {
        if (maThanhToan == null || maThanhToan.trim().isEmpty() || maThanhToan.trim().length() > 30) {
            throw new IllegalArgumentException("Mã thanh toán không được rỗng và tối đa 30 ký tự.");
        }
        this.maThanhToan = maThanhToan.trim();
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

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        if (ngayThanhToan == null) {
            throw new IllegalArgumentException("Ngày thanh toán không được để trống.");
        }

        if (ngayThanhToan.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày thanh toán không được ở tương lai.");
        }

        this.ngayThanhToan = ngayThanhToan;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        if (phuongThuc == null || phuongThuc.trim().isEmpty() || phuongThuc.trim().length() > 30) {
            throw new IllegalArgumentException(
                    "Phương thức thanh toán không được rỗng và tối đa 30 ký tự.");
        }
        this.phuongThuc = phuongThuc.trim();
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        if (maGiaoDich == null || maGiaoDich.trim().isEmpty()) {
            this.maGiaoDich = null;
            return;
        }

        if (maGiaoDich.trim().length() > 100) {
            throw new IllegalArgumentException("Mã giao dịch tối đa 100 ký tự.");
        }

        this.maGiaoDich = maGiaoDich.trim();
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
        return "PhuongThucThanhToan [maThanhToan=" + maThanhToan
                + ", maHoaDon=" + maHoaDon
                + ", ngayThanhToan=" + ngayThanhToan
                + ", phuongThuc=" + phuongThuc
                + ", maGiaoDich=" + maGiaoDich
                + ", trangThai=" + trangThai + "]";
    }
}