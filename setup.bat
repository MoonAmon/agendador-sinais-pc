@echo off
echo ==================================================
echo    Agendador de Sinais - Setup para Windows
echo ==================================================
echo.

REM Verificar se Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Java não encontrado!
    echo.
    echo Por favor, instale o Java 11 ou superior:
    echo https://adoptium.net/
    echo.
    pause
    exit /b 1
)

echo [OK] Java encontrado
java -version

echo.
echo Verificando Maven...

REM Verificar se Maven está instalado
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [AVISO] Maven não encontrado no PATH
    echo.
    echo Opções para executar o projeto:
    echo.
    echo 1. Instalar Maven:
    echo    - Baixe de: https://maven.apache.org/download.cgi
    echo    - Adicione ao PATH do sistema
    echo.
    echo 2. Usar Maven Wrapper (recomendado):
    echo    - Execute: .\mvnw.cmd clean compile exec:java
    echo.
    echo 3. Usar IDE (Eclipse, IntelliJ, VS Code):
    echo    - Importe como projeto Maven
    echo    - Execute a classe: com.agendador.App
    echo.
    echo 4. Compilar manualmente:
    echo    - Use javac com as dependências no classpath
    echo.
    pause
    exit /b 0
)

echo [OK] Maven encontrado
mvn -version

echo.
echo ==================================================
echo Compilando o projeto...
echo ==================================================

mvn clean compile

if %errorlevel% neq 0 (
    echo [ERRO] Falha na compilação!
    pause
    exit /b 1
)

echo.
echo [OK] Compilação concluída com sucesso!
echo.

echo ==================================================
echo Executando testes...
echo ==================================================

mvn test

echo.
echo ==================================================
echo Criando JAR executável...
echo ==================================================

mvn package

if %errorlevel% neq 0 (
    echo [ERRO] Falha ao criar JAR!
    pause
    exit /b 1
)

echo.
echo [OK] JAR criado com sucesso!
echo.

echo ==================================================
echo Setup concluído!
echo ==================================================
echo.
echo Para executar a aplicação:
echo.
echo 1. Com Maven:
echo    mvn exec:java -Dexec.mainClass="com.agendador.App"
echo.
echo 2. Com JAR:
echo    java -jar target\agendador-sinais-1.0.0.jar
echo.
echo 3. No VS Code:
echo    - Use Ctrl+Shift+P
echo    - Digite "Tasks: Run Task"
echo    - Selecione "Build and Run Agendador"
echo.
echo A aplicação será executada com interface gráfica.
echo Quando minimizada, ficará disponível na barra de tarefas.
echo.
pause
