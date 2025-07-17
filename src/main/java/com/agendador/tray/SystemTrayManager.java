package com.agendador.tray;

import com.agendador.controller.AgendadorController;
import com.agendador.view.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Gerenciador do System Tray para execução em segundo plano
 */
public class SystemTrayManager {
    
    private final MainWindow mainWindow;
    private final AgendadorController controller;
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    
    public SystemTrayManager(MainWindow mainWindow, AgendadorController controller) {
        this.mainWindow = mainWindow;
        this.controller = controller;
    }
    
    /**
     * Inicializa o System Tray
     */
    public void initializeSystemTray() {
        if (!SystemTray.isSupported()) {
            System.err.println("System Tray não é suportado");
            return;
        }
        
        systemTray = SystemTray.getSystemTray();
        
        // Criar ícone para o tray
        Image trayImage = createTrayIcon();
        
        // Criar menu popup
        PopupMenu popup = createPopupMenu();
        
        // Criar TrayIcon
        trayIcon = new TrayIcon(trayImage, "Agendador de Sinais", popup);
        trayIcon.setImageAutoSize(true);
        
        // Adicionar listener para duplo clique
        trayIcon.addActionListener(e -> mostrarJanela());
        
        try {
            systemTray.add(trayIcon);
            System.out.println("System Tray inicializado com sucesso");
        } catch (AWTException e) {
            System.err.println("Erro ao adicionar ícone ao System Tray: " + e.getMessage());
        }
    }
    
    /**
     * Cria um ícone simples para o system tray
     */
    private Image createTrayIcon() {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Habilitar antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desenhar um ícone simples (círculo com nota musical)
        g2d.setColor(Color.BLUE);
        g2d.fillOval(1, 1, size-2, size-2);
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(3, 7, 4, 3);
        g2d.fillRect(7, 4, 1, 6);
        g2d.fillOval(8, 3, 2, 2);
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Cria o menu popup do system tray
     */
    private PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu();
        
        // Item para mostrar janela
        MenuItem showItem = new MenuItem("Mostrar Agendador");
        showItem.addActionListener(e -> mostrarJanela());
        popup.add(showItem);
        
        popup.addSeparator();
        
        // Item de status
        MenuItem statusItem = new MenuItem("Status: Carregando...");
        statusItem.setEnabled(false);
        popup.add(statusItem);
        
        // Atualizar status periodicamente
        Timer statusTimer = new Timer(5000, e -> atualizarStatus(statusItem));
        statusTimer.start();
        
        popup.addSeparator();
        
        // Item para parar/iniciar agendador
        MenuItem toggleItem = new MenuItem("Pausar Agendador");
        toggleItem.addActionListener(e -> toggleAgendador(toggleItem));
        popup.add(toggleItem);
        
        // Item para parar reprodução atual
        MenuItem stopItem = new MenuItem("Parar Reprodução");
        stopItem.addActionListener(e -> controller.pararReproducaoAtual());
        popup.add(stopItem);
        
        popup.addSeparator();
        
        // Item para sair
        MenuItem exitItem = new MenuItem("Sair");
        exitItem.addActionListener(e -> sairAplicacao());
        popup.add(exitItem);
        
        return popup;
    }
    
    /**
     * Mostra a janela principal
     */
    private void mostrarJanela() {
        if (mainWindow != null) {
            mainWindow.setVisible(true);
            mainWindow.setExtendedState(JFrame.NORMAL);
            mainWindow.toFront();
            mainWindow.requestFocus();
        }
    }
    
    /**
     * Esconde a janela principal para o system tray
     */
    public void esconderParaTray() {
        if (mainWindow != null) {
            mainWindow.setVisible(false);
        }
        
        // Mostrar notificação
        if (trayIcon != null) {
            trayIcon.displayMessage(
                "Agendador de Sinais",
                "Aplicação minimizada para a barra de tarefas",
                TrayIcon.MessageType.INFO
            );
        }
    }
    
    /**
     * Atualiza o status no menu do tray
     */
    private void atualizarStatus(MenuItem statusItem) {
        if (controller != null) {
            boolean isRunning = controller.isAgendadorAtivo();
            boolean isPlaying = controller.isReproducing();
            
            String status;
            if (isPlaying) {
                status = "Status: Reproduzindo";
            } else if (isRunning) {
                status = "Status: Ativo";
            } else {
                status = "Status: Pausado";
            }
            
            statusItem.setLabel(status);
        }
    }
    
    /**
     * Liga/desliga o agendador
     */
    private void toggleAgendador(MenuItem toggleItem) {
        if (controller != null) {
            if (controller.isAgendadorAtivo()) {
                controller.pararAgendador();
                toggleItem.setLabel("Iniciar Agendador");
                mostrarNotificacao("Agendador pausado", TrayIcon.MessageType.WARNING);
            } else {
                controller.iniciarAgendador();
                toggleItem.setLabel("Pausar Agendador");
                mostrarNotificacao("Agendador iniciado", TrayIcon.MessageType.INFO);
            }
        }
    }
    
    /**
     * Mostra uma notificação no system tray
     */
    public void mostrarNotificacao(String mensagem, TrayIcon.MessageType tipo) {
        if (trayIcon != null) {
            trayIcon.displayMessage("Agendador de Sinais", mensagem, tipo);
        }
    }
    
    /**
     * Sai da aplicação
     */
    private void sairAplicacao() {
        // Confirmar saída
        int resposta = JOptionPane.showConfirmDialog(
            null,
            "Tem certeza que deseja sair do Agendador de Sinais?",
            "Confirmar Saída",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (resposta == JOptionPane.YES_OPTION) {
            // Parar agendador
            if (controller != null) {
                controller.pararAgendador();
            }
            
            // Remover do system tray
            if (systemTray != null && trayIcon != null) {
                systemTray.remove(trayIcon);
            }
            
            // Sair da aplicação
            System.exit(0);
        }
    }
    
    /**
     * Remove o ícone do system tray
     */
    public void removerDoTray() {
        if (systemTray != null && trayIcon != null) {
            systemTray.remove(trayIcon);
        }
    }
}
