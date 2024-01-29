package com.example.shortgenserver.service;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class GenerationService {
    public ByteArrayOutputStream createAndSaveVideo(String text1, String text2, String youtubeChannel) {
        String outputFile = "CONST.mp4";
        try {
            String inputAudioFile = "audio.mp3";
            int width = 1080;
            int height = 1920;
            int frameRate = 30;
            int durationInSeconds = 10;
            int audioChannels = 2;  // Количество аудио каналов
            int audioSampleRate = 48000;

            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, width, height);
            recorder.setFormat("mp4");
            recorder.setFrameRate(frameRate);
            recorder.setAudioChannels(audioChannels);
            recorder.setSampleRate(audioSampleRate);
            recorder.start();

            for (int i = 0; i < durationInSeconds * frameRate * 0.7; i++) {
                Frame frame = convertImageToFrame(createImage(text1, youtubeChannel));
                recorder.record(frame);
            }
            for (int i = 0; i < durationInSeconds * frameRate * 0.3; i++) {
                Frame frame = convertImageToFrame(createImage(text2, youtubeChannel));
                recorder.record(frame);
            }

            FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(inputAudioFile);
            audioGrabber.start();

            // Считываем аудиофайл и записываем его в видео
            Frame audioFrame;
            for (int i = 0; i < durationInSeconds * frameRate; i++) {
                audioFrame = audioGrabber.grabFrame();
                recorder.record(audioFrame);
            }


            recorder.stop();
            audioGrabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loadMp4FileToByteArrayOutputStream(new File(outputFile));
    }
    public ByteArrayOutputStream loadMp4FileToByteArrayOutputStream(File mp4File){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = new FileInputStream(mp4File)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // Удаление файла после чтения и записи в ByteArrayOutputStream
            mp4File.delete();
        }
        return byteArrayOutputStream;
    }

    private static org.bytedeco.javacv.Frame convertImageToFrame(BufferedImage image) {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        return converter.getFrame(image);
    }
    private static BufferedImage createImage(String text, String youtubeChannel) {
        // Ширина и высота изображения
        int width = 1080;
        int height = 1920;

        // Создаем изображение
        BufferedImage background = loadImage("sea.png");
//        BufferedImage image = createImageWithBackgroundImage(width, height, background);

        // Получаем Graphics2D контекст изображения
        Graphics2D g2d = background.createGraphics();

        // Задаем цвет и шрифт для текста
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));

        // Рисуем текст
        int textX = 300;
        int textY = 600;
        g2d.drawString(text, textX, textY);

        g2d.drawString("Подписывайся на канал" + youtubeChannel, textX, textY+300);

        // Освобождаем ресурсы Graphics2D
        g2d.dispose();
        return background;
    }
    private static BufferedImage createImageWithBackgroundImage(int width, int height, BufferedImage backgroundImage) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Рисуем фон из другого изображения
        g2d.drawImage(backgroundImage, 0, 0, width, height, null);

        // Освобождаем ресурсы Graphics2D
        g2d.dispose();

        return image;
    }
    private static BufferedImage loadImage(String imagePath) {
        try {
            return ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}