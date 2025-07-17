package com.agendador.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Gerenciador de áudio para reprodução de sinais
 */
public class AudioManager {
    
    private Clip currentClip;
    private boolean isPlaying = false;
    
    /**
     * Reproduz um arquivo de áudio por uma duração específica
     * @param caminhoArquivo Caminho para o arquivo de áudio
     * @param duracaoSegundos Duração em segundos para reproduzir
     * @param dispositivoAudio Nome do dispositivo de áudio (opcional)
     */
    public CompletableFuture<Void> reproduzirAudio(String caminhoArquivo, int duracaoSegundos, String dispositivoAudio) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Parar reprodução atual se existir
                pararReproducao();
                
                File audioFile = new File(caminhoArquivo);
                if (!audioFile.exists()) {
                    throw new IllegalArgumentException("Arquivo de áudio não encontrado: " + caminhoArquivo);
                }
                
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                
                // Configurar mixer específico se fornecido
                Mixer mixer = obterMixer(dispositivoAudio);
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                
                if (mixer != null) {
                    currentClip = (Clip) mixer.getLine(info);
                } else {
                    currentClip = AudioSystem.getClip();
                }
                
                currentClip.open(audioStream);
                
                // Configurar para loop contínuo
                currentClip.loop(Clip.LOOP_CONTINUOUSLY);
                currentClip.start();
                isPlaying = true;
                
                System.out.println("Reproduzindo áudio: " + caminhoArquivo + " por " + duracaoSegundos + " segundos");
                
                // Aguardar duração especificada
                Thread.sleep(duracaoSegundos * 1000L);
                
                // Parar reprodução
                pararReproducao();
                
            } catch (UnsupportedAudioFileException e) {
                System.err.println("Formato de áudio não suportado: " + e.getMessage());
                throw new RuntimeException("Formato de áudio não suportado", e);
            } catch (IOException e) {
                System.err.println("Erro ao ler arquivo de áudio: " + e.getMessage());
                throw new RuntimeException("Erro ao ler arquivo de áudio", e);
            } catch (LineUnavailableException e) {
                System.err.println("Linha de áudio não disponível: " + e.getMessage());
                throw new RuntimeException("Linha de áudio não disponível", e);
            } catch (InterruptedException e) {
                System.out.println("Reprodução interrompida");
                pararReproducao();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.err.println("Erro inesperado na reprodução: " + e.getMessage());
                throw new RuntimeException("Erro na reprodução de áudio", e);
            }
        });
    }
    
    /**
     * Para a reprodução atual
     */
    public void pararReproducao() {
        if (currentClip != null && currentClip.isOpen()) {
            currentClip.stop();
            currentClip.close();
            isPlaying = false;
            System.out.println("Reprodução parada");
        }
    }
    
    /**
     * Verifica se está reproduzindo áudio
     */
    public boolean isPlaying() {
        return isPlaying && currentClip != null && currentClip.isRunning();
    }
    
    /**
     * Obtém lista de dispositivos de áudio disponíveis
     */
    public List<String> obterDispositivosAudio() {
        List<String> dispositivos = new ArrayList<>();
        
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            
            // Verificar se o mixer suporta reprodução
            Line.Info[] sourceLineInfos = mixer.getSourceLineInfo();
            if (sourceLineInfos.length > 0) {
                dispositivos.add(mixerInfo.getName());
            }
        }
        
        return dispositivos;
    }
    
    /**
     * Obtém o mixer correspondente ao nome do dispositivo
     */
    private Mixer obterMixer(String nomeDispositivo) {
        if (nomeDispositivo == null || nomeDispositivo.trim().isEmpty()) {
            return null;
        }
        
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixerInfos) {
            if (mixerInfo.getName().equals(nomeDispositivo)) {
                return AudioSystem.getMixer(mixerInfo);
            }
        }
        
        System.err.println("Dispositivo de áudio não encontrado: " + nomeDispositivo);
        return null;
    }
    
    /**
     * Valida se um arquivo de áudio é suportado
     */
    public boolean validarArquivoAudio(String caminhoArquivo) {
        try {
            File audioFile = new File(caminhoArquivo);
            if (!audioFile.exists()) {
                return false;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioStream.close();
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtém informações sobre um arquivo de áudio
     */
    public AudioInfo obterInformacoesAudio(String caminhoArquivo) throws IOException, UnsupportedAudioFileException {
        File audioFile = new File(caminhoArquivo);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        
        AudioFormat format = audioStream.getFormat();
        long frames = audioStream.getFrameLength();
        double durationInSeconds = (frames + 0.0) / format.getFrameRate();
        
        audioStream.close();
        
        return new AudioInfo(
            format.getSampleRate(),
            format.getChannels(),
            format.getSampleSizeInBits(),
            durationInSeconds,
            audioFile.length()
        );
    }
    
    /**
     * Classe para armazenar informações de áudio
     */
    public static class AudioInfo {
        private final float sampleRate;
        private final int channels;
        private final int sampleSizeInBits;
        private final double durationInSeconds;
        private final long fileSizeInBytes;
        
        public AudioInfo(float sampleRate, int channels, int sampleSizeInBits, 
                        double durationInSeconds, long fileSizeInBytes) {
            this.sampleRate = sampleRate;
            this.channels = channels;
            this.sampleSizeInBits = sampleSizeInBits;
            this.durationInSeconds = durationInSeconds;
            this.fileSizeInBytes = fileSizeInBytes;
        }
        
        // Getters
        public float getSampleRate() { return sampleRate; }
        public int getChannels() { return channels; }
        public int getSampleSizeInBits() { return sampleSizeInBits; }
        public double getDurationInSeconds() { return durationInSeconds; }
        public long getFileSizeInBytes() { return fileSizeInBytes; }
        
        @Override
        public String toString() {
            return String.format("Taxa: %.1f Hz, Canais: %d, Bits: %d, Duração: %.2fs, Tamanho: %d bytes",
                sampleRate, channels, sampleSizeInBits, durationInSeconds, fileSizeInBytes);
        }
    }
}
