@echo off
set DIR=%~dp0
set DISCO_HOME=%DIR%..
rem set PATH=%DISCO_HOME%\jre\bin;%PATH%
java -jar "%DISCO_HOME%\lib\disco.jar" --app:disruptor %*
