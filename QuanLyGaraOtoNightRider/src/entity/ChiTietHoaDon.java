
package entity;

import java.math.BigDecimal;

public class ChiTietHoaDon {

    private String maHD;
    private String maPhuTung;
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal giamGia;
    
    public ChiTietHoaDon() {
        this.soLuong = 1;
        this.donGia = BigDecimal.ZERO;
        this.giamGia = BigDecimal.ZERO;
    }

    public ChiTietHoaDon(String maHD, String maPhuTung, int soLuong, BigDecimal donGia, BigDecimal giamGia) {
        this.maHD = maHD;
        this.maPhuTung = maPhuTung;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.giamGia = giamGia;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaPhuTung() {
        return maPhuTung;
    }

    public void setMaPhuTung(String maPhuTung) {
        this.maPhuTung = maPhuTung;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public BigDecimal getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(BigDecimal giamGia) {
        this.giamGia = giamGia;
    }

    @Override
    public String toString() {
        return maPhuTung + " - SL: " + soLuong;
    }
}
