package com.agendador.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.DayOfWeek;

/**
 * Testes unitários para o enum DiaSemana
 */
public class DiaSemanaTest {
    
    @Test
    void testValores() {
        assertEquals(1, DiaSemana.DOMINGO.getValor());
        assertEquals(2, DiaSemana.SEGUNDA.getValor());
        assertEquals(3, DiaSemana.TERCA.getValor());
        assertEquals(4, DiaSemana.QUARTA.getValor());
        assertEquals(5, DiaSemana.QUINTA.getValor());
        assertEquals(6, DiaSemana.SEXTA.getValor());
        assertEquals(7, DiaSemana.SABADO.getValor());
    }
    
    @Test
    void testDescricoes() {
        assertEquals("Domingo", DiaSemana.DOMINGO.getDescricao());
        assertEquals("Segunda-feira", DiaSemana.SEGUNDA.getDescricao());
        assertEquals("Terça-feira", DiaSemana.TERCA.getDescricao());
        assertEquals("Quarta-feira", DiaSemana.QUARTA.getDescricao());
        assertEquals("Quinta-feira", DiaSemana.QUINTA.getDescricao());
        assertEquals("Sexta-feira", DiaSemana.SEXTA.getDescricao());
        assertEquals("Sábado", DiaSemana.SABADO.getDescricao());
    }
    
    @Test
    void testFromValor() {
        assertEquals(DiaSemana.DOMINGO, DiaSemana.fromValor(1));
        assertEquals(DiaSemana.SEGUNDA, DiaSemana.fromValor(2));
        assertEquals(DiaSemana.SABADO, DiaSemana.fromValor(7));
        
        assertThrows(IllegalArgumentException.class, () -> DiaSemana.fromValor(0));
        assertThrows(IllegalArgumentException.class, () -> DiaSemana.fromValor(8));
    }
    
    @Test
    void testFromDayOfWeek() {
        assertEquals(DiaSemana.DOMINGO, DiaSemana.fromDayOfWeek(DayOfWeek.SUNDAY));
        assertEquals(DiaSemana.SEGUNDA, DiaSemana.fromDayOfWeek(DayOfWeek.MONDAY));
        assertEquals(DiaSemana.TERCA, DiaSemana.fromDayOfWeek(DayOfWeek.TUESDAY));
        assertEquals(DiaSemana.QUARTA, DiaSemana.fromDayOfWeek(DayOfWeek.WEDNESDAY));
        assertEquals(DiaSemana.QUINTA, DiaSemana.fromDayOfWeek(DayOfWeek.THURSDAY));
        assertEquals(DiaSemana.SEXTA, DiaSemana.fromDayOfWeek(DayOfWeek.FRIDAY));
        assertEquals(DiaSemana.SABADO, DiaSemana.fromDayOfWeek(DayOfWeek.SATURDAY));
    }
    
    @Test
    void testToDayOfWeek() {
        assertEquals(DayOfWeek.SUNDAY, DiaSemana.DOMINGO.toDayOfWeek());
        assertEquals(DayOfWeek.MONDAY, DiaSemana.SEGUNDA.toDayOfWeek());
        assertEquals(DayOfWeek.TUESDAY, DiaSemana.TERCA.toDayOfWeek());
        assertEquals(DayOfWeek.WEDNESDAY, DiaSemana.QUARTA.toDayOfWeek());
        assertEquals(DayOfWeek.THURSDAY, DiaSemana.QUINTA.toDayOfWeek());
        assertEquals(DayOfWeek.FRIDAY, DiaSemana.SEXTA.toDayOfWeek());
        assertEquals(DayOfWeek.SATURDAY, DiaSemana.SABADO.toDayOfWeek());
    }
    
    @Test
    void testToString() {
        assertEquals("Domingo", DiaSemana.DOMINGO.toString());
        assertEquals("Segunda-feira", DiaSemana.SEGUNDA.toString());
    }
}
