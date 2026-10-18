package entity;

import java.time.LocalDateTime;

public class PhanQuyen {
    private String maPhanQuyen;
    private String tenChuc;
    private boolean choPhep;
    private LocalDateTime ngayCap;

    public PhanQuyen() {
        this.choPhep = true;
        this.ngayCap = LocalDateTime.now();
    }

    public PhanQuyen(String maPhanQuyen, String tenChuc, boolean choPhep, LocalDateTime ngayCap) {
        setMaPhanQuyen(maPhanQuyen);
        setTenChuc(tenChuc);
        setChoPhep(choPhep);
        setNgayCap(ngayCap);
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
        if (tenChuc == null || tenChuc.trim().isEmpty() || tenChuc.trim().length() > 80) {
            throw new IllegalArgumentException("Tên chức vụ không được rỗng và tối đa 80 ký tự.");
        }
        this.tenChuc = tenChuc.trim();
    }

    public boolean isChoPhep() {
        return choPhep;
    }

    public void setChoPhep(boolean choPhep) {
        this.choPhep = choPhep;
    }

    public LocalDateTime getNgayCap() {
        return ngayCap;
    }

    public void setNgayCap(LocalDateTime ngayCap) {
        if (ngayCap == null) {throw new IllegalArgumentException("Ngày cấp không được để trống.");
        }
        this.ngayCap = ngayCap;
    }

	@Override
	public String toString() {
		return "PhanQuyen [maPhanQuyen=" + maPhanQuyen + ", tenChuc=" + tenChuc + ", choPhep=" + choPhep + ", ngayCap="
				+ ngayCap + "]";
	}

    
}