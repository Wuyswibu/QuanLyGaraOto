package entity;

import java.time.LocalDateTime;

public class PhieuTiepNhan {
    private String maTiepNhan;
    private String maPhuongTien;
    private String maNhanVien;
    private LocalDateTime ngayGioTiepNhan;
    private LocalDateTime ngayGioHenTra;
    private int odo;
    private String mucNhienLieu;
    private String tinhTrangBanDau;
    private String yeuCauKhachHang;
    private String ghiChu;
    private String trangThai;

    public PhieuTiepNhan() {
        this.ngayGioTiepNhan = LocalDateTime.now();
    }

    public PhieuTiepNhan(String maTiepNhan, String maPhuongTien, String maNhanVien, LocalDateTime ngayGioTiepNhan, LocalDateTime ngayGioHenTra, int odo, String mucNhienLieu, String tinhTrangBanDau, String yeuCauKhachHang, String ghiChu, String trangThai) {
		this.maTiepNhan = maTiepNhan;
		this.maPhuongTien = maPhuongTien;
		this.maNhanVien = maNhanVien;
		this.ngayGioTiepNhan = ngayGioTiepNhan;
		this.ngayGioHenTra = ngayGioHenTra;
		this.odo = odo;
		this.mucNhienLieu = mucNhienLieu;
		this.tinhTrangBanDau = tinhTrangBanDau;
		this.yeuCauKhachHang = yeuCauKhachHang;
		this.ghiChu = ghiChu;
		this.trangThai = trangThai;
	}

	public String getMaTiepNhan() {
        return maTiepNhan;
    }

    public void setMaTiepNhan(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty() || maTiepNhan.trim().length() > 30) {
            throw new IllegalArgumentException("Mã tiếp nhận không được rỗng và tối đa 30 ký tự.");
        }
        this.maTiepNhan = maTiepNhan.trim();
    }

    public String getMaPhuongTien() {
        return maPhuongTien;
    }

    public void setMaPhuongTien(String maPhuongTien) {
        if (maPhuongTien == null || maPhuongTien.trim().isEmpty() || maPhuongTien.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phương tiện không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhuongTien = maPhuongTien.trim();
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

    public LocalDateTime getNgayGioTiepNhan() {
        return ngayGioTiepNhan;
    }

    public void setNgayGioTiepNhan(LocalDateTime ngayGioTiepNhan) {
        if (ngayGioTiepNhan == null) {
            throw new IllegalArgumentException("Ngày giờ tiếp nhận không được để trống.");
        }

        if (ngayGioTiepNhan.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày giờ tiếp nhận không được ở tương lai.");
        }

        this.ngayGioTiepNhan = ngayGioTiepNhan;
    }

    public LocalDateTime getNgayGioHenTra() {
        return ngayGioHenTra;
    }

    public void setNgayGioHenTra(LocalDateTime ngayGioHenTra) {
        this.ngayGioHenTra = ngayGioHenTra;
    }

    public int getOdo() {
        return odo;
    }

    public void setOdo(int odo) {
        if (odo < 0) {
            throw new IllegalArgumentException("Số km đã đi không được âm.");
        }
        this.odo = odo;
    }

    public String getMucNhienLieu() {
        return mucNhienLieu;
    }

    public void setMucNhienLieu(String mucNhienLieu) {
        if (mucNhienLieu == null || mucNhienLieu.trim().isEmpty() || mucNhienLieu.trim().length() > 20) {
            throw new IllegalArgumentException("Mức nhiên liệu không được rỗng và tối đa 20 ký tự.");
        }
        this.mucNhienLieu = mucNhienLieu.trim();
    }

    public String getTinhTrangBanDau() {
        return tinhTrangBanDau;
    }

    public void setTinhTrangBanDau(String tinhTrangBanDau) {
        if (tinhTrangBanDau == null || tinhTrangBanDau.trim().isEmpty() || tinhTrangBanDau.trim().length() > 2000) {
            throw new IllegalArgumentException("Tình trạng ban đầu không được rỗng và tối đa 2000 ký tự.");
        }
        this.tinhTrangBanDau = tinhTrangBanDau.trim();
    }

    public String getYeuCauKhachHang() {
        return yeuCauKhachHang;
    }

    public void setYeuCauKhachHang(String yeuCauKhachHang) {
        if (yeuCauKhachHang == null || yeuCauKhachHang.trim().isEmpty() || yeuCauKhachHang.trim().length() > 2000) {
            throw new IllegalArgumentException("Yêu cầu khách hàng không được rỗng và tối đa 2000 ký tự.");
        }
        this.yeuCauKhachHang = yeuCauKhachHang.trim();
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        if (ghiChu == null || ghiChu.trim().isEmpty()) {
            this.ghiChu = null;
            return;
        }

        if (ghiChu.trim().length() > 1000) {
            throw new IllegalArgumentException("Ghi chú tối đa 1000 ký tự.");
        }
        this.ghiChu = ghiChu.trim();
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
        return "PhieuTiepNhan [maTiepNhan=" + maTiepNhan
                + ", maPhuongTien=" + maPhuongTien
                + ", maNhanVien=" + maNhanVien
                + ", ngayGioTiepNhan=" + ngayGioTiepNhan
                + ", ngayGioHenTra=" + ngayGioHenTra
                + ", odo=" + odo
                + ", mucNhienLieu=" + mucNhienLieu
                + ", tinhTrangBanDau=" + tinhTrangBanDau
                + ", yeuCauKhachHang=" + yeuCauKhachHang
                + ", ghiChu=" + ghiChu
                + ", trangThai=" + trangThai + "]";
    }
}