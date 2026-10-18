package entity;

import java.math.BigDecimal;

public class HangMucDichVu {
    private String maHangMuc;
    private String maPhieuSuaChua;
    private String maDichVu;
    private String tenHangMuc;
    private String moTa;
    private String trangThai;
    private BigDecimal chiPhiDuKien;
    private BigDecimal chiPhiThucTe;
    private String ketQua;
    private String ghiChu;

    public HangMucDichVu() {
        this.chiPhiDuKien = BigDecimal.ZERO;
        this.chiPhiThucTe = BigDecimal.ZERO;
    }

    public HangMucDichVu(String maHangMuc, String maPhieuSuaChua, String maDichVu, String tenHangMuc, String moTa, String trangThai, BigDecimal chiPhiDuKien,
            BigDecimal chiPhiThucTe, String ketQua, String ghiChu) {
        setMaHangMuc(maHangMuc);
        setMaPhieuSuaChua(maPhieuSuaChua);
        setMaDichVu(maDichVu);
        setTenHangMuc(tenHangMuc);
        setMoTa(moTa);
        setTrangThai(trangThai);
        setChiPhiDuKien(chiPhiDuKien);
        setChiPhiThucTe(chiPhiThucTe);
        setKetQua(ketQua);
        setGhiChu(ghiChu);
    }

    public String getMaHangMuc() {
        return maHangMuc;
    }

    public void setMaHangMuc(String maHangMuc) {
        if (maHangMuc == null || maHangMuc.trim().isEmpty() || maHangMuc.trim().length() > 30) {
            throw new IllegalArgumentException("Mã hạng mục không được rỗng và tối đa 30 ký tự.");
        }
        this.maHangMuc = maHangMuc.trim();
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

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        if (maDichVu == null || maDichVu.trim().isEmpty() || maDichVu.trim().length() > 30) {
            throw new IllegalArgumentException("Mã dịch vụ không được rỗng và tối đa 30 ký tự.");
        }
        this.maDichVu = maDichVu.trim();
    }

    public String getTenHangMuc() {
        return tenHangMuc;
    }

    public void setTenHangMuc(String tenHangMuc) {
        if (tenHangMuc == null || tenHangMuc.trim().isEmpty() || tenHangMuc.trim().length() > 150) {
            throw new IllegalArgumentException("Tên hạng mục không được rỗng và tối đa 150 ký tự.");
        }
        this.tenHangMuc = tenHangMuc.trim();
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        if (moTa == null || moTa.trim().isEmpty()) {
            this.moTa = null;
            return;
        }

        if (moTa.trim().length() > 2000) {
            throw new IllegalArgumentException("Mô tả tối đa 2000 ký tự.");
        }
        this.moTa = moTa.trim();
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

    public BigDecimal getChiPhiDuKien() {
        return chiPhiDuKien;
    }

    public void setChiPhiDuKien(BigDecimal chiPhiDuKien) {
        if (chiPhiDuKien == null) {
            throw new IllegalArgumentException("Chi phí dự kiến không được để trống.");
        }

        if (chiPhiDuKien.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Chi phí dự kiến không được âm.");
        }
        this.chiPhiDuKien = chiPhiDuKien;
    }

    public BigDecimal getChiPhiThucTe() {
        return chiPhiThucTe;
    }

    public void setChiPhiThucTe(BigDecimal chiPhiThucTe) {
        if (chiPhiThucTe == null) {
            throw new IllegalArgumentException("Chi phí thực tế không được để trống.");
        }

        if (chiPhiThucTe.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Chi phí thực tế không được âm.");
        }
        this.chiPhiThucTe = chiPhiThucTe;
    }

    public String getKetQua() {
        return ketQua;
    }

    public void setKetQua(String ketQua) {
        if (ketQua == null || ketQua.trim().isEmpty()) {
            this.ketQua = null;
            return;
        }

        if (ketQua.trim().length() > 2000) {
            throw new IllegalArgumentException("Kết quả tối đa 2000 ký tự.");
        }
        this.ketQua = ketQua.trim();
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
        return "HangMucDichVu [maHangMuc=" + maHangMuc
                + ", maPhieuSuaChua=" + maPhieuSuaChua
                + ", maDichVu=" + maDichVu
                + ", tenHangMuc=" + tenHangMuc
                + ", moTa=" + moTa
                + ", trangThai=" + trangThai
                + ", chiPhiDuKien=" + chiPhiDuKien
                + ", chiPhiThucTe=" + chiPhiThucTe
                + ", ketQua=" + ketQua
                + ", ghiChu=" + ghiChu + "]";
    }
}