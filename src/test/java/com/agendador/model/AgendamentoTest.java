package com.agendador.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;

/**
 * Testes unitários para a classe Agendamento
 */
public class AgendamentoTest {
    
    private Agendamento agendamento;
    
    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
    }
    
    @Test
    void testCriarAgendamentoVazio() {
        assertNotNull(agendamento);
        assertNotNull(agendamento.getDiasSemana());
        assertTrue(agendamento.getDiasSemana().isEmpty());
        assertTrue(agendamento.isAtivo());
        assertEquals(30, agendamento.getDuracaoSegundos());
    }
    
    @Test
    void testCriarAgendamentoComParametros() {
        String nome = "Teste";
        String caminho = "/path/to/audio.wav";
        LocalTime horario = LocalTime.of(8, 30);
        int duracao = 60;
        String dispositivo = "Speakers";
        
        Agendamento ag = new Agendamento(nome, caminho, horario, duracao, dispositivo);
        
        assertEquals(nome, ag.getNome());
        assertEquals(caminho, ag.getCaminhoAudio());
        assertEquals(horario, ag.getHorario());
        assertEquals(duracao, ag.getDuracaoSegundos());
        assertEquals(dispositivo, ag.getDispositivoAudio());
    }
    
    @Test
    void testAdicionarRemoverDiaSemana() {
        agendamento.adicionarDiaSemana(DiaSemana.SEGUNDA);
        agendamento.adicionarDiaSemana(DiaSemana.SEXTA);
        
        assertEquals(2, agendamento.getDiasSemana().size());
        assertTrue(agendamento.getDiasSemana().contains(DiaSemana.SEGUNDA));
        assertTrue(agendamento.getDiasSemana().contains(DiaSemana.SEXTA));
        
        agendamento.removerDiaSemana(DiaSemana.SEGUNDA);
        assertEquals(1, agendamento.getDiasSemana().size());
        assertFalse(agendamento.getDiasSemana().contains(DiaSemana.SEGUNDA));
    }
    
    @Test
    void testToString() {
        agendamento.setNome("Teste");
        agendamento.setHorario(LocalTime.of(10, 30));
        agendamento.setDuracaoSegundos(45);
        
        String expected = "Teste - 10:30 (45s)";
        assertEquals(expected, agendamento.toString());
    }
    
    @Test
    void testEqualsEHashCode() {
        Agendamento ag1 = new Agendamento();
        ag1.setId(1L);
        
        Agendamento ag2 = new Agendamento();
        ag2.setId(1L);
        
        Agendamento ag3 = new Agendamento();
        ag3.setId(2L);
        
        assertEquals(ag1, ag2);
        assertNotEquals(ag1, ag3);
        assertEquals(ag1.hashCode(), ag2.hashCode());
    }
}
