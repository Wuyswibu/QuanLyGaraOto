package ui;

import java.awt.*;
import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import service.PhuTungService;
import entity.PhuTung;

public final class QuanLyPhuTungFrame extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color RED = new Color(190, 22, 58);
	private static final Color BACKGROUND = new Color(240, 243, 246);
	private static final Color BORDER = new Color(220, 224, 230);

	// ==================== SERVICE VÀ DỮ LIỆU ====================
	private final PhuTungService phuTungService;
	private String tuKhoaDangApDung = "";
	private ArrayList<PhuTung> danhSachPhuTung = new ArrayList<>();
	private boolean dangTaiDuLieu;
	private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

	// ==================== FORM ====================
	private final JTextField codeField = new JTextField();
	private final JTextField nameField = new JTextField();

	// Đã thay thế unitField bằng unitCombo với 4 lựa chọn
	private final JComboBox<String> unitCombo = new JComboBox<>(new String[] { "Cái", "Chai", "Bình", "Bộ" });

	private final JTextField descriptionField = new JTextField();
	private final JTextField salePriceField = new JTextField();
	private final JTextField stockField = new JTextField();
	private final JTextField minimumStockField = new JTextField();
	private final JTextField statusField = new JTextField();
	private final JTextField searchField = new JTextField();
	private final JComboBox<String> filterCombo = new JComboBox<>(new String[] { "Tất cả" });
	private final JLabel lblSoLuong = new JLabel("Đang tải dữ liệu...");

	// ==================== NÚT ====================
	private final JButton newButton = new JButton("Nhập mới");
	private final JButton homeButton = new JButton("Về màn hình chính");
	private final JButton reloadButton = new JButton("Tải lại");
	private final JButton addButton = new JButton("Thêm phụ tùng");
	private final JButton editButton = new JButton("Sửa phụ tùng");
	private final JButton stopButton = new JButton("Ngừng kinh doanh");
	private final JButton searchButton = new JButton("Tìm kiếm");

	// ==================== BẢNG ====================
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new String[] { "Mã PT", "Tên phụ tùng", "ĐVT", "Giá bán", "Tồn kho", "Tồn tối thiểu", "Trạng thái" }, 0) {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		@Override
		public Class<?> getColumnClass(int column) {
			if (column == 3)
				return BigDecimal.class;
			if (column == 4 || column == 5)
				return Integer.class;
			return String.class;
		}
	};

	private final JTable partsTable = new JTable(tableModel);
	private final TableRowSorter<DefaultTableModel> boLoc = new TableRowSorter<>(tableModel);

	public QuanLyPhuTungFrame() {
		this(new PhuTungService());
	}

	public QuanLyPhuTungFrame(PhuTungService phuTungService) {
		this.phuTungService = java.util.Objects.requireNonNull(phuTungService);
		moneyFormat.setMaximumFractionDigits(2);
		buildInterface();
		bindEvents();
		loadPhuTungFromSQL();
	}

	// ==================== GIAO DIỆN ====================
	private void buildInterface() {
		setLayout(new BorderLayout());
		setBackground(BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

		JPanel content = new JPanel(new BorderLayout(0, 8));
		content.setBackground(Color.WHITE);
		content.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
				BorderFactory.createEmptyBorder(12, 14, 12, 14)));

		content.add(createFormSection(), BorderLayout.NORTH);
		content.add(createTableSection(), BorderLayout.CENTER);
		content.add(createButtonSection(), BorderLayout.SOUTH);
		add(content, BorderLayout.CENTER);
	}

	private JPanel createFormSection() {
		JPanel section = new JPanel(new BorderLayout(0, 10));
		section.setOpaque(false);

		JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
		titlePanel.setOpaque(false);
		JLabel title = new JLabel("QUẢN LÝ PHỤ TÙNG");
		title.setFont(new Font("Arial", Font.BOLD, 18));
		title.setForeground(new Color(30, 35, 45));

		JLabel description = new JLabel("Danh mục phụ tùng; tồn kho cập nhật qua phiếu nhập và xuất.");
		description.setFont(new Font("Arial", Font.PLAIN, 13));
		description.setForeground(Color.GRAY);
		titlePanel.add(title);
		titlePanel.add(description);

		JPanel form = new JPanel(new GridLayout(2, 4, 12, 6));
		form.setOpaque(false);

		codeField.setEditable(false);
		stockField.setEditable(false);
		statusField.setEditable(false);

		unitCombo.setFont(new Font("Arial", Font.PLAIN, 12));
		unitCombo.setBackground(Color.WHITE);

		form.add(createField("Mã phụ tùng", codeField));
		form.add(createField("Tên phụ tùng", nameField));
		form.add(createField("Đơn vị tính", unitCombo));
		form.add(createField("Mô tả", descriptionField));
		form.add(createField("Giá bán (đ)", salePriceField));
		form.add(createField("Số lượng tồn", stockField));
		form.add(createField("Tồn tối thiểu", minimumStockField));
		form.add(createField("Trạng thái", statusField));

		section.add(titlePanel, BorderLayout.NORTH);
		section.add(form, BorderLayout.CENTER);
		return section;
	}

	private JPanel createField(String label, JComponent input) {
		JPanel panel = new JPanel(new BorderLayout(0, 3));
		panel.setOpaque(false);
		JLabel fieldLabel = new JLabel(label);
		fieldLabel.setFont(new Font("Arial", Font.PLAIN, 13));
		input.setFont(new Font("Arial", Font.PLAIN, 12));
		input.setPreferredSize(new Dimension(180, 26));
		panel.add(fieldLabel, BorderLayout.NORTH);
		panel.add(input, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createTableSection() {
		JPanel section = new JPanel(new BorderLayout(0, 6));
		section.setOpaque(false);

		JPanel toolbar = new JPanel(new BorderLayout(15, 0));
		toolbar.setBackground(new Color(237, 240, 244));
		toolbar.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

		JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
		searchPanel.setOpaque(false);
		searchPanel.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);

		searchField.setPreferredSize(new Dimension(300, 26));
		searchField.setToolTipText("Tìm theo mã hoặc tên phụ tùng");

		searchButton.setFont(new Font("Arial", Font.BOLD, 12));
		searchButton.setPreferredSize(new Dimension(100, 26));
		searchButton.setFocusPainted(false);
		searchPanel.add(searchField, BorderLayout.CENTER);
		searchPanel.add(searchButton, BorderLayout.EAST);

		JPanel filterPanel = new JPanel(new BorderLayout(8, 0));
		filterPanel.setOpaque(false);
		filterPanel.add(new JLabel("Trạng thái:"), BorderLayout.WEST);
		filterPanel.add(filterCombo, BorderLayout.CENTER);

		toolbar.add(searchPanel, BorderLayout.CENTER);
		toolbar.add(filterPanel, BorderLayout.EAST);

		partsTable.setFont(new Font("Arial", Font.PLAIN, 13));
		partsTable.setRowHeight(28);
		partsTable.setPreferredScrollableViewportSize(new Dimension(1100, 8 * 28));
		partsTable.setFillsViewportHeight(true);
		partsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		partsTable.setRowSorter(boLoc);
		partsTable.setSelectionBackground(new Color(49, 106, 140));
		partsTable.setSelectionForeground(Color.WHITE);
		partsTable.setShowVerticalLines(false);
		partsTable.setGridColor(BORDER);

		partsTable.getTableHeader().setReorderingAllowed(false);
		partsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
		partsTable.getTableHeader().setPreferredSize(new Dimension(0, 28));

		partsTable.getColumnModel().getColumn(0).setPreferredWidth(90);
		partsTable.getColumnModel().getColumn(1).setPreferredWidth(300);
		partsTable.getColumnModel().getColumn(2).setPreferredWidth(70);
		partsTable.getColumnModel().getColumn(3).setPreferredWidth(150);
		partsTable.getColumnModel().getColumn(6).setPreferredWidth(160);

		DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void setValue(Object value) {
				if (value instanceof Number) {
					setText(moneyFormat.format(value) + " đ");
				} else {
					super.setValue(value);
				}
			}
		};
		moneyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		partsTable.getColumnModel().getColumn(3).setCellRenderer(moneyRenderer);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		partsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		partsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		partsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

		JScrollPane scrollPane = new JScrollPane(partsTable);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));

		lblSoLuong.setForeground(Color.GRAY);

		section.add(toolbar, BorderLayout.NORTH);
		section.add(scrollPane, BorderLayout.CENTER);
		section.add(lblSoLuong, BorderLayout.SOUTH);
		return section;
	}

	private JPanel createButtonSection() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		panel.setOpaque(false);

		homeButton.setPreferredSize(new Dimension(165, 30));
		reloadButton.setPreferredSize(new Dimension(100, 30));
		addButton.setPreferredSize(new Dimension(145, 30));
		editButton.setPreferredSize(new Dimension(145, 30));
		stopButton.setPreferredSize(new Dimension(165, 30));
		newButton.setPreferredSize(new Dimension(110, 30));

		homeButton.setContentAreaFilled(false);
		homeButton.setOpaque(true);
		homeButton.setBackground(RED);
		homeButton.setForeground(Color.WHITE);
		homeButton.setFocusPainted(false);
		homeButton.setBorderPainted(false);

		stopButton.setContentAreaFilled(false);
		stopButton.setOpaque(true);
		stopButton.setBackground(RED);
		stopButton.setForeground(Color.WHITE);
		stopButton.setFocusPainted(false);
		stopButton.setBorderPainted(false);

		panel.add(homeButton);
		panel.add(newButton);
		panel.add(reloadButton);
		panel.add(addButton);
		panel.add(editButton);
		panel.add(stopButton);
		return panel;
	}

	// ==================== TẢI DỮ LIỆU QUA SERVICE ====================
	public void loadPhuTungFromSQL() {
		if (dangTaiDuLieu)
			return;
		searchField.setText("");
		tuKhoaDangApDung = "";
		taiDanhSach(false);
	}

	private void timKiem() {
		if (dangTaiDuLieu)
			return;
		tuKhoaDangApDung = searchField.getText().trim();
		taiDanhSach(true);
	}

	private void taiDanhSach(boolean giuBoLoc) {
		final String tuKhoa = tuKhoaDangApDung;
		final String trangThaiCu = giuBoLoc ? (String) filterCombo.getSelectedItem() : "Tất cả";

		chayNen(() -> tuKhoa.isEmpty() ? phuTungService.layDanhSachPhuTung() : phuTungService.timKiem(tuKhoa),
				ketQua -> hienThiDanhSach(ketQua, trangThaiCu), "Đang tải danh sách phụ tùng…");
	}

	private void hienThiDanhSach(ArrayList<PhuTung> ketQua, String trangThaiCu) {
		dangTaiDuLieu = true;
		try {
			partsTable.clearSelection();
			boLoc.setRowFilter(null);
			tableModel.setRowCount(0);
			danhSachPhuTung = new ArrayList<>(ketQua);

			filterCombo.removeAllItems();
			filterCombo.addItem("Tất cả");
			filterCombo.addItem("Đang kinh doanh");
			filterCombo.addItem("Ngừng kinh doanh");

			for (PhuTung pt : danhSachPhuTung) {
				tableModel.addRow(new Object[] { pt.getMaPhuTung(), pt.getTenPhuTung(), pt.getDonViTinh(),
						pt.getDonGia(), pt.getSoLuongTon(), pt.getMucTonToiThieu(), pt.getTrangThai() });

				String trangThai = pt.getTrangThai();
				DefaultComboBoxModel<String> trangThaiModel = (DefaultComboBoxModel<String>) filterCombo.getModel();

				if (trangThai != null && trangThaiModel.getIndexOf(trangThai) < 0) {
					filterCombo.addItem(trangThai);
				}
			}

			filterCombo.setSelectedItem(trangThaiCu == null ? "Tất cả" : trangThaiCu);
			clearForm();
		} finally {
			dangTaiDuLieu = false;
		}
		locDuLieu();
		capNhatNutTheoLuaChon();
	}

	// ==================== SỰ KIỆN ====================
	private void bindEvents() {
		partsTable.getSelectionModel().addListSelectionListener(event -> {
			if (event.getValueIsAdjusting() || dangTaiDuLieu)
				return;

			int row = partsTable.getSelectedRow();
			if (row >= 0) {
				showPart(partsTable.convertRowIndexToModel(row));
			} else {
				clearForm();
			}
			capNhatNutTheoLuaChon();
		});

		homeButton.addActionListener(event -> {
			if (dangTaiDuLieu)
				return;
			Window cuaSo = SwingUtilities.getWindowAncestor(this);
			if (cuaSo instanceof GaraManHinhChinh) {
				((GaraManHinhChinh) cuaSo).hienThiTrangChu();
			} else {
				JOptionPane.showMessageDialog(this, "Hãy mở giao diện từ GaraManHinhChinh.");
			}
		});

		newButton.addActionListener(event -> {
			if (dangTaiDuLieu)
				return;
			partsTable.clearSelection();
			clearForm();
			capNhatNutTheoLuaChon();
			nameField.requestFocusInWindow();
		});

		reloadButton.addActionListener(event -> loadPhuTungFromSQL());
		searchButton.addActionListener(event -> timKiem());
		searchField.addActionListener(event -> timKiem());
		filterCombo.addActionListener(event -> locDuLieu());
		addButton.addActionListener(event -> luuPhuTung(false));
		editButton.addActionListener(event -> luuPhuTung(true));
		stopButton.addActionListener(event -> ngungKinhDoanh());
	}

	private void locDuLieu() {
		if (dangTaiDuLieu)
			return;
		final String trangThai = (String) filterCombo.getSelectedItem();

		boLoc.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
			@Override
			public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
				return trangThai == null || "Tất cả".equals(trangThai) || trangThai.equals(entry.getStringValue(6));
			}
		});

		lblSoLuong.setText(
				"Hiển thị: " + partsTable.getRowCount() + " / " + tableModel.getRowCount() + " phụ tùng - Dữ liệu SQL");
		if (partsTable.getSelectedRow() < 0)
			clearForm();
		capNhatNutTheoLuaChon();
	}

	// ==================== THÊM / SỬA / NGỪNG KINH DOANH ====================
	private PhuTung phuTungDangChon() {
		int row = partsTable.getSelectedRow();
		if (row < 0)
			return null;
		int modelRow = partsTable.convertRowIndexToModel(row);
		return modelRow < danhSachPhuTung.size() ? danhSachPhuTung.get(modelRow) : null;
	}

	private void capNhatNutTheoLuaChon() {
		if (dangTaiDuLieu)
			return;
		PhuTung pt = phuTungDangChon();
		addButton.setEnabled(pt == null);
		editButton.setEnabled(pt != null);
		stopButton.setEnabled(pt != null && !"Ngừng kinh doanh".equalsIgnoreCase(pt.getTrangThai()));
	}

	private PhuTung docForm(PhuTung ptCu) {
		String tenPT = nameField.getText().trim();
		if (tenPT.isEmpty()) {
			throw new IllegalArgumentException("Tên phụ tùng không được để trống.");
		}

		if (!tenPT.matches("^[\\p{Lu}][^\\p{Lu}]*$")) {
			throw new IllegalArgumentException(
					"Tên phụ tùng chỉ được viết hoa chữ cái đầu tiên, các chữ phía sau phải viết thường!");
		}

		PhuTung pt = new PhuTung();
		pt.setTenPhuTung(tenPT);

		String dvt = unitCombo.getSelectedItem() != null ? unitCombo.getSelectedItem().toString().trim() : "Cái";
		pt.setDonViTinh(dvt);
		pt.setMoTa(descriptionField.getText().trim());
		pt.setDonGia(docGiaBan(salePriceField.getText()));
		pt.setMucTonToiThieu(docTonToiThieu(minimumStockField.getText()));

		if (ptCu != null) {
			pt.setMaPhuTung(ptCu.getMaPhuTung());
			pt.setMaPhieuSuaChua(ptCu.getMaPhieuSuaChua());
			pt.setSoLuongTon(ptCu.getSoLuongTon());
			pt.setTrangThai(ptCu.getTrangThai());
		}
		return pt;
	}

	private BigDecimal docGiaBan(String giaTri) {
		String s = giaTri == null ? "" : giaTri.trim();
		if (!s.matches("(?:[0-9]+|[0-9]{1,3}(?:\\.[0-9]{3})+)(?:,[0-9]{1,2})?")) {
			throw new IllegalArgumentException("Giá bán phải là số hoặc số không âm. Ví dụ: 120000 hoặc 120.000,50.");
		}
		return new BigDecimal(s.replace(".", "").replace(',', '.'));
	}

	private int docTonToiThieu(String giaTri) {
		String s = giaTri == null ? "" : giaTri.trim();
		try {
			if (!s.matches("[0-9]+"))
				throw new NumberFormatException();
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Tồn tối thiểu phải là số nguyên từ 0 đến 2147483647.");
		}
	}

	private void luuPhuTung(boolean sua) {
		if (dangTaiDuLieu)
			return;
		PhuTung ptCu = phuTungDangChon();
		if (sua && ptCu == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn phụ tùng cần sửa.");
			return;
		}
		if (!sua && ptCu != null) {
			JOptionPane.showMessageDialog(this, "Bấm Nhập mới trước khi thêm phụ tùng.");
			return;
		}

		final PhuTung phuTung;
		try {
			phuTung = docForm(sua ? ptCu : null);
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
			return;
		}

		chayNen(() -> sua ? phuTungService.suaPhuTung(phuTung) : phuTungService.themPhuTung(phuTung), thanhCong -> {
			if (!thanhCong) {
				JOptionPane.showMessageDialog(this, "Chưa lưu được phụ tùng.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			JOptionPane.showMessageDialog(this, sua ? "Sửa phụ tùng thành công." : "Thêm phụ tùng thành công.");
			taiDanhSach(true);
		}, sua ? "Đang sửa phụ tùng…" : "Đang thêm phụ tùng…");
	}

	private void ngungKinhDoanh() {
		if (dangTaiDuLieu)
			return;
		PhuTung pt = phuTungDangChon();
		if (pt == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn phụ tùng.");
			return;
		}
		if (JOptionPane.showConfirmDialog(this,
				"Ngừng kinh doanh phụ tùng " + pt.getMaPhuTung() + " — " + pt.getTenPhuTung() + "?", "Xác nhận",
				JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
			return;

		String ma = pt.getMaPhuTung();
		chayNen(() -> phuTungService.ngungKinhDoanh(ma), thanhCong -> {
			if (!thanhCong) {
				JOptionPane.showMessageDialog(this, "Chưa cập nhật được trạng thái.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			JOptionPane.showMessageDialog(this, "Đã ngừng kinh doanh phụ tùng.");
			taiDanhSach(true);
		}, "Đang cập nhật trạng thái…");
	}

	// ==================== XỬ LÝ NỀN ====================
	private void khoaThaoTac(boolean khoa) {
		dangTaiDuLieu = khoa;
		homeButton.setEnabled(!khoa);
		newButton.setEnabled(!khoa);
		reloadButton.setEnabled(!khoa);
		addButton.setEnabled(!khoa);
		editButton.setEnabled(!khoa);
		stopButton.setEnabled(!khoa);
		searchButton.setEnabled(!khoa);
		searchField.setEnabled(!khoa);
		filterCombo.setEnabled(!khoa);
		partsTable.setEnabled(!khoa);
		nameField.setEnabled(!khoa);
		unitCombo.setEnabled(!khoa); // Đã sửa khóa thao tác trên unitCombo
		descriptionField.setEnabled(!khoa);
		salePriceField.setEnabled(!khoa);
		minimumStockField.setEnabled(!khoa);
		if (!khoa)
			capNhatNutTheoLuaChon();
	}

	private <T> void chayNen(Callable<T> viec, Consumer<T> khiThanhCong, String thongBao) {
		if (dangTaiDuLieu)
			return;
		khoaThaoTac(true);
		lblSoLuong.setText(thongBao);

		new SwingWorker<T, Void>() {
			@Override
			protected T doInBackground() throws Exception {
				return viec.call();
			}

			@Override
			protected void done() {
				T ketQua = null;
				boolean thanhCong = false;
				try {
					ketQua = get();
					thanhCong = true;
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					lblSoLuong.setText("Đã ngắt thao tác.");
				} catch (ExecutionException ex) {
					Throwable loi = ex.getCause();
					loi.printStackTrace();
					lblSoLuong.setText("Thao tác thất bại; dữ liệu trên bảng có thể đã cũ.");
					JOptionPane.showMessageDialog(QuanLyPhuTungFrame.this, loi.getMessage(), "Không thực hiện được",
							JOptionPane.ERROR_MESSAGE);
				} finally {
					khoaThaoTac(false);
				}
				if (thanhCong)
					khiThanhCong.accept(ketQua);
			}
		}.execute();
	}

	// ==================== HIỂN THỊ LÊN FORM ====================
	private void showPart(int row) {
		if (row < 0 || row >= danhSachPhuTung.size())
			return;
		PhuTung pt = danhSachPhuTung.get(row);

		codeField.setText(pt.getMaPhuTung());
		nameField.setText(pt.getTenPhuTung());
		if (pt.getDonViTinh() != null) {
			unitCombo.setSelectedItem(pt.getDonViTinh());
		}
		descriptionField.setText(pt.getMoTa() == null ? "" : pt.getMoTa());
		salePriceField.setText(pt.getDonGia() == null ? "" : moneyFormat.format(pt.getDonGia()));
		stockField.setText(String.valueOf(pt.getSoLuongTon()));
		minimumStockField.setText(String.valueOf(pt.getMucTonToiThieu()));
		statusField.setText(pt.getTrangThai());
	}

	private void clearForm() {
		codeField.setText("");
		nameField.setText("");
		if (unitCombo.getItemCount() > 0) {
			unitCombo.setSelectedIndex(0);
		}
		descriptionField.setText("");
		salePriceField.setText("");
		stockField.setText("0");
		minimumStockField.setText("0");
		statusField.setText("Đang kinh doanh");
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			connectDB.ConnectDB.getInstance().connect();
		} catch (Exception e) {
			System.err.println("Chưa kết nối được CSDL: " + e.getMessage());
		}

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Quản lý phụ tùng - NIGHT INVADER");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setContentPane(new QuanLyPhuTungFrame());
			frame.setSize(1340, 840);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}