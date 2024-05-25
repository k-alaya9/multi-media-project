import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ImageGalleryScreen {

    static XRayClassifier classifier = new XRayClassifier();

    private static AudioRecorder audioRecorder;
    private static Button startButton;
    private static Button stopButton;


    private static final String IMAGE_FOLDER_PATH = "C:\\Users\\DELL\\Documents\\Project1\\editImage";

    public static Scene createScene(Stage primaryStage) {
        List<File> allImageFiles = loadImagesFromFolder(IMAGE_FOLDER_PATH);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");


        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Button sizeSort= new Button("size");
        sizeSort.setOnAction(e->{
            allImageFiles.sort(Comparator.comparingLong(File::length));
            updateImageGallery(gridPane,allImageFiles,"");
        });
        Button DateSort=new Button("Last Modified");
        DateSort.setOnAction(e->{
            allImageFiles.sort(Comparator.comparingLong(File::lastModified));
            updateImageGallery(gridPane,allImageFiles,"");
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateImageGallery(gridPane, allImageFiles, newValue));


        Button addBtn = new Button("add new image");
        addBtn.setOnAction((e) -> {
            addImageScene(primaryStage);
        });
        Button cButton=new Button("Compare images");
        cButton.setOnAction((e) -> {
            addImageSceneToCompare(primaryStage);
        });
        Button classifyButton=new Button("classify image" );
        classifyButton.setOnAction((e) -> {
            addImageSceneToClassify(primaryStage);
        });

        Button MedicalReport= new Button("create Medical Report");
        MedicalReport.setOnAction(e->{
            primaryStage.setScene(MedicalReportScreen.createScene(primaryStage,createGalleryScene(primaryStage)));
        });

        HBox hbox = new HBox(10.0D,addBtn,cButton,classifyButton,MedicalReport);
        hbox.setStyle("-fx-padding: 10;");
        HBox header=new HBox(10.0D,searchField,sizeSort,DateSort);
        VBox layout = new VBox(10,header , new ScrollPane(gridPane), hbox);
        layout.setPadding(new Insets(10));

        Scene galleryScene = new Scene(layout, 900, 700);

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
            if (col > 8) {
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
    private static Button getButton(Stage primaryStage, InteractiveImageView interactiveImageView) {
        Button btnLoad = new Button("Load Image");
        btnLoad.setOnAction((e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter("Image Files", new String[]{"*.png", "*.jpg", "*.gif", "*.bmp"})});
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
private static Button getButton2(Stage primaryStage, InteractiveImageView imageView1, InteractiveImageView imageView2) {
    Button button = new Button("Load Images");
    button.setOnAction(event -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select First Image");
        File file1 = fileChooser.showOpenDialog(primaryStage);
        if (file1 != null) {
            Image image1 = new Image(file1.toURI().toString());
            imageView1.setImage(image1);
        }

        fileChooser.setTitle("Select Second Image");
        File file2 = fileChooser.showOpenDialog(primaryStage);
        if (file2 != null) {
            Image image2 = new Image(file2.toURI().toString());
            imageView2.setImage(image2);
        }
    });
    return button;
}

    private static void addImageScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene addImageScene = new Scene(root, 900.0D, 700.0D);
        InteractiveImageView interactiveImageView = new InteractiveImageView();
        interactiveImageView.setUpControls();
        root.setCenter(interactiveImageView);
        Button btnLoad = getButton(primaryStage, interactiveImageView);
        Button btnSave = interactiveImageView.getSaveButton();
        //record voic --------------------------------
        audioRecorder = new AudioRecorder();

        startButton = new Button("Start Recording");
        stopButton = new Button("Stop Recording");

//        startButton.setDisable(true);
        stopButton.setDisable(true);

        startButton.setOnAction(e -> {
            startRecording();
            startButton.setDisable(true);
            stopButton.setDisable(false);
        });

        stopButton.setOnAction(e -> {
            stopRecording();
            startButton.setDisable(false);
            stopButton.setDisable(true);
        });






        //zip image...................................................................

        Image image = interactiveImageView.getImage();
        byte[] imageData = new byte[]{};
        if (image != null) {
            // Convert Image to byte array
            imageData = imageToByteArray(image);
        } else {
            System.out.println("ImageView does not have an image set.");
        }

        // byte[] imageData = new byte[]{/* Image data */};
        byte[] audioData = new byte[]{/* Audio data */};
        byte[] pdfData = new byte[]{/* PDF data */};

        String zipFileName = "C:\\Users\\DELL\\Documents\\Project1\\compreseed file/file.zip"; // Change this path to your desired location

        Button zipButton=new Button("zip image");
        byte[] finalImageData = imageData;
        zipButton.setOnAction((e) -> {
            ZipFiles.zipFiles(finalImageData, audioData, pdfData, zipFileName);
        });

        Button back = new Button("Back");
        back.setOnAction((e) -> {
            primaryStage.setScene(createGalleryScene(primaryStage));
        });
        HBox hBox = new HBox(10.0D, new Node[]{back, btnLoad, interactiveImageView.getColorPicker(), zipButton,btnSave,startButton,stopButton});
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
    private static void addImageSceneToCompare(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene addImageScene1 = new Scene(root, 900.0D, 700.0D);
        InteractiveImageView interactiveImageView1 = new InteractiveImageView();
        InteractiveImageView interactiveImageView2 = new InteractiveImageView();
        interactiveImageView1.setFitWidth(400);
        interactiveImageView1.setFitHeight(400);
        interactiveImageView2.setFitWidth(400);
        interactiveImageView2.setFitHeight(400);
        Button btnLoad1 = getButton2(primaryStage,
                interactiveImageView1,interactiveImageView2);


        Button compare=new Button("compare");
        compare.setOnAction(event -> Comparsion.compareImages(interactiveImageView1,interactiveImageView2));

        Button back = new Button("Back");
        back.setOnAction((e) -> {
            primaryStage.setScene(createGalleryScene(primaryStage));
        });
        HBox hBox = new HBox(10.0D, new Node[]{back, btnLoad1,compare});
        hBox.setStyle("-fx-padding: 10;");
        root.setRight(interactiveImageView2);
        root.setLeft(interactiveImageView1);
        root.setTop(hBox);
        primaryStage.setScene(addImageScene1);
    }
    private static void addImageSceneToClassify(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene addImageScene = new Scene(root, 900.0D, 700.0D);
        InteractiveImageView interactiveImageView1 = new InteractiveImageView();
        interactiveImageView1.setUpControls();
        root.setCenter(interactiveImageView1);
        Button btnLoad1 = getButton(primaryStage, interactiveImageView1);

        Button classifyButton = new Button("Classify");
        classifyButton.setDisable(true); // Disable the button by default

        interactiveImageView1.getColorPicker().valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                classifyButton.setDisable(false); // Enable the button when a color is selected
            } else {
                classifyButton.setDisable(true); // Disable the button when no color is selected
            }
        });

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
            primaryStage.setScene(createGalleryScene(primaryStage));
        });

        HBox hBox = new HBox(10.0D, new Node[]{back, btnLoad1, classifyButton, interactiveImageView1.getColorPicker()});
        hBox.setStyle("-fx-padding: 10;");
        root.setTop(hBox);
        primaryStage.setScene(addImageScene);
    }
//    private static void addImageSceneToClassify(Stage primaryStage) {
//        BorderPane root = new BorderPane();
//        Scene addImageScene = new Scene(root, 900.0D, 700.0D);
//        InteractiveImageView interactiveImageView1 = new InteractiveImageView();
//        interactiveImageView1.setUpControls();
//        root.setCenter(interactiveImageView1);
//        Button btnLoad1 = getButton(primaryStage, interactiveImageView1);
//
//        Button classifyButton = new Button("Classify");
//        classifyButton.setOnAction(event -> {
//            Image image = interactiveImageView1.getImage();
//            if (image != null) {
//                XRayClassifier.Severity severity = classifier.classify(image);
//                classifier.displayClassificationResult(severity);
//            } else {
//                System.out.println("Error: No image loaded to classify.");
//            }
//        });
//
//        Button back = new Button("Back");
//        back.setOnAction((e) -> {
//            primaryStage.setScene(createGalleryScene(primaryStage));
//        });
//
//        HBox hBox = new HBox(10.0D, new Node[]{back, btnLoad1, classifyButton, interactiveImageView1.getColorPicker()});
//        hBox.setStyle("-fx-padding: 10;");
//        root.setTop(hBox);
//        primaryStage.setScene(addImageScene);
//    }
    private static Scene createGalleryScene(Stage primaryStage) {
        return ImageGalleryScreen.createScene(primaryStage);
    }

    private static void startRecording() {
        audioRecorder.startRecording();
    }

    private static void stopRecording() {
        audioRecorder.stopRecording();
    }



}
