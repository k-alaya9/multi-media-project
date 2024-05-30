import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ShapeDrawer {
    private Pane drawingPane;
    private Shape previewShape;

    public ShapeDrawer(Pane drawingPane) {
        this.drawingPane = drawingPane;
    }

    public void previewShape(String shapeType, double startX, double startY, double endX, double endY) {
        if (previewShape != null) {
            drawingPane.getChildren().remove(previewShape);
        }

        previewShape = createShape(shapeType, startX, startY, endX, endY);
        if (previewShape != null) {
            previewShape.setStroke(Color.GRAY);
            drawingPane.getChildren().add(previewShape);
        }
    }

    public void drawShape(String shapeType, double startX, double startY, double endX, double endY) {
        Shape shape = createShape(shapeType, startX, startY, endX, endY);
        if (shape != null) {
            drawingPane.getChildren().add(shape);
        }
        if (previewShape != null) {
            drawingPane.getChildren().remove(previewShape);
            previewShape = null;
        }
    }

    private Shape createShape(String shapeType, double startX, double startY, double endX, double endY) {
        Shape shape = null;
        switch (shapeType) {
            case "Circle":
                double radius = Math.hypot(endX - startX, endY - startY) / 2;
                shape = new Circle((startX + endX) / 2, (startY + endY) / 2, radius);
                shape.setFill(Color.TRANSPARENT);
                shape.setStroke(Color.RED);
                break;
            case "Ellipse":
                shape = new Ellipse((startX + endX) / 2, (startY + endY) / 2, Math.abs(endX - startX) / 2, Math.abs(endY - startY) / 2);
                shape.setFill(Color.TRANSPARENT);
                shape.setStroke(Color.ORANGE);
                break;
            case "Triangle":
                shape = new Polygon(startX, startY, endX, endY, startX, endY);
                shape.setFill(Color.TRANSPARENT);
                shape.setStroke(Color.BLUE);
                break;
            case "Rectangle":
                shape = new Rectangle(Math.min(startX, endX), Math.min(startY, endY), Math.abs(endX - startX), Math.abs(endY - startY));
                shape.setFill(Color.TRANSPARENT);
                shape.setStroke(Color.PURPLE);
                break;
            case "Line":
                shape = new Line(startX, startY, endX, endY);
                shape.setStroke(Color.BROWN);
                break;
            case "Polygon":
                shape = new Polygon(startX, startY, endX, endY, startX, endY, endX, startY);
                shape.setFill(Color.TRANSPARENT);
                shape.setStroke(Color.GREEN);
                break;
        }
        return shape;
    }
}
