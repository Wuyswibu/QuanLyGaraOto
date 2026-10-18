package entity;

import java.math.BigDecimal;

public class ChiTietHoaDon {
    private String maHoaDon;
    private String maPhuTung;
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal giamGia;

    public ChiTietHoaDon() {
        this.giamGia = BigDecimal.ZERO;
    }

    public ChiTietHoaDon(String maHoaDon, String maPhuTung, int soLuong, BigDecimal donGia, BigDecimal giamGia) {
        setMaHoaDon(maHoaDon);
        setMaPhuTung(maPhuTung);
        setSoLuong(soLuong);
        setDonGia(donGia);
        setGiamGia(giamGia);
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

    public String getMaPhuTung() {
        return maPhuTung;
    }

    public void setMaPhuTung(String maPhuTung) {
        if (maPhuTung == null || maPhuTung.trim().isEmpty() || maPhuTung.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phụ tùng không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhuTung = maPhuTung.trim();
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        if (donGia == null) {
            throw new IllegalArgumentException("Đơn giá không được để trống.");
        }

        if (donGia.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá không được âm.");
        }

        this.donGia = donGia;
    }

    public BigDecimal getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(BigDecimal giamGia) {
        if (giamGia == null) {
            throw new IllegalArgumentException("Giảm giá không được để trống.");
        }

        if (giamGia.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giảm giá không được âm.");
        }

        this.giamGia = giamGia;
    }

    @Override
    public String toString() {
        return "CTHoaDon [maHoaDon=" + maHoaDon
                + ", maPhuTung=" + maPhuTung
                + ", soLuong=" + soLuong
                + ", donGia=" + donGia
                + ", giamGia=" + giamGia + "]";
    }
}