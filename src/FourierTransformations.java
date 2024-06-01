import org.jtransforms.fft.DoubleFFT_2D;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
public class FourierTransformations {

    private javafx.scene.image.Image imagePath;
    public javafx.scene.image.Image Edited_Image;


    FourierTransformations(javafx.scene.image.Image imagePath) throws IOException {
        this.imagePath=imagePath;
        BufferedImage image = SwingFXUtils.fromFXImage(imagePath,null);
        int width = image.getWidth();
        int height = image.getHeight();
        // Convert image to grayscale
        double[][] grayscale = new double[height][width];
        Raster raster = image.getData();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                grayscale[y][x] = 0.21 * r + 0.72 * g + 0.07 * b;
            }
        }

        // Prepare the data for FFT
        DoubleFFT_2D fft2d = new DoubleFFT_2D(height, width);
        double[] fftData = new double[height * width * 2]; // Complex numbers have 2 components
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                fftData[(y * width + x) * 2] = grayscale[y][x]; // real part
                fftData[(y * width + x) * 2 + 1] = 0.0; // imaginary part
            }
        }

        // Perform the forward FFT
        fft2d.complexForward(fftData);

        // Apply high-pass filter (example: simple radius-based filter)
        int centerX = width / 2;
        int centerY = height / 2;
        double radius = 50.0; // Adjust as needed
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int dx = x - centerX;
                int dy = y - centerY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance < radius) {
                    fftData[(y * width + x) * 2] = 0.0;
                    fftData[(y * width + x) * 2 + 1] = 0.0;
                }
            }
        }

        // Perform the inverse FFT
        fft2d.complexInverse(fftData, true);
        double[][] enhanced = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                enhanced[y][x] = fftData[(y * width + x) * 2]; // Take the real part
            }
        }

        // Normalize the enhanced image
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (enhanced[y][x] < min) {
                    min = enhanced[y][x];
                }
                if (enhanced[y][x] > max) {
                    max = enhanced[y][x];
                }
            }
        }
        BufferedImage enhancedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = (int) ((enhanced[y][x] - min) / (max - min) * 255);
                int gray = (value << 16) | (value << 8) | value;
                enhancedImage.setRGB(x, y, gray);
            }
        }
        showImages(image,enhancedImage);
    }

//    private int nearestPowerOfTwo(int value) {
//        int power = 1;
//        while (power < value) {
//            power <<= 1;
//        }
//        return power;
//    }
//
//    private BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
//        Image tmp = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
//        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2d = resized.createGraphics();
//        g2d.drawImage(tmp, 0, 0, null);
//        g2d.dispose();
//        return resized;
//    }
//
//    private BufferedImage toGrayscale(BufferedImage original) {
//        BufferedImage grayImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//        Graphics g = grayImage.getGraphics();
//        g.drawImage(original, 0, 0, null);
//        g.dispose();
//        return grayImage;
//    }
//
//    private Complex[][] forwardFFT(BufferedImage grayImage) {
//        int width = grayImage.getWidth();
//        int height = grayImage.getHeight();
//        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
//
//        Complex[][] complexImage = new Complex[height][width];
//        double[] rowReal = new double[width];
//        double[] colReal = new double[height];
//
//        // FFT row-wise
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                rowReal[x] = grayImage.getRaster().getSampleDouble(x, y, 0);
//            }
//            Complex[] rowComplex = fft.transform(rowReal, TransformType.FORWARD);
//            for (int x = 0; x < width; x++) {
//                complexImage[y][x] = rowComplex[x];
//            }
//        }
//
//        // FFT column-wise
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                colReal[y] = complexImage[y][x].getReal();
//            }
//            Complex[] colComplex = fft.transform(colReal, TransformType.FORWARD);
//            for (int y = 0; y < height; y++) {
//                complexImage[y][x] = colComplex[y];
//            }
//        }
//
//        return complexImage;
//    }
//
//    private void applyFilterMask(Complex[][] complexImage, double[][] filterMask) {
//        int height = complexImage.length;
//        int width = complexImage[0].length;
//
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                complexImage[y][x] = complexImage[y][x].multiply(filterMask[y][x]);
//            }
//        }
//    }
//
//    private double[][] createLowPassFilterMask(int width, int height, double cutoffFrequency) {
//        double[][] filterMask = new double[height][width];
//
//        int centerX = width / 2;
//        int centerY = height / 2;
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                double distance = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
//                if (distance <= cutoffFrequency) {
//                    filterMask[y][x] = 1; // Pass low frequencies
//                } else {
//                    filterMask[y][x] = 0; // Suppress high frequencies
//                }
//            }
//        }
//        return filterMask;
//    }
//
////    private BufferedImage inverseFFT(Complex[][] complexImage) {
////        int width = complexImage[0].length;
////        int height = complexImage.length;
////        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
////
////        double[] rowReal = new double[width];
////        double[] colReal = new double[height];
////
////        // IFFT column-wise
////        for (int x = 0; x < width; x++) {
////            for (int y = 0; y < height; y++) {
////                colReal[y] = complexImage[y][x].getReal();
////            }
////            Complex[] colComplex = fft.transform(colReal, TransformType.INVERSE);
////            for (int y = 0; y < height; y++) {
////                complexImage[y][x] = colComplex[y];
////            }
////        }
////
////        for (int y = 0; y < height; y++) {
////            for (int x = 0; x < width; x++) {
////                rowReal[x] = complexImage[y][x].getReal();
////            }
////            Complex[] rowComplex = fft.transform(rowReal, TransformType.INVERSE);
////            for (int x = 0; x < width; x++) {
////                complexImage[y][x] = rowComplex[x];
////            }
////        }
////
////        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
////        for (int y = 0; y < height; y++) {
////            for (int x = 0; x < width; x++) {
////                int pixelValue = (int) Math.round(complexImage[y][x].getReal());
////                pixelValue = Math.min(Math.max(pixelValue, 0), 255); // Clamp values to 0-255
////                outputImage.getRaster().setSample(x, y, 0, pixelValue);
////            }
////        }
////        return outputImage;
////    }
//private BufferedImage inverseFFT(Complex[][] complexImage) {
//    int width = complexImage[0].length;
//    int height = complexImage.length;
//    FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
//
//    double[] rowReal = new double[width];
//    double[] colReal = new double[height];
//
//    // IFFT column-wise
//    for (int x = 0; x < width; x++) {
//        for (int y = 0; y < height; y++) {
//            colReal[y] = complexImage[y][x].getReal();
//        }
//        Complex[] colComplex = fft.transform(colReal, TransformType.INVERSE);
//        for (int y = 0; y < height; y++) {
//            complexImage[y][x] = colComplex[y];
//        }
//    }
//
//    // IFFT row-wise
//    for (int y = 0; y < height; y++) {
//        for (int x = 0; x < width; x++) {
//            rowReal[x] = complexImage[y][x].getReal();
//        }
//        Complex[] rowComplex = fft.transform(rowReal, TransformType.INVERSE);
//        for (int x = 0; x < width; x++) {
//            complexImage[y][x] = rowComplex[x];
//        }
//    }
//
//    // Normalize the output
//    BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//    double normalizationFactor = width * height;
//
//    for (int y = 0; y < height; y++) {
//        for (int x = 0; x < width; x++) {
//            int pixelValue = (int) Math.round(complexImage[y][x].getReal() / normalizationFactor);
//            pixelValue = Math.min(Math.max(pixelValue, 0), 255); // Clamp values to 0-255
//            outputImage.getRaster().setSample(x, y, 0, pixelValue);
//        }
//    }
//    return outputImage;
//}

    private void showImages(BufferedImage originalImage, BufferedImage filteredImage) {
        ImageView originalImageView = new ImageView(SwingFXUtils.toFXImage(originalImage, null));
        ImageView filteredImageView = new ImageView(SwingFXUtils.toFXImage(filteredImage, null));
        originalImageView.setFitHeight(200);
        originalImageView.setFitWidth(200);
        filteredImageView.setFitWidth(200);
        filteredImageView.setFitHeight(200);

        HBox hbox = new HBox(20,originalImageView, filteredImageView);
        hbox.setStyle("-fx-padding:10px");
        Button btn=new Button("Apply Edit");
        BorderPane layout=new BorderPane();
        layout.setTop(hbox);
        layout.setPadding(new Insets(8,8,8,8));
        layout.setCenter(btn);
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("style.css");
        Stage stage = new Stage();
        stage.setTitle("FourierTransformations");
        stage.setScene(scene);
        btn.setOnAction(e->{
            Edited_Image=filteredImageView.getImage();
            stage.close();
        });
        stage.showAndWait();

    }


}
