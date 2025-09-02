@echo off
REM Script completo para preparar aplicação para produção
echo =====================================
echo    AGENDADOR DE SINAIS - PRODUÇÃO
echo =====================================

REM Configurar JAVA_HOME
if "%JAVA_HOME%"=="" (
    echo Configurando JAVA_HOME...
    set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot
)

echo.
echo 1️⃣ Limpando build anterior...
call mvnw.cmd clean

echo.
echo 2️⃣ Executando testes...
call mvnw.cmd test
if %errorlevel% neq 0 (
    echo ❌ Testes falharam! Corrija os erros antes de continuar.
    pause
    exit /b 1
)

echo.
echo 3️⃣ Compilando e empacotando...
call mvnw.cmd package
if %errorlevel% neq 0 (
    echo ❌ Erro na compilação/empacotamento!
    pause
    exit /b 1
)

echo.
echo 4️⃣ Verificando JAR executável...
if not exist "target\agendador-sinais-1.0.0-shaded.jar" (
    echo ❌ JAR não encontrado!
    pause
    exit /b 1
)

echo.
echo 5️⃣ Testando execução do JAR...
timeout /t 3 >nul
start /min java -jar "target\agendador-sinais-1.0.0-shaded.jar"
echo Aguardando 5 segundos para teste...
timeout /t 5 >nul
taskkill /f /im java.exe >nul 2>&1

echo.
echo 6️⃣ Criando estrutura de distribuição...
if not exist "target\distribuicao" mkdir "target\distribuicao"
copy "target\agendador-sinais-1.0.0-shaded.jar" "target\distribuicao\"
copy "executar.cmd" "target\distribuicao\"
copy "README.md" "target\distribuicao\"

echo.
echo 7️⃣ Criando ZIP de distribuição...
if exist "target\AgendadorSinais-1.0.0.zip" del "target\AgendadorSinais-1.0.0.zip"
powershell -command "Compress-Archive -Path 'target\distribuicao\*' -DestinationPath 'target\AgendadorSinais-1.0.0.zip'"

echo.
echo 8️⃣ Gerando instalador MSI (se disponível)...
if exist "%JAVA_HOME%\bin\jpackage.exe" (
    call criar-msi.cmd
) else (
    echo ⚠️  jpackage não disponível. MSI não será criado.
    echo    Para criar MSI, certifique-se de ter Java 21+ com jpackage.
)

echo.
echo =====================================
echo           BUILD CONCLUÍDO!
echo =====================================
echo.
echo 📦 Arquivos de distribuição criados:
echo    ✅ target\agendador-sinais-1.0.0-shaded.jar (JAR executável)
echo    ✅ target\AgendadorSinais-1.0.0.zip (Pacote ZIP)
if exist "target\installer\*.msi" (
    echo    ✅ target\installer\*.msi (Instalador MSI)
)
echo.
echo 🚀 Para distribuir:
echo    1. JAR: Envie o arquivo .jar + instruções de instalação do Java
echo    2. ZIP: Pacote completo com scripts
echo    3. MSI: Instalador nativo do Windows
echo.
echo 📋 Próximos passos recomendados:
echo    1. Testar em máquina limpa (sem Java)
echo    2. Considerar assinatura digital
echo    3. Criar documentação de instalação
echo    4. Configurar distribuição automática
echo.

pause
