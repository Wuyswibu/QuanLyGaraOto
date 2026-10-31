package service;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.*;
import java.util.*;
import connectDB.ConnectDB;
import dao.PhieuTiepNhanDao;
import dao.TiepNhanHoSoDao;
import entity.*;

/** Nghiệp vụ tiếp nhận, không phụ thuộc Swing. */
public class PhieuTiepNhanService {
    public static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    public static final String MOI = "Đã tiếp nhận";
    public static final List<String> NHIEN_LIEU = Collections.unmodifiableList(
            Arrays.asList("Cạn", "1/4", "1/2", "3/4", "Đầy"));
    @FunctionalInterface public interface NguonKetNoi { Connection mo() throws SQLException; }
    private final NguonKetNoi nguon;
    private final boolean dongSauKhiDung;
    private final KhachHangServices khService = new KhachHangServices();
    /** Tương thích ConnectDB hiện tại. Các DAO khác dùng chung phải đồng bộ ConnectDB.class. */
    public PhieuTiepNhanService() { this(ConnectDB::getConnection, false); }
    /** Khuyến nghị truyền nguồn cấp kết nối MỚI mỗi lần và dongSauKhiDung=true khi tích hợp pool. */
    public PhieuTiepNhanService(NguonKetNoi nguon, boolean dongSauKhiDung) {
        this.nguon=Objects.requireNonNull(nguon); this.dongSauKhiDung=dongSauKhiDung;
    }
    @FunctionalInterface private interface Viec<T> { T chay(Connection c) throws SQLException; }
    private <T> T doc(Viec<T> v) throws SQLException {
        synchronized (ConnectDB.class) {
            Connection c=nguon.mo();
            if (c==null || c.isClosed()) throw new SQLException("Chưa kết nối database. Hãy chạy từ Main.");
            try { return v.chay(c); } finally { if (dongSauKhiDung) c.close(); }
        }
    }
    protected TiepNhanHoSoDao hoSo(Connection c) { return new TiepNhanHoSoDao(c); }
    protected PhieuTiepNhanDao phieuDao(Connection c) { return new PhieuTiepNhanDao(c); }
    public KhachHang timKhach(String so) throws SQLException {
        return doc(c -> hoSo(c).khachTheoSo(chuanHoaSo(so),false));
    }
    public Xe timXe(String bienSo) throws SQLException {
        return doc(c -> hoSo(c).xeTheoBienSo(chuanHoaBienSo(bienSo),false));
    }
    public KhachHang khachTheoMa(String ma) throws SQLException {
        return doc(c -> hoSo(c).khachTheoMa(ma,false));
    }
    public List<Xe> xeCuaKhach(String ma) throws SQLException {
        return doc(c -> hoSo(c).xeCuaKhach(ma));
    }
    public List<String> mauXeDaCo() throws SQLException { return doc(c -> hoSo(c).mauXe()); }
    public List<PhieuTiepNhan> danhSach(String tu, String trangThai) throws SQLException {
        return doc(c -> {
            PhieuTiepNhanDao d=phieuDao(c);
            List<PhieuTiepNhan> ds=trim(tu).isEmpty()?d.layDanhSachPhieuTiepNhan():d.timKiem(trim(tu));
            if (trangThai!=null && !"Tất cả".equals(trangThai)) ds.removeIf(p -> !trangThai.equals(p.getTrangThai()));
            return ds;
        });
    }
    public static String trim(String s) { return s==null?"":s.trim(); }
    public static String chuanHoaSo(String s) {
        s=trim(s); return s.startsWith("+84")?"0"+s.substring(3):s;
    }
    public static String chuanHoaBienSo(String s) {
        return trim(s).replaceAll("[\\s.\\-]", "").toUpperCase(Locale.ROOT);
    }
    public static class DuLieu {
        // Mã rỗng => tạo hồ sơ mới. Mã có giá trị => đọc lại hồ sơ SQL, không cập nhật hồ sơ tại đây.
        public String maKH="", hoTen="", soDienThoai="", email="", diaChi="";
        public String maXe="", bienSo="", mauXe="", maNhanVien="";
        public String odo="", nhienLieu="", henTra="", tinhTrang="", yeuCau="", ghiChu="";
        public boolean xacNhanOdoThap;
    }
    public static class LoiDuLieu extends IllegalArgumentException {
        private static final long serialVersionUID=1L;
        private final Map<String,String> loi;
        public LoiDuLieu(Map<String,String> loi) {
            super(loi.values().iterator().next()); this.loi=Collections.unmodifiableMap(new LinkedHashMap<>(loi));
        }
        public Map<String,String> getLoi() { return loi; }
    }
    public static class CanXacNhanOdo extends IllegalArgumentException {
        private static final long serialVersionUID=1L;
        public CanXacNhanOdo(int km) { super("Số km nhỏ hơn lần tiếp nhận gần nhất ("+km+" km). Bạn có chắc số km đã đối chiếu đúng?"); }
    }
    private static LoiDuLieu loi(String truong,String s) { return new LoiDuLieu(Collections.singletonMap(truong,s)); }
    public Map<String,String> kiemTra(DuLieu d) {
        Map<String,String> e=new LinkedHashMap<>();
        if (trim(d.maKH).isEmpty()) e.putAll(khService.kiemTraDuLieu(d.hoTen,d.soDienThoai,d.email,d.diaChi));
        if (trim(d.maXe).isEmpty()) {
            if (chuanHoaBienSo(d.bienSo).isEmpty() || trim(d.bienSo).length()>20)
                e.put("bienSo","Biển số không được rỗng, tối đa 20 ký tự.");
            if (trim(d.mauXe).isEmpty() || trim(d.mauXe).length()>100)
                e.put("mauXe","Mẫu xe không được rỗng, tối đa 100 ký tự.");
        }
        if (trim(d.maNhanVien).isEmpty() || trim(d.maNhanVien).length()>30)
            e.put("nhanVien","Thiếu mã nhân viên đăng nhập. Cần nối phiên đăng nhập trước khi lưu.");
        try { if (!trim(d.odo).matches("[0-9]+") || Integer.parseInt(trim(d.odo))<0) throw new NumberFormatException(); }
        catch (NumberFormatException x) { e.put("odo","Nhập số km nguyên từ 0 đến 2147483647."); }
        if (!NHIEN_LIEU.contains(d.nhienLieu)) e.put("nhienLieu","Vui lòng chọn mức nhiên liệu.");
        if (trim(d.tinhTrang).isEmpty() || trim(d.tinhTrang).length()>2000)
            e.put("tinhTrang","Nhập hiện trạng, tối đa 2000 ký tự.");
        if (trim(d.yeuCau).isEmpty() || trim(d.yeuCau).length()>2000)
            e.put("yeuCau","Nhập yêu cầu, tối đa 2000 ký tự.");
        if (trim(d.ghiChu).length()>1000) e.put("ghiChu","Ghi chú tối đa 1000 ký tự.");
        if (!trim(d.henTra).isEmpty()) {
            try {
                if (LocalDateTime.parse(trim(d.henTra),NGAY).isBefore(LocalDateTime.now()))
                    e.put("henTra","Hẹn trả phải sau thời điểm tiếp nhận.");
            } catch (DateTimeParseException x) { e.put("henTra","Dùng định dạng dd/MM/yyyy HH:mm hoặc để trống."); }
        }
        return e;
    }
    public PhieuTiepNhan luu(DuLieu d) throws SQLException {
        Map<String,String> e=kiemTra(d); if (!e.isEmpty()) throw new LoiDuLieu(e);
        return doc(c -> {
            if (!c.getAutoCommit()) throw new SQLException("Kết nối đang có transaction khác; không thể lập phiếu.");
            return transaction(c, tx -> {
                    TiepNhanHoSoDao h=hoSo(c);
                    if (!h.nhanVienTonTai(trim(d.maNhanVien))) throw loi("nhanVien","Nhân viên đăng nhập không còn tồn tại.");
                    KhachHang k;
                    if (trim(d.maKH).isEmpty()) {
                        if (h.khachTheoSo(chuanHoaSo(d.soDienThoai),true)!=null)
                            throw loi("soDienThoai","Điện thoại đã có hồ sơ. Bấm Tìm khách để dùng hồ sơ đó.");
                        k=new KhachHang(); k.setMaKhachHang(h.taoMa("KH"));
                        k.setHoTen(java.text.Normalizer.normalize(trim(d.hoTen),java.text.Normalizer.Form.NFC).replaceAll("[\\p{Z}\\s]+"," "));
                        k.setSoDienThoai(chuanHoaSo(d.soDienThoai)); k.setEmail(d.email); k.setDiaChi(d.diaChi);
                        k.setTrangThai(true); h.themKhach(k);
                    } else {
                        k=h.khachTheoMa(trim(d.maKH),true);
                        if (k==null || !k.isTrangThai()) throw loi("soDienThoai","Khách không tồn tại hoặc đã ngừng sử dụng. Khôi phục trước.");
                    }
                    Xe x;
                    if (trim(d.maXe).isEmpty()) {
                        if (h.xeTheoBienSo(chuanHoaBienSo(d.bienSo),true)!=null)
                            throw loi("bienSo","Biển số đã tồn tại. Bấm Tìm xe để dùng hồ sơ có sẵn.");
                        x=new Xe(); x.setMaPhuongTien(h.taoMa("PT")); x.setMaKhachHang(k.getMaKhachHang());
                        x.setBienSo(trim(d.bienSo).replaceAll("\\s+","").toUpperCase(Locale.ROOT));
                        x.setMauXe(trim(d.mauXe)); x.setTrangThai("Hoạt động"); h.themXe(x);
                    } else {
                        x=h.xeTheoMa(trim(d.maXe),true);
                        if (x==null || !"Hoạt động".equals(x.getTrangThai()))
                            throw loi("bienSo","Xe không tồn tại hoặc đã ngừng theo dõi. Khôi phục trước.");
                        if (!k.getMaKhachHang().equals(x.getMaKhachHang()))
                            throw loi("bienSo","Xe không thuộc khách hàng đang chọn.");
                    }
                    String mo=h.phieuDangMo(x.getMaPhuongTien());
                    if (mo!=null) throw loi("bienSo","Xe đang có phiếu chưa kết thúc: "+mo);
                    Integer cu=h.odoGanNhat(x.getMaPhuongTien()); int odo=Integer.parseInt(trim(d.odo));
                    if (cu!=null && odo<cu && !d.xacNhanOdoThap) throw new CanXacNhanOdo(cu);
                    PhieuTiepNhan p=new PhieuTiepNhan();
                    p.setMaTiepNhan(h.taoMa("PTN")); p.setMaPhuongTien(x.getMaPhuongTien());
                    p.setMaNhanVien(trim(d.maNhanVien)); p.setNgayGioTiepNhan(LocalDateTime.now().withNano(0));
                    LocalDateTime hen=trim(d.henTra).isEmpty()?null:LocalDateTime.parse(trim(d.henTra),NGAY);
                    if (hen!=null && hen.isBefore(p.getNgayGioTiepNhan())) throw loi("henTra","Hẹn trả đã trước giờ tiếp nhận. Hãy nhập lại.");
                    p.setNgayGioHenTra(hen); p.setOdo(odo); p.setMucNhienLieu(d.nhienLieu);
                    p.setTinhTrangBanDau(d.tinhTrang); p.setYeuCauKhachHang(d.yeuCau); p.setGhiChu(d.ghiChu); p.setTrangThai(MOI);
                    if (!phieuDao(c).themPhieuTiepNhan(p)) throw new SQLException("Không lưu được phiếu.");
                    return p;
            });
        });
    }
    private <T> T transaction(Connection c, Viec<T> viec) throws SQLException {
        int isolation=c.getTransactionIsolation();
        Throwable failure=null;
        boolean ended=false;
        try {
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            c.setAutoCommit(false);
            T result=viec.chay(c);
            c.commit(); ended=true;
            return result;
        } catch (SQLException | RuntimeException ex) {
            failure=ex;
            try { c.rollback(); ended=true; }
            catch(SQLException rollback) {
                ex.addSuppressed(rollback);
                // Không setAutoCommit(true) khi rollback lỗi vì có thể commit dữ liệu dở.
                try { c.close(); } catch(SQLException close) { ex.addSuppressed(close); }
            }
            throw ex;
        } finally {
            if (ended) {
                try { c.setAutoCommit(true); c.setTransactionIsolation(isolation); }
                catch(SQLException reset) {
                    try { c.close(); } catch(SQLException close) { reset.addSuppressed(close); }
                    if(failure!=null) failure.addSuppressed(reset);
                    else System.err.println("Phiếu đã lưu; không khôi phục được kết nối, cần kết nối lại: "+reset.getMessage());
                }
            }
        }
    }
}
