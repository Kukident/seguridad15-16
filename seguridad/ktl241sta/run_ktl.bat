@echo off
cls

REM --- BEG MACHINE-DEPENDENT ----
set HOME_JAVA=java
REM set HOME_JAVA=C:\Program Files\Java\jdk1.6.0\jre\bin\java
REM --- END MACHINE-DEPENDENT ----

set HOME_JAVA="%HOME_JAVA%"
set ARG_MEMORY=-Xms128m -Xmx196m
set MAIN_JAR=rc15ktl.jar

set _CMD_=%HOME_JAVA% %ARG_MEMORY% -jar %MAIN_JAR%

@echo %_CMD_%
%_CMD_%