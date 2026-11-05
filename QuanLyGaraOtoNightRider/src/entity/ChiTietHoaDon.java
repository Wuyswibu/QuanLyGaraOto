package entity;

import java.math.BigDecimal;

public class ChiTietHoaDon {
	private HoaDon maHoaDon;
	private PhuTung maPhuTung;
	private int soLuong;
	private BigDecimal donGia;
	private BigDecimal giamGia;

	public ChiTietHoaDon() {
		this.giamGia = BigDecimal.ZERO;
	}

	public ChiTietHoaDon(HoaDon maHoaDon, PhuTung maPhuTung, int soLuong, BigDecimal donGia, BigDecimal giamGia) {
		super();
		this.maHoaDon = maHoaDon;
		this.maPhuTung = maPhuTung;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.giamGia = giamGia;
	}

	public HoaDon getMaHoaDon() {
		return maHoaDon;
	}

	public void setMaHoaDon(HoaDon maHoaDon) {
		if (maHoaDon == null) {
			throw new IllegalArgumentException("Hóa đơn không được để trống.");
		}

		String ma = maHoaDon.getMaHoaDon();
		if (ma == null || ma.trim().isEmpty() || ma.trim().length() > 30) {
			throw new IllegalArgumentException("Mã hóa đơn không được rỗng và tối đa 30 ký tự.");
		}

		this.maHoaDon = maHoaDon;
	}

	public PhuTung getMaPhuTung() {
		return maPhuTung;
	}

	public void setMaPhuTung(PhuTung maPhuTung) {
		if (maPhuTung == null) {
			throw new IllegalArgumentException("Phụ tùng không được để trống.");
		}

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
		return "CTHoaDon [maHoaDon=" + maHoaDon + ", maPhuTung=" + maPhuTung + ", soLuong=" + soLuong + ", donGia="
				+ donGia + ", giamGia=" + giamGia + "]";
	}
}