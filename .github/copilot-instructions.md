<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Agendador de Sinais - Custom Instructions

Este é um projeto Java para agendamento de sinais de áudio com as seguintes características:

## Arquitetura do Projeto
- **Interface Gráfica**: Swing para UI desktop
- **Persistência**: SQLite para armazenamento local
- **Áudio**: Java Sound API para reprodução
- **Agendamento**: ScheduledExecutorService para tarefas
- **System Tray**: Para execução em segundo plano

## Padrões de Código
- Use padrão MVC (Model-View-Controller)
- Implemente Singleton para DatabaseManager
- Use Observer pattern para atualizações de UI
- Aplique tratamento adequado de exceções
- Mantenha logs para debugging

## Funcionalidades Principais
1. Seleção de arquivo de música (.wav, .mp3, .aiff)
2. Configuração de duração (segundos)
3. Seleção de dispositivo de áudio
4. Agendamento por dias da semana
5. Execução em background
6. Acesso via system tray
7. Persistência de configurações

## Estrutura de Packages
- `com.agendador.model` - Entidades e dados
- `com.agendador.view` - Interface gráfica
- `com.agendador.controller` - Lógica de negócio
- `com.agendador.database` - Acesso a dados
- `com.agendador.audio` - Reprodução de áudio
- `com.agendador.scheduler` - Agendamento de tarefas
- `com.agendador.tray` - System tray integration
