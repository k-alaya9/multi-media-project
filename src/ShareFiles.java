import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ShareFiles {
    File sharedFile;
    private static final String BOT_TOKEN = "7269016052:AAGNGXrooEYp7gVfMcGK6-bMBvPEIuSAO84";

    ShareFiles(File sharedFile){
        this.sharedFile=sharedFile;
        try {
          String CHAT_ID=ShowChats();
          if(CHAT_ID!=null) {
              sendFileToTelegram(sharedFile, CHAT_ID);
          }
        } catch (IOException e) {
            showAlert("Error", "Failed to send the file. Please try again.",Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            showAlert("Error", "Failed to send the file. Please try again.",Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    };
    private void sendFileToTelegram(File file,String CHAT_ID) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String boundary = UUID.randomUUID().toString();
        HttpRequest.BodyPublisher bodyPublisher = createMultipartBodyPublisher(file, boundary,CHAT_ID);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + BOT_TOKEN + "/sendDocument"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(bodyPublisher)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Unexpected code " + response.statusCode());
        }
        showAlert("File sent successfully", "File sent successfully!",Alert.AlertType.CONFIRMATION);
        System.out.println("File sent successfully: " + response.body());
    }

    private HttpRequest.BodyPublisher createMultipartBodyPublisher(File file, String boundary,String CHAT_ID) throws IOException {
        String fileName = file.getName();
        String mimeType = Files.probeContentType(file.toPath());

        byte[] fileBytes = Files.readAllBytes(file.toPath());
        String filePartHeader = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"document\"; filename=\"" + fileName + "\"\r\n" +
                "Content-Type: " + mimeType + "\r\n\r\n";

        String chatIdPart = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n" +
                CHAT_ID + "\r\n";

        String endBoundary = "--" + boundary + "--\r\n";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(chatIdPart.getBytes());
        outputStream.write(filePartHeader.getBytes());
        outputStream.write(fileBytes);
        outputStream.write("\r\n".getBytes());
        outputStream.write(endBoundary.getBytes());

        return HttpRequest.BodyPublishers.ofByteArray(outputStream.toByteArray());
    }

    private String ShowChats(){
        Stage primaryStage=new Stage();
        primaryStage.setTitle("Telegram Chat Viewer");

        ListView<String> listView = new ListView<>();
        Map<String, Long> chatMap = new HashMap<>();
        listView.setStyle("   -fx-background-color: #f0f0f0; /* Background color */\n" +
                "    -fx-border-color: #ccc; /* Border color */\n" +
                "    -fx-border-width: 1px; /* Border width */");

        try {
            String jsonResponse = getUpdates();
            JSONArray resultArray = new JSONObject(jsonResponse).getJSONArray("result");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject update = resultArray.getJSONObject(i);
                if (update.has("message")) {
                    JSONObject message = update.getJSONObject("message");
                    JSONObject chat = message.getJSONObject("chat");
                    String chatTitle = chat.getString("first_name");
                    long chatId = chat.getLong("id");
                    listView.getItems().add(chatTitle);
                    chatMap.put(chatTitle, chatId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String[] CHAT_ID = {null};
        listView.setOnMouseClicked(event -> {
            String selectedChat = listView.getSelectionModel().getSelectedItem();
            if (selectedChat != null) {
                Long chatId = chatMap.get(selectedChat);
                System.out.println(chatId);
                CHAT_ID[0] =String.valueOf(chatId);
                primaryStage.close();
            }
        });

        VBox vbox = new VBox(listView);
        Scene scene = new Scene(vbox, 300, 200);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.showAndWait();
        return CHAT_ID[0];
    }
    private String getUpdates() throws Exception {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/getUpdates";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) { // Success
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return content.toString();
        } else {
            throw new RuntimeException("Failed to fetch updates: HTTP code " + responseCode);
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