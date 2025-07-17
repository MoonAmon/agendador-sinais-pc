@echo off
REM Script para configurar JAVA_HOME para o projeto Agendador de Sinais

echo Configurando JAVA_HOME...
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot

echo.
echo JAVA_HOME configurado para: %JAVA_HOME%
echo.

REM Verificar se o caminho está correto
if exist "%JAVA_HOME%\bin\java.exe" (
    echo ✓ Java encontrado em: %JAVA_HOME%\bin\java.exe
    
    REM Testar versão do Java
    echo.
    echo Versão do Java:
    "%JAVA_HOME%\bin\java.exe" -version
    
    echo.
    echo ✓ Configuração concluída com sucesso!
    echo.
    echo Para usar o Maven Wrapper, execute:
    echo   .\mvnw.cmd --version
    echo   .\mvnw.cmd compile
    echo   .\mvnw.cmd exec:java -Dexec.mainClass="com.agendador.App"
    
) else (
    echo ✗ Erro: Java não encontrado no caminho especificado
    echo Verifique se o Java está instalado corretamente
)

echo.
pause
