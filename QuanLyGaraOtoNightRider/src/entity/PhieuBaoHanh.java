package entity;

import java.time.LocalDateTime;

public class PhieuBaoHanh {
    private String maBaoHanh;
    private String maPhieuSuaChua;
    private String maPhuongTien;
    private LocalDateTime ngayYeuCau;
    private String noiDungYeuCau;
    private String ketQuaKiemTra;
    private String ketQuaXuLy;
    private String lyDoTuChoi;
    private String trangThai;

    public PhieuBaoHanh() {
        this.ngayYeuCau = LocalDateTime.now();
    }

    public PhieuBaoHanh(String maBaoHanh, String maPhieuSuaChua, String maPhuongTien, LocalDateTime ngayYeuCau, String noiDungYeuCau, String ketQuaKiemTra, String ketQuaXuLy, String lyDoTuChoi, String trangThai) {
		this.maBaoHanh = maBaoHanh;
		this.maPhieuSuaChua = maPhieuSuaChua;
		this.maPhuongTien = maPhuongTien;
		this.ngayYeuCau = ngayYeuCau;
		this.noiDungYeuCau = noiDungYeuCau;
		this.ketQuaKiemTra = ketQuaKiemTra;
		this.ketQuaXuLy = ketQuaXuLy;
		this.lyDoTuChoi = lyDoTuChoi;
		this.trangThai = trangThai;
	}

	public String getMaBaoHanh() {
        return maBaoHanh;
    }

    public void setMaBaoHanh(String maBaoHanh) {
        if (maBaoHanh == null || maBaoHanh.trim().isEmpty() || maBaoHanh.trim().length() > 30) {
            throw new IllegalArgumentException("Mã bảo hành không được rỗng và tối đa 30 ký tự.");
        }
        this.maBaoHanh = maBaoHanh.trim();
    }

    public String getMaPhieuSuaChua() {
        return maPhieuSuaChua;
    }

    public void setMaPhieuSuaChua(String maPhieuSuaChua) {
        if (maPhieuSuaChua == null || maPhieuSuaChua.trim().isEmpty() || maPhieuSuaChua.trim().length() > 30) {
            throw new IllegalArgumentException(
                    "Mã phiếu sửa chữa không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhieuSuaChua = maPhieuSuaChua.trim();
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

    public LocalDateTime getNgayYeuCau() {
        return ngayYeuCau;
    }

    public void setNgayYeuCau(LocalDateTime ngayYeuCau) {
        if (ngayYeuCau == null) {
            throw new IllegalArgumentException("Ngày yêu cầu không được để trống.");
        }

        if (ngayYeuCau.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày yêu cầu không được ở tương lai.");
        }

        this.ngayYeuCau = ngayYeuCau;
    }

    public String getNoiDungYeuCau() {
        return noiDungYeuCau;
    }

    public void setNoiDungYeuCau(String noiDungYeuCau) {
        if (noiDungYeuCau == null || noiDungYeuCau.trim().isEmpty() || noiDungYeuCau.trim().length() > 2000) {
            throw new IllegalArgumentException(
                    "Nội dung yêu cầu không được rỗng và tối đa 2000 ký tự.");
        }
        this.noiDungYeuCau = noiDungYeuCau.trim();
    }

    public String getKetQuaKiemTra() {
        return ketQuaKiemTra;
    }

    public void setKetQuaKiemTra(String ketQuaKiemTra) {
        if (ketQuaKiemTra == null || ketQuaKiemTra.trim().isEmpty()) {
            this.ketQuaKiemTra = null;
            return;
        }

        if (ketQuaKiemTra.trim().length() > 2000) {
            throw new IllegalArgumentException("Kết quả kiểm tra tối đa 2000 ký tự.");
        }

        this.ketQuaKiemTra = ketQuaKiemTra.trim();
    }

    public String getKetQuaXuLy() {
        return ketQuaXuLy;
    }

    public void setKetQuaXuLy(String ketQuaXuLy) {
        if (ketQuaXuLy == null || ketQuaXuLy.trim().isEmpty()) {
            this.ketQuaXuLy = null;
            return;
        }

        if (ketQuaXuLy.trim().length() > 2000) {
            throw new IllegalArgumentException("Kết quả xử lý tối đa 2000 ký tự.");
        }

        this.ketQuaXuLy = ketQuaXuLy.trim();
    }

    public String getLyDoTuChoi() {
        return lyDoTuChoi;
    }

    public void setLyDoTuChoi(String lyDoTuChoi) {
        if (lyDoTuChoi == null || lyDoTuChoi.trim().isEmpty()) {
            this.lyDoTuChoi = null;
            return;
        }

        if (lyDoTuChoi.trim().length() > 1000) {
            throw new IllegalArgumentException("Lý do từ chối tối đa 1000 ký tự.");
        }

        this.lyDoTuChoi = lyDoTuChoi.trim();
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
        return "PhieuBaoHanh [maBaoHanh=" + maBaoHanh
                + ", maPhieuSuaChua=" + maPhieuSuaChua
                + ", maPhuongTien=" + maPhuongTien
                + ", ngayYeuCau=" + ngayYeuCau
                + ", noiDungYeuCau=" + noiDungYeuCau
                + ", ketQuaKiemTra=" + ketQuaKiemTra
                + ", ketQuaXuLy=" + ketQuaXuLy
                + ", lyDoTuChoi=" + lyDoTuChoi
                + ", trangThai=" + trangThai + "]";
    }
}