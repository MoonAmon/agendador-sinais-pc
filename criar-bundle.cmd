@echo off
REM Script para criar bundle de aplicação (sem necessidade de WiX)
echo ========================================
echo     CRIANDO BUNDLE DE APLICAÇÃO
echo ========================================

REM Verificar se o JAR existe
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

echo 📦 Criando bundle de distribuição...

REM Criar estrutura de diretórios
if exist "target\AgendadorSinais-Bundle" rmdir /s /q "target\AgendadorSinais-Bundle"
mkdir "target\AgendadorSinais-Bundle"
mkdir "target\AgendadorSinais-Bundle\bin"
mkdir "target\AgendadorSinais-Bundle\docs"

REM Copiar arquivos
copy "target\%JAR_FILE%" "target\AgendadorSinais-Bundle\bin\agendador-sinais.jar"
copy "README.md" "target\AgendadorSinais-Bundle\docs\"

REM Criar script de execução Windows
echo @echo off > "target\AgendadorSinais-Bundle\AgendadorSinais.cmd"
echo cd /d "%%~dp0" >> "target\AgendadorSinais-Bundle\AgendadorSinais.cmd"
echo java -jar "bin\agendador-sinais.jar" >> "target\AgendadorSinais-Bundle\AgendadorSinais.cmd"

REM Criar script de execução com JAVA_HOME
echo @echo off > "target\AgendadorSinais-Bundle\AgendadorSinais-ComJavaHome.cmd"
echo set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot >> "target\AgendadorSinais-Bundle\AgendadorSinais-ComJavaHome.cmd"
echo cd /d "%%~dp0" >> "target\AgendadorSinais-Bundle\AgendadorSinais-ComJavaHome.cmd"
echo "%%JAVA_HOME%%\bin\java.exe" -jar "bin\agendador-sinais.jar" >> "target\AgendadorSinais-Bundle\AgendadorSinais-ComJavaHome.cmd"

REM Criar arquivo de instruções
echo # Agendador de Sinais - Instruções de Instalação > "target\AgendadorSinais-Bundle\INSTALAR.md"
echo. >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo ## Pré-requisitos >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo - Java 21 ou superior instalado >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo - Download: https://adoptium.net/temurin/releases/ >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo. >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo ## Como Executar >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo. >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo ### Opção 1: Com Java no PATH >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo Duplo clique em: `AgendadorSinais.cmd` >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo. >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo ### Opção 2: Com JAVA_HOME específico >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo Duplo clique em: `AgendadorSinais-ComJavaHome.cmd` >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo. >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo ### Opção 3: Linha de comando >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo ```cmd >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo java -jar bin\agendador-sinais.jar >> "target\AgendadorSinais-Bundle\INSTALAR.md"
echo ``` >> "target\AgendadorSinais-Bundle\INSTALAR.md"

REM Criar arquivo ZIP
echo 📦 Criando arquivo ZIP...
if exist "target\AgendadorSinais-1.0.0-Bundle.zip" del "target\AgendadorSinais-1.0.0-Bundle.zip"
powershell -command "Compress-Archive -Path 'target\AgendadorSinais-Bundle\*' -DestinationPath 'target\AgendadorSinais-1.0.0-Bundle.zip'"

echo.
echo ✅ SUCESSO! Bundle de aplicação criado!
echo.
echo 📍 Arquivos criados:
echo    📁 target\AgendadorSinais-Bundle\ (pasta completa)
echo    📦 target\AgendadorSinais-1.0.0-Bundle.zip (arquivo para distribuição)
echo.
echo 🚀 Como distribuir:
echo    1. Envie o arquivo ZIP para os usuários
echo    2. Usuários extraem e executam AgendadorSinais.cmd
echo    3. Funciona em qualquer Windows com Java 21+
echo.
echo 💡 Para abrir a pasta:
echo    explorer target\AgendadorSinais-Bundle
echo.

pause
