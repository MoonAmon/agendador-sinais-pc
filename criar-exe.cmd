@echo off
REM Script para gerar instalador EXE usando jpackage (sem necessidade do WiX)
echo ========================================
echo   CRIANDO INSTALADOR EXE - JPACKAGE
echo ========================================

REM Configurar JAVA_HOME se necessário
if "%JAVA_HOME%"=="" (
    echo Configurando JAVA_HOME...
    set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot
)

REM Verificar se jpackage está disponível
if not exist "%JAVA_HOME%\bin\jpackage.exe" (
    echo ❌ Erro: jpackage não encontrado em %JAVA_HOME%\bin\
    echo    Verifique se o Java 21+ está instalado.
    pause
    exit /b 1
)

REM Verificar se o JAR existe (tentar ambas as versões)
set JAR_FILE=
if exist "target\agendador-sinais-1.0.0-shaded.jar" (
    set JAR_FILE=agendador-sinais-1.0.0-shaded.jar
) else if exist "target\agendador-sinais-1.0.0.jar" (
    set JAR_FILE=agendador-sinais-1.0.0.jar
) else (
    echo ❌ Erro: JAR não encontrado!
    echo    Execute primeiro: mvnw.cmd package
    pause
    exit /b 1
)

echo    JAR encontrado: %JAR_FILE%

REM Criar diretório de trabalho
if not exist "target\installer" mkdir "target\installer"

echo.
echo 📦 Informações do instalador:
echo    Nome: Agendador de Sinais
echo    Versão: 1.0.0
echo    Vendor: Thiago Amancio Reis Caetano
echo    Tipo: EXE (Windows Executable)
echo.

REM Gerar o instalador EXE
echo 🔨 Gerando instalador EXE...
"%JAVA_HOME%\bin\jpackage.exe" ^
    --type exe ^
    --input target ^
    --dest target/installer ^
    --name "Agendador de Sinais" ^
    --main-class com.agendador.App ^
    --main-jar %JAR_FILE% ^
    --app-version 1.0.0 ^
    --vendor "Thiago Amancio Reis Caetano" ^
    --description "Sistema para agendamento de sinais de áudio" ^
    --copyright "Copyright 2025 Thiago Amancio Reis Caetano" ^
    --win-dir-chooser ^
    --win-menu ^
    --win-shortcut ^
    --win-per-user-install ^
    --java-options "-Dfile.encoding=UTF-8" ^
    --verbose

if %errorlevel% equ 0 (
    echo.
    echo ✅ SUCESSO! Instalador EXE criado!
    echo.
    echo 📍 Localização: target\installer\Agendador de Sinais-1.0.0.exe
    echo 📏 Tamanho: 
    for %%I in ("target\installer\*.exe") do echo    %%~zI bytes
    echo.
    echo 🚀 Próximos passos:
    echo    1. Teste o instalador em uma máquina limpa
    echo    2. Verifique se instala/desinstala corretamente
    echo    3. Considere assinar digitalmente para produção
    echo    4. Distribua o arquivo .exe
    echo.
    echo 💡 Para abrir a pasta do instalador:
    echo    explorer target\installer
    echo.
    echo 📋 Vantagens do EXE vs MSI:
    echo    ✅ Não precisa do WiX Toolset
    echo    ✅ Mais simples de criar
    echo    ✅ Funciona em qualquer Windows
    echo    ❌ Menos opções de configuração que MSI
) else (
    echo.
    echo ❌ ERRO ao criar o instalador EXE
    echo    Verifique os logs acima para mais detalhes
    echo.
    echo 🔍 Possíveis soluções:
    echo    1. Certifique-se de que o Java 21+ está instalado
    echo    2. Execute como administrador
    echo    3. Verifique se o JAR está presente
    echo    4. Libere espaço em disco se necessário
)

echo.
pause
