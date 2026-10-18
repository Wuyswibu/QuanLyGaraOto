package entity;

public class Xe {
    public static final String HOAT_DONG = "Hoạt động";
    public static final String NGUNG_THEO_DOI = "Ngừng theo dõi";

    private String maPhuongTien;
    private String maKhachHang;
    private String bienSo;
    private String mauXe;
    private String trangThai;

    public Xe() {
        this.trangThai = HOAT_DONG;
    }

    public Xe(String maPhuongTien, String maKhachHang, String bienSo, String mauXe, String trangThai) {
        setMaPhuongTien(maPhuongTien);
        setMaKhachHang(maKhachHang);
        setBienSo(bienSo);
        setMauXe(mauXe);
        setTrangThai(trangThai);
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

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty() || maKhachHang.trim().length() > 30) {
            throw new IllegalArgumentException("Mã khách hàng không được rỗng và tối đa 30 ký tự.");
        }
        this.maKhachHang = maKhachHang.trim();
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        if (bienSo == null || bienSo.trim().isEmpty() || bienSo.trim().length() > 20) {
            throw new IllegalArgumentException("Biển số không được rỗng và tối đa 20 ký tự.");
        }
        this.bienSo = bienSo.trim();
    }

    public String getMauXe() {
        return mauXe;
    }

    public void setMauXe(String mauXe) {
        if (mauXe == null || mauXe.trim().isEmpty() || mauXe.trim().length() > 100) {
            throw new IllegalArgumentException("Mẫu xe không được rỗng và tối đa 100 ký tự.");
        }
        this.mauXe = mauXe.trim();
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        if (!HOAT_DONG.equals(trangThai) && !NGUNG_THEO_DOI.equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái xe phải là Hoạt động hoặc Ngừng theo dõi.");
        }
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