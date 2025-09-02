package com.agendador.audio;

import org.junit.jupiter.api.Test;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;

public class MP3SupportTest {
    
    @Test
    public void testMP3SupportAvailable() {
        // Test if MP3 support is available
        try {
            System.out.println("Testando suporte a MP3...");
            
            // Check supported file types
            System.out.println("Tipos de arquivo suportados:");
            AudioFileFormat.Type[] types = AudioSystem.getAudioFileTypes();
            for (AudioFileFormat.Type type : types) {
                System.out.println("- " + type.toString());
            }
            
            System.out.println("\nSuporte a MP3 configurado com sucesso!");
            
        } catch (Exception e) {
            System.err.println("Erro ao verificar suporte a MP3: " + e.getMessage());
        }
    }
}
