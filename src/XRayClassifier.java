import javafx.scene.image.Image;
import javafx.scene.paint.Color;



    public class XRayClassifier {

        public enum Severity {
            MILD,
            MODERATE,
            SEVERE
        }

        // Method to classify X-ray image severity
        public Severity classify(Image xRayImage) {
            // Extract pixel colors from the image
            int mildCount = 0;
            int moderateCount = 0;
            int severeCount = 0;

            int width = (int) xRayImage.getWidth();
            int height = (int) xRayImage.getHeight();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color pixelColor = xRayImage.getPixelReader().getColor(x, y);
                    // Analyze pixel color and update severity counts accordingly
                    // This is a placeholder implementation

                    // For demonstration, let's assume a simple analysis based on grayscale intensity
                    double brightness = pixelColor.getBrightness();
                    if (brightness < 0.3) {
                        mildCount++;
                    } else if (brightness < 0.6) {
                        moderateCount++;
                    } else {
                        severeCount++;
                    }
                }
            }

            // Determine severity based on counts
            if (severeCount > moderateCount && severeCount > mildCount) {
                return Severity.SEVERE;
            } else if (moderateCount > mildCount && moderateCount > severeCount) {
                return Severity.MODERATE;
            } else {
                return Severity.MILD;
            }
        }

        // Method to display classification result
        public void displayClassificationResult(Severity severity) {
            switch (severity) {
                case MILD:
                    System.out.println("The medical condition is classified as MILD.");
                    break;
                case MODERATE:
                    System.out.println("The medical condition is classified as MODERATE.");
                    break;
                case SEVERE:
                    System.out.println("The medical condition is classified as SEVERE.");
                    break;
            }
        }
}
