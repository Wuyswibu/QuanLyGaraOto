package entity;

import java.time.LocalDate;

public class NhanVien {
    private String maNhanVien;
    private String maPhanQuyen;
    private String tenChuc;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private LocalDate ngaySinh;
    private LocalDate ngayVaoLam;
    private String trangThai;

    public NhanVien() {
    }


    public NhanVien(String maNhanVien, String maPhanQuyen, String tenChuc, String hoTen, String soDienThoai, String email, LocalDate ngaySinh, LocalDate ngayVaoLam, String trangThai) {

		this.maNhanVien = maNhanVien;
		this.maPhanQuyen = maPhanQuyen;
        this.tenChuc = tenChuc;
		this.hoTen = hoTen;
		this.soDienThoai = soDienThoai;
		this.email = email;
		this.ngaySinh = ngaySinh;
		this.ngayVaoLam = ngayVaoLam;
		this.trangThai = trangThai;
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

    public String getMaPhanQuyen() {
        return maPhanQuyen;
    }

    public void setMaPhanQuyen(String maPhanQuyen) {
        if (maPhanQuyen == null || maPhanQuyen.trim().isEmpty() || maPhanQuyen.trim().length() > 30) {
            throw new IllegalArgumentException("Mã phân quyền không được rỗng và tối đa 30 ký tự.");
        }
        this.maPhanQuyen = maPhanQuyen.trim();
    }
    public String getTenChuc() {
        return tenChuc;
    }
    public void setTenChuc(String tenChuc) {
        this.tenChuc = tenChuc;
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
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            this.soDienThoai = null;
            return;
        }

        String sdt = soDienThoai.trim();

        if (sdt.length() > 15) {
            throw new IllegalArgumentException( "Số điện thoại tối đa 15 ký tự.");
        }

        this.soDienThoai = sdt;
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

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh != null && ngaySinh.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không được ở tương lai.");
        }
        this.ngaySinh = ngaySinh;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        if (ngayVaoLam == null) {
            throw new IllegalArgumentException("Ngày vào làm không được để trống.");
        }

        if (ngayVaoLam.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày vào làm không được ở tương lai.");
        }

        this.ngayVaoLam = ngayVaoLam;
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
		return "NhanVien [maNhanVien=" + maNhanVien + ", maPhanQuyen=" + maPhanQuyen + ", hoTen=" + hoTen
				+ ", soDienThoai=" + soDienThoai + ", email=" + email + ", ngaySinh=" + ngaySinh + ", ngayVaoLam="
				+ ngayVaoLam + ", trangThai=" + trangThai + "]";
	}
}