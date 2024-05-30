//public class ZipFiles {
//    public static void zipFiles(String[] filesToZip, String zipFileName) {
//        byte[] buffer = new byte[1024];
//
//        try {
//            FileOutputStream fos = new FileOutputStream(zipFileName);
//            ZipOutputStream zos = new ZipOutputStream(fos);
//
//            for (String file : filesToZip) {
//                FileInputStream fis = new FileInputStream(file);
//                zos.putNextEntry(new ZipEntry(file));
//
//                int length;
//                while ((length = fis.read(buffer)) > 0) {
//                    zos.write(buffer, 0, length);
//                }
//
//                zos.closeEntry();
//                fis.close();
//            }
//
//            zos.close();
//            fos.close();
//
//            System.out.println("Files successfully zipped.");
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//}
import java.io.*;
import java.util.zip.*;

public class ZipFiles {
    public static void zipFiles(byte[] imageData, byte[] audioData, byte[] pdfData, String zipFileName) {
        byte[] buffer = new byte[1024];
        System.out.println(pdfData);

        try {
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);


            if (imageData != null && imageData.length!=0) {
                zos.putNextEntry(new ZipEntry("image.jpg"));
                zos.write(imageData);
                zos.closeEntry();
            }

            // Add audio data to zip
            if (audioData != null && audioData.length !=0) {
                zos.putNextEntry(new ZipEntry("audio.wav"));
                zos.write(audioData);
                zos.closeEntry();
            }

            // Add PDF data to zip
            if (pdfData != null && pdfData.length !=0) {
                zos.putNextEntry(new ZipEntry("document.pdf"));
                zos.write(pdfData);
                zos.closeEntry();
            }

            zos.close();
            fos.close();

            System.out.println("Files successfully zipped.");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
