package entity;

public class KhachHang {
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private boolean trangThai;

    public KhachHang() {
        this.trangThai = true;
    }

    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, String email, String diaChi, boolean trangThai) {
		this.maKhachHang = maKhachHang;
		this.hoTen = hoTen;
		this.soDienThoai = soDienThoai;
		this.email = email;
		this.diaChi = diaChi;
		this.trangThai = trangThai;
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

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty() || hoTen.trim().length() > 120) {
            throw new IllegalArgumentException("Họ tên không được rỗng và tối đa 120 ký tự.");
        }
        this.hoTen = hoTen.trim();
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty() || soDienThoai.trim().length() > 15) {
            throw new IllegalArgumentException("Số điện thoại không được rỗng và tối đa 15 ký tự.");
        }
        this.soDienThoai = soDienThoai.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            this.email = null;
            return;
        }

        if (email.trim().length() > 254) {
            throw new IllegalArgumentException("Email tối đa 254 ký tự.");
        }
        this.email = email.trim();
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty()) {
            this.diaChi = null;
            return;
        }

        if (diaChi.trim().length() > 255) {
            throw new IllegalArgumentException("Địa chỉ tối đa 255 ký tự.");
        }
        this.diaChi = diaChi.trim();
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "KhachHang [maKhachHang=" + maKhachHang
                + ", hoTen=" + hoTen
                + ", soDienThoai=" + soDienThoai
                + ", email=" + email
                + ", diaChi=" + diaChi
                + ", trangThai=" + trangThai + "]";
    }
}