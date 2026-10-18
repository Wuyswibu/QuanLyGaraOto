package entity;

import java.math.BigDecimal;

public class PhuTung {
    private String maPhuTung;
    private String maPhieuSuaChua;
    private String tenPhuTung;
    private String moTa;
    private String donViTinh;
    private BigDecimal donGia;
    private int soLuongTon;
    private int mucTonToiThieu;
    private String trangThai;

    public PhuTung() {
        this.donGia = BigDecimal.ZERO;
    }

    public PhuTung(String maPhuTung, String maPhieuSuaChua, String tenPhuTung, String moTa, String donViTinh, BigDecimal donGia, int soLuongTon, int mucTonToiThieu, String trangThai) {
        setMaPhuTung(maPhuTung);
        setMaPhieuSuaChua(maPhieuSuaChua);
        setTenPhuTung(tenPhuTung);
        setMoTa(moTa);
        setDonViTinh(donViTinh);
        setDonGia(donGia);
        setSoLuongTon(soLuongTon);
        setMucTonToiThieu(mucTonToiThieu);
        setTrangThai(trangThai);
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

    public String getMaPhieuSuaChua() {
        return maPhieuSuaChua;
    }

    public void setMaPhieuSuaChua(String maPhieuSuaChua) {
        if (maPhieuSuaChua == null || maPhieuSuaChua.trim().isEmpty()) {
            this.maPhieuSuaChua = null;
            return;
        }

        if (maPhieuSuaChua.trim().length() > 30) {
            throw new IllegalArgumentException( "Mã phiếu sửa chữa tối đa 30 ký tự.");
        }
        this.maPhieuSuaChua = maPhieuSuaChua.trim();
    }

    public String getTenPhuTung() {
        return tenPhuTung;
    }

    public void setTenPhuTung(String tenPhuTung) {
        if (tenPhuTung == null || tenPhuTung.trim().isEmpty() || tenPhuTung.trim().length() > 150) {
            throw new IllegalArgumentException("Tên phụ tùng không được rỗng và tối đa 150 ký tự.");
        }
        this.tenPhuTung = tenPhuTung.trim();
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        if (moTa == null || moTa.trim().isEmpty()) {
            this.moTa = null;
            return;
        }

        if (moTa.trim().length() > 1000) {
            throw new IllegalArgumentException("Mô tả tối đa 1000 ký tự.");
        }
        this.moTa = moTa.trim();
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        if (donViTinh == null || donViTinh.trim().isEmpty() || donViTinh.trim().length() > 30) {
            throw new IllegalArgumentException("Đơn vị tính không được rỗng và tối đa 30 ký tự.");
        }
        this.donViTinh = donViTinh.trim();
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

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0) {
            throw new IllegalArgumentException("Số lượng tồn không được âm.");
        }
        this.soLuongTon = soLuongTon;
    }

    public int getMucTonToiThieu() {
        return mucTonToiThieu;
    }

    public void setMucTonToiThieu(int mucTonToiThieu) {
        if (mucTonToiThieu < 0) {
            throw new IllegalArgumentException("Mức tồn tối thiểu không được âm.");
        }
        this.mucTonToiThieu = mucTonToiThieu;
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
        return "PhuTung [maPhuTung=" + maPhuTung
                + ", maPhieuSuaChua=" + maPhieuSuaChua
                + ", tenPhuTung=" + tenPhuTung
                + ", moTa=" + moTa
                + ", donViTinh=" + donViTinh
                + ", donGia=" + donGia
                + ", soLuongTon=" + soLuongTon
                + ", mucTonToiThieu=" + mucTonToiThieu
                + ", trangThai=" + trangThai + "]";
    }
}