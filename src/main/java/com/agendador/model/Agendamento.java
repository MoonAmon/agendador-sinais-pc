package com.agendador.model;

import java.time.LocalTime;
import java.util.Set;
import java.util.HashSet;

/**
 * Modelo de dados para um agendamento de sinal
 */
public class Agendamento {
    
    private Long id;
    private String nome;
    private String caminhoAudio;
    private LocalTime horario;
    private int duracaoSegundos;
    private String dispositivoAudio;
    private Set<DiaSemana> diasSemana;
    private boolean ativo;
    private String observacoes;
    
    public Agendamento() {
        this.diasSemana = new HashSet<>();
        this.ativo = true;
        this.duracaoSegundos = 30; // Duração padrão
    }
    
    public Agendamento(String nome, String caminhoAudio, LocalTime horario, 
                      int duracaoSegundos, String dispositivoAudio) {
        this();
        this.nome = nome;
        this.caminhoAudio = caminhoAudio;
        this.horario = horario;
        this.duracaoSegundos = duracaoSegundos;
        this.dispositivoAudio = dispositivoAudio;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCaminhoAudio() {
        return caminhoAudio;
    }
    
    public void setCaminhoAudio(String caminhoAudio) {
        this.caminhoAudio = caminhoAudio;
    }
    
    public LocalTime getHorario() {
        return horario;
    }
    
    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }
    
    public int getDuracaoSegundos() {
        return duracaoSegundos;
    }
    
    public void setDuracaoSegundos(int duracaoSegundos) {
        this.duracaoSegundos = duracaoSegundos;
    }
    
    public String getDispositivoAudio() {
        return dispositivoAudio;
    }
    
    public void setDispositivoAudio(String dispositivoAudio) {
        this.dispositivoAudio = dispositivoAudio;
    }
    
    public Set<DiaSemana> getDiasSemana() {
        return diasSemana;
    }
    
    public void setDiasSemana(Set<DiaSemana> diasSemana) {
        this.diasSemana = diasSemana;
    }
    
    public void adicionarDiaSemana(DiaSemana dia) {
        this.diasSemana.add(dia);
    }
    
    public void removerDiaSemana(DiaSemana dia) {
        this.diasSemana.remove(dia);
    }
    
    public boolean isAtivo() {
        return ativo;
    }
    
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%ds)", 
            nome != null ? nome : "Sem nome", 
            horario != null ? horario.toString() : "Sem horário",
            duracaoSegundos);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Agendamento that = (Agendamento) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
