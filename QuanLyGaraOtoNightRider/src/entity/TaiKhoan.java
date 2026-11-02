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

    public TaiKhoan(String maTaiKhoan, String maNhanVien, String tenDangNhap, String matKhau, String trangThai) {
        setMaTaiKhoan(maTaiKhoan);
        setMaNhanVien(maNhanVien);
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setTrangThai(trangThai);
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        if (maTaiKhoan == null || maTaiKhoan.trim().isEmpty() || maTaiKhoan.trim().length() > 30) {
            throw new IllegalArgumentException("Mã tài khoản không được rỗng và tối đa 30 ký tự.");
        }
        this.maTaiKhoan = maTaiKhoan.trim();
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

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty() || tenDangNhap.trim().length() > 80) {
            throw new IllegalArgumentException("Tên đăng nhập không được rỗng và tối đa 80 ký tự.");
        }
        this.tenDangNhap = tenDangNhap.trim();
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        if (matKhau == null || matKhau.trim().isEmpty() || matKhau.length() > 255) {
            throw new IllegalArgumentException("Mật khẩu không được rỗng và tối đa 255 ký tự.");
        }
        this.matKhau = matKhau;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        if (!HOAT_DONG.equals(trangThai) && !KHOA.equals(trangThai)) {
            throw new IllegalArgumentException("Trạng thái tài khoản phải là Hoạt động hoặc Khóa.");
        }
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "TaiKhoan [maTaiKhoan=" + maTaiKhoan
                + ", maNhanVien=" + maNhanVien
                + ", tenDangNhap=" + tenDangNhap
                + ", trangThai=" + trangThai + "]";
    }
}