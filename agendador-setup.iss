; Script do Inno Setup para Agendador de Sinais
; Baixe o Inno Setup em: https://jrsoftware.org/isinfo.php

[Setup]
AppName=Agendador de Sinais
AppVersion=1.0.0
AppPublisher=Seu Nome
AppPublisherURL=https://github.com/MoonAmon/agendador-sinais-pc
DefaultDirName={autopf}\Agendador de Sinais
DefaultGroupName=Agendador de Sinais
OutputDir=target\installer
OutputBaseFilename=AgendadorSinais-1.0.0-Setup
Compression=lzma
SolidCompression=yes
WizardStyle=modern
SetupIconFile=src\main\resources\icon.ico
UninstallDisplayIcon={app}\AgendadorSinais.exe

[Languages]
Name: "portuguese"; MessagesFile: "compiler:Languages\Portuguese.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "target\agendador-sinais-1.0.0-shaded.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "executar.cmd"; DestDir: "{app}"; Flags: ignoreversion
Source: "README.md"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\Agendador de Sinais"; Filename: "{app}\executar.cmd"; WorkingDir: "{app}"
Name: "{group}\{cm:UninstallProgram,Agendador de Sinais}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\Agendador de Sinais"; Filename: "{app}\executar.cmd"; WorkingDir: "{app}"; Tasks: desktopicon

[Run]
Filename: "{app}\executar.cmd"; Description: "{cm:LaunchProgram,Agendador de Sinais}"; Flags: nowait postinstall skipifsilent

[Registry]
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\AgendadorSinais"; ValueType: string; ValueName: "DisplayName"; ValueData: "Agendador de Sinais"
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\AgendadorSinais"; ValueType: string; ValueName: "DisplayVersion"; ValueData: "1.0.0"
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\AgendadorSinais"; ValueType: string; ValueName: "Publisher"; ValueData: "Seu Nome"
