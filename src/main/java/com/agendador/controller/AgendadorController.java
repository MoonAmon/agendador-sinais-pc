package com.agendador.controller;

import com.agendador.database.DatabaseManager;
import com.agendador.model.Agendamento;
import com.agendador.scheduler.SchedulerManager;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Controlador principal da aplicação
 * Coordena as operações entre a interface, banco de dados e agendador
 */
public class AgendadorController implements SchedulerManager.SchedulerListener {
    
    private final DatabaseManager databaseManager;
    private final SchedulerManager schedulerManager;
    private final List<AgendadorListener> listeners;
    
    public AgendadorController() {
        this.databaseManager = DatabaseManager.getInstance();
        this.schedulerManager = new SchedulerManager();
        this.listeners = new ArrayList<>();
        
        // Configurar listener do scheduler
        this.schedulerManager.setSchedulerListener(this);
        
        // Iniciar agendador automaticamente
        iniciarAgendador();
    }
    
    /**
     * Inicia o agendador
     */
    public void iniciarAgendador() {
        schedulerManager.iniciar();
    }
    
    /**
     * Para o agendador
     */
    public void pararAgendador() {
        schedulerManager.parar();
    }
    
    /**
     * Verifica se o agendador está ativo
     */
    public boolean isAgendadorAtivo() {
        return schedulerManager.isRunning();
    }
    
    /**
     * Para reprodução atual
     */
    public void pararReproducaoAtual() {
        schedulerManager.pararReproducaoAtual();
    }
    
    /**
     * Verifica se está reproduzindo áudio
     */
    public boolean isReproducing() {
        return schedulerManager.isReproducing();
    }
    
    /**
     * Salva um novo agendamento
     */
    public Long salvarAgendamento(Agendamento agendamento) throws SQLException {
        Long id = databaseManager.salvarAgendamento(agendamento);
        notificarListeners(listener -> listener.onAgendamentoSalvo(agendamento));
        return id;
    }
    
    /**
     * Atualiza um agendamento existente
     */
    public void atualizarAgendamento(Agendamento agendamento) throws SQLException {
        databaseManager.atualizarAgendamento(agendamento);
        notificarListeners(listener -> listener.onAgendamentoAtualizado(agendamento));
    }
    
    /**
     * Remove um agendamento
     */
    public void removerAgendamento(Long id) throws SQLException {
        databaseManager.removerAgendamento(id);
        notificarListeners(listener -> listener.onAgendamentoRemovido(id));
    }
    
    /**
     * Busca todos os agendamentos
     */
    public List<Agendamento> buscarTodosAgendamentos() throws SQLException {
        return databaseManager.buscarTodosAgendamentos();
    }
    
    /**
     * Busca agendamentos ativos
     */
    public List<Agendamento> buscarAgendamentosAtivos() throws SQLException {
        return databaseManager.buscarAgendamentosAtivos();
    }
    
    /**
     * Executa um agendamento imediatamente (para teste)
     */
    public void testarAgendamento(Agendamento agendamento) {
        schedulerManager.executarAgora(agendamento);
    }
    
    /**
     * Obtém lista de dispositivos de áudio
     */
    public List<String> obterDispositivosAudio() {
        return schedulerManager.obterDispositivosAudio();
    }
    
    /**
     * Valida um arquivo de áudio
     */
    public boolean validarArquivoAudio(String caminho) {
        return schedulerManager.validarArquivoAudio(caminho);
    }
    
    /**
     * Adiciona um listener para eventos do controlador
     */
    public void adicionarListener(AgendadorListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove um listener
     */
    public void removerListener(AgendadorListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifica todos os listeners
     */
    private void notificarListeners(ListenerNotification notification) {
        for (AgendadorListener listener : listeners) {
            try {
                notification.notificar(listener);
            } catch (Exception e) {
                System.err.println("Erro ao notificar listener: " + e.getMessage());
            }
        }
    }
    
    // Implementação do SchedulerManager.SchedulerListener
    
    @Override
    public void onSchedulerStarted() {
        notificarListeners(listener -> listener.onAgendadorIniciado());
    }
    
    @Override
    public void onSchedulerStopped() {
        notificarListeners(listener -> listener.onAgendadorParado());
    }
    
    @Override
    public void onAgendamentoExecutado(Agendamento agendamento) {
        notificarListeners(listener -> listener.onAgendamentoExecutado(agendamento));
    }
    
    @Override
    public void onReproducaoConcluida(Agendamento agendamento) {
        notificarListeners(listener -> listener.onReproducaoConcluida(agendamento));
    }
    
    @Override
    public void onError(String mensagem) {
        notificarListeners(listener -> listener.onErro(mensagem));
    }
    
    /**
     * Interface funcional para notificação de listeners
     */
    @FunctionalInterface
    private interface ListenerNotification {
        void notificar(AgendadorListener listener);
    }
    
    /**
     * Interface para receber eventos do controlador
     */
    public interface AgendadorListener {
        default void onAgendadorIniciado() {}
        default void onAgendadorParado() {}
        default void onAgendamentoSalvo(Agendamento agendamento) {}
        default void onAgendamentoAtualizado(Agendamento agendamento) {}
        default void onAgendamentoRemovido(Long id) {}
        default void onAgendamentoExecutado(Agendamento agendamento) {}
        default void onReproducaoConcluida(Agendamento agendamento) {}
        default void onErro(String mensagem) {}
    }
}
