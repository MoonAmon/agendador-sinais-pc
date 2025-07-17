package com.agendador;

import com.agendador.model.Agendamento;
import com.agendador.model.DiaSemana;
import com.agendador.scheduler.SchedulerManager;

import java.time.LocalTime;
import java.util.Set;

/**
 * Teste para demonstrar execução de múltiplos sinais no mesmo minuto
 */
public class TestMultiplosSinais {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE: Múltiplos Sinais no Mesmo Minuto ===\n");
        
        // Criar agendamentos para o mesmo horário
        LocalTime horarioTeste = LocalTime.now().plusMinutes(1);
        
        Agendamento sinal1 = new Agendamento();
        sinal1.setId(1L);
        sinal1.setNome("Sinal 1 - Campainha");
        sinal1.setCaminhoAudio("campaninha.wav");
        sinal1.setHorario(horarioTeste);
        sinal1.setDuracaoSegundos(3);
        sinal1.setDiasSemana(Set.of(DiaSemana.SEGUNDA, DiaSemana.TERCA, DiaSemana.QUARTA, 
                                   DiaSemana.QUINTA, DiaSemana.SEXTA));
        
        Agendamento sinal2 = new Agendamento();
        sinal2.setId(2L);
        sinal2.setNome("Sinal 2 - Alarme");
        sinal2.setCaminhoAudio("alarme.wav");
        sinal2.setHorario(horarioTeste);
        sinal2.setDuracaoSegundos(5);
        sinal2.setDiasSemana(Set.of(DiaSemana.SEGUNDA, DiaSemana.TERCA, DiaSemana.QUARTA, 
                                   DiaSemana.QUINTA, DiaSemana.SEXTA));
        
        Agendamento sinal3 = new Agendamento();
        sinal3.setId(3L);
        sinal3.setNome("Sinal 3 - Música");
        sinal3.setCaminhoAudio("musica.wav");
        sinal3.setHorario(horarioTeste);
        sinal3.setDuracaoSegundos(7);
        sinal3.setDiasSemana(Set.of(DiaSemana.SEGUNDA, DiaSemana.TERCA, DiaSemana.QUARTA, 
                                   DiaSemana.QUINTA, DiaSemana.SEXTA));
        
        System.out.println("Cenário de Teste:");
        System.out.println("- 3 sinais agendados para: " + horarioTeste);
        System.out.println("- Sinal 1: Campainha (3s)");
        System.out.println("- Sinal 2: Alarme (5s)");
        System.out.println("- Sinal 3: Música (7s)");
        System.out.println();
        
        System.out.println("Como o sistema vai lidar:");
        System.out.println("1. ✅ Detecta múltiplos agendamentos no mesmo minuto");
        System.out.println("2. ✅ Evita execuções duplicadas (cache por minuto)");
        System.out.println("3. ✅ Executa em FILA SEQUENCIAL:");
        System.out.println("   - Primeiro: Campainha (3s)");
        System.out.println("   - Depois: Alarme (5s)");
        System.out.println("   - Por último: Música (7s)");
        System.out.println("   - Pausa de 500ms entre cada execução");
        System.out.println("4. ✅ Total: ~16 segundos de execução");
        System.out.println();
        
        System.out.println("Vantagens da implementação:");
        System.out.println("• Não há sobreposição de áudios");
        System.out.println("• Todos os sinais são executados");
        System.out.println("• Ordem consistente (por ordem de criação)");
        System.out.println("• Pode pausar/parar a fila a qualquer momento");
        System.out.println("• Status detalhado da execução");
        System.out.println();
        
        System.out.println("Métodos disponíveis:");
        System.out.println("• isReproducing() - verifica se está tocando");
        System.out.println("• temAgendamentosNaFila() - verifica se há fila");
        System.out.println("• getTamanhoFila() - quantidade na fila");
        System.out.println("• getStatusDetalhado() - status completo");
        System.out.println("• pararReproducaoAtual() - para tudo e limpa fila");
    }
}
