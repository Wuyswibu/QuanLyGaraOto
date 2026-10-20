package entity;

import java.math.BigDecimal;

public class ChiTietPhieuNhap {
    private String maPhieuNhap;
    private String maPhuTung;
    private int soLuong;
    private BigDecimal donGia;

    public ChiTietPhieuNhap() {
    }

    public ChiTietPhieuNhap(String maPhieuNhap, String maPhuTung, int soLuong, BigDecimal donGia) {
		this.maPhieuNhap = maPhieuNhap;
		this.maPhuTung = maPhuTung;
		this.soLuong = soLuong;
		this.donGia = donGia;
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
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
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

    @Override
    public String toString() {
        return "ChiTietPhieuNhap [maPhieuNhap=" + maPhieuNhap
                + ", maPhuTung=" + maPhuTung
                + ", soLuong=" + soLuong
                + ", donGia=" + donGia + "]";
    }
}