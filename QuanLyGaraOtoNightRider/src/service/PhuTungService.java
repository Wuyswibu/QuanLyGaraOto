package service;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.PhuTungDao;
import entity.PhuTung;

public class PhuTungService {

    private PhuTungDao phuTungDao;

    public PhuTungService() {
        this.phuTungDao = new PhuTungDao();
    }

    public boolean themPhuTung(PhuTung phuTung) throws Exception {
        String maMoi = phuTungDao.taoMaPhuTung();
        phuTung.setMaPhuTung(maMoi);
        phuTung.setSoLuongTon(0);
        phuTung.setTrangThai("Đang kinh doanh");

        return phuTungDao.themPhuTung(phuTung);
    }

    public boolean suaPhuTung(PhuTung phuTung) throws Exception {
        if (phuTung.getMaPhuTung() == null || phuTung.getMaPhuTung().trim().isEmpty()) {
            throw new Exception("Lỗi: Không xác định được mã phụ tùng cần sửa!");
        }
        
        PhuTung ptCu = phuTungDao.timTheoMa(phuTung.getMaPhuTung());
        if (ptCu == null) {
            throw new Exception("Lỗi: Phụ tùng này không tồn tại trong hệ thống!");
        }

        return phuTungDao.suaPhuTung(phuTung);
    }

    public boolean ngungKinhDoanh(String maPhuTung) throws Exception {
        if (maPhuTung == null || maPhuTung.trim().isEmpty()) {
            throw new Exception("Vui lòng chọn một phụ tùng để thao tác!");
        }
        
        PhuTung pt = phuTungDao.timTheoMa(maPhuTung);
        if (pt == null) {
            throw new Exception("Lỗi: Phụ tùng không tồn tại!");
        }
        
        if (pt.getTrangThai().equalsIgnoreCase("Ngừng kinh doanh")) {
            throw new Exception("Phụ tùng này đã ở trạng thái ngừng kinh doanh từ trước!");
        }

        return phuTungDao.capNhatTrangThai(maPhuTung, "Ngừng kinh doanh");
    }

    public ArrayList<PhuTung> timKiem(String tuKhoa) throws SQLException {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return phuTungDao.layDanhSachPhuTung();
        }
        return phuTungDao.timKiem(tuKhoa.trim());
    }

    public ArrayList<PhuTung> layDanhSachPhuTung() throws SQLException {
        return phuTungDao.layDanhSachPhuTung();
    }
    
}