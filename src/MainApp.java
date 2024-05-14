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
    private Scene reportScene;
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        mainScene = new Scene(root, 900, 700);
        galleryScene = createGalleryScene(primaryStage);
        Button addBtn=new Button("add new image");
        addBtn.setOnAction(e->addImageScene(primaryStage));
        Button displayButton = new Button("Display Gallery");
        displayButton.setOnAction(e -> primaryStage.setScene(galleryScene));
        root.setBottom(displayButton);

        reportScene = MedicalReportScreen.createScene(primaryStage, mainScene);
        Button reportButton = new Button("Create Medical Report");
        reportButton.setOnAction(e -> primaryStage.setScene(reportScene));

        HBox hbox = new HBox(10,addBtn,displayButton,reportButton);
        hbox.setStyle("-fx-padding: 10;");
        root.setBottom(hbox); // Set the HBox at the bottom

        primaryStage.setTitle("Multi-media project");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private static Button getButton(Stage primaryStage, InteractiveImageView interactiveImageView) {
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
        return btnLoad;
    }

    private void addImageScene(Stage primaryStage){
        BorderPane root = new BorderPane();
        Scene addImageScene=new Scene(root,900,700);
        InteractiveImageView interactiveImageView = new InteractiveImageView();
        root.setCenter(interactiveImageView);
        Button btnLoad = getButton(primaryStage, interactiveImageView);
        Button btnSave = interactiveImageView.getSaveButton();
        Button back=new Button("Back");
        back.setOnAction(e->primaryStage.setScene(mainScene));
        HBox hBox=new HBox(10, back,btnLoad, interactiveImageView.getColorPicker(), btnSave);
        hBox.setStyle("-fx-padding: 10;");
        root.setTop(hBox);
        primaryStage.setScene(addImageScene);

    }
    private Scene createGalleryScene(Stage primaryStage) {
        return ImageGalleryScreen.createScene(primaryStage, mainScene);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
