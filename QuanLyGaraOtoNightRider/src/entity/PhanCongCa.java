package entity;

import java.time.LocalDate;

public class PhanCongCa {
    private String maPhanCongCa;
    private String maNhanVien;
    private String maCa;
    private LocalDate ngayLamViec;
    private String trangThai;
    private String ghiChu;

    public PhanCongCa() {
    }

    public PhanCongCa(String maPhanCongCa, String maNhanVien, String maCa, LocalDate ngayLamViec, String trangThai, String ghiChu) {
        setMaPhanCongCa(maPhanCongCa);
        setMaNhanVien(maNhanVien);
        setMaCa(maCa);
        setNgayLamViec(ngayLamViec);
        setTrangThai(trangThai);
        setGhiChu(ghiChu);
    }

    public String getMaPhanCongCa() {
        return maPhanCongCa;
    }

    public void setMaPhanCongCa(String maPhanCongCa) {
        if (maPhanCongCa == null || maPhanCongCa.trim().isEmpty() || maPhanCongCa.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phân công ca không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhanCongCa = maPhanCongCa.trim();
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

    public String getMaCa() {
        return maCa;
    }

    public void setMaCa(String maCa) {
        if (maCa == null || maCa.trim().isEmpty() || maCa.trim().length() > 30) {
            throw new IllegalArgumentException("Mã ca không được rỗng và tối đa 30 ký tự.");
        }
        this.maCa = maCa.trim();
    }

    public LocalDate getNgayLamViec() {
        return ngayLamViec;
    }

    public void setNgayLamViec(LocalDate ngayLamViec) {
        if (ngayLamViec == null) {
            throw new IllegalArgumentException("Ngày làm việc không được để trống.");
        }
        this.ngayLamViec = ngayLamViec;
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

        if (ghiChu.trim().length() > 255) {
            throw new IllegalArgumentException("Ghi chú tối đa 255 ký tự.");
        }
        this.ghiChu = ghiChu.trim();
    }

    @Override
    public String toString() {
        return "PhanCongCa [maPhanCongCa=" + maPhanCongCa
                + ", maNhanVien=" + maNhanVien
                + ", maCa=" + maCa
                + ", ngayLamViec=" + ngayLamViec
                + ", trangThai=" + trangThai
                + ", ghiChu=" + ghiChu + "]";
    }
}