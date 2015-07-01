;This file will be executed next to the application bundle image
;I.e. current directory will contain folder SAIL with application files
[Setup]
AppId={{fxApplication}}
AppName=SAIL
AppVersion=1.1
AppVerName=SAIL 1.1
AppPublisher=University of Illinois
AppComments=SAIL
AppCopyright=Copyright (C) 2015
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={pf}\SAIL
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=University of Illinois
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=SAIL-1.1
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=SAIL\SAIL.ico
UninstallDisplayIcon={app}\SAIL.ico
UninstallDisplayName=SAIL
WizardImageStretch=No
WizardSmallImageFile=SAIL-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "SAIL\SAIL.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "SAIL\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\SAIL"; Filename: "{app}\SAIL.exe"; IconFilename: "{app}\SAIL.ico"; Check: returnTrue()
Name: "{commondesktop}\SAIL"; Filename: "{app}\SAIL.exe";  IconFilename: "{app}\SAIL.ico"; Check: returnTrue()


[Run]
Filename: "{app}\SAIL.exe"; Description: "{cm:LaunchProgram,SAIL}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\SAIL.exe"; Parameters: "-install -svcName ""SAIL"" -svcDesc ""SAIL"" -mainExe ""SAIL.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\SAIL.exe "; Parameters: "-uninstall -svcName SAIL -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
