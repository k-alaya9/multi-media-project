import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


 class MedicalReportScreen {

     public static Scene createScene(Stage primaryStage, Scene mainScene) {
         // Create input fields
         TextField nameField = new TextField();
         TextField genderField = new TextField();
         TextField ageField = new TextField();
         TextField medicalRecordNoField = new TextField();
         TextField nationalityField = new TextField();
         TextField diagnosisField = new TextField();
         TextField dateField = new TextField();

         // Create labels
         Label nameLabel = new Label("Name:");
         Label genderLabel = new Label("Gender:");
         Label ageLabel = new Label("Age:");
         Label medicalRecordNoLabel = new Label("Medical Record No:");
         Label nationalityLabel = new Label("Nationality:");
         Label diagnosisLabel = new Label("Diagnosis:");
         Label dateLabel = new Label("Date:");

         // Create layout and add components
         GridPane gridPane = new GridPane();
         gridPane.setPadding(new Insets(10,10,10,10));
         gridPane.setHgap(10);
         gridPane.setVgap(10);

         gridPane.add(nameLabel, 0, 0);
         gridPane.add(nameField, 1, 0);
         gridPane.add(genderLabel, 0, 1);
         gridPane.add(genderField, 1, 1);
         gridPane.add(ageLabel, 0, 2);
         gridPane.add(ageField, 1, 2);
         gridPane.add(medicalRecordNoLabel, 0, 3);
         gridPane.add(medicalRecordNoField, 1, 3);

         gridPane.add(nationalityLabel, 0, 4);
         gridPane.add(nationalityField, 1, 4);

         gridPane.add(diagnosisLabel, 0, 5);
         gridPane.add(diagnosisField, 1, 5);

         gridPane.add(dateLabel, 0, 6);
         gridPane.add(dateField, 1, 6);

         Button saveButton = new Button("Save Report");

         saveButton.setOnAction(e -> {
             try {
                 saveReport(nameField.getText(), genderField.getText(), ageField.getText(),medicalRecordNoField.getText(),nationalityField.getText(),diagnosisField.getText(),dateField.getText());

                 primaryStage.setScene(mainScene);
             } catch (IOException ex) {
                 ex.printStackTrace();
             }
         });

         Button backButton = new Button("Back");
         backButton.setOnAction(e -> primaryStage.setScene(mainScene));
         HBox hBox= new HBox(10,backButton,saveButton);
         hBox.getStyleClass().add("hbox");
         VBox layout = new VBox(10, hBox,gridPane);
        Scene root=new Scene(layout, primaryStage.getWidth(),primaryStage.getHeight());
         root.getStylesheets().add("style.css");
         return root;
     }

     private static void saveReport(String name, String gender, String age,String Id,String Nationality,String diagonsis,String date) throws IOException {
         // Sample data
         List<Patient> patients = new ArrayList<>();
         patients.add(new Patient(name, Integer.parseInt(age), Id, Nationality, diagonsis, date));

         // Define columns
         JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(patients);

         JasperReportBuilder report =DynamicReports.report();

         report.columns(
                 Columns.column("Name", "name", DataTypes.stringType()),
                 Columns.column("Age", "age", DataTypes.integerType()),
                 Columns.column("Medical Record No", "medicalRecordNo", DataTypes.stringType()),
                 Columns.column("Nationality", "nationality", DataTypes.stringType()),
                 Columns.column("Diagnosis", "diagnosis", DataTypes.stringType()),
                 Columns.column("Date", "date", DataTypes.stringType())
         );

         report.title(
                 Components.text("Patient Medical Report")
                         .setHorizontalAlignment(HorizontalAlignment.CENTER)
                         .setStyle(DynamicReports.stl.style().setFontSize(18).bold())
         );

         // Set data source
         report.setDataSource(dataSource);
         // Save report to PDF
         String filePath =String.format("MedicalReport_%s.pdf",Id) ;
         try (OutputStream outputStream = Files.newOutputStream(new File("C:\\Users\\DELL\\Documents\\Project1\\reports\\"+filePath).toPath())) {
             report.toPdf(outputStream);
             showAlert("Report Saved", "Report saved Successfully!",Alert.AlertType.CONFIRMATION);
         } catch (DRException e) {
             showAlert("Error Saving Report", "Failed to save the Report. Please try again.",Alert.AlertType.ERROR);

             throw new RuntimeException(e);
         }
     }
     private static void showAlert(String title, String content, Alert.AlertType x) {
         Alert alert = new Alert(x);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(content);
         alert.showAndWait();
     }
 }
