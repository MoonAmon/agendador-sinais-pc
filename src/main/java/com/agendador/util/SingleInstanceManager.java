package com.agendador.util;

import javax.swing.JOptionPane;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gerenciador para garantir que apenas uma instância da aplicação seja executada
 */
public class SingleInstanceManager {
    
    private static final String APP_NAME = "AgendadorSinais";
    private static final int DEFAULT_PORT = 19283; // Porta única para a aplicação
    private static final String LOCK_FILE_NAME = "agendador-sinais.lock";
    
    private ServerSocket serverSocket;
    private FileChannel lockFileChannel;
    private FileLock fileLock;
    private File lockFile;
    
    private static SingleInstanceManager instance;
    
    private SingleInstanceManager() {
        // Singleton
    }
    
    public static synchronized SingleInstanceManager getInstance() {
        if (instance == null) {
            instance = new SingleInstanceManager();
        }
        return instance;
    }
    
    /**
     * Verifica se é possível executar uma nova instância
     * @return true se pode executar, false se já existe uma instância
     */
    public boolean tryLock() {
        return tryLockBySocket() && tryLockByFile();
    }
    
    /**
     * Tenta fazer lock usando socket (método mais confiável)
     */
    private boolean tryLockBySocket() {
        try {
            // Tenta abrir um ServerSocket na porta específica
            serverSocket = new ServerSocket(DEFAULT_PORT, 0, InetAddress.getLocalHost());
            
            System.out.println("Socket lock adquirido na porta: " + DEFAULT_PORT);
            return true;
            
        } catch (IOException e) {
            System.err.println("Não foi possível adquirir socket lock - outra instância já está executando");
            return false;
        }
    }
    
    /**
     * Tenta fazer lock usando arquivo (método adicional)
     */
    private boolean tryLockByFile() {
        try {
            // Criar diretório de trabalho no temp ou home do usuário
            String userHome = System.getProperty("user.home");
            String tempDir = System.getProperty("java.io.tmpdir");
            
            Path lockDir = Paths.get(userHome, ".agendador");
            if (!Files.exists(lockDir)) {
                lockDir = Paths.get(tempDir, "agendador");
            }
            
            // Criar diretório se não existir
            Files.createDirectories(lockDir);
            
            // Criar arquivo de lock
            lockFile = new File(lockDir.toFile(), LOCK_FILE_NAME);
            
            // Criar FileChannel e tentar fazer lock
            lockFileChannel = new RandomAccessFile(lockFile, "rw").getChannel();
            fileLock = lockFileChannel.tryLock();
            
            if (fileLock == null) {
                System.err.println("Não foi possível adquirir file lock - outra instância já está executando");
                return false;
            }
            
            // Escrever informações da instância no arquivo
            writeInstanceInfo();
            
            System.out.println("File lock adquirido: " + lockFile.getAbsolutePath());
            return true;
            
        } catch (IOException e) {
            System.err.println("Erro ao tentar adquirir file lock: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Escreve informações da instância atual no arquivo de lock
     */
    private void writeInstanceInfo() {
        try {
            String info = String.format(
                "PID: %s%nStartTime: %d%nUser: %s%nJavaVersion: %s%n",
                ProcessHandle.current().pid(),
                System.currentTimeMillis(),
                System.getProperty("user.name"),
                System.getProperty("java.version")
            );
            
            lockFileChannel.position(0);
            lockFileChannel.write(java.nio.ByteBuffer.wrap(info.getBytes()));
            lockFileChannel.force(true);
            
        } catch (IOException e) {
            System.err.println("Erro ao escrever informações da instância: " + e.getMessage());
        }
    }
    
    /**
     * Libera todos os locks adquiridos
     */
    public void releaseLocks() {
        // Liberar file lock
        if (fileLock != null) {
            try {
                fileLock.release();
                System.out.println("File lock liberado");
            } catch (IOException e) {
                System.err.println("Erro ao liberar file lock: " + e.getMessage());
            }
        }
        
        // Fechar canal do arquivo
        if (lockFileChannel != null) {
            try {
                lockFileChannel.close();
                System.out.println("Lock file channel fechado");
            } catch (IOException e) {
                System.err.println("Erro ao fechar lock file channel: " + e.getMessage());
            }
        }
        
        // Remover arquivo de lock
        if (lockFile != null && lockFile.exists()) {
            if (lockFile.delete()) {
                System.out.println("Arquivo de lock removido: " + lockFile.getName());
            } else {
                System.err.println("Não foi possível remover arquivo de lock: " + lockFile.getName());
            }
        }
        
        // Liberar socket lock
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("Socket lock liberado");
            } catch (IOException e) {
                System.err.println("Erro ao liberar socket lock: " + e.getMessage());
            }
        }
    }
    
    /**
     * Mostra uma mensagem de erro quando já existe uma instância executando
     */
    public void showAlreadyRunningMessage() {
        String message = 
            "O Agendador de Sinais já está em execução!\n\n" +
            "Para acessar a aplicação:\n" +
            "• Verifique a área de notificação (system tray)\n" +
            "• Clique no ícone do Agendador de Sinais\n" +
            "• Ou use Alt+Tab para localizar a janela\n\n" +
            "Apenas uma instância pode ser executada por vez.";
        
        JOptionPane.showMessageDialog(
            null,
            message,
            "Aplicação Já Executando",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Configura shutdown hook para limpar locks automaticamente
     */
    public void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Executando limpeza de locks...");
            releaseLocks();
        }, "SingleInstance-Cleanup"));
    }
    
    /**
     * Verifica se existe alguma instância executando baseado no arquivo de lock
     */
    public boolean isAnotherInstanceRunning() {
        String userHome = System.getProperty("user.home");
        String tempDir = System.getProperty("java.io.tmpdir");
        
        Path lockPath1 = Paths.get(userHome, ".agendador", LOCK_FILE_NAME);
        Path lockPath2 = Paths.get(tempDir, "agendador", LOCK_FILE_NAME);
        
        return Files.exists(lockPath1) || Files.exists(lockPath2);
    }
    
    /**
     * Força a limpeza de locks órfãos (se a aplicação travou)
     */
    public void forceCleanOldLocks() {
        try {
            String userHome = System.getProperty("user.home");
            String tempDir = System.getProperty("java.io.tmpdir");
            
            Path[] possibleLockPaths = {
                Paths.get(userHome, ".agendador", LOCK_FILE_NAME),
                Paths.get(tempDir, "agendador", LOCK_FILE_NAME)
            };
            
            for (Path lockPath : possibleLockPaths) {
                if (Files.exists(lockPath)) {
                    try {
                        Files.delete(lockPath);
                        System.out.println("Lock órfão removido: " + lockPath);
                    } catch (IOException e) {
                        System.err.println("Não foi possível remover lock órfão: " + lockPath);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao limpar locks órfãos: " + e.getMessage());
        }
    }
    
    /**
     * Obter informações da instância atual
     */
    public String getInstanceInfo() {
        return String.format(
            "Instância Atual:%n" +
            "PID: %s%n" +
            "Usuário: %s%n" +
            "Java: %s%n" +
            "Porta: %d%n" +
            "Lock File: %s",
            ProcessHandle.current().pid(),
            System.getProperty("user.name"),
            System.getProperty("java.version"),
            DEFAULT_PORT,
            lockFile != null ? lockFile.getAbsolutePath() : "N/A"
        );
    }
}