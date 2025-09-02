package com.agendador.scheduler;

import com.agendador.audio.AudioManager;
import com.agendador.database.DatabaseManager;
import com.agendador.model.Agendamento;
import com.agendador.model.DiaSemana;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Gerenciador de agendamento de sinais
 * Executa verificações periódicas e dispara reproduções de áudio
 */
public class SchedulerManager {
    
    private ScheduledExecutorService scheduler;
    private final AudioManager audioManager;
    private final DatabaseManager databaseManager;
    private boolean isRunning = false;
    
    // Controle de execução de múltiplos agendamentos
    private final Set<String> agendamentosExecutadosNoMinuto = new HashSet<>();
    private LocalDateTime ultimaVerificacao = null;
    private final Queue<Agendamento> filaExecucao = new LinkedList<>();
    private boolean executandoFila = false;
    
    // Listeners para notificações
    private SchedulerListener listener;
    
    public SchedulerManager() {
        this.audioManager = new AudioManager();
        this.databaseManager = DatabaseManager.getInstance();
        this.scheduler = Executors.newScheduledThreadPool(2);
    }
    
    /**
     * Inicia o agendador
     */
    public void iniciar() {
        if (isRunning) {
            return;
        }

        if (scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(2);
        }
        
        isRunning = true;
        // Verificar agendamentos a cada segundo
        scheduler.scheduleAtFixedRate(this::verificarAgendamentos, 0, 1, TimeUnit.SECONDS);

        System.out.println("Agendador iniciado - verificações a cada segundo");

        if (listener != null) {
            listener.onSchedulerStarted();
        }
    }
    
    /**
     * Para o agendador
     */
    public void parar() {
        if (!isRunning) {
            return;
        }
        
        isRunning = false;
        
        // Parar reprodução e limpar fila
        audioManager.pararReproducao();
        filaExecucao.clear();
        executandoFila = false;
        agendamentosExecutadosNoMinuto.clear();
        
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Agendador parado - fila limpa");
        
        if (listener != null) {
            listener.onSchedulerStopped();
        }
    }
    
    /**
     * Verifica se há agendamentos para executar no momento atual
     */
    private void verificarAgendamentos() {
        try {
            LocalDateTime agora = LocalDateTime.now();
            LocalTime horaAtual = agora.toLocalTime();
            DiaSemana diaAtual = DiaSemana.fromDayOfWeek(agora.getDayOfWeek());
            
            // Limpar cache de execuções se mudou o minuto
            if (ultimaVerificacao == null || 
                ultimaVerificacao.getMinute() != agora.getMinute() ||
                ultimaVerificacao.getHour() != agora.getHour()) {
                
                agendamentosExecutadosNoMinuto.clear();
                ultimaVerificacao = agora;
                System.out.println("Nova verificação para: " + agora.toLocalTime().toString());
            }
            
            List<Agendamento> agendamentos = databaseManager.buscarAgendamentosAtivos();
            List<Agendamento> agendamentosParaExecutar = new ArrayList<>();
            
            for (Agendamento agendamento : agendamentos) {
                if (deveExecutar(agendamento, horaAtual, diaAtual)) {
                    String chaveAgendamento = agendamento.getId() + "_" + horaAtual.getHour() + "_" + horaAtual.getMinute();
                    
                    // Verificar se já foi executado neste minuto
                    if (!agendamentosExecutadosNoMinuto.contains(chaveAgendamento)) {
                        agendamentosParaExecutar.add(agendamento);
                        agendamentosExecutadosNoMinuto.add(chaveAgendamento);
                    }
                }
            }
            
            // Se há múltiplos agendamentos, executar em fila
            if (agendamentosParaExecutar.size() > 1) {
                System.out.println("Múltiplos agendamentos encontrados (" + agendamentosParaExecutar.size() + ") - executando em sequência");
                executarFilaAgendamentos(agendamentosParaExecutar);
            } else if (agendamentosParaExecutar.size() == 1) {
                executarAgendamento(agendamentosParaExecutar.get(0));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao verificar agendamentos: " + e.getMessage());
            if (listener != null) {
                listener.onError("Erro ao verificar agendamentos: " + e.getMessage());
            }
        }
    }
    
    /**
     * Verifica se um agendamento deve ser executado
     */
    private boolean deveExecutar(Agendamento agendamento, LocalTime horaAtual, DiaSemana diaAtual) {
        // Verificar se é o dia correto
        if (!agendamento.getDiasSemana().contains(diaAtual)) {
            return false;
        }
        
        // Verificar se é a hora correta (com tolerância de 1 minuto)
        LocalTime horarioAgendado = agendamento.getHorario();
        
        // Considerar execução se estivermos no minuto exato
        return horaAtual.getHour() == horarioAgendado.getHour() && 
               horaAtual.getMinute() == horarioAgendado.getMinute();
    }
    
    /**
     * Executa uma fila de agendamentos em sequência
     */
    private void executarFilaAgendamentos(List<Agendamento> agendamentos) {
        if (executandoFila) {
            System.out.println("Fila já está sendo executada - adicionando agendamentos à fila");
            filaExecucao.addAll(agendamentos);
            return;
        }
        
        executandoFila = true;
        filaExecucao.addAll(agendamentos);
        
        // Executar em thread separada para não bloquear o scheduler
        scheduler.execute(() -> {
            try {
                while (!filaExecucao.isEmpty() && isRunning) {
                    Agendamento agendamento = filaExecucao.poll();
                    if (agendamento != null) {
                        System.out.println("Executando da fila: " + agendamento.getNome() + 
                                         " (restam " + filaExecucao.size() + " na fila)");
                        
                        // Executar agendamento e aguardar conclusão
                        try {
                            audioManager.reproduzirAudio(
                                agendamento.getCaminhoAudio(), 
                                agendamento.getDuracaoSegundos(), 
                                agendamento.getDispositivoAudio()
                            ).get(); // Aguardar conclusão
                            
                            if (listener != null) {
                                listener.onAgendamentoExecutado(agendamento);
                                listener.onReproducaoConcluida(agendamento);
                            }
                            
                            System.out.println("Agendamento concluído: " + agendamento.getNome());
                            
                            // Pequena pausa entre execuções
                            Thread.sleep(500);
                            
                        } catch (Exception e) {
                            String erro = "Erro na execução em fila: " + e.getMessage();
                            System.err.println(erro);
                            if (listener != null) {
                                listener.onError(erro);
                            }
                        }
                    }
                }
            } finally {
                executandoFila = false;
                System.out.println("Execução da fila concluída");
            }
        });
    }
    
    /**
     * Executa um agendamento específico
     */
    private void executarAgendamento(Agendamento agendamento) {
        System.out.println("Executando agendamento: " + agendamento.getNome());
        
        if (listener != null) {
            listener.onAgendamentoExecutado(agendamento);
        }
        
        // Verificar se o arquivo existe
        if (!audioManager.validarArquivoAudio(agendamento.getCaminhoAudio())) {
            String erro = "Arquivo de áudio não encontrado: " + agendamento.getCaminhoAudio();
            System.err.println(erro);
            if (listener != null) {
                listener.onError(erro);
            }
            return;
        }
        
        // Reproduzir áudio de forma assíncrona
        audioManager.reproduzirAudio(
            agendamento.getCaminhoAudio(), 
            agendamento.getDuracaoSegundos(), 
            agendamento.getDispositivoAudio()
        ).whenComplete((result, throwable) -> {
            if (throwable != null) {
                String erro = "Erro na reprodução: " + throwable.getMessage();
                System.err.println(erro);
                if (listener != null) {
                    listener.onError(erro);
                }
            } else {
                System.out.println("Reprodução concluída: " + agendamento.getNome());
                if (listener != null) {
                    listener.onReproducaoConcluida(agendamento);
                }
            }
        });
    }
    
    /**
     * Executa um agendamento imediatamente (para teste)
     */
    public void executarAgora(Agendamento agendamento) {
        scheduler.execute(() -> executarAgendamento(agendamento));
    }
    
    /**
     * Para qualquer reprodução em andamento
     */
    public void pararReproducaoAtual() {
        audioManager.pararReproducao();
        
        // Limpar fila se necessário
        if (executandoFila) {
            filaExecucao.clear();
            executandoFila = false;
            System.out.println("Fila de execução interrompida e limpa");
        }
    }
    
    /**
     * Verifica se está reproduzindo áudio
     */
    public boolean isReproducing() {
        return audioManager.isPlaying();
    }
    
    /**
     * Verifica se há agendamentos na fila
     */
    public boolean temAgendamentosNaFila() {
        return !filaExecucao.isEmpty() || executandoFila;
    }
    
    /**
     * Obtém o número de agendamentos na fila
     */
    public int getTamanhoFila() {
        return filaExecucao.size();
    }
    
    /**
     * Obtém informações sobre o estado atual
     */
    public String getStatusDetalhado() {
        StringBuilder status = new StringBuilder();
        status.append("Agendador: ").append(isRunning ? "ATIVO" : "PAUSADO").append("\n");
        status.append("Reproduzindo: ").append(isReproducing() ? "SIM" : "NÃO").append("\n");
        status.append("Fila: ").append(getTamanhoFila()).append(" agendamentos");
        
        if (executandoFila) {
            status.append(" (executando)");
        }
        
        return status.toString();
    }
    
    /**
     * Verifica se o agendador está em execução
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Define um listener para notificações
     */
    public void setSchedulerListener(SchedulerListener listener) {
        this.listener = listener;
    }
    
    /**
     * Obtém lista de dispositivos de áudio disponíveis
     */
    public List<String> obterDispositivosAudio() {
        return audioManager.obterDispositivosAudio();
    }
    
    /**
     * Valida um arquivo de áudio
     */
    public boolean validarArquivoAudio(String caminho) {
        return audioManager.validarArquivoAudio(caminho);
    }
    
    /**
     * Interface para receber notificações do agendador
     */
    public interface SchedulerListener {
        void onSchedulerStarted();
        void onSchedulerStopped();
        void onAgendamentoExecutado(Agendamento agendamento);
        void onReproducaoConcluida(Agendamento agendamento);
        void onError(String mensagem);
        
        // Novos métodos para fila
        default void onFilaIniciada(int tamanho) {}
        default void onFilaConcluida() {}
        default void onStatusChanged(String status) {}
    }
}
