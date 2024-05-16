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
    private Comparsion comparsion;
    private Scene galleryScene;
    private Scene mainScene;
    private Scene reportScene;
    XRayClassifier classifier = new XRayClassifier();


    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        this.mainScene = new Scene(root, 900.0D, 700.0D);
        this.galleryScene = this.createGalleryScene(primaryStage);
        Button addBtn = new Button("add new image");
        addBtn.setOnAction((e) -> {
            this.addImageScene(primaryStage);
        });
        Button displayButton = new Button("Display Gallery");
        displayButton.setOnAction((e) -> {
            primaryStage.setScene(this.galleryScene);
        });
        root.setBottom(displayButton);
        Button cButton=new Button("Compare images");
        cButton.setOnAction((e) -> {
            this.addImageSceneToCompare(primaryStage);
        });
        Button classifyButton=new Button("classify image" );
        classifyButton.setOnAction((e) -> {
            this.addImageSceneToClassify(primaryStage);
        });

        HBox hbox = new HBox(10.0D, new Node[]{addBtn, displayButton,cButton,classifyButton});
        hbox.setStyle("-fx-padding: 10;");
        root.setBottom(hbox);
        primaryStage.setTitle("Multi-media project");
        primaryStage.setScene(this.mainScene);
        primaryStage.show();
    }

    private static Button getButton(Stage primaryStage, InteractiveImageView interactiveImageView) {
        Button btnLoad = new Button("Load Image");
        btnLoad.setOnAction((e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter[]{new ExtensionFilter("Image Files", new String[]{"*.png", "*.jpg", "*.gif", "*.bmp"})});
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    Image img = new Image(file.toURI().toString(), 800.0D, 600.0D, false, false);
                    interactiveImageView.setImage(img);
                } catch (Exception var6) {
                    var6.printStackTrace();
                }
            }

        });
        return btnLoad;
    }

    private void addImageScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene addImageScene = new Scene(root, 900.0D, 700.0D);
        InteractiveImageView interactiveImageView = new InteractiveImageView();
        root.setCenter(interactiveImageView);
        Button btnLoad = getButton(primaryStage, interactiveImageView);
        Button btnSave = interactiveImageView.getSaveButton();
        //zip image...................................................................

        Image image = interactiveImageView.getImage();
        byte[] imageData = new byte[0];
        if (image != null) {
            // Convert Image to byte array
            imageData = imageToByteArray(image);
        } else {
            System.out.println("ImageView does not have an image set.");
        }

        // byte[] imageData = new byte[]{/* Image data */};
        byte[] audioData = new byte[]{/* Audio data */};
        byte[] pdfData = new byte[]{/* PDF data */};

        String zipFileName = "C://Users//asus//Desktop//compressed_files.zip"; // Change this path to your desired location

        Button zipButton=new Button("zip image");
        byte[] finalImageData = imageData;
        zipButton.setOnAction((e) -> {
            ZipFiles.zipFiles(finalImageData, audioData, pdfData, zipFileName);
        });

        Button back = new Button("Back");
        back.setOnAction((e) -> {
            primaryStage.setScene(this.mainScene);
        });
        HBox hBox = new HBox(10.0D, new Node[]{back, btnLoad, interactiveImageView.getColorPicker(), zipButton,btnSave});
        hBox.setStyle("-fx-padding: 10;");
        root.setTop(hBox);
        primaryStage.setScene(addImageScene);
    }
    private static byte[] imageToByteArray(Image image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // Write the image data to the output stream
            javax.imageio.ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
    private void addImageSceneToCompare(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene addImageScene = new Scene(root, 900.0D, 700.0D);
        InteractiveImageView interactiveImageView1 = new InteractiveImageView();
        InteractiveImageView interactiveImageView2 = new InteractiveImageView();
        interactiveImageView1.setFitWidth(400);
        interactiveImageView1.setFitHeight(400);
        interactiveImageView2.setFitWidth(400);
        interactiveImageView2.setFitHeight(400);

        root.setRight(interactiveImageView2);
        root.setLeft(interactiveImageView1);
        Button btnLoad1 = getButton(primaryStage, interactiveImageView1);
        Button btnLoad2 = getButton(primaryStage, interactiveImageView2);
        Button compare=new Button("compare");
        compare.setOnAction(event -> Comparsion.compareImages(interactiveImageView1,interactiveImageView2));

        Button back = new Button("Back");
        back.setOnAction((e) -> {
            primaryStage.setScene(this.mainScene);
        });
        HBox hBox = new HBox(10.0D, new Node[]{back, btnLoad1,btnLoad2,compare, interactiveImageView1.getColorPicker(),interactiveImageView2.getColorPicker()});
        hBox.setStyle("-fx-padding: 10;");
        root.setTop(hBox);
        primaryStage.setScene(addImageScene);
    }
    private void addImageSceneToClassify(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene addImageScene = new Scene(root, 900.0D, 700.0D);
        InteractiveImageView interactiveImageView1 = new InteractiveImageView();
        root.setCenter(interactiveImageView1);
        Button btnLoad1 = getButton(primaryStage, interactiveImageView1);

        Button classifyButton = new Button("Classify");
        classifyButton.setOnAction(event -> {
            Image image = interactiveImageView1.getImage();
            if (image != null) {
                XRayClassifier.Severity severity = classifier.classify(image);
                classifier.displayClassificationResult(severity);
            } else {
                System.out.println("Error: No image loaded to classify.");
            }
        });

        Button back = new Button("Back");
        back.setOnAction((e) -> {
            primaryStage.setScene(this.mainScene);
        });

        HBox hBox = new HBox(10.0D, new Node[]{back, btnLoad1, classifyButton, interactiveImageView1.getColorPicker()});
        hBox.setStyle("-fx-padding: 10;");
        root.setTop(hBox);
        primaryStage.setScene(addImageScene);
    }



    private Scene createGalleryScene(Stage primaryStage) {
        return ImageGalleryScreen.createScene(primaryStage, this.mainScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}1