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
    private Scene galleryScene;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        mainScene = new Scene(root, 900, 700);
        galleryScene = createGalleryScene(primaryStage);

        InteractiveImageView interactiveImageView = new InteractiveImageView();
        root.setCenter(interactiveImageView);
        Button displayButton = new Button("Display Gallery");
        displayButton.setStyle("-fx-padding: 10;");
        displayButton.setOnAction(e -> primaryStage.setScene(galleryScene));
        root.setBottom(displayButton);

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
        HBox hbox = new HBox(10, btnLoad, interactiveImageView.getColorPicker(), btnSave,displayButton);
        hbox.setStyle("-fx-padding: 10;");
        root.setBottom(hbox); // Set the HBox at the bottom

        primaryStage.setTitle("Interactive Image Viewer");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    private Scene createGalleryScene(Stage primaryStage) {
        return ImageGalleryScreen.createScene(primaryStage, mainScene);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
