import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class TextDrawer {
    private Pane drawingPane;
    private TextField textField;

    public TextDrawer(Pane drawingPane) {
        this.drawingPane = drawingPane;
        setUpTextField();
    }

    private void setUpTextField() {
        textField = new TextField();
        textField.setPromptText("Enter text and press Enter");
        textField.setOnAction(e -> {
            if (!textField.getText().trim().isEmpty()) {
                addTextToPane(textField.getText(), textField.getLayoutX(), textField.getLayoutY());
                drawingPane.getChildren().remove(textField);
            }
        });
    }

    public void startTextInput(MouseEvent event) {
        textField.setLayoutX(event.getX());
        textField.setLayoutY(event.getY());
        drawingPane.getChildren().add(textField);
        textField.requestFocus();
    }

    private void addTextToPane(String text, double x, double y) {
        Text newText = new Text(x, y, text);
        newText.setFill(Color.BLACK); // Set text color, you can customize it
        drawingPane.getChildren().add(newText);
    }
}
