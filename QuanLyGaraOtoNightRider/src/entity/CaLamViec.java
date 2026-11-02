package entity;

import java.time.LocalTime;

public class CaLamViec {
    private String maCa;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private boolean trangThai;

    public CaLamViec() {
        this.trangThai = true;
    }
    
    public CaLamViec(String maCa, String tenCa, LocalTime gioBatDau, LocalTime gioKetThuc, boolean trangThai) {
		this.maCa = maCa;
		this.tenCa = tenCa;
		this.gioBatDau = gioBatDau;
		this.gioKetThuc = gioKetThuc;
		this.trangThai = trangThai;
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

    public String getTenCa() {
        return tenCa;
    }

    public void setTenCa(String tenCa) {
        if (tenCa == null || tenCa.trim().isEmpty() || tenCa.trim().length() > 80) {
            throw new IllegalArgumentException("Tên ca không được rỗng và tối đa 80 ký tự.");
        }
        this.tenCa = tenCa.trim();
    }

    public LocalTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalTime gioBatDau) {
        if (gioBatDau == null) {
            throw new IllegalArgumentException("Giờ bắt đầu không được để trống.");
        }
        this.gioBatDau = gioBatDau;
    }

    public LocalTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalTime gioKetThuc) {
        if (gioKetThuc == null) {
            throw new IllegalArgumentException("Giờ kết thúc không được để trống.");
        }
        this.gioKetThuc = gioKetThuc;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "CaLamViec [maCa=" + maCa
                + ", tenCa=" + tenCa
                + ", gioBatDau=" + gioBatDau
                + ", gioKetThuc=" + gioKetThuc
                + ", trangThai=" + trangThai + "]";
    }
}