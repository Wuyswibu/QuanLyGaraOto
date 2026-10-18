package entity;

import java.time.LocalDateTime;

public class PhieuNhapKho {
    private String maPhieuNhap;
    private LocalDateTime ngayNhap;
    private String trangThai;
    private String ghiChu;

    public PhieuNhapKho() {
        this.ngayNhap = LocalDateTime.now();
    }

    public PhieuNhapKho(String maPhieuNhap, LocalDateTime ngayNhap, String trangThai, String ghiChu) {
        setMaPhieuNhap(maPhieuNhap);
        setNgayNhap(ngayNhap);
        setTrangThai(trangThai);
        setGhiChu(ghiChu);
    }

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty() || maPhieuNhap.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phiếu nhập không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhieuNhap = maPhieuNhap.trim();
    }

    public LocalDateTime getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDateTime ngayNhap) {
        if (ngayNhap == null) {
            throw new IllegalArgumentException("Ngày nhập không được để trống.");
        }

        if (ngayNhap.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày nhập không được ở tương lai.");
        }

        this.ngayNhap = ngayNhap;
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

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        if (ghiChu == null || ghiChu.trim().isEmpty()) {
            this.ghiChu = null;
            return;
        }

        if (ghiChu.trim().length() > 1000) {
            throw new IllegalArgumentException("Ghi chú tối đa 1000 ký tự.");
        }
        this.ghiChu = ghiChu.trim();
    }

    @Override
    public String toString() {
        return "PhieuNhapKho [maPhieuNhap=" + maPhieuNhap
                + ", ngayNhap=" + ngayNhap
                + ", trangThai=" + trangThai
                + ", ghiChu=" + ghiChu + "]";
    }
}