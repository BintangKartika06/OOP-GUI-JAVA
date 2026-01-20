import ProjectInterface.MahasiswaInterface;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Mengatur tema tampilan agar mengikuti sistem operasi (opsional tapi disarankan)
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            MahasiswaInterface app = new MahasiswaInterface();
            app.tampilkanInterface();
        });
    }
}