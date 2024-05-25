import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MainApp extends Application {
    private Scene galleryScene;

    @Override
    public void start(Stage primaryStage) {
        this.galleryScene = this.createGalleryScene(primaryStage);
        primaryStage.setTitle("X-Ray application");
        primaryStage.setScene(this.galleryScene);
        primaryStage.show();
    }




    private Scene createGalleryScene(Stage primaryStage) {
        return ImageGalleryScreen.createScene(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}