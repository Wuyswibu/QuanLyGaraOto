package service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import dao.ChiTietPhieuNhapDao;
import dao.PhieuNhapKhoDao;
import dao.PhuTungDao;
import entity.ChiTietPhieuNhap;
import entity.PhieuNhapKho;

public class PhieuNhapKhoService {

    private final PhieuNhapKhoDao phieuNhapKhoDao;
    private final ChiTietPhieuNhapDao ctPhieuNhapDao;
    private final PhuTungDao phuTungDao;

    public PhieuNhapKhoService() {
        this.phieuNhapKhoDao = new PhieuNhapKhoDao();
        this.ctPhieuNhapDao = new ChiTietPhieuNhapDao();
        this.phuTungDao = new PhuTungDao();
    }

    /**
     * Lấy toàn bộ danh sách phiếu nhập kho
     */
    public ArrayList<PhieuNhapKho> layTatCaPhieuNhap() throws SQLException {
        return phieuNhapKhoDao.layDanhSachPhieuNhap();
    }

    /**
     * Tìm phiếu nhập theo mã
     */
    public PhieuNhapKho timTheoMa(String maPhieuNhap) throws SQLException {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu nhập không được rỗng.");
        }
        return phieuNhapKhoDao.timTheoMa(maPhieuNhap.trim());
    }

    /**
     * Tìm kiếm theo từ khóa (Mã hoặc Ghi chú)
     */
    public ArrayList<PhieuNhapKho> timKiem(String tuKhoa) throws SQLException {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return phieuNhapKhoDao.layDanhSachPhieuNhap();
        }
        return phieuNhapKhoDao.timKiem(tuKhoa.trim());
    }

    /**
     * Lọc danh sách theo trạng thái
     */
    public ArrayList<PhieuNhapKho> locTheoTrangThai(String trangThai) throws SQLException {
        if (trangThai == null || trangThai.trim().isEmpty() || "Tất cả".equalsIgnoreCase(trangThai)) {
            return phieuNhapKhoDao.layDanhSachPhieuNhap();
        }
        return phieuNhapKhoDao.timTheoTrangThai(trangThai.trim());
    }

    /**
     * Tự động sinh mã phiếu nhập tiếp theo (dạng PNKxxx)
     */
    public String taoMaTuDong() throws SQLException {
        return phieuNhapKhoDao.taoMaPhieuNhap();
    }

    /**
     * Lập phiếu nhập kho mới kèm danh sách mặt hàng nhập
     * Nghiệp vụ: Thêm phiếu -> Thêm từng chi tiết -> Tăng tồn kho tương ứng
     */
    public boolean lapPhieuNhapKho(PhieuNhapKho phieuNhap, ArrayList<ChiTietPhieuNhap> danhSachCT) throws SQLException {
        if (phieuNhap == null) {
            throw new IllegalArgumentException("Thông tin phiếu nhập không được null.");
        }
        if (danhSachCT == null || danhSachCT.isEmpty()) {
            throw new IllegalArgumentException("Phiếu nhập kho phải có ít nhất một mặt hàng.");
        }

        // Tự sinh mã nếu chưa có
        if (phieuNhap.getMaPhieuNhap() == null || phieuNhap.getMaPhieuNhap().trim().isEmpty()) {
            phieuNhap.setMaPhieuNhap(phieuNhapKhoDao.taoMaPhieuNhap());
        }

        if (phieuNhap.getNgayNhap() == null) {
            phieuNhap.setNgayNhap(LocalDateTime.now());
        }

        if (phieuNhap.getTrangThai() == null || phieuNhap.getTrangThai().trim().isEmpty()) {
            phieuNhap.setTrangThai("Đã nhập kho");
        }

        // 1. Thêm phiếu nhập
        boolean themPhieu = phieuNhapKhoDao.themPhieuNhap(phieuNhap);
        if (!themPhieu) {
            return false;
        }

        // 2. Thêm chi tiết và cập nhật tồn kho
        for (ChiTietPhieuNhap ct : danhSachCT) {
            ct.setMaPhieuNhap(phieuNhap.getMaPhieuNhap());
            ctPhieuNhapDao.themChiTiet(ct);
            phuTungDao.tangTonKho(ct.getMaPhuTung(), ct.getSoLuong());
        }

        return true;
    }

    /**
     * Cập nhật trạng thái phiếu nhập kho
     */
    public boolean capNhatTrangThai(String maPhieuNhap, String trangThaiMoi) throws SQLException {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu nhập không được rỗng.");
        }
        if (trangThaiMoi == null || trangThaiMoi.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái mới không được rỗng.");
        }
        return phieuNhapKhoDao.capNhatTrangThai(maPhieuNhap.trim(), trangThaiMoi.trim());
    }
}