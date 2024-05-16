import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
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

        double scaleX = imageView.getBoundsInLocal().getWidth() / image.getWidth();
        double scaleY = imageView.getBoundsInLocal().getHeight() / image.getHeight();

        int width = (int) Math.round(selection.getWidth() / scaleX);
        int height = (int) Math.round(selection.getHeight() / scaleY);
        if (width <= 0 || height <= 0) {
            return; // Skip processing for zero or negative dimensions
        }

        PixelReader reader = image.getPixelReader();
        if (reader == null) return;
        PixelWriter writer = editableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int imageX = (int) (selection.getX() / scaleX) + x;
                int imageY = (int) (selection.getY() / scaleY) + y;
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
                ImageIO.write(SwingFXUtils.fromFXImage(editableImage, null), "png", file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}