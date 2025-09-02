package com.agendador.test;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import java.io.File;

public class TestMP3Support {
    public static void main(String[] args) {
        System.out.println("=== Teste de Suporte a MP3 ===");
        
        // Check supported file types
        System.out.println("\nTipos de arquivo suportados:");
        AudioFileFormat.Type[] types = AudioSystem.getAudioFileTypes();
        boolean mp3Supported = false;
        
        for (AudioFileFormat.Type type : types) {
            System.out.println("- " + type.toString());
            if (type.toString().toLowerCase().contains("mp3") || 
                type.toString().toLowerCase().contains("mpeg")) {
                mp3Supported = true;
            }
        }
        
        System.out.println("\nSuporte a MP3: " + (mp3Supported ? "✅ SIM" : "❌ NÃO"));
        
        // Try to load MP3 SPI classes
        System.out.println("\nTestando carregamento de classes MP3:");
        
        String[] mp3Classes = {
            "javazoom.spi.mpeg.sampled.file.MpegAudioFileReader",
            "javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider"
        };
        
        for (String className : mp3Classes) {
            try {
                Class.forName(className);
                System.out.println("✅ " + className + " - CARREGADO");
            } catch (ClassNotFoundException e) {
                System.out.println("❌ " + className + " - NÃO ENCONTRADO");
            }
        }
        
        // Test with a dummy MP3 file creation attempt
        System.out.println("\nTestando reconhecimento de arquivo MP3...");
        try {
            // This will test if the AudioSystem can recognize MP3 files
            File testFile = new File("test.mp3");
            if (!testFile.exists()) {
                System.out.println("⚠️  Arquivo test.mp3 não encontrado - crie um arquivo MP3 para teste completo");
            } else {
                AudioInputStream stream = AudioSystem.getAudioInputStream(testFile);
                System.out.println("✅ Arquivo MP3 reconhecido com sucesso!");
                stream.close();
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao tentar ler arquivo MP3: " + e.getMessage());
        }
        
        System.out.println("\n=== Teste concluído ===");
    }
}
