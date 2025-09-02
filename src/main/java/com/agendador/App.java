package com.agendador;

import com.agendador.database.DatabaseManager;
import com.agendador.util.SingleInstanceManager;
import com.agendador.view.MainWindow;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.sql.SQLException;

/**
 * Classe principal da aplicação Agendador de Sinais
 */
public class App {
    
    public static void main(String[] args) {
        // 1. Verificar instância única ANTES de qualquer inicialização
        SingleInstanceManager singleInstance = SingleInstanceManager.getInstance();
        
        if (!singleInstance.tryLock()) {
            System.err.println("Outra instância já está executando!");
            singleInstance.showAlreadyRunningMessage();
            System.exit(1);
            return;
        }
        
        System.out.println("✅ Instância única confirmada - iniciando aplicação...");
        
        // 2. Configurar shutdown hook para limpeza automática
        singleInstance.setupShutdownHook();
        
        // 3. Configurar look and feel nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Look and Feel configurado: " + UIManager.getLookAndFeel().getName());
        } catch (ClassNotFoundException | InstantiationException | 
                IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Erro ao configurar Look and Feel: " + e.getMessage());
        }
        
        // 4. Configurar encoding UTF-8
        System.setProperty("file.encoding", "UTF-8");
        
        // 5. Inicializar banco de dados
        DatabaseManager dbManager = DatabaseManager.getInstance();
        try {
            dbManager.initializeDatabase();
            System.out.println("✅ Banco de dados inicializado com sucesso");
            
            // Mostrar informações do banco
            String dbInfo = dbManager.obterInformacoesBanco();
            System.out.println("📊 Informações do banco de dados:\n" + dbInfo);
            
        } catch (SQLException e) {
            System.err.println("❌ Erro fatal ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar erro ao usuário e sair
            javax.swing.JOptionPane.showMessageDialog(
                null,
                "Erro ao inicializar banco de dados:\n" + e.getMessage() + 
                "\n\nA aplicação será encerrada.",
                "Erro Fatal",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
            
            singleInstance.releaseLocks();
            System.exit(1);
            return;
        }
        
        // 6. Inicializar interface gráfica na EDT
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("🚀 Iniciando interface gráfica...");
                
                // Mostrar informações da instância
                System.out.println("📋 " + singleInstance.getInstanceInfo());
                
                MainWindow mainWindow = new MainWindow();
                mainWindow.setVisible(true);
                
                System.out.println("✅ Aplicação iniciada com sucesso!");
                System.out.println("💡 Para encerrar, feche a janela ou use o system tray");
                
            } catch (Exception e) {
                System.err.println("❌ Erro fatal ao inicializar interface: " + e.getMessage());
                e.printStackTrace();
                
                javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "Erro ao inicializar interface gráfica:\n" + e.getMessage(),
                    "Erro Fatal",
                    javax.swing.JOptionPane.ERROR_MESSAGE
                );
                
                singleInstance.releaseLocks();
                System.exit(1);
            }
        });
    }
}