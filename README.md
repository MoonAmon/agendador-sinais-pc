# Agendador de Sinais

# Agendador de Sinais

Sistema completo para agendamento de reprodução de áudios com interface gráfica, execução em segundo plano e persistência de dados.

## 🎵 Funcionalidades

- **Seleção de Música**: Interface para escolher arquivos de áudio (.wav, .mp3, .aiff)
- **Controle de Duração**: Configuração dos segundos de reprodução
- **Dispositivos de Áudio**: Seleção do dispositivo de saída de áudio
- **Agendamento Semanal**: Configuração dos dias da semana para repetição
- **Execução em Segundo Plano**: Serviço que roda mesmo com a aplicação minimizada
- **System Tray**: Acesso pela barra de tarefas para configuração rápida
- **Persistência**: Banco de dados SQLite local para salvar configurações

## 🛠️ Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Swing** - Interface gráfica desktop
- **SQLite** - Banco de dados local
- **Java Sound API** - Reprodução de áudio
- **Maven** - Gerenciamento de dependências

## 📋 Pré-requisitos

### Obrigatório
- **Java 21** - Eclipse Adoptium (OpenJDK)
  - Download: https://adoptium.net/temurin/releases/
  - Versão instalada detectada: `Java 21.0.7 (Temurin)`

### Opcional
- Maven 3.9+ (incluído via Maven Wrapper)

## 🚀 Como Executar

### Opção 1: Usando o Script de Execução (Recomendado)

1. **Configure o JAVA_HOME** (necessário apenas uma vez):
   ```cmd
   .\setup-java.cmd
   ```

2. **Execute a aplicação**:
   ```cmd
   .\executar.cmd
   ```

### Opção 2: Usando Maven (Desenvolvimento)

1. **Configure JAVA_HOME na sessão atual**:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot"
   ```

2. **Compile e execute**:
   ```cmd
   .\mvnw.cmd clean compile exec:java -Dexec.mainClass="com.agendador.App"
   ```

### Opção 3: JAR Executável

1. **Gere o JAR**:
   ```cmd
   .\mvnw.cmd clean package
   ```

2. **Execute o JAR**:
   ```cmd
   java -jar target\agendador-sinais-1.0.0.jar
   ```

### Opção 4: Debug no VS Code (Desenvolvimento)

1. **Configure o ambiente**:
   - Instale a extensão "Extension Pack for Java" no VS Code
   - Configure o JAVA_HOME se necessário

2. **Execute em modo debug**:
   - Abra o projeto no VS Code
   - Vá para a aba "Run and Debug" (Ctrl+Shift+D)
   - Selecione "Agendador de Sinais"
   - Clique em "Start Debugging" (F5)

3. **Recursos de debug**:
   - Breakpoints
   - Inspeção de variáveis
   - Stack trace
   - Console integrado

## 📖 Como Usar

### 1. Interface Principal
- **Lista de Agendamentos**: Visualize todos os agendamentos configurados
- **Formulário**: Crie ou edite agendamentos
- **Controles**: Inicie/pause o agendador

### 2. Criando um Agendamento

1. **Nome**: Digite um nome identificador
2. **Arquivo de Áudio**: Clique em "..." para selecionar o arquivo
3. **Horário**: Configure hora e minuto
4. **Duração**: Defina quantos segundos o áudio deve tocar
5. **Dispositivo**: Escolha o dispositivo de saída (opcional)
6. **Dias da Semana**: Marque os dias para repetição
7. **Observações**: Adicione notas opcionais
8. **Salvar**: Clique para confirmar

### 3. System Tray (Barra de Tarefas)

Quando minimizada, a aplicação fica disponível na barra de tarefas com:
- **Mostrar Agendador**: Volta à janela principal
- **Status**: Mostra o estado atual (Ativo/Pausado/Reproduzindo)
- **Pausar/Iniciar**: Controla o agendador
- **Parar Reprodução**: Para áudio em execução
- **Sair**: Fecha a aplicação

### 4. Testando Agendamentos

- Selecione um agendamento na lista
- Clique em "Testar" para reproduzir imediatamente
- O áudio será reproduzido pela duração configurada

## 📁 Estrutura do Projeto

```
agendador/
├── src/main/java/com/agendador/
│   ├── App.java                    # Classe principal
│   ├── model/                      # Modelos de dados
│   │   ├── Agendamento.java
│   │   └── DiaSemana.java
│   ├── view/                       # Interface gráfica
│   │   └── MainWindow.java
│   ├── controller/                 # Lógica de negócio
│   │   └── AgendadorController.java
│   ├── database/                   # Acesso a dados
│   │   └── DatabaseManager.java
│   ├── audio/                      # Reprodução de áudio
│   │   └── AudioManager.java
│   ├── scheduler/                  # Agendamento
│   │   └── SchedulerManager.java
│   └── tray/                       # System tray
│       └── SystemTrayManager.java
├── src/test/java/                  # Testes unitários
├── target/                         # Arquivos compilados
├── pom.xml                         # Configuração Maven
├── mvnw.cmd                        # Maven Wrapper
├── setup-java.cmd                  # Script de configuração
├── executar.cmd                    # Script de execução
└── README.md                       # Este arquivo
```

## 🎵 Formatos de Áudio Suportados

- **WAV** - Recomendado para melhor compatibilidade
- **MP3** - Suporte através do Java Sound API
- **AIFF** - Formato alternativo
- **AU** - Formato básico

## 💾 Banco de Dados

A aplicação cria automaticamente um arquivo `agendador.db` (SQLite) no diretório da aplicação com as seguintes tabelas:

- **agendamentos**: Dados principais dos agendamentos
- **agendamento_dias**: Relacionamento com dias da semana

## 🔧 Solução de Problemas

### Erro: "JAVA_HOME not found"
```cmd
# Execute o script de configuração
.\setup-java.cmd

# Ou configure manualmente
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot
```

### Erro: "System Tray não suportado"
- Verifique se o sistema operacional suporta system tray
- Execute como administrador se necessário

### Erro: "Arquivo de áudio não suportado"
- Verifique se o arquivo não está corrompido
- Teste com arquivo WAV simples
- Verifique se o formato é suportado

### Problemas de Áudio
- Verifique se o dispositivo de áudio está funcionando
- Teste com dispositivo padrão primeiro
- Verifique permissões do sistema

## 🧪 Testes

Execute os testes unitários:
```cmd
.\mvnw.cmd test
```

## 📦 Build de Produção

Para criar uma versão para distribuição:
```cmd
.\mvnw.cmd clean package
```

O JAR executável será gerado em: `target/agendador-sinais-1.0.0.jar`

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 📞 Suporte

Para problemas ou sugestões:
- Abra uma issue no repositório
- Verifique a documentação acima
- Execute o script de configuração `setup-java.cmd`

## 🏗️ Desenvolvimento

### Adicionando Nova Funcionalidade

1. **Model**: Defina entidades em `src/main/java/com/agendador/model/`
2. **Database**: Atualize schema em `DatabaseManager.java`
3. **Controller**: Implemente lógica em `AgendadorController.java`
4. **View**: Adicione interface em `MainWindow.java`
5. **Testes**: Crie testes em `src/test/java/`

### Padrões de Código

- Use padrão MVC (Model-View-Controller)
- Implemente Singleton para gerenciadores
- Use Observer pattern para atualizações de UI
- Trate exceções adequadamente
- Mantenha logs para debugging

---

**Agendador de Sinais v1.0** - Sistema completo para agendamento de áudios 🎵

## 🎵 Funcionalidades

- **Seleção de Música**: Interface gráfica para escolher arquivos de áudio (.wav, .mp3, .aiff, .au)
- **Controle de Duração**: Configuração precisa dos segundos de reprodução
- **Dispositivos de Áudio**: Seleção do dispositivo de saída de áudio
- **Agendamento Semanal**: Configuração dos dias da semana para repetição
- **Execução em Segundo Plano**: Continua funcionando mesmo quando minimizado
- **System Tray**: Acesso rápido via barra de tarefas do sistema
- **Persistência**: Armazenamento local em banco SQLite
- **Interface Amigável**: Interface gráfica intuitiva com Swing

## 🚀 Tecnologias Utilizadas

- **Java 11+** - Linguagem principal
- **Swing** - Interface gráfica
- **SQLite** - Banco de dados local
- **Java Sound API** - Reprodução de áudio
- **Maven** - Gerenciamento de dependências

## 📋 Pré-requisitos

- Java 11 ou superior
- Maven 3.6+
- Sistema operacional com suporte ao System Tray (Windows, Linux, macOS)

## 🔧 Instalação e Execução

### 1. Clone o repositório
```bash
git clone <url-do-repositorio>
cd agendador
```

### 2. Compile o projeto
```bash
mvn clean compile
```

### 3. Execute a aplicação
```bash
mvn exec:java -Dexec.mainClass="com.agendador.App"
```

### 4. Ou gere o JAR executável
```bash
mvn clean package
java -jar target/agendador-sinais-1.0.0.jar
```

## 📱 Como Usar

### Criando um Agendamento

1. **Nome**: Digite um nome identificador para o agendamento
2. **Arquivo de Áudio**: Clique em "..." para selecionar o arquivo de música
3. **Horário**: Configure hora e minuto para execução
4. **Duração**: Defina por quantos segundos o áudio será reproduzido
5. **Dispositivo**: Escolha o dispositivo de áudio (opcional)
6. **Dias da Semana**: Marque os dias em que deve repetir
7. **Observações**: Adicione comentários (opcional)
8. Clique em **Salvar**

### Gerenciando Agendamentos

- **Editar**: Selecione um agendamento na lista e clique em "Editar"
- **Remover**: Selecione e clique em "Remover" 
- **Testar**: Clique em "Testar" para reproduzir imediatamente
- **Pausar/Iniciar**: Controle o agendador com o botão superior

### System Tray

Quando minimizada, a aplicação fica acessível na barra de tarefas:
- **Mostrar Agendador**: Restaura a janela principal
- **Status**: Mostra o estado atual (Ativo/Pausado/Reproduzindo)
- **Pausar/Iniciar Agendador**: Controle rápido
- **Parar Reprodução**: Para áudio em execução
- **Sair**: Fecha completamente a aplicação

## 🏗️ Arquitetura

O projeto segue o padrão MVC (Model-View-Controller):

```
src/main/java/com/agendador/
├── App.java                 # Classe principal
├── model/                   # Modelos de dados
│   ├── Agendamento.java     # Entidade agendamento
│   └── DiaSemana.java       # Enum dias da semana
├── view/                    # Interface gráfica
│   └── MainWindow.java      # Janela principal
├── controller/              # Lógica de negócio
│   └── AgendadorController.java
├── database/                # Acesso a dados
│   └── DatabaseManager.java # Gerenciador SQLite
├── audio/                   # Sistema de áudio
│   └── AudioManager.java    # Reprodução de som
├── scheduler/               # Agendamento
│   └── SchedulerManager.java # Gerenciador de tarefas
└── tray/                    # System tray
    └── SystemTrayManager.java
```

## 💾 Banco de Dados

A aplicação cria automaticamente um banco SQLite (`agendador.db`) com as tabelas:

- **agendamentos**: Dados principais dos agendamentos
- **agendamento_dias**: Relacionamento com dias da semana

## 🎵 Formatos de Áudio Suportados

- WAV (recomendado)
- MP3
- AIFF
- AU

## ⚙️ Configurações

### Dispositivos de Áudio
A aplicação detecta automaticamente todos os dispositivos de áudio disponíveis no sistema.

### Agendamento
- Verificação a cada minuto
- Execução precisa no horário configurado
- Suporte a múltiplos agendamentos simultâneos

## 🐛 Solução de Problemas

### Arquivo de áudio não reproduz
- Verifique se o arquivo existe no caminho especificado
- Confirme se o formato é suportado
- Teste com um arquivo WAV pequeno

### System Tray não aparece
- Verifique se o sistema operacional suporta System Tray
- Execute como administrador se necessário

### Erro de banco de dados
- Verifique permissões de escrita na pasta da aplicação
- Delete o arquivo `agendador.db` para recriar

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para detalhes.

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📞 Suporte

Para dúvidas ou problemas:
1. Verifique a seção de solução de problemas
2. Consulte os logs da aplicação no console
3. Abra uma issue no repositório

---

Desenvolvido com ❤️ em Java
