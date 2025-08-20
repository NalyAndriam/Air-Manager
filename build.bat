@echo off
setlocal EnableDelayedExpansion

REM ====== CONFIGURATION ======
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

set PROJECT_DIR=%~dp0
set PROJECT_NAME=fiaramanidina

set TOMCAT_HOME=C:\Program Files\Apache Software Foundation\Tomcat 10.1
set DEPLOY_DIR=%TOMCAT_HOME%\webapps

set SRC_DIR=%PROJECT_DIR%src\java
set CONF_DIR=%PROJECT_DIR%src\conf
set WEB_DIR=%PROJECT_DIR%web
set BUILD_DIR=%PROJECT_DIR%build
set CLASSES_DIR=%BUILD_DIR%\WEB-INF\classes
set LIB_DIR=%PROJECT_DIR%lib

REM ====== Création du classpath ======
set CLASSPATH="%TOMCAT_HOME%\lib\servlet-api.jar"
for %%f in ("%LIB_DIR%\*.jar") do (
    set CLASSPATH=!CLASSPATH!;%%f
)

REM ====== Nettoyage et création des dossiers ======
if exist %BUILD_DIR% rd /s /q %BUILD_DIR%
mkdir "%CLASSES_DIR%"
mkdir "%BUILD_DIR%\WEB-INF\lib"

echo === Compilation des fichiers Java avec les libs ===
javac -encoding UTF-8 -d "%CLASSES_DIR%" -cp !CLASSPATH! ^
  %SRC_DIR%\model\*.java ^
  %SRC_DIR%\controller\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Compilation échouée
    pause
    exit /b %ERRORLEVEL%
)

echo === Copie du web.xml et conf ===
xcopy /s /e /y "%CONF_DIR%\*" "%BUILD_DIR%\WEB-INF\" > nul

echo === Copie des fichiers Web (JSP, assets, etc.) ===
xcopy /s /e /y "%WEB_DIR%\*" "%BUILD_DIR%\" > nul

echo === Copie des librairies (.jar) dans WEB-INF/lib ===
xcopy /s /e /y "%LIB_DIR%\*.jar" "%BUILD_DIR%\WEB-INF\lib\" > nul

echo === Création du WAR ===
cd "%BUILD_DIR%"
jar -cvf "%PROJECT_NAME%.war" * > nul

echo === Déploiement dans Tomcat ===
copy /y "%PROJECT_NAME%.war" "%DEPLOY_DIR%\" > nul

cd "%PROJECT_DIR%"
rd /s /q "%BUILD_DIR%"

echo === Déploiement terminé avec succès ===
pause
endlocal
