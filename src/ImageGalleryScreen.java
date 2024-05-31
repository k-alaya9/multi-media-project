import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImageGalleryScreen {
    private static AudioRecorder audioRecorder;
    private static Button startButton;
    private static Button stopButton;


    private static final String IMAGE_FOLDER_PATH = "editImage";

    public static Scene createScene(Stage primaryStage) {
        List<File> allImageFiles = loadImagesFromFolder(IMAGE_FOLDER_PATH);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.getStyleClass().add("grid-pane");


        Button sizeSort= new Button("filter by Size");
        sizeSort.setOnAction(e->{
            allImageFiles.sort(Comparator.comparingLong(File::length));
            updateImageGallery(gridPane,allImageFiles,"",primaryStage);
        });
        Button DateSort=new Button("filter by Last Modified");
        DateSort.setOnAction(e->{
            allImageFiles.sort(Comparator.comparingLong(File::lastModified));
            updateImageGallery(gridPane,allImageFiles,"",primaryStage);
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateImageGallery(gridPane, allImageFiles, newValue,primaryStage));


        Button addBtn = new Button("+");
        addBtn.setOnAction((e) -> {
            addImageScene(primaryStage);
        });
        addBtn.setStyle("    -fx-padding: 8px 15px;\n" +
                "    -fx-font-size: 40px;" +
                "    -fx-background-color: #2CAc8d;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.5, 0, 5);\n" +
                "    -fx-cursor: hand;");
        Button cButton=new Button("Compare images");
        cButton.setOnAction((e) -> {
            addImageSceneToCompare(primaryStage);
        });

        Button MedicalReport= new Button("create Medical Report");
         MedicalReport.setOnAction(e->{
             primaryStage.setScene(MedicalReportScreen.createScene(primaryStage,createGalleryScene(primaryStage)));
         });


        //zip image...................................................................
        Button zipButton=new Button("zip image");
        zipButton.setOnAction((e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            File dic=new File(
                    "editImage/"
            );

            fileChooser.setInitialDirectory(dic);
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter("Image Files", new String[]{"*.png", "*.jpg", "*.gif", "*.bmp"})});
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    Image image = new Image(file.toURI().toString(), 800.0D, 600.0D, false, false);
                    byte[] imageData = new byte[]{};
                    if (image != null) {
                        // Convert Image to byte array
                        imageData = imageToByteArray(image);
                    } else {
                        System.out.println("ImageView does not have an image set.");
                    }
                    byte[] audioData = new byte[]{};
                    FileChooser fileChooser1 = new FileChooser();
                    fileChooser1.setTitle("Open Audio File");
                    File dic1=new File(
                            "voice record/"
                    );
                    fileChooser1.setInitialDirectory(dic1);
                    fileChooser1.getExtensionFilters().addAll(new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter("Audio Files", new String[]{"*.wav", "*.mp3"})});
                    File file1= fileChooser1.showOpenDialog(primaryStage);
                    if(file1!=null){
                        audioData= Files.readAllBytes(file1.toPath());
                    }
                    byte[] pdfData = new byte[]{};
                    FileChooser fileChooser2 = new FileChooser();
                    fileChooser2.setTitle("Open PDF File");
                    File dic2=new File(
                            "reports/"
                    );
                    fileChooser2.setInitialDirectory(dic2);
                    fileChooser2.getExtensionFilters().addAll(new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter("PDF Files", new String[]{"*.pdf"})});
                    File file2= fileChooser2.showOpenDialog(primaryStage);
                    if(file2!=null){
                        pdfData= Files.readAllBytes(file2.toPath());
                    }
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String zipFileName = "C:\\Users\\HP\\Desktop\\multi-media-project-main\\compreseed file\\" + timestamp + ".zip";
                    ZipFiles.zipFiles(imageData, audioData, pdfData, zipFileName);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Zip Result");
                    alert.setHeaderText(null);
                    alert.setContentText("Files successfully zipped.");
                    alert.showAndWait();
                } catch (Exception var6) {
                    var6.printStackTrace();
                }
            }

        });

        HBox header=new HBox(10.0D,searchField,sizeSort,DateSort,cButton,MedicalReport,zipButton);
        header.getStyleClass().add("hbox");

        BorderPane layout = new BorderPane();
        layout.setTop(header);
        ScrollPane scrollPane=new ScrollPane(gridPane);
        layout.setCenter(scrollPane);
        StackPane stackPane= new StackPane(layout,addBtn);
        StackPane.setMargin(addBtn,new Insets(30));
        StackPane.setAlignment(addBtn,Pos.BOTTOM_RIGHT);
        Scene galleryScene= new Scene(stackPane, 900, 700);
        galleryScene.getStylesheets().add("style.css");
        updateImageGallery(gridPane, allImageFiles, "",primaryStage);
        String filter="";
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> updateGrid(gridPane, allImageFiles, filter, primaryStage));
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> updateGrid(gridPane, allImageFiles, filter, primaryStage));
        return galleryScene;
    }
    private static void updateImageGallery(GridPane gridPane, List<File> allImageFiles, String filter, Stage primaryStage) {
        gridPane.getChildren().clear();

        List<File> filteredFiles = allImageFiles.stream()
                .filter(file -> file.getName().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());

        updateGrid(gridPane, filteredFiles, filter, primaryStage);
    }

    private static void updateGrid(GridPane gridPane, List<File> filteredFiles, String filter, Stage primaryStage) {
        gridPane.getChildren().clear();

        Scene scene = primaryStage.getScene();
        if (scene == null) return; // Return if scene is not set yet

        double sceneWidth = scene.getWidth();
        double imageWidth = 150; // Width of the image view
        double spacing = 20; // Spacing between images

        // Calculate number of columns
        int numCols = (int) (sceneWidth / (imageWidth + spacing));

        int row = 0;
        int col = 0;

        for (File imageFile : filteredFiles) {
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(imageWidth);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-padding: 5;" +
                    "-fx-border-color: #084B83;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;");
            imageView.setOnMouseEntered(e -> imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(10, 35, 66, 0.5), 10, 0.7, 0, 0);" +
                    "-fx-cursor: hand;"));
            imageView.setOnMouseExited(e -> imageView.setStyle("-fx-padding: 5;" +
                    "-fx-border-color: #084B83;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;"));
            imageView.setOnMouseClicked(e -> {
                BorderPane root = new BorderPane();
                Scene addImageScene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
                addImageScene.getStylesheets().add("style.css");
                InteractiveImageView interactiveImageView = new InteractiveImageView(false);
                interactiveImageView.setImage(imageView.getImage());
                interactiveImageView.setUpControls();
                root.setCenter(interactiveImageView);
                Button btnLoad = getButton(primaryStage, interactiveImageView);
                Button btnSave = interactiveImageView.getSaveButton();

                // Audio recording
                AudioRecorder audioRecorder = new AudioRecorder();
                Button startButton = new Button("Start Recording");
                Button stopButton = new Button("Stop Recording");

                stopButton.setDisable(true);

                startButton.setOnAction(event -> {
                    audioRecorder.startRecording();
                    startButton.setDisable(true);
                    stopButton.setDisable(false);
                });

                stopButton.setOnAction(event -> {
                    audioRecorder.stopRecording();
                    startButton.setDisable(false);
                    stopButton.setDisable(true);
                    showAlert("Audio Saved", "Audio saved Successfully!");
                });

                Button back = new Button("Back");
                back.setOnAction(event -> primaryStage.setScene(createGalleryScene(primaryStage)));

                HBox hBox = new HBox(10.0, back, btnLoad, interactiveImageView.getColorPicker(), btnSave, startButton, stopButton, interactiveImageView.getCropButton());
                hBox.setStyle("-fx-padding: 10;");
                hBox.getStyleClass().add("hbox");
                root.setTop(hBox);
                primaryStage.setScene(addImageScene);
            });

            Label label = new Label(imageFile.getName());
            label.getStyleClass().add("label");

            gridPane.add(imageView, col, row);
            gridPane.add(label, col, row + 1);

            col++;
            if (col >= numCols) {
                col = 0;
                row += 2;
            }
        }
    }


    //    private static void updateImageGallery(GridPane gridPane, List<File> allImageFiles, String filter,Stage primaryStage) {
//        gridPane.getChildren().clear();
//
//        List<File> filteredFiles = allImageFiles.stream()
//                .filter(file -> file.getName().toLowerCase().contains(filter.toLowerCase()))
//                .collect(Collectors.toList());
//
//        int row = 0;
//        int col = 0;
//        for (File imageFile : filteredFiles) {
//            Image image = new Image(imageFile.toURI().toString());
//            ImageView imageView = new ImageView(image);
//            imageView.setFitWidth(150);
//            imageView.setFitHeight(200);
//            imageView.setPreserveRatio(true);
//            imageView.setStyle("  -fx-padding: 5;\n" +
//                    "    -fx-border-color: #084B83;\n" +
//                    "    -fx-border-width: 2;\n" +
//                    "    -fx-border-radius: 10;\n" +
//                    "    -fx-background-radius: 10;\n"
//                        );
//            imageView.setOnMouseEntered(e->
//                    imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(10, 35, 66, 0.5), 10, 0.7, 0, 0);\n" +
//            "    -fx-cursor: hand;"));
//            imageView.setOnMouseExited(e->imageView.setStyle("  -fx-padding: 5;\n" +
//                    "    -fx-border-color: #084B83;\n" +
//                    "    -fx-border-width: 2;\n" +
//                    "    -fx-border-radius: 10;\n" +
//                    "    -fx-background-radius: 10;\n"
//            ));
//            imageView.setOnMouseClicked(e->
//            {
//                BorderPane root = new BorderPane();
//                Scene addImageScene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
//                addImageScene.getStylesheets().add("style.css");
//                InteractiveImageView interactiveImageView = new InteractiveImageView(false);
//                interactiveImageView.setImage(imageView.getImage());
//                interactiveImageView.setUpControls();
//                root.setCenter(interactiveImageView);
//                Button btnLoad = getButton(primaryStage, interactiveImageView);
//                Button btnSave = interactiveImageView.getSaveButton();
//                //record voic --------------------------------
//                audioRecorder = new AudioRecorder();
//
//                startButton = new Button("Start Recording");
//                stopButton = new Button("Stop Recording");
//
//                stopButton.setDisable(true);
//
//                startButton.setOnAction(event -> {
//                    startRecording();
//                    startButton.setDisable(true);
//                    stopButton.setDisable(false);
//                });
//
//                stopButton.setOnAction(event -> {
//                    stopRecording();
//                    startButton.setDisable(false);
//                    stopButton.setDisable(true);
//                    showAlert("Audio Saved", "Audio saved Successfully!");
//                });
//
//                Button back = new Button("Back");
//                back.setOnAction((event) -> {
//                    primaryStage.setScene(createGalleryScene(primaryStage));
//                });
//                HBox hBox = new HBox(10.0D, new Node[]{back, btnLoad, interactiveImageView.getColorPicker(), btnSave, startButton, stopButton, interactiveImageView.getCropButton()});
//                hBox.setStyle("-fx-padding: 10;");
//                hBox.getStyleClass().add("hbox");
//                root.setTop(hBox);
//                primaryStage.setScene(addImageScene);
//            });
//
//            Label label = new Label(imageFile.getName());
//            label.getStyleClass().add("label");
//
//            gridPane.add(imageView, col, row);
//            gridPane.add(label, col, row + 1);
//
//            col++;
//            if (col > 4) {
//                col = 0;
//                row += 2;
//            }
//        }
//    }
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
        Scene addImageScene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        addImageScene.getStylesheets().add("style.css");
        InteractiveImageView interactiveImageView = new InteractiveImageView(false);
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
            showAlert("Audio Saved", "Audio saved Successfully!");
        });

        Button back = new Button("Back");
        back.setOnAction((e) -> {
            primaryStage.setScene(createGalleryScene(primaryStage));
        });
        HBox hBox = new HBox(10.0D, new Node[]{back, btnLoad, interactiveImageView.getColorPicker(),btnSave,startButton,stopButton, interactiveImageView.getCropButton()});
        hBox.getStyleClass().add("hbox");
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
        Scene addImageScene1 = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        addImageScene1.getStylesheets().add("style.css");
        InteractiveImageView interactiveImageView1 = new InteractiveImageView(true);
        InteractiveImageView interactiveImageView2 = new InteractiveImageView(true);
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
        hBox.getStyleClass().add("hbox");
        HBox center=new HBox(10,interactiveImageView2,interactiveImageView1);
        root.setCenter(center);
        root.setTop(hBox);
        primaryStage.setScene(addImageScene1);
    }
    private static Scene createGalleryScene(Stage primaryStage) {
        return ImageGalleryScreen.createScene(primaryStage);
    }

    private static void startRecording() {
        audioRecorder.startRecording();
    }

    private static void stopRecording() {
        audioRecorder.stopRecording();
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
