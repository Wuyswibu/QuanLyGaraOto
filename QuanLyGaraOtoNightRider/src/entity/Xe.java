package entity;

public class Xe {
    private String maPhuongTien;
    private String maKhachHang;
    private String bienSo;
    private String mauXe;
    private String trangThai;

    public Xe() {
    }

    public Xe(String maPhuongTien, String maKhachHang, String bienSo, String mauXe, String trangThai) {
        this.maPhuongTien = maPhuongTien;
        this.maKhachHang = maKhachHang;
        this.bienSo = bienSo;
        this.mauXe = mauXe;
        this.trangThai = trangThai;
    }

    public String getMaPhuongTien() {
        return maPhuongTien;
    }

    public void setMaPhuongTien(String maPhuongTien) {
        this.maPhuongTien = maPhuongTien;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
    }

    public String getMauXe() {
        return mauXe;
    }

    public void setMauXe(String mauXe) {
        this.mauXe = mauXe;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "Xe [maPhuongTien=" + maPhuongTien
                + ", maKhachHang=" + maKhachHang
                + ", bienSo=" + bienSo
                + ", mauXe=" + mauXe
                + ", trangThai=" + trangThai + "]";
    }
}