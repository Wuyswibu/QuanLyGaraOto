
package entity;

public class TaiKhoan {

    public static final String HOAT_DONG = "Hoạt động";
    public static final String KHOA = "Khóa";

    private String maTaiKhoan;
    private String maNhanVien;
    private String tenDangNhap;
    private String matKhau;
    private String trangThai;

    public TaiKhoan() {
        this.trangThai = HOAT_DONG;
    }

    public TaiKhoan(String maTaiKhoan, String maNhanVien,
                    String tenDangNhap, String matKhau,
                    String trangThai) {
        this.maTaiKhoan = maTaiKhoan;
        this.maNhanVien = maNhanVien;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        setTrangThai(trangThai);
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        if (!HOAT_DONG.equals(trangThai) && !KHOA.equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái tài khoản không hợp lệ: " + trangThai);
        }
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return tenDangNhap;
    }
}
