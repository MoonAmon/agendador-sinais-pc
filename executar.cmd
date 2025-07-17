@echo off
REM Script para executar o Agendador de Sinais
echo Iniciando Agendador de Sinais...

REM Configurar JAVA_HOME se necessário
if "%JAVA_HOME%"=="" (
    echo Configurando JAVA_HOME...
    set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot
)

REM Verificar se Java está disponível
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Erro: Java não encontrado. Verifique se o Java 21 está instalado.
    pause
    exit /b 1
)

REM Executar com classpath explícito
echo Executando aplicação...
"%JAVA_HOME%\bin\java.exe" -cp "target\classes;target\lib\*" com.agendador.App

if errorlevel 1 (
    echo.
    echo Erro na execução. Tentando com Maven...
    call mvnw.cmd exec:java -Dexec.mainClass="com.agendador.App"
)

pause
