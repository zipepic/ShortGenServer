package com.example.shortgenserver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class VideoDownloader {

    public static void main(String[] args) {
        String videoUrl = "http://localhost:8080"; // URL сервера генерации видео
        String saveFilePath = "./video_download.mp4"; // Путь для сохранения видео-файла

        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> request = RequestEntity.get(videoUrl)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .build();

        ResponseEntity<byte[]> response = restTemplate.exchange(request, byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            saveVideoFile(response.getBody(), saveFilePath);
            System.out.println("Video downloaded successfully!");
        } else {
            System.out.println("Failed to download video. Status code: " + response.getStatusCodeValue());
        }
    }

    private static void saveVideoFile(byte[] videoBytes, String saveFilePath) {
        try (FileOutputStream outputStream = new FileOutputStream(saveFilePath)) {
            if (videoBytes != null) {
                outputStream.write(videoBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
