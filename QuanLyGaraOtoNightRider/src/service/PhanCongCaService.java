package service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.ConnectDB;
import dao.CaLamViecDao;
import dao.NhanVienDao;
import dao.PhanCongCaDao;
import entity.CaLamViec;
import entity.NhanVien;
import entity.PhanCongCa;

public class PhanCongCaService {

    public static final String DA_PHAN_CONG = "Đã phân công";
    public static final String DA_HUY = "Đã hủy";

    private final PhanCongCaDao phanCongDao = new PhanCongCaDao();
    private final CaLamViecDao caDao = new CaLamViecDao();
    private final NhanVienDao nhanVienDao = new NhanVienDao();

    public ArrayList<NhanVien> layNhanVienDangLam()
            throws SQLException {

        ArrayList<NhanVien> ketQua = new ArrayList<>();

        for (NhanVien nv : nhanVienDao.layDanhSachNhanVien()) {
            if ("Đang làm".equals(nv.getTrangThai())) {
                ketQua.add(nv);
            }
        }

        return ketQua;
    }

    public ArrayList<CaLamViec> layDanhSachCa()
            throws SQLException {

        return caDao.layDanhSachCaLamViec();
    }

    public ArrayList<NhanVien> layDanhSachNhanVien()
            throws SQLException {

        return nhanVienDao.layDanhSachNhanVien();
    }

    public ArrayList<PhanCongCa> layDanhSach()
            throws SQLException {

        return phanCongDao.layDanhSachPhanCongCa();
    }

    public PhanCongCa phanCong(
            String maNhanVien,
            String maCa,
            LocalDate ngay,
            String ghiChu) throws SQLException {

        if (ngay == null) {
            throw new IllegalArgumentException(
                    "Vui lòng chọn ngày làm việc.");
        }

        if (ngay.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Không được phân công cho ngày đã qua.");
        }

        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Vui lòng chọn nhân viên.");
        }

        if (maCa == null || maCa.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Vui lòng chọn ca làm việc.");
        }

        synchronized (ConnectDB.class) {
            NhanVien nv = nhanVienDao.timTheoMa(maNhanVien);

            if (nv == null || !"Đang làm".equals(nv.getTrangThai())) {
                throw new IllegalArgumentException(
                        "Nhân viên không tồn tại hoặc đã ngừng làm.");
            }

            CaLamViec caMoi = caDao.timTheoMa(maCa);

            if (caMoi == null || !caMoi.isTrangThai()) {
                throw new IllegalArgumentException(
                        "Ca làm việc không tồn tại hoặc đã ngừng áp dụng.");
            }

            if (caMoi.getGioBatDau() == null
                    || caMoi.getGioKetThuc() == null
                    || !caMoi.getGioBatDau()
                            .isBefore(caMoi.getGioKetThuc())) {

                throw new IllegalArgumentException(
                        "Giờ làm của ca không hợp lệ.");
            }

            for (PhanCongCa pc
                    : phanCongDao.timTheoMaNhanVien(maNhanVien)) {

                if (!ngay.equals(pc.getNgayLamViec())
                        || DA_HUY.equals(pc.getTrangThai())) {
                    continue;
                }

                if (maCa.equals(pc.getMaCa())) {
                    throw new IllegalArgumentException(
                            "Nhân viên đã được phân công ca này trong ngày.");
                }

                CaLamViec caCu = caDao.timTheoMa(pc.getMaCa());

                if (caCu == null
                        || caCu.getGioBatDau() == null
                        || caCu.getGioKetThuc() == null) {
                    throw new IllegalArgumentException(
                            "Không đọc được giờ của ca đã phân công.");
                }

                boolean trungGio =
                        caMoi.getGioBatDau()
                                .isBefore(caCu.getGioKetThuc())
                        && caCu.getGioBatDau()
                                .isBefore(caMoi.getGioKetThuc());

                if (trungGio) {
                    throw new IllegalArgumentException(
                            "Ca được chọn trùng giờ với ca "
                            + caCu.getTenCa() + " của nhân viên.");
                }
            }

            PhanCongCa pc = new PhanCongCa();

            // Dùng setter để thực hiện kiểm tra của entity.
            pc.setMaPhanCongCa(phanCongDao.taoMaPhanCongCa());
            pc.setMaNhanVien(maNhanVien);
            pc.setMaCa(maCa);
            pc.setNgayLamViec(ngay);
            pc.setTrangThai(DA_PHAN_CONG);
            pc.setGhiChu(ghiChu);

            if (!phanCongDao.themPhanCongCa(pc)) {
                throw new SQLException("Không lưu được phân công ca.");
            }

            return pc;
        }
    }

    public void huyPhanCong(String maPhanCong)
            throws SQLException {

        synchronized (ConnectDB.class) {
            PhanCongCa pc = phanCongDao.timTheoMa(maPhanCong);

            if (pc == null) {
                throw new IllegalArgumentException(
                        "Phân công không còn tồn tại.");
            }

            if (!DA_PHAN_CONG.equals(pc.getTrangThai())) {
                throw new IllegalArgumentException(
                        "Chỉ được hủy ca đang ở trạng thái Đã phân công.");
            }

            if (pc.getNgayLamViec() == null
                    || pc.getNgayLamViec().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Không được hủy phân công của ngày đã qua.");
            }

            if (!phanCongDao.capNhatTrangThai(
                    maPhanCong, DA_HUY)) {
                throw new SQLException("Không hủy được phân công.");
            }
        }
    }
}