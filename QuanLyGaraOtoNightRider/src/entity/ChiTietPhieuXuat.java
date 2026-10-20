package entity;

import java.math.BigDecimal;

public class ChiTietPhieuXuat {
    private String maPhieuXuat;
    private String maPhuTung;
    private int soLuong;
    private BigDecimal donGia;

    public ChiTietPhieuXuat() {
    }

    public ChiTietPhieuXuat(String maPhieuXuat, String maPhuTung, int soLuong, BigDecimal donGia) {
		this.maPhieuXuat = maPhieuXuat;
		this.maPhuTung = maPhuTung;
		this.soLuong = soLuong;
		this.donGia = donGia;
	}

	public String getMaPhieuXuat() {
        return maPhieuXuat;
    }

    public void setMaPhieuXuat(String maPhieuXuat) {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty() || maPhieuXuat.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phiếu xuất không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhieuXuat = maPhieuXuat.trim();
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
            throw new IllegalArgumentException("Số lượng xuất phải lớn hơn 0.");
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
        return "ChiTietPhieuXuat [maPhieuXuat=" + maPhieuXuat
                + ", maPhuTung=" + maPhuTung
                + ", soLuong=" + soLuong
                + ", donGia=" + donGia + "]";
    }
}