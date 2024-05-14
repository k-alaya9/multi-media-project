import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageGalleryScreen {

    private static final String IMAGE_FOLDER_PATH = "editImage";

    public static Scene createScene(Stage primaryStage, Scene mainScene) {
        List<File> allImageFiles = loadImagesFromFolder(IMAGE_FOLDER_PATH);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateImageGallery(gridPane, allImageFiles, newValue);
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        VBox layout = new VBox(10, searchField, new ScrollPane(gridPane), backButton);
        layout.setPadding(new Insets(10));

        Scene galleryScene = new Scene(layout, 800, 600);

        // Initially display all images
        updateImageGallery(gridPane, allImageFiles, "");

        return galleryScene;
    }

    private static void updateImageGallery(GridPane gridPane, List<File> allImageFiles, String filter) {
        gridPane.getChildren().clear();

        List<File> filteredFiles = allImageFiles.stream()
                .filter(file -> file.getName().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());

        int row = 0;
        int col = 0;
        for (File imageFile : filteredFiles) {
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            Label label = new Label(imageFile.getName());

            gridPane.add(imageView, col, row);
            gridPane.add(label, col, row + 1);

            col++;
            if (col > 3) { // Adjust column count as needed
                col = 0;
                row += 2;
            }
        }
    }
    public static List<File> loadImagesFromFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        List<File> imageFiles = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && isImageFile(file)) {
                    imageFiles.add(file);
                }
            }
        }
        return imageFiles;
    }

    private static boolean isImageFile(File file) {
        String[] imageExtensions = { "jpg", "jpeg", "png", "gif", "bmp" };
        String fileName = file.getName().toLowerCase();
        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
