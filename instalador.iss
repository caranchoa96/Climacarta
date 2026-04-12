[Setup]
AppName=ClimaApp
AppVersion=1.0
DefaultDirName={autopf}\ClimaApp
DefaultGroupName=ClimaApp
; La carpeta donde Inno Setup escupirá el instalador final
OutputDir=Output
OutputBaseFilename=Instalador_ClimaApp
; El ícono del instalador en sí
SetupIconFile=lib/app.ico
Compression=lzma
SolidCompression=yes
; Evita que pida permisos de administrador si no son estrictamente necesarios
PrivilegesRequired=lowest 

[Tasks]
Name: "desktopicon"; Description: "Crear un acceso directo en el escritorio"; GroupDescription: "Accesos directos:"

[Files]
Source: "clima.exe"; DestDir: "{app}"; Flags: ignoreversion
; Las DLLs se quedan en la raíz
Source: "*.dll"; DestDir: "{app}"; Flags: ignoreversion
; Esto ya copia todo lo que esté en la carpeta 'lib' (fuentes + ícono) hacia {app}\lib
Source: "lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
; Ahora apuntamos a {app}\lib\app.ico para los íconos
Name: "{autoprograms}\ClimaApp"; Filename: "{app}\clima.exe"; IconFilename: "{app}\lib\app.ico"
Name: "{autodesktop}\ClimaApp"; Filename: "{app}\clima.exe"; Tasks: desktopicon; IconFilename: "{app}\lib\app.ico"