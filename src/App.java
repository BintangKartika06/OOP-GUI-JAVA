import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Membuat label, text field, dan button
        Label label = new Label("Masukkan Nama:");
        TextField textField = new TextField();
        Button button = new Button("Tampilkan Nama");

        // Label untuk menampilkan hasil
        Label resultLabel = new Label();

        // Aksi saat tombol ditekan
        button.setOnAction(e -> {
            String nama = textField.getText();
            resultLabel.setText("Nama Anda: " + nama);
        });

        // Menyusun komponen-komponen dalam layout VBox
        VBox vbox = new VBox(10, label, textField, button, resultLabel);
        vbox.setStyle("-fx-padding: 20;");

        // Membuat scene dan menetapkan ukuran window
        Scene scene = new Scene(vbox, 300, 200);

        // Menetapkan judul dan menampilkan window
        primaryStage.setTitle("GUI Sederhana JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}