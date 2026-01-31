package ProjectInterface;

import javax.swing.*; //Untuk UI Swing
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*; //Untuk Layout dan Warna, desain.
import java.awt.event.*;
import java.sql.*; //Koneksi database
import java.util.Vector;

public class MahasiswaInterface extends JFrame { //Mendeklarasikan class MahasiswaInterface yang merupakan turunan dari JFrame (jendela aplikasi).

    // --- Konfigurasi Database ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/uas_java";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    // --- Palet Warna ---
    private final Color CLR_BG = new Color(244, 247, 254);
    private final Color CLR_WHITE = Color.WHITE;
    private final Color CLR_PRIMARY = new Color(67, 24, 255);
    private final Color CLR_TEXT_DARK = new Color(43, 54, 116);
    private final Color CLR_TEXT_GRAY = new Color(163, 174, 208);
    private final Color CLR_DANGER = new Color(238, 93, 80);
    private final Color CLR_WARNING = new Color(255, 181, 71); // Warna Kuning untuk Edit

    // --- Komponen UI ---
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtNim, txtTglLahir;
    private JComboBox<String> cmbProdi;
    private JLabel lblTotalMhs, lblTotalProdi, lblTopProdi;
    
    // Komponen Tombol yang perlu diakses global
    private ModernButton btnSimpan, btnReset;

    // --- Variabel Logika Edit ---
    private String editNim = null; // -1 artinya mode TAMBAH, selain itu mode EDIT

    public void tampilkanInterface() {
        setTitle("Dashboard Mahasiswa");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout(20, 20)); 
        getContentPane().setBackground(CLR_BG);
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. NORTH (Header & Stats)
        add(initNorthPanel(), BorderLayout.NORTH);

        // 2. EAST (Form Input)
        add(initEastPanel(), BorderLayout.EAST);

        // 3. CENTER (Tabel)
        add(initCenterPanel(), BorderLayout.CENTER);

        loadData();
        updateStatistik();

        setVisible(true);
    }

    // =========================================
    // SECTION 1: HEADER & STATISTIK (NORTH)
    // =========================================
    private JPanel initNorthPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        // --- PERBAIKAN 1: CENTER ALIGNMENT ---
        JLabel title = new JLabel("Database Mahasiswa");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(CLR_TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // Agar pas di tengah
        
        JLabel subtitle = new JLabel("Sistem Informasi Akademik Institut");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(CLR_TEXT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT); // Agar pas di tengah

        // Statistik Cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(20, 0, 10, 0));
        statsGrid.setMaximumSize(new Dimension(2000, 120)); // Membatasi tinggi agar tidak terlalu lebar

        lblTotalMhs = new JLabel("0");
        lblTotalProdi = new JLabel("0");
        lblTopProdi = new JLabel("-");

        statsGrid.add(createStatCard("Total Mahasiswa", lblTotalMhs, "\uD83D\uDC65"));
        statsGrid.add(createStatCard("Program Studi", lblTotalProdi, "\uD83D\uDCDA"));
        statsGrid.add(createStatCard("Prodi Terpopuler", lblTopProdi, "\uD83D\uDCC8"));

        container.add(title);
        container.add(subtitle);
        container.add(statsGrid);

        return container;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String iconText) {
        RoundedPanel card = new RoundedPanel(20, CLR_WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTitle.setForeground(CLR_TEXT_GRAY);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(CLR_TEXT_DARK);

        JLabel lblIcon = new JLabel(iconText);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        lblIcon.setForeground(CLR_PRIMARY);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    // =========================================
    // SECTION 2: FORM INPUT (EAST)
    // =========================================
    private JPanel initEastPanel() {
        RoundedPanel panel = new RoundedPanel(20, CLR_WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(320, 0));

        JLabel lblForm = new JLabel("Form Data");
        lblForm.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblForm.setForeground(CLR_TEXT_DARK);
        lblForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Input Fields
        txtNama = createModernTextField();
        txtNim = createModernTextField();
        txtTglLahir = createModernTextField();
        
        String[] prodi = {"Teknologi Informasi", "Sistem Informasi", "Bisnis Digital", "Manajemen Informatika", "Sistem Komputer"};
        cmbProdi = new JComboBox<>(prodi);
        cmbProdi.setBackground(Color.WHITE);
        cmbProdi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // --- Tombol Aksi ---
        btnSimpan = new ModernButton("Simpan Data", CLR_PRIMARY, Color.WHITE);
        btnSimpan.addActionListener(e -> prosesSimpan()); // Logic baru: Cek Insert/Update
        
        btnReset = new ModernButton("Reset Form", new Color(230, 230, 240), CLR_TEXT_GRAY);
        btnReset.addActionListener(e -> clearForm());

        // Susun Layout
        panel.add(lblForm);
        panel.add(Box.createVerticalStrut(25));
        
        addFormItem(panel, "Nama Lengkap", txtNama);
        addFormItem(panel, "NIM", txtNim);
        addFormItem(panel, "Tanggal Lahir (YYYY-MM-DD)", txtTglLahir);
        addFormItem(panel, "Program Studi", cmbProdi);

        panel.add(Box.createVerticalGlue());
        panel.add(btnSimpan);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnReset);

        return panel;
    }

    private void addFormItem(JPanel p, String label, JComponent c) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(CLR_TEXT_DARK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        p.add(l);
        p.add(Box.createVerticalStrut(8));
        p.add(c);
        p.add(Box.createVerticalStrut(20));
    }

    private JTextField createModernTextField() {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setPreferredSize(new Dimension(200, 40));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return f;
    }

    // =========================================
    // SECTION 3: TABEL DATA (CENTER)
    // =========================================
    private JPanel initCenterPanel() {
        RoundedPanel panel = new RoundedPanel(20, CLR_WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));
        
        JLabel lblTable = new JLabel("Daftar Mahasiswa");
        lblTable.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTable.setForeground(CLR_TEXT_DARK);
        headerPanel.add(lblTable, BorderLayout.WEST);

        String[] columns = {"ID", "NIM", "Nama Lengkap", "Program Studi", "Tgl Lahir"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(240, 245, 255));
        table.setSelectionForeground(CLR_TEXT_DARK);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(CLR_TEXT_GRAY);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(240,240,240)));
        
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBorder(BorderFactory.createEmptyBorder());

        // --- FOOTER DENGAN TOMBOL EDIT & HAPUS ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        
        // Tombol Edit (Kuning/Oranye)
        ModernButton btnEdit = new ModernButton("Edit Data", CLR_WARNING, Color.WHITE);
        btnEdit.setPreferredSize(new Dimension(120, 35));
        btnEdit.addActionListener(e -> tarikDataKeForm()); // Aksi Edit
        
        // Tombol Hapus (Merah)
        ModernButton btnHapus = new ModernButton("Hapus Data", CLR_DANGER, Color.WHITE);
        btnHapus.setPreferredSize(new Dimension(120, 35));
        btnHapus.addActionListener(e -> hapusData());
        
        footer.add(btnEdit);
        footer.add(btnHapus);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================
    // CUSTOM COMPONENTS
    // =========================================
    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    class ModernButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        ModernButton(String text, Color bg, Color fg) {
            super(text);
            this.normalColor = bg;
            this.hoverColor = bg.darker();
            setBackground(bg);
            setForeground(fg);
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setFocusPainted(true);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(hoverColor); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(normalColor); repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground() == null ? normalColor : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
        }
    }

    // =========================================
    // LOGIC DATABASE (CRUD LENGKAP)
    // =========================================
    
    // 1. READ
    private void loadData() {
    // Hapus kolom ID dari tampilan tabel
    String[] columns = {"NIM", "Nama Lengkap", "Program Studi", "Tgl Lahir"};
    tableModel.setColumnIdentifiers(columns);
    
    tableModel.setRowCount(0);
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         Statement stmt = conn.createStatement();
         // Urutkan berdasarkan NIM
         ResultSet rs = stmt.executeQuery("SELECT * FROM mahasiswa ORDER BY nim ASC")) {
        
        while(rs.next()) {
            Vector<Object> row = new Vector<>();
            // Tidak ada rs.getInt("id") lagi
            row.add(rs.getString("nim"));
            row.add(rs.getString("nama"));
            row.add(rs.getString("prodi"));
            row.add(rs.getDate("tanggal_lahir"));
            tableModel.addRow(row);
        }
    } catch (SQLException e) { e.printStackTrace(); }
}

    // 2. CREATE & UPDATE (Digabung dalam satu tombol)
    private void prosesSimpan() { //Fungsi ini bersifat ganda. Jika editNim kosong, ia menjalankan perintah SQL INSERT (menambah data).
    if(txtNama.getText().isEmpty() || txtNim.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama dan NIM wajib diisi!"); return;
    }

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        if (editNim == null) {
            // --- MODE INSERT ---
            // Cek dulu apakah NIM sudah ada (Opsional, karena DB akan menolak jika duplicate)
            String sql = "INSERT INTO mahasiswa (nim, nama, tanggal_lahir, prodi) VALUES (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtNim.getText());
            ps.setString(2, txtNama.getText());
            ps.setString(3, txtTglLahir.getText());
            ps.setString(4, cmbProdi.getSelectedItem().toString());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Berhasil Ditambah!");
        } else {
            // --- MODE UPDATE ---
            // Query UPDATE tidak mengubah NIM, karena NIM adalah Primary Key yang sedang diedit
            String sql = "UPDATE mahasiswa SET nama=?, tanggal_lahir=?, prodi=? WHERE nim=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtNama.getText());
            ps.setString(2, txtTglLahir.getText());
            ps.setString(3, cmbProdi.getSelectedItem().toString());
            ps.setString(4, editNim); // Kunci pencarian berdasarkan NIM lama
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Berhasil Diperbarui!");
        }
        
        clearForm();
        loadData();
        updateStatistik();
        
    } catch (SQLException e) {
        // Tangkap error jika NIM kembar (Duplicate entry)
        if(e.getMessage().contains("Duplicate entry")) {
            JOptionPane.showMessageDialog(this, "Gagal: NIM tersebut sudah terdaftar!");
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}

    // 3. LOGIC EDIT (Tarik data ke Form)
    private void tarikDataKeForm() {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Pilih baris data yang ingin diedit!");
        return;
    }

    // Ambil data dari model tabel (Index berubah karena kolom ID dihapus)
    String nim = (String) tableModel.getValueAt(row, 0);
    String nama = (String) tableModel.getValueAt(row, 1);
    String prodi = (String) tableModel.getValueAt(row, 2);
    Date tgl = (Date) tableModel.getValueAt(row, 3);

    // Simpan NIM yang sedang diedit ke variabel global
    editNim = nim;

    // Isi ke Form
    txtNim.setText(nim);
    txtNama.setText(nama);
    txtTglLahir.setText(tgl.toString());
    cmbProdi.setSelectedItem(prodi);

    // --- KUNCI FIELD NIM AGAR TIDAK BISA DIEDIT ---
    txtNim.setEditable(false);
    txtNim.setBackground(new Color(240, 240, 240)); // Ubah warna jadi abu-abu agar terlihat mati

    // Ubah Tampilan Tombol
    btnSimpan.setText("Update Data");
    btnSimpan.setBackground(CLR_WARNING);
    btnReset.setText("Batal Edit");
}

    // 4. DELETE
    private void hapusData() {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Pilih baris tabel dulu!"); return;
    }
    
    // Ambil NIM dari kolom ke-0
    String nim = tableModel.getValueAt(row, 0).toString();
    
    int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data NIM " + nim + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
    if(confirm == JOptionPane.YES_OPTION) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement("DELETE FROM mahasiswa WHERE nim=?")) {
            ps.setString(1, nim); 
            ps.executeUpdate();
            loadData(); 
            updateStatistik();
            clearForm();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}

    // 5. RESET FORM
    private void clearForm() {
    txtNama.setText(""); 
    txtNim.setText(""); 
    txtTglLahir.setText(""); 
    cmbProdi.setSelectedIndex(0);
    
    // --- RESET LOGIKA EDIT ---
    editNim = null; // Kembali ke mode Insert
    
    // Aktifkan kembali input NIM
    txtNim.setEditable(true);
    txtNim.setBackground(Color.WHITE); // Kembalikan warna putih
    
    btnSimpan.setText("Simpan Data");
    btnSimpan.setBackground(CLR_PRIMARY);
    btnReset.setText("Reset Form");
}

    private void updateStatistik() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM mahasiswa");
            if(rs1.next()) lblTotalMhs.setText(rs1.getString(1));
            
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(DISTINCT prodi) FROM mahasiswa");
            if(rs2.next()) lblTotalProdi.setText(rs2.getString(1));
            
            ResultSet rs3 = stmt.executeQuery("SELECT prodi FROM mahasiswa GROUP BY prodi ORDER BY COUNT(*) DESC LIMIT 1");
            if(rs3.next()) lblTopProdi.setText(rs3.getString(1));
            else lblTopProdi.setText("-");
            
        } catch (SQLException e) { e.printStackTrace(); }
    }
}