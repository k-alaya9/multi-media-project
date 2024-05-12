import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox; // Import HBox
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.File;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 900, 700);

        InteractiveImageView interactiveImageView = new InteractiveImageView();
        root.setCenter(interactiveImageView);

        Button btnLoad = new Button("Load Image");
        btnLoad.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    Image img = new Image(file.toURI().toString(), 800, 600, false, false);
                    interactiveImageView.setImage(img);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        Button btnSave = interactiveImageView.getSaveButton();
        HBox hbox = new HBox(10, btnLoad, interactiveImageView.getColorPicker(), btnSave);
        hbox.setStyle("-fx-padding: 10;");
        root.setBottom(hbox); // Set the HBox at the bottom

        primaryStage.setTitle("Interactive Image Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
