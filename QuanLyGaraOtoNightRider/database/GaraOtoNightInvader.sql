CREATE DATABASE NightInvader;
GO
USE NightInvader;
GO

CREATE TABLE dbo.PhanQuyen (
    maPhanQuyen VARCHAR(30) NOT NULL,
    tenChuc NVARCHAR(80) NOT NULL,
    choPhep BIT NOT NULL,
    ngayCap DATETIME2(0) NOT NULL,
    CONSTRAINT PK_PhanQuyen PRIMARY KEY (maPhanQuyen)
);

CREATE TABLE dbo.NhanVien (
    maNhanVien VARCHAR(30) NOT NULL,
    maPhanQuyen VARCHAR(30) NOT NULL,
    hoTen NVARCHAR(120) NOT NULL,
    soDienThoai VARCHAR(15) NULL,
    email VARCHAR(254) NULL,
    ngaySinh DATE NULL,
    ngayVaoLam DATE NOT NULL,
    trangThai NVARCHAR(40) NOT NULL,
    CONSTRAINT PK_NhanVien PRIMARY KEY (maNhanVien),
    CONSTRAINT FK_NhanVien_PhanQuyen FOREIGN KEY (maPhanQuyen) REFERENCES dbo.PhanQuyen(maPhanQuyen)
);

CREATE TABLE dbo.TaiKhoan (
    maTaiKhoan VARCHAR(30) NOT NULL,
    maNhanVien VARCHAR(30) NOT NULL,
    tenDangNhap VARCHAR(80) NOT NULL,
    matKhau VARCHAR(255) NOT NULL,
    trangThai NVARCHAR(40) NOT NULL,
    CONSTRAINT PK_TaiKhoan PRIMARY KEY (maTaiKhoan),
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNhanVien) REFERENCES dbo.NhanVien(maNhanVien)
);

CREATE TABLE dbo.CaLamViec (
    maCa VARCHAR(30) NOT NULL,
    tenCa NVARCHAR(80) NOT NULL,
    gioBatDau TIME(0) NOT NULL,
    gioKetThuc TIME(0) NOT NULL,
    trangThai BIT NOT NULL,
    CONSTRAINT PK_CaLamViec PRIMARY KEY (maCa)
);

CREATE TABLE dbo.PhanCongCa (
    maPhanCongCa VARCHAR(30) NOT NULL,
    maNhanVien VARCHAR(30) NOT NULL,
    maCa VARCHAR(30) NOT NULL,
    ngayLamViec DATE NOT NULL,
    trangThai NVARCHAR(40) NOT NULL,
    ghiChu NVARCHAR(255) NULL,
    CONSTRAINT PK_PhanCongCa PRIMARY KEY (maPhanCongCa),
    CONSTRAINT FK_PhanCongCa_NhanVien FOREIGN KEY (maNhanVien) REFERENCES dbo.NhanVien(maNhanVien),
    CONSTRAINT FK_PhanCongCa_CaLamViec FOREIGN KEY (maCa) REFERENCES dbo.CaLamViec(maCa)
);

CREATE TABLE dbo.KhachHang (
    maKhachHang VARCHAR(30) NOT NULL,
    hoTen NVARCHAR(120) NOT NULL,
    soDienThoai VARCHAR(15) NOT NULL,
    email VARCHAR(254) NULL,
    diaChi NVARCHAR(255) NULL,
    trangThai BIT NOT NULL,
    CONSTRAINT PK_KhachHang PRIMARY KEY (maKhachHang)
);

CREATE TABLE dbo.PhuongTien (
    maPhuongTien VARCHAR(30) NOT NULL,
    maKhachHang VARCHAR(30) NOT NULL,
    bienSo VARCHAR(20) NOT NULL,
    mauXe NVARCHAR(100) NOT NULL,
    trangThai NVARCHAR(40) NOT NULL,
    CONSTRAINT PK_PhuongTien PRIMARY KEY (maPhuongTien),
    CONSTRAINT FK_PhuongTien_KhachHang FOREIGN KEY (maKhachHang) REFERENCES dbo.KhachHang(maKhachHang)
);

CREATE TABLE dbo.PhieuTiepNhan (
    maTiepNhan VARCHAR(30) NOT NULL,
    maPhuongTien VARCHAR(30) NOT NULL,
    maNhanVien VARCHAR(30) NOT NULL,
    ngayGioTiepNhan DATETIME2(0) NOT NULL,
	ngayGioHenTra DATETIME2(0) NULL,
    odo INT NOT NULL,
    mucNhienLieu NVARCHAR(20) NOT NULL,
    tinhTrangBanDau NVARCHAR(2000) NOT NULL,
    yeuCauKhachHang NVARCHAR(2000) NOT NULL,
    ghiChu NVARCHAR(1000) NULL,
    trangThai NVARCHAR(40) NOT NULL,
    CONSTRAINT PK_PhieuTiepNhan PRIMARY KEY (maTiepNhan),
    CONSTRAINT FK_PhieuTiepNhan_PhuongTien FOREIGN KEY (maPhuongTien) REFERENCES dbo.PhuongTien(maPhuongTien),
    CONSTRAINT FK_PhieuTiepNhan_NhanVien FOREIGN KEY (maNhanVien) REFERENCES dbo.NhanVien(maNhanVien)
);

CREATE TABLE dbo.PhieuSuaChua (
    maPhieuSuaChua VARCHAR(30) NOT NULL,
    maTiepNhan VARCHAR(30) NOT NULL,
    ngayLap DATETIME2(0) NOT NULL,
    chanDoan NVARCHAR(2000) NULL,
    loaiPhieu NVARCHAR(20) NOT NULL,
    trangThai NVARCHAR(40) NOT NULL,
    ghiChu NVARCHAR(1000) NULL,
    CONSTRAINT PK_PhieuSuaChua PRIMARY KEY (maPhieuSuaChua),
    CONSTRAINT FK_PhieuSuaChua_TiepNhan FOREIGN KEY (maTiepNhan) REFERENCES dbo.PhieuTiepNhan(maTiepNhan)
);

CREATE TABLE dbo.DichVu (
    maDichVu VARCHAR(30) NOT NULL,
    tenDichVu NVARCHAR(150) NOT NULL,
    moTa NVARCHAR(1000) NULL,
    donGia DECIMAL(18,2) NOT NULL,
    trangThai BIT NOT NULL,
    CONSTRAINT PK_DichVu PRIMARY KEY (maDichVu)
);

CREATE TABLE dbo.HangMucDichVu (
    maHangMuc VARCHAR(30) NOT NULL,
    maPhieuSuaChua VARCHAR(30) NOT NULL,
    maDichVu VARCHAR(30) NOT NULL,
    tenHangMuc NVARCHAR(150) NOT NULL,
    moTa NVARCHAR(2000) NULL,
    trangThai NVARCHAR(40) NOT NULL,
    chiPhiDuKien DECIMAL(18,2) NOT NULL,
    chiPhiThucTe DECIMAL(18,2) NOT NULL,
    ketQua NVARCHAR(2000) NULL,
    ghiChu NVARCHAR(1000) NULL,
    CONSTRAINT PK_HangMucDichVu PRIMARY KEY (maHangMuc),
    CONSTRAINT FK_HangMuc_PhieuSuaChua FOREIGN KEY (maPhieuSuaChua) REFERENCES dbo.PhieuSuaChua(maPhieuSuaChua),
    CONSTRAINT FK_HangMuc_DichVu FOREIGN KEY (maDichVu) REFERENCES dbo.DichVu(maDichVu)
);

CREATE TABLE dbo.PhuTung (
    maPhuTung VARCHAR(30) NOT NULL,
    maPhieuSuaChua VARCHAR(30) NULL,
    tenPhuTung NVARCHAR(150) NOT NULL,
    moTa NVARCHAR(1000) NULL,
    donViTinh NVARCHAR(30) NOT NULL,
    donGia DECIMAL(18,2) NOT NULL,
    soLuongTon INT NOT NULL,
    mucTonToiThieu INT NOT NULL,
    trangThai NVARCHAR(40) NOT NULL,
    CONSTRAINT PK_PhuTung PRIMARY KEY (maPhuTung),
    CONSTRAINT FK_PhuTung_PhieuSuaChua FOREIGN KEY (maPhieuSuaChua) REFERENCES dbo.PhieuSuaChua(maPhieuSuaChua)
);

CREATE TABLE dbo.PhieuNhapKho (
    maPhieuNhap VARCHAR(30) NOT NULL,
    ngayNhap DATETIME2(0) NOT NULL,
    trangThai NVARCHAR(40) NOT NULL,
    ghiChu NVARCHAR(1000) NULL,
    CONSTRAINT PK_PhieuNhapKho PRIMARY KEY (maPhieuNhap)
);

CREATE TABLE dbo.ChiTietPhieuNhap (
    maPhieuNhap VARCHAR(30) NOT NULL,
    maPhuTung VARCHAR(30) NOT NULL,
    soLuong INT NOT NULL,
    donGia DECIMAL(18,2) NOT NULL,
    CONSTRAINT PK_ChiTietPhieuNhap PRIMARY KEY (maPhieuNhap, maPhuTung),
    CONSTRAINT FK_ChiTietPhieuNhap_PhieuNhap FOREIGN KEY (maPhieuNhap) REFERENCES dbo.PhieuNhapKho(maPhieuNhap),
    CONSTRAINT FK_ChiTietPhieuNhap_PhuTung FOREIGN KEY (maPhuTung) REFERENCES dbo.PhuTung(maPhuTung)
);

CREATE TABLE dbo.PhieuXuatKho (
    maPhieuXuat VARCHAR(30) NOT NULL,
    ngayXuat DATETIME2(0) NOT NULL,
    lyDoXuat NVARCHAR(255) NULL,
    trangThai NVARCHAR(40) NOT NULL,
    ghiChu NVARCHAR(1000) NULL,
    CONSTRAINT PK_PhieuXuatKho PRIMARY KEY (maPhieuXuat)
);

CREATE TABLE dbo.ChiTietPhieuXuat (
    maPhieuXuat VARCHAR(30) NOT NULL,
    maPhuTung VARCHAR(30) NOT NULL,
    soLuong INT NOT NULL,
    donGia DECIMAL(18,2) NOT NULL,
    CONSTRAINT PK_ChiTietPhieuXuat PRIMARY KEY (maPhieuXuat, maPhuTung),
    CONSTRAINT FK_ChiTietPhieuXuat_PhieuXuat FOREIGN KEY (maPhieuXuat) REFERENCES dbo.PhieuXuatKho(maPhieuXuat),
    CONSTRAINT FK_ChiTietPhieuXuat_PhuTung FOREIGN KEY (maPhuTung) REFERENCES dbo.PhuTung(maPhuTung)
);

CREATE TABLE dbo.HoaDon (
    maHoaDon VARCHAR(30) NOT NULL,
    maPhieuSuaChua VARCHAR(30) NOT NULL,
    maKhachHang VARCHAR(30) NOT NULL,
    maPhuongTien VARCHAR(30) NOT NULL,
    maNhanVien VARCHAR(30) NOT NULL,
    ngayLap DATETIME2(0) NOT NULL,
    vat DECIMAL(5,2) NOT NULL,
    trangThai NVARCHAR(40) NOT NULL,
    CONSTRAINT PK_HoaDon PRIMARY KEY (maHoaDon),
    CONSTRAINT FK_HoaDon_PhieuSuaChua FOREIGN KEY (maPhieuSuaChua) REFERENCES dbo.PhieuSuaChua(maPhieuSuaChua),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKhachHang) REFERENCES dbo.KhachHang(maKhachHang),
    CONSTRAINT FK_HoaDon_PhuongTien FOREIGN KEY (maPhuongTien) REFERENCES dbo.PhuongTien(maPhuongTien),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNhanVien) REFERENCES dbo.NhanVien(maNhanVien)
);


CREATE TABLE dbo.CTHoaDon (
    maHoaDon VARCHAR(30) NOT NULL,
    maPhuTung VARCHAR(30) NOT NULL,
    soLuong INT NOT NULL,
    donGia DECIMAL(18,2) NOT NULL,
    giamGia DECIMAL(18,2) NOT NULL,
    CONSTRAINT PK_CTHoaDon PRIMARY KEY (maHoaDon, maPhuTung),
    CONSTRAINT FK_CTHoaDon_HoaDon FOREIGN KEY (maHoaDon) REFERENCES dbo.HoaDon(maHoaDon),
    CONSTRAINT FK_CTHoaDon_PhuTung FOREIGN KEY (maPhuTung) REFERENCES dbo.PhuTung(maPhuTung)
);

CREATE TABLE dbo.PhuongThucThanhToan (
    maThanhToan VARCHAR(30) NOT NULL,
    maHoaDon VARCHAR(30) NOT NULL,
    ngayThanhToan DATETIME2(0) NOT NULL,
    phuongThuc NVARCHAR(30) NOT NULL,
    maGiaoDich VARCHAR(100) NULL,
    trangThai NVARCHAR(40) NOT NULL,
    CONSTRAINT PK_PhuongThucThanhToan PRIMARY KEY (maThanhToan),
    CONSTRAINT FK_ThanhToan_HoaDon FOREIGN KEY (maHoaDon) REFERENCES dbo.HoaDon(maHoaDon)
);

CREATE TABLE dbo.PhieuBaoHanh (
    maBaoHanh VARCHAR(30) NOT NULL,
    maPhieuSuaChua VARCHAR(30) NOT NULL,
    maPhuongTien VARCHAR(30) NOT NULL,
    ngayYeuCau DATETIME2(0) NOT NULL,
    noiDungYeuCau NVARCHAR(2000) NOT NULL,
    ketQuaKiemTra NVARCHAR(2000) NULL,
    ketQuaXuLy NVARCHAR(2000) NULL,
    lyDoTuChoi NVARCHAR(1000) NULL,
    trangThai NVARCHAR(40) NOT NULL,
    CONSTRAINT PK_PhieuBaoHanh PRIMARY KEY (maBaoHanh),
    CONSTRAINT FK_PhieuBaoHanh_PhieuSuaChua FOREIGN KEY (maPhieuSuaChua) REFERENCES dbo.PhieuSuaChua(maPhieuSuaChua),
    CONSTRAINT FK_PhieuBaoHanh_PhuongTien FOREIGN KEY (maPhuongTien) REFERENCES dbo.PhuongTien(maPhuongTien)
);

ALTER TABLE dbo.TaiKhoan ADD CONSTRAINT UQ_TaiKhoan_TenDangNhap UNIQUE (tenDangNhap);
ALTER TABLE dbo.PhuongTien ADD CONSTRAINT UQ_PhuongTien_BienSo UNIQUE (bienSo);
ALTER TABLE dbo.TaiKhoan ADD CONSTRAINT UQ_TaiKhoan_NhanVien UNIQUE (maNhanVien);
ALTER TABLE dbo.PhieuSuaChua ADD CONSTRAINT UQ_PhieuSuaChua_TiepNhan UNIQUE (maTiepNhan);
ALTER TABLE dbo.HoaDon ADD CONSTRAINT UQ_HoaDon_PhieuSuaChua UNIQUE (maPhieuSuaChua);
ALTER TABLE dbo.HoaDon ADD CONSTRAINT CK_HoaDon_VAT CHECK (vat >= 0 AND vat <= 100);
ALTER TABLE dbo.HoaDon ADD CONSTRAINT CK_HoaDon_TrangThai CHECK (trangThai IN (N'Chưa thanh toán', N'Đã thanh toán', N'Đã hủy'));
ALTER TABLE dbo.PhuongThucThanhToan ADD CONSTRAINT UQ_ThanhToan_HoaDon UNIQUE (maHoaDon);
ALTER TABLE dbo.CaLamViec ADD CONSTRAINT CK_CaLamViec_Gio CHECK (gioKetThuc > gioBatDau);
ALTER TABLE dbo.NhanVien ADD CONSTRAINT CK_NhanVien_Ngay CHECK (ngaySinh IS NULL OR ngayVaoLam > ngaySinh);
ALTER TABLE dbo.PhieuTiepNhan ADD CONSTRAINT CK_PhieuTiepNhan_ODO CHECK (odo >= 0);
ALTER TABLE dbo.DichVu ADD CONSTRAINT CK_DichVu_DonGia CHECK (donGia >= 0);
ALTER TABLE dbo.HangMucDichVu ADD CONSTRAINT CK_HangMuc_ChiPhiDuKien CHECK (chiPhiDuKien >= 0);
ALTER TABLE dbo.HangMucDichVu ADD CONSTRAINT CK_HangMuc_ChiPhiThucTe CHECK (chiPhiThucTe >= 0);
ALTER TABLE dbo.PhuTung ADD CONSTRAINT CK_PhuTung_DonGia CHECK (donGia >= 0);
ALTER TABLE dbo.PhuTung ADD CONSTRAINT CK_PhuTung_SoLuongTon CHECK (soLuongTon >= 0);
ALTER TABLE dbo.PhuTung ADD CONSTRAINT CK_PhuTung_MucTonToiThieu CHECK (mucTonToiThieu >= 0);
ALTER TABLE dbo.ChiTietPhieuNhap ADD CONSTRAINT CK_ChiTietPhieuNhap_SoLuong CHECK (soLuong > 0);
ALTER TABLE dbo.ChiTietPhieuNhap ADD CONSTRAINT CK_ChiTietPhieuNhap_DonGia CHECK (donGia >= 0);
ALTER TABLE dbo.ChiTietPhieuXuat ADD CONSTRAINT CK_ChiTietPhieuXuat_SoLuong CHECK (soLuong > 0);
ALTER TABLE dbo.ChiTietPhieuXuat ADD CONSTRAINT CK_ChiTietPhieuXuat_DonGia CHECK (donGia >= 0);
ALTER TABLE dbo.CTHoaDon ADD CONSTRAINT CK_CTHoaDon_SoLuong CHECK (soLuong > 0);
ALTER TABLE dbo.CTHoaDon ADD CONSTRAINT CK_CTHoaDon_DonGia CHECK (donGia >= 0);
ALTER TABLE dbo.CTHoaDon ADD CONSTRAINT CK_CTHoaDon_GiamGia CHECK (giamGia >= 0);

ALTER TABLE dbo.PhieuSuaChua 
ADD maKyThuatVien VARCHAR(30) NULL;

ALTER TABLE dbo.PhieuSuaChua 
ADD CONSTRAINT FK_PhieuSuaChua_KTV 
FOREIGN KEY (maKyThuatVien) REFERENCES dbo.NhanVien(maNhanVien);
