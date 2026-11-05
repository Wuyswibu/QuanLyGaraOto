package entity;

import java.math.BigDecimal;

public class ChiTietPhieuNhap {
    private PhieuNhapKho maPhieuNhap;
    private PhuTung maPhuTung;
    private int soLuong;
    private BigDecimal donGia;

    public ChiTietPhieuNhap() {
    }

    public ChiTietPhieuNhap(PhieuNhapKho maPhieuNhap, PhuTung maPhuTung, int soLuong, BigDecimal donGia) {
		this.maPhieuNhap = maPhieuNhap;
		this.maPhuTung = maPhuTung;
		this.soLuong = soLuong;
		this.donGia = donGia;
	}

	public PhieuNhapKho getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(PhieuNhapKho maPhieuNhap) {
    	
    	String ma = maPhieuNhap.getMaPhieuNhap();
        if (ma == null || ma.trim().isEmpty() || ma.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phiếu nhập không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhieuNhap = maPhieuNhap;
    }

    public PhuTung getMaPhuTung() {
        return maPhuTung;
    }

    public void setMaPhuTung(PhuTung maPhuTung) {
    	String ma = maPhuTung.getMaPhuTung();
        if (ma == null || ma.trim().isEmpty() || ma.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phụ tùng không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhuTung = maPhuTung;
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