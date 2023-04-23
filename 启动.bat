@echo off
set cwd=%~dp0

>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"
if '%errorlevel%' NEQ '0' (
goto UACPrompt
) else ( goto gotAdmin )
:UACPrompt
echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
echo UAC.ShellExecute "%~s0", "", "", "runas", 1 >> "%temp%\getadmin.vbs"
"%temp%\getadmin.vbs"
exit /B
:gotAdmin


chcp 65001>nul
echo 本程序需要使用管理员权限来完成文件链接的创建。本程序完全开源，不存在其他操作行为。
cd /d %cwd%
java -Dfile.encoding=utf8 -jar ZeroPointServerWrapper-1.0-SNAPSHOT.jar

echo 按任意键退出
pause>nul
