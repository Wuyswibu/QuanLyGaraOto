package service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import dao.ChiTietPhieuXuatDao;
import dao.PhieuXuatKhoDao;
import dao.PhuTungDao;
import entity.ChiTietPhieuXuat;
import entity.PhieuXuatKho;
import entity.PhuTung;

public class PhieuXuatKhoService {

    private final PhieuXuatKhoDao phieuXuatKhoDao;
    private final ChiTietPhieuXuatDao ctPhieuXuatDao;
    private final PhuTungDao phuTungDao;

    public PhieuXuatKhoService() {
        this.phieuXuatKhoDao = new PhieuXuatKhoDao();
        this.ctPhieuXuatDao = new ChiTietPhieuXuatDao();
        this.phuTungDao = new PhuTungDao();
    }

    /**
     * Lấy danh sách toàn bộ phiếu xuất kho
     */
    public ArrayList<PhieuXuatKho> layTatCaPhieuXuat() throws SQLException {
        return phieuXuatKhoDao.layDanhSachPhieuXuat();
    }

    /**
     * Tìm phiếu xuất theo mã
     */
    public PhieuXuatKho timTheoMa(String maPhieuXuat) throws SQLException {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu xuất không được rỗng.");
        }
        return phieuXuatKhoDao.timTheoMa(maPhieuXuat.trim());
    }

    /**
     * Tìm kiếm theo từ khóa (Mã phiếu, Lý do xuất, Ghi chú)
     */
    public ArrayList<PhieuXuatKho> timKiem(String tuKhoa) throws SQLException {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return phieuXuatKhoDao.layDanhSachPhieuXuat();
        }
        return phieuXuatKhoDao.timKiem(tuKhoa.trim());
    }

    /**
     * Lọc danh sách theo trạng thái phiếu xuất
     */
    public ArrayList<PhieuXuatKho> locTheoTrangThai(String trangThai) throws SQLException {
        if (trangThai == null || trangThai.trim().isEmpty() || "Tất cả".equalsIgnoreCase(trangThai)) {
            return phieuXuatKhoDao.layDanhSachPhieuXuat();
        }
        return phieuXuatKhoDao.timTheoTrangThai(trangThai.trim());
    }

    /**
     * Tự động tạo mã phiếu xuất tiếp theo (dạng PXKxxx)
     */
    public String taoMaTuDong() throws SQLException {
        return phieuXuatKhoDao.taoMaPhieuXuat();
    }

    /**
     * Lập phiếu xuất kho mới kèm danh sách chi tiết
     * Nghiệp vụ bắt buộc: 
     * 1. Số lượng xuất <= Số lượng tồn hiện tại của từng phụ tùng
     * 2. Lưu phiếu -> Lưu chi tiết -> Trừ tồn kho phụ tùng
     */
    public boolean lapPhieuXuatKho(PhieuXuatKho phieuXuat, ArrayList<ChiTietPhieuXuat> danhSachCT) throws SQLException {
        if (phieuXuat == null) {
            throw new IllegalArgumentException("Thông tin phiếu xuất không được null.");
        }
        if (danhSachCT == null || danhSachCT.isEmpty()) {
            throw new IllegalArgumentException("Phiếu xuất kho phải có ít nhất một mặt hàng phụ tùng.");
        }

        // Kiểm tra tồn kho trước khi thực hiện ghi dữ liệu
        for (ChiTietPhieuXuat ct : danhSachCT) {
            PhuTung pt = phuTungDao.timTheoMa(ct.getMaPhuTung());
            if (pt == null) {
                throw new IllegalArgumentException("Phụ tùng mã " + ct.getMaPhuTung() + " không tồn tại.");
            }
            if (pt.getSoLuongTon() < ct.getSoLuong()) {
                throw new IllegalArgumentException("Phụ tùng '" + pt.getTenPhuTung() 
                        + "' không đủ số lượng xuất! (Hiện tồn: " + pt.getSoLuongTon() 
                        + ", Cần xuất: " + ct.getSoLuong() + ")");
            }
        }

        // Tự sinh mã phiếu xuất nếu chưa có
        if (phieuXuat.getMaPhieuXuat() == null || phieuXuat.getMaPhieuXuat().trim().isEmpty()) {
            phieuXuat.setMaPhieuXuat(phieuXuatKhoDao.taoMaPhieuXuat());
        }

        if (phieuXuat.getNgayXuat() == null) {
            phieuXuat.setNgayXuat(LocalDateTime.now());
        }

        if (phieuXuat.getTrangThai() == null || phieuXuat.getTrangThai().trim().isEmpty()) {
            phieuXuat.setTrangThai("Đã xuất kho");
        }

        // 1. Thêm phiếu xuất
        boolean themPhieu = phieuXuatKhoDao.themPhieuXuat(phieuXuat);
        if (!themPhieu) {
            return false;
        }

        // 2. Thêm chi tiết và trừ tồn kho
        for (ChiTietPhieuXuat ct : danhSachCT) {
            ct.setMaPhieuXuat(phieuXuat.getMaPhieuXuat());
            ctPhieuXuatDao.themChiTiet(ct);
            phuTungDao.giamTonKho(ct.getMaPhuTung(), ct.getSoLuong());
        }

        return true;
    }

    /**
     * Cập nhật trạng thái phiếu xuất kho
     */
    public boolean capNhatTrangThai(String maPhieuXuat, String trangThaiMoi) throws SQLException {
        if (maPhieuXuat == null || maPhieuXuat.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu xuất không được rỗng.");
        }
        if (trangThaiMoi == null || trangThaiMoi.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái mới không được rỗng.");
        }
        return phieuXuatKhoDao.capNhatTrangThai(maPhieuXuat.trim(), trangThaiMoi.trim());
    }
}