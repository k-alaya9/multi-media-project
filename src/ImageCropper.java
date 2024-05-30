import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ImageCropper {
    private Rectangle cropArea;
    private ImageView imageView;
    private boolean isCropping;

    public ImageCropper(ImageView imageView) {
        this.imageView = imageView;
        cropArea = new Rectangle();
        cropArea.setStroke(Color.BLACK);
        cropArea.setStrokeWidth(2);
        cropArea.setFill(Color.TRANSPARENT);
        cropArea.setVisible(false);
    }

    public Rectangle getCropArea() {
        return cropArea;
    }

    public void startCrop(Pane drawingPane) {
        if (isCropping) {
            drawingPane.getChildren().remove(cropArea);
        }
        isCropping = true;
        cropArea.setVisible(true);
        if (!drawingPane.getChildren().contains(cropArea)) {
            drawingPane.getChildren().add(cropArea);
        }

        drawingPane.setOnMousePressed(event -> {
            if (isCropping) {
                cropArea.setX(event.getX());
                cropArea.setY(event.getY());
                cropArea.setWidth(0);
                cropArea.setHeight(0);
            }
        });

        drawingPane.setOnMouseDragged(event -> {
            if (isCropping) {
                cropArea.setWidth(Math.abs(event.getX() - cropArea.getX()));
                cropArea.setHeight(Math.abs(event.getY() - cropArea.getY()));
                cropArea.setX(Math.min(cropArea.getX(), event.getX()));
                cropArea.setY(Math.min(cropArea.getY(), event.getY()));
            }
        });

        drawingPane.setOnMouseReleased(event -> {
            if (isCropping) {
                finishCrop(drawingPane);
                isCropping = false;
            }
        });
    }

    private void finishCrop(Pane drawingPane) {
        if (cropArea.getWidth() == 0 || cropArea.getHeight() == 0) {
            cropArea.setVisible(false);
            System.out.println("Crop area was too small; reset and try again.");
            return;
        }

        if (imageView.getImage() == null) {
            System.out.println("No image available for cropping.");
            cropArea.setVisible(false);
            return;
        }

        double scaleX = imageView.getImage().getWidth() / imageView.getBoundsInLocal().getWidth();
        double scaleY = imageView.getImage().getHeight() / imageView.getBoundsInLocal().getHeight();

        int x = (int) Math.round(cropArea.getX() * scaleX);
        int y = (int) Math.round(cropArea.getY() * scaleY);
        int width = (int) Math.round(cropArea.getWidth() * scaleX);
        int height = (int) Math.round(cropArea.getHeight() * scaleY);

        if (width > 0 && height > 0) {
            try {
                PixelReader reader = imageView.getImage().getPixelReader();
                WritableImage newImage = new WritableImage(reader, x, y, width, height);
                imageView.setImage(newImage);
            } catch (Exception e) {
                System.out.println("Error during cropping: " + e.getMessage());
            }
        }

        cropArea.setVisible(false);
        drawingPane.getChildren().remove(cropArea);
    }
}
