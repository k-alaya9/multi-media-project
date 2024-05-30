import javafx.application.Platform;
import javafx.concurrent.Task;
import javax.sound.sampled.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioRecorder {
    private TargetDataLine line;
    private Task<Void> recordingTask;

    private String generateUniqueFilename() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "voice record\\" + timestamp + ".wav";
    }

    public void startRecording() {
        String audioFilePath = generateUniqueFilename();
        File audioFile = new File(audioFilePath);
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            System.out.println("Recording...");

            recordingTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    AudioInputStream ais = new AudioInputStream(line);
                    try {
                        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        Platform.runLater(() -> {
                            line.stop();
                            line.close();
                            System.out.println("Recording stopped. Audio saved to: " + audioFile.getAbsolutePath());
                        });
                    }
                    return null;
                }
            };

            Thread recordingThread = new Thread(recordingTask);
            recordingThread.start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (line != null && line.isOpen()) {
            line.stop();
            line.close();
        }
        if (recordingTask != null) {
            recordingTask.cancel();
        }
    }
}
