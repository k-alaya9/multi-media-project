import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;

public class InteractiveImageView extends BorderPane {
    private ImageView imageView;
    static XRayClassifier classifier = new XRayClassifier();
    private Image image;
    private WritableImage editableImage;
    private ColorPicker colorPicker;
    private Button btnSave, btnCrop, btnAddText ,btnColor,classifyButton,FTButton;

    private MenuButton btnShapes;
    private Pane drawingPane;
    private ShapeDrawer shapeDrawer;
    private TextDrawer textDrawer;
    private ImageCropper imageCropper;
    private String currentShapeType;
    private boolean IsCompare;

    private FourierTransformations Ft;


    public InteractiveImageView(boolean IsCompare) {
        this.IsCompare=IsCompare;
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        drawingPane = new Pane();
        drawingPane.getChildren().add(imageView);
        setCenter(drawingPane);

        shapeDrawer = new ShapeDrawer(drawingPane);
        textDrawer = new TextDrawer(drawingPane);
        imageCropper = new ImageCropper(imageView);
        drawingPane.getChildren().add(imageCropper.getCropArea());
        if(!IsCompare) {
            setUpControls();
        }
        setUpDrawingPaneMouseEvents();
    }

    public void setUpControls() {
        colorPicker = new ColorPicker();
        colorPicker.getStyleClass().add("menu-button");
        btnSave = new Button("Save Image");
        btnCrop = new Button("Crop Image");
        btnColor = new Button("Color the shape");
        btnAddText = new Button("Add Text");
        btnShapes = new MenuButton("Shapes");
        btnShapes.getStyleClass().add("menu-button");
        FTButton= new Button("FourierTransformations");
        FTButton.setOnAction(e->{
            try {
                Ft= new FourierTransformations(image);
                if(Ft.Edited_Image!=null){
                    imageView.setImage(Ft.Edited_Image);
                    setImage(Ft.Edited_Image);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        MenuItem drawCircle = new MenuItem("Draw Circle");
        MenuItem drawEllipse = new MenuItem("Draw Ellipse");
        MenuItem drawTriangle = new MenuItem("Draw Triangle");
        MenuItem drawRectangle = new MenuItem("Draw Rectangle");
        MenuItem drawPolygon = new MenuItem("Draw Polygon");
        MenuItem drawLine = new MenuItem("Draw Line");

        btnShapes.getItems().addAll(drawCircle, drawEllipse, drawTriangle, drawRectangle, drawPolygon, drawLine);

        btnSave.setOnAction(e -> saveImage());
        btnCrop.setOnAction(e -> cropImageToShape());
        btnAddText.setOnAction(e -> startTextInputMode());
        btnColor.setOnAction(e->applyColorOverlay());

        drawCircle.setOnAction(e -> currentShapeType = "Circle");
        drawEllipse.setOnAction(e -> currentShapeType = "Ellipse");
        drawTriangle.setOnAction(e -> currentShapeType = "Triangle");
        drawRectangle.setOnAction(e -> currentShapeType = "Rectangle");
        drawPolygon.setOnAction(e -> currentShapeType = "Polygon");
        drawLine.setOnAction(e -> currentShapeType = "Line");
        classifyButton = new Button("Classify");

        classifyButton.setOnAction(event -> {
          classifyImage();
        });

        HBox controlsBox = new HBox(10,  btnCrop, btnAddText, btnShapes,btnColor,classifyButton,FTButton);
        controlsBox.getStyleClass().add("hbox");
        controlsBox.setStyle("-fx-padding: 10;");
        setBottom(controlsBox);
    }
    private void setUpDrawingPaneMouseEvents() {
        drawingPane.setOnMousePressed(event -> {
            if (currentShapeType != null) {
                double startX = event.getX();
                double startY = event.getY();

                drawingPane.setOnMouseDragged(e -> {
                    double endX = e.getX();
                    double endY = e.getY();
                    shapeDrawer.previewShape(currentShapeType, startX, startY, endX, endY);
                });

                drawingPane.setOnMouseReleased(e -> {
                    double endX = e.getX();
                    double endY = e.getY();
                    shapeDrawer.drawShape(currentShapeType, startX, startY, endX, endY);
                    drawingPane.setOnMouseDragged(null);
                    drawingPane.setOnMouseReleased(null);
                });
            }
        });
    }

    private void startTextInputMode() {
        drawingPane.setOnMousePressed(event -> {
            textDrawer.startTextInput(event);
            drawingPane.setOnMousePressed(null); // Disable text input mode after placing one text
        });
    }

    public Button getCropButton() {
        return btnCrop;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }
    public Image getImage() {
        return this.image;
    }
    public void setFitWidth(double width) {
        imageView.setFitWidth(width);
    }

    public void setFitHeight(double height) {
        imageView.setFitHeight(height);
    }
    public Button getSaveButton() {
        return btnSave;
    }

    public void setImage(Image image) {
        this.image = image;
        editableImage = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
        imageView.setImage(editableImage);
        imageView.setPreserveRatio(true);
    }
    private void cropImageToShape() {
        Shape lastShape = null;
        // Find the last Shape added to the drawingPane
        for (int i = drawingPane.getChildren().size() - 1; i >= 0; i--) {
            Node child = drawingPane.getChildren().get(i);
            if (child instanceof Shape) {
                lastShape = (Shape) child;
                break;
            }
        }
        Shape selection = lastShape;

        if (selection == null) {
            return; // No shape found, exit the method
        }

        double scaleX = imageView.getBoundsInLocal().getWidth() / image.getWidth();
        double scaleY = imageView.getBoundsInLocal().getHeight() / image.getHeight();

        int width = (int) Math.round(selection.getBoundsInParent().getWidth() / scaleX);
        int height = (int) Math.round(selection.getBoundsInParent().getHeight() / scaleY);
        if (width <= 0 || height <= 0) {
            return; // Skip processing for zero or negative dimensions
        }

        PixelReader reader = image.getPixelReader();
        if (reader == null) return;

        WritableImage croppedImage = new WritableImage(width, height);
        PixelWriter writer = croppedImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int imageX = (int) (selection.getBoundsInParent().getMinX() / scaleX) + x;
                int imageY = (int) (selection.getBoundsInParent().getMinY() / scaleY) + y;

                // Check if the point (imageX, imageY) is inside the shape
                if (selection.contains(selection.getBoundsInParent().getMinX() + x * scaleX, selection.getBoundsInParent().getMinY() + y * scaleY)) {
                    Color color = reader.getColor(imageX, imageY);
                    writer.setColor(x, y, color);
                } else {
                    // Optionally, you can set the color to transparent or any background color you prefer
                    writer.setColor(x, y, Color.TRANSPARENT);
                }
            }
        }
        drawingPane.getChildren().remove(selection);
        imageView.setImage(croppedImage);
    }
    private void applyColorOverlay() {
        Shape lastShape = null;
        for (int i = drawingPane.getChildren().size() - 1; i >= 0; i--) {
            Node child = drawingPane.getChildren().get(i);
            if (child instanceof Shape) {
                lastShape = (Shape) child;
                break;
            }
        }
        Shape selection=lastShape;

        double scaleX = imageView.getBoundsInLocal().getWidth() / image.getWidth();
        double scaleY = imageView.getBoundsInLocal().getHeight() / image.getHeight();

        int width = (int) Math.round(selection.getBoundsInParent().getWidth() / scaleX);
        int height = (int) Math.round(selection.getBoundsInParent().getHeight() / scaleY);
        if (width <= 0 || height <= 0) {
            return; // Skip processing for zero or negative dimensions
        }

        PixelReader reader = image.getPixelReader();
        if (reader == null) return;
        PixelWriter writer = editableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int imageX = (int) (selection.getBoundsInParent().getMinX() / scaleX) + x;
                int imageY = (int) (selection.getBoundsInParent().getMinY() / scaleY) + y;

                // Check if the point (imageX, imageY) is inside the shape
                if (!selection.contains(selection.getBoundsInParent().getMinX() + x * scaleX, selection.getBoundsInParent().getMinY() + y * scaleY)) {
                    continue; // Skip points outside the shape
                }
                Color color = reader.getColor(imageX, imageY);

                double brightness = color.getBrightness();

                double blendFactor = 1 - brightness;

                // Blend between white and the picked color
                Color blendedColor = Color.color(
                        colorPicker.getValue().getRed() * blendFactor + Color.WHITE.getRed() * brightness,
                        colorPicker.getValue().getGreen() * blendFactor + Color.WHITE.getGreen() * brightness,
                        colorPicker.getValue().getBlue() * blendFactor + Color.WHITE.getBlue() * brightness
                );
                if(colorPicker.getValue()!=Color.WHITE)
                    writer.setColor(imageX, imageY, blendedColor);
                else{Color overlayColor=Color.hsb((1-brightness)*240
                        ,1,1);
                    writer.setColor(imageX,imageY,overlayColor);
                }
            }
        }
        imageView.setImage(editableImage);
    }

    private  void classifyImage(){
        Shape lastShape = null;
        // Find the last Shape added to the drawingPane
        for (int i = drawingPane.getChildren().size() - 1; i >= 0; i--) {
            Node child = drawingPane.getChildren().get(i);
            if (child instanceof Shape) {
                lastShape = (Shape) child;
                break;
            }
        }
        Shape selection = lastShape;

        if (selection == null) {
            return; // No shape found, exit the method
        }
        if (selection != null) {
            classifyButton.setDisable(false);
        } else {
            classifyButton.setDisable(true);
        }

        double scaleX = imageView.getBoundsInLocal().getWidth() / image.getWidth();
        double scaleY = imageView.getBoundsInLocal().getHeight() / image.getHeight();

        int width = (int) Math.round(selection.getBoundsInParent().getWidth() / scaleX);
        int height = (int) Math.round(selection.getBoundsInParent().getHeight() / scaleY);
        if (width <= 0 || height <= 0) {
            return; // Skip processing for zero or negative dimensions
        }

        PixelReader reader = image.getPixelReader();
        if (reader == null) return;

        WritableImage croppedImage = new WritableImage(width, height);
        PixelWriter writer = croppedImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int imageX = (int) (selection.getBoundsInParent().getMinX() / scaleX) + x;
                int imageY = (int) (selection.getBoundsInParent().getMinY() / scaleY) + y;

                // Check if the point (imageX, imageY) is inside the shape
                if (selection.contains(selection.getBoundsInParent().getMinX() + x * scaleX, selection.getBoundsInParent().getMinY() + y * scaleY)) {
                    Color color = reader.getColor(imageX, imageY);
                    writer.setColor(x, y, color);
                } else {
                    // Optionally, you can set the color to transparent or any background color you prefer
                    writer.setColor(x, y, Color.TRANSPARENT);
                }
            }
        }
        drawingPane.getChildren().remove(selection);
        XRayClassifier.Severity severity = classifier.classify(croppedImage);
        classifier.displayClassificationResult(severity);
    }
    private void saveImage() {
        FileChooser fileChooser = new FileChooser();
        File dic=new File(
                "editImage/"
        );
        fileChooser.setInitialDirectory(dic);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"),
                new FileChooser.ExtensionFilter("BMP Files", "*.bmp")
        );
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) drawingPane.getWidth(), (int) drawingPane.getHeight());
                drawingPane.snapshot(null, writableImage);
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                showAlert("Image Saved", "imaged saved Successfully!",Alert.AlertType.CONFIRMATION);

            } catch (Exception ex) {
                showAlert("Error Saving Image", "Failed to save the image. Please try again.",Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        }
    }
    private void showAlert(String title, String content, Alert.AlertType x) {
        Alert alert = new Alert(x);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}