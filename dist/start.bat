@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION
SET classpath=.
FOR %%i IN (lib/*.*) DO (
    SET classpath=!classpath!;lib/%%i
)
SET mainClass=com.shinwell.mda.gui.MdaAppWindow
jre\bin\java -classpath %classpath% %mainClass%
endlocal