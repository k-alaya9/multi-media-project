import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.File;

public class InteractiveImageView extends BorderPane {
    private ImageView imageView;
    private Image image;
    private WritableImage editableImage;
    private ColorPicker colorPicker;
    private Button btnSave;

    public InteractiveImageView() {
        imageView = new ImageView();
        setCenter(imageView);
        setUpControls();
        setUpDragging();
    }

    private void setUpControls() {
        colorPicker = new ColorPicker();
        btnSave = new Button("Save Image");
        btnSave.setOnAction(e -> saveImage());

        HBox controlsBox = new HBox(10);
        controlsBox.getChildren().addAll(colorPicker, btnSave);
        controlsBox.setStyle("-fx-padding: 10;");
        setAlignment(controlsBox, javafx.geometry.Pos.CENTER);

        setBottom(controlsBox);
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
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

    private void setUpDragging() {
        final double[] anchorX = new double[1];
        final double[] anchorY = new double[1];

        imageView.setOnMousePressed(event -> {
            anchorX[0] = event.getX();
            anchorY[0] = event.getY();
            Rectangle selection = new Rectangle(anchorX[0], anchorY[0], 0, 0);
            selection.setFill(Color.TRANSPARENT);
            selection.setStroke(Color.BLUE);  // Temporary for visualization

            this.getChildren().add(selection);

            imageView.setOnMouseDragged(e -> {
                selection.setWidth(Math.abs(e.getX() - anchorX[0]));
                selection.setHeight(Math.abs(e.getY() - anchorY[0]));
                selection.setX(Math.min(anchorX[0], e.getX()));
                selection.setY(Math.min(anchorY[0], e.getY()));
            });

            imageView.setOnMouseReleased(e -> {
                applyColorOverlay(selection);
                imageView.setOnMouseDragged(null);
                imageView.setOnMouseReleased(null);
            });
        });
    }

    private void applyColorOverlay(Rectangle selection) {
        System.out.println(colorPicker.getValue());
        int width = (int) Math.round(selection.getWidth());
        int height = (int) Math.round(selection.getHeight());

        // Check if either width or height is zero (or very small)
        if (width <= 0 || height <= 0) {
            return; // Skip processing for zero or negative dimensions
        }

        PixelReader reader = image.getPixelReader();
        if (reader == null) return;

        WritableImage overlayImage = new WritableImage(width, height);
        PixelWriter writer = overlayImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int imageX = (int)selection.getX() + x;
                int imageY = (int)selection.getY() + y;
                Color color = reader.getColor(imageX, imageY);
//                double brightness = (1-color.getBrightness())*240;
//                Color overlayColor=Color.hsb(brightness
//                        ,1,1);
////                writer.setColor(x, y,colorPicker.getValue()==Color.BLUE? overlayColor:colorPicker.getValue()); // Apply color with transparency
//                writer.setColor(x, y, colorPicker.getValue().deriveColor(0, 1, brightness / 255, 1));
                // Calculate the brightness of the pixel

                double brightness = color.getBrightness();

                // Calculate the blend factor based on brightness
                double blendFactor = 1 - brightness;

                // Blend between white and the picked color
                Color blendedColor = Color.color(
                        colorPicker.getValue().getRed() * blendFactor + Color.WHITE.getRed() * brightness,
                        colorPicker.getValue().getGreen() * blendFactor + Color.WHITE.getGreen() * brightness,
                        colorPicker.getValue().getBlue() * blendFactor + Color.WHITE.getBlue() * brightness
                );
                if(colorPicker.getValue()!=Color.WHITE)
                writer.setColor(x, y, blendedColor);
                else{Color overlayColor=Color.hsb((1-brightness)*240
                        ,1,1);
                    writer.setColor(x,y,overlayColor);
                }
            }
        }
        ImageView overlayView = new ImageView(overlayImage);
        overlayView.setX(selection.getX());
        overlayView.setY(selection.getY());
        this.getChildren().add(overlayView);
    }

    private void saveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"),
                new FileChooser.ExtensionFilter("BMP Files", "*.bmp")
        );
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(editableImage, null), "png", file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public static WritableImage convertToHeatMap(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(width, height);
        PixelReader pixelReader = inputImage.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                double brightness = color.getBrightness();

                // Convert brightness to a heat map color
                double hue = (1 - brightness) * 240; // hue range for heat map: 0-240
                Color heatMapColor = Color.hsb(hue, 1, 1);

                outputImage.getPixelWriter().setColor(x, y, heatMapColor);
            }
        }

        return outputImage;
    }
}
