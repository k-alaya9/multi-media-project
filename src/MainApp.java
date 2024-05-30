import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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