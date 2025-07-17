package com.agendador;

import com.agendador.controller.AgendadorController;
import com.agendador.database.DatabaseManager;
import com.agendador.tray.SystemTrayManager;
import com.agendador.view.MainWindow;
import javax.swing.*;
import java.awt.*;

/**
 * Classe principal do Agendador de Sinais
 * Aplicação desktop para agendamento de reprodução de áudios
 */
public class App {
    
    public static void main(String[] args) {
        // Configurar Look and Feel para melhor aparência
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Erro ao configurar Look and Feel: " + e.getMessage());
        }
        
        // Verificar se o sistema suporta system tray
        if (!SystemTray.isSupported()) {
            System.err.println("System Tray não é suportado neste sistema.");
        }
        
        // Executar na EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                // Inicializar banco de dados
                DatabaseManager.getInstance().initializeDatabase();
                
                // Criar e inicializar a aplicação
                AgendadorController controller = new AgendadorController();
                MainWindow mainWindow = new MainWindow(controller);
                
                // Configurar system tray
                SystemTrayManager trayManager = new SystemTrayManager(mainWindow, controller);
                trayManager.initializeSystemTray();
                mainWindow.setSystemTrayManager(trayManager);
                
                // Exibir janela principal
                mainWindow.setVisible(true);
                
                System.out.println("Agendador de Sinais iniciado com sucesso!");
                
            } catch (Exception e) {
                System.err.println("Erro ao inicializar aplicação: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Erro ao inicializar aplicação:\n" + e.getMessage(),
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
