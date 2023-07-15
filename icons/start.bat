@echo off
set JAVA_HOME = %JAVA_HOME%

%JAVA_HOME%\javaw.exe --module-path .\lib\ --add-modules javafx.controls,javafx.media,javafx.swing -jar ZPL.exe
pause