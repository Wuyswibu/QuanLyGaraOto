package entity;

import java.time.LocalDateTime;

public class PhieuSuaChua {
    private String maPhieuSuaChua;
    private String maTiepNhan;
    private LocalDateTime ngayLap;
    private String chanDoan;
    private String loaiPhieu;
    private String trangThai;
    private String ghiChu;

    public PhieuSuaChua() {
        this.ngayLap = LocalDateTime.now();
    }

    public PhieuSuaChua(String maPhieuSuaChua, String maTiepNhan, LocalDateTime ngayLap, String chanDoan, String loaiPhieu, String trangThai, String ghiChu) {
        setMaPhieuSuaChua(maPhieuSuaChua);
        setMaTiepNhan(maTiepNhan);
        setNgayLap(ngayLap);
        setChanDoan(chanDoan);
        setLoaiPhieu(loaiPhieu);
        setTrangThai(trangThai);
        setGhiChu(ghiChu);
    }

    public String getMaPhieuSuaChua() {
        return maPhieuSuaChua;
    }

    public void setMaPhieuSuaChua(String maPhieuSuaChua) {
        if (maPhieuSuaChua == null || maPhieuSuaChua.trim().isEmpty() || maPhieuSuaChua.trim().length() > 30) {
            throw new IllegalArgumentException(
                    "Mã phiếu sửa chữa không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhieuSuaChua = maPhieuSuaChua.trim();
    }

    public String getMaTiepNhan() {
        return maTiepNhan;
    }

    public void setMaTiepNhan(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty() || maTiepNhan.trim().length() > 30) {
            throw new IllegalArgumentException("Mã tiếp nhận không được rỗng và tối đa 30 ký tự.");
        }
        this.maTiepNhan = maTiepNhan.trim();
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

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String chanDoan) {
        if (chanDoan == null || chanDoan.trim().isEmpty()) {
            this.chanDoan = null;
            return;
        }

        if (chanDoan.trim().length() > 2000) {
            throw new IllegalArgumentException("Chẩn đoán tối đa 2000 ký tự.");
        }

        this.chanDoan = chanDoan.trim();
    }

    public String getLoaiPhieu() {
        return loaiPhieu;
    }

    public void setLoaiPhieu(String loaiPhieu) {
        if (loaiPhieu == null || loaiPhieu.trim().isEmpty() || loaiPhieu.trim().length() > 20) {
            throw new IllegalArgumentException("Loại phiếu không được rỗng và tối đa 20 ký tự.");
        }
        this.loaiPhieu = loaiPhieu.trim();
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
        return "PhieuSuaChua [maPhieuSuaChua=" + maPhieuSuaChua
                + ", maTiepNhan=" + maTiepNhan
                + ", ngayLap=" + ngayLap
                + ", chanDoan=" + chanDoan
                + ", loaiPhieu=" + loaiPhieu
                + ", trangThai=" + trangThai
                + ", ghiChu=" + ghiChu + "]";
    }
}