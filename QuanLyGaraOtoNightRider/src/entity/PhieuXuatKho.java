package entity;

import java.time.LocalDateTime;

public class PhieuXuatKho {
    private String maPhieuXuat;
    private String maPhieuSuaChua;
    private LocalDateTime ngayXuat;
    private String lyDoXuat;
    private String trangThai;
    private String ghiChu;

    public PhieuXuatKho() {
        this.ngayXuat = LocalDateTime.now();
    }

    public PhieuXuatKho(String maPhieuXuat, LocalDateTime ngayXuat, String lyDoXuat, String trangThai, String ghiChu) {
		this.maPhieuXuat = maPhieuXuat;
		this.ngayXuat = ngayXuat;
		this.lyDoXuat = lyDoXuat;
		this.trangThai = trangThai;
		this.ghiChu = ghiChu;
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
    
    public String getMaPhieuSuaChua() {
        return maPhieuSuaChua;
    }

    public void setMaPhieuSuaChua(String maPhieuSuaChua) {
        if (maPhieuSuaChua == null || maPhieuSuaChua.trim().isEmpty()) {
            this.maPhieuSuaChua = null;
            return;
        }

        if (maPhieuSuaChua.trim().length() > 30) {
            throw new IllegalArgumentException(
                    "Mã phiếu sửa chữa tối đa 30 ký tự.");
        }

        this.maPhieuSuaChua = maPhieuSuaChua.trim();
    }

    public LocalDateTime getNgayXuat() {
        return ngayXuat;
    }

    public void setNgayXuat(LocalDateTime ngayXuat) {
        if (ngayXuat == null) {
            throw new IllegalArgumentException("Ngày xuất không được để trống.");
        }

        if (ngayXuat.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày xuất không được ở tương lai.");
        }

        this.ngayXuat = ngayXuat;
    }

    public String getLyDoXuat() {
        return lyDoXuat;
    }

    public void setLyDoXuat(String lyDoXuat) {
        if (lyDoXuat == null || lyDoXuat.trim().isEmpty()) {
            this.lyDoXuat = null;
            return;
        }

        if (lyDoXuat.trim().length() > 255) {
            throw new IllegalArgumentException("Lý do xuất tối đa 255 ký tự.");
        }
        this.lyDoXuat = lyDoXuat.trim();
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

    @Override
    public String toString() {
        return "PhieuXuatKho [maPhieuXuat=" + maPhieuXuat
                + ", ngayXuat=" + ngayXuat
                + ", lyDoXuat=" + lyDoXuat
                + ", trangThai=" + trangThai
                + ", ghiChu=" + ghiChu + "]";
    }
}