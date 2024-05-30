//import javafx.embed.swing.SwingFXUtils;
//import javafx.event.ActionEvent;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.ColorPicker;
//import javafx.scene.image.Image;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.HBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//
//public class Comparsion {
//
//    private InteractiveImageView imageView1;
//    private InteractiveImageView imageView2;
//    private ColorPicker colorPicker;
//
//    public void compare(Stage primaryStage) {
//        imageView1 = new InteractiveImageView();
//        imageView2 = new InteractiveImageView();
//
//        colorPicker = new ColorPicker();
//
//        Button loadButton1 = new Button("Load Image 1");
//        loadButton1.setOnAction(this::loadImage1);
//
//        Button loadButton2 = new Button("Load Image 2");
//        loadButton2.setOnAction(this::loadImage2);
//
//        Button compareButton = new Button("Compare Images");
//        compareButton.setOnAction(this::compareImages);
//
//        HBox controls = new HBox(10, loadButton1, loadButton2, colorPicker, compareButton);
//        controls.setPadding(new Insets(10));
//
//        BorderPane root = new BorderPane();
//        root.setCenter(new HBox(10, imageView1, imageView2));
//        root.setBottom(controls);
//
//        Scene scene = new Scene(root, 800, 600);
//
//        primaryStage.setTitle("Image Comparison App");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//
//        // Add compare button for primaryStage
//        Button compare = new Button("Compare");
//        compare.setOnAction(event -> compareImages(null));
//        controls.getChildren().add(compare);
//    }
//
//    private void loadImage1(ActionEvent event) {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Open Image File");
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp")
//        );
//        File selectedFile = fileChooser.showOpenDialog(null);
//        if (selectedFile != null) {
//            Image image = new Image(selectedFile.toURI().toString(), 400, 400, false, false);
//            imageView1.setImage(image);
//        }
//    }
//
//    private void loadImage2(ActionEvent event) {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Open Image File");
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp")
//        );
//        File selectedFile = fileChooser.showOpenDialog(null);
//        if (selectedFile != null) {
//            Image image = new Image(selectedFile.toURI().toString(), 400, 400, false, false);
//            imageView2.setImage(image);
//        }
//    }
//
//    private void compareImages(ActionEvent event) {
//        if (imageView1.getImage() == null || imageView2.getImage() == null) {
//            System.out.println("Error: Both images must be loaded.");
//            return;
//        }
//
//        BufferedImage bufferedImage1 = SwingFXUtils.fromFXImage(imageView1.getImage(), null);
//        BufferedImage bufferedImage2 = SwingFXUtils.fromFXImage(imageView2.getImage(), null);
//
//        int difference = compareBufferedImages(bufferedImage1, bufferedImage2, colorPicker.getValue());
//        System.out.println("Number of differences: " + difference);
//        int differenceThreshold = 50;
//
//        if (difference > differenceThreshold) {
//            System.out.println("The patient's condition has changed.");
//        } else {
//            System.out.println("The patient's condition remains the same.");
//        }
//    }
//
//    private int compareBufferedImages(BufferedImage image1, BufferedImage image2, javafx.scene.paint.Color ignoreColor) {
//        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
//            System.out.println("Error: Images must have the same dimensions.");
//            return -1;
//        }
//
//        int differences = 0;
//
//        for (int x = 0; x < image1.getWidth(); x++) {
//            for (int y = 0; y < image1.getHeight(); y++) {
//                int color1 = image1.getRGB(x, y);
//                int color2 = image2.getRGB(x, y);
//
//                javafx.scene.paint.Color fxColor1 = javafx.scene.paint.Color.rgb(
//                        (color1 >> 16) & 0xFF,
//                        (color1 >> 8) & 0xFF,
//                        color1 & 0xFF
//                );
//
//                javafx.scene.paint.Color fxColor2 = javafx.scene.paint.Color.rgb(
//                        (color2 >> 16) & 0xFF,
//                        (color2 >> 8) & 0xFF,
//                        color2 & 0xFF
//                );
//
//                if (!fxColor1.equals(fxColor2) && !fxColor1.equals(ignoreColor)) {
//                    differences++;
//                }
//            }
//        }
//
//        return differences;
//    }
//}

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class Comparsion {



    private  ImageView imageView1;
    private  ImageView imageView2;
    private  ColorPicker colorPicker;



    public static void compareImages(InteractiveImageView imageView1,InteractiveImageView imageView2) {
        ColorPicker colorPicker=new ColorPicker();
        if (imageView1.getImage() == null || imageView2.getImage() == null) {
            System.out.println("Error: Both images must be loaded.");

        }

        BufferedImage bufferedImage1 = SwingFXUtils.fromFXImage(imageView1.getImage(), null);
        BufferedImage bufferedImage2 = SwingFXUtils.fromFXImage(imageView2.getImage(), null);

        int difference = compareImages(bufferedImage1, bufferedImage2, colorPicker.getValue());
        System.out.println("Number of differences: " + difference);
        int differenceThreshold = 50;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);


        if (difference > differenceThreshold) {
            alert.setContentText("The patient's condition has changed.");
            System.out.println("The patient's condition has changed.");
        } else {
            alert.setContentText("The patient's condition remains the same.");
            System.out.println("The patient's condition remains the same.");
        }
        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(ButtonType.OK);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("OK button clicked.");
            }
        });

    }

    private static int compareImages(BufferedImage image1, BufferedImage image2, javafx.scene.paint.Color ignoreColor) {
        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
            System.out.println("Error: Images must have the same dimensions.");
            return -1;
        }

        int differences = 0;

        for (int x = 0; x < image1.getWidth(); x++) {
            for (int y = 0; y < image1.getHeight(); y++) {
                int color1 = image1.getRGB(x, y);
                int color2 = image2.getRGB(x, y);

                javafx.scene.paint.Color fxColor1 = javafx.scene.paint.Color.rgb(
                        (color1 >> 16) & 0xFF,
                        (color1 >> 8) & 0xFF,
                        color1 & 0xFF
                );

                javafx.scene.paint.Color fxColor2 = javafx.scene.paint.Color.rgb(
                        (color2 >> 16) & 0xFF,
                        (color2 >> 8) & 0xFF,
                        color2 & 0xFF
                );

                if (!fxColor1.equals(fxColor2) && !fxColor1.equals(ignoreColor)) {
                    differences++;
                }
            }
        }

        return differences;
    }


}
