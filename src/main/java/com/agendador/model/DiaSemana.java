package com.agendador.model;

/**
 * Enumeração para os dias da semana
 */
public enum DiaSemana {
    DOMINGO(1, "Domingo"),
    SEGUNDA(2, "Segunda-feira"),
    TERCA(3, "Terça-feira"),
    QUARTA(4, "Quarta-feira"),
    QUINTA(5, "Quinta-feira"),
    SEXTA(6, "Sexta-feira"),
    SABADO(7, "Sábado");
    
    private final int valor;
    private final String descricao;
    
    DiaSemana(int valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }
    
    public int getValor() {
        return valor;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    /**
     * Converte valor numérico para DiaSemana
     * @param valor Valor de 1 (Domingo) a 7 (Sábado)
     * @return DiaSemana correspondente
     */
    public static DiaSemana fromValor(int valor) {
        for (DiaSemana dia : values()) {
            if (dia.valor == valor) {
                return dia;
            }
        }
        throw new IllegalArgumentException("Valor inválido para dia da semana: " + valor);
    }
    
    /**
     * Converte java.time.DayOfWeek para DiaSemana
     * @param dayOfWeek DayOfWeek do Java
     * @return DiaSemana correspondente
     */
    public static DiaSemana fromDayOfWeek(java.time.DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case SUNDAY: return DOMINGO;
            case MONDAY: return SEGUNDA;
            case TUESDAY: return TERCA;
            case WEDNESDAY: return QUARTA;
            case THURSDAY: return QUINTA;
            case FRIDAY: return SEXTA;
            case SATURDAY: return SABADO;
            default: throw new IllegalArgumentException("DayOfWeek inválido: " + dayOfWeek);
        }
    }
    
    /**
     * Converte DiaSemana para java.time.DayOfWeek
     * @return DayOfWeek correspondente
     */
    public java.time.DayOfWeek toDayOfWeek() {
        switch (this) {
            case DOMINGO: return java.time.DayOfWeek.SUNDAY;
            case SEGUNDA: return java.time.DayOfWeek.MONDAY;
            case TERCA: return java.time.DayOfWeek.TUESDAY;
            case QUARTA: return java.time.DayOfWeek.WEDNESDAY;
            case QUINTA: return java.time.DayOfWeek.THURSDAY;
            case SEXTA: return java.time.DayOfWeek.FRIDAY;
            case SABADO: return java.time.DayOfWeek.SATURDAY;
            default: throw new IllegalStateException("DiaSemana inválido: " + this);
        }
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}
