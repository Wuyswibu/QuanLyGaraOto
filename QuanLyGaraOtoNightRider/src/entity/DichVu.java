package entity;

import java.math.BigDecimal;

public class DichVu {
    private String maDichVu;
    private String tenDichVu;
    private String moTa;
    private BigDecimal donGia;
    private boolean trangThai;

    public DichVu() {
        this.trangThai = true;
    }

    public DichVu(String maDichVu, String tenDichVu, String moTa, BigDecimal donGia, boolean trangThai) {
		this.maDichVu = maDichVu;
		this.tenDichVu = tenDichVu;
		this.moTa = moTa;
		this.donGia = donGia;
		this.trangThai = trangThai;
	}

	public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        if (maDichVu == null || maDichVu.trim().isEmpty() || maDichVu.trim().length() > 30) {
            throw new IllegalArgumentException("Mã dịch vụ không được rỗng và tối đa 30 ký tự.");
        }
        this.maDichVu = maDichVu.trim();
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        if (tenDichVu == null || tenDichVu.trim().isEmpty() || tenDichVu.trim().length() > 150) {
            throw new IllegalArgumentException("Tên dịch vụ không được rỗng và tối đa 150 ký tự.");
        }
        this.tenDichVu = tenDichVu.trim();
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

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        if (donGia == null) {
            throw new IllegalArgumentException( "Đơn giá không được để trống.");
        }

        if (donGia.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException( "Đơn giá không được âm.");
        }

        this.donGia = donGia;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "DichVu [maDichVu=" + maDichVu
                + ", tenDichVu=" + tenDichVu
                + ", moTa=" + moTa
                + ", donGia=" + donGia
                + ", trangThai=" + trangThai + "]";
    }
}