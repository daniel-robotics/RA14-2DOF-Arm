@echo off
:: Made by Daniel Kennedy
:: This is a genaric automated build script for Java apps. Copy this to the root folder of the project.
:: You will need to edit some of these values accordingly in order for the script to work.

::*****************************************************************************************************
::* TASK 1 *
::* This will launch the editor. Change the text in [ ] according the project's name.
::*****************************************************************************************************

echo Launching editor...
start "" "B:\Program Files (x86)\JCreatorV5LE\JCreator.exe" "DrawingArmPC2.jcw"

:PREP
echo To begin prep tasks,
pause


::*****************************************************************************************************
::* TASK 2 *
::* This will put your app in a JAR according to a pre-configured JARMaker config.
::* It must be titled "jarmaker.JMS" and be in the root folder of the project.
::*****************************************************************************************************

if exist jarmaker.JMS (
	echo Making JAR file...
	java -jar "B:\Program Files (No Install)\JarMaker\JARMaker.jar" jarmaker.JMS
) else (
	echo Config file "jarmaker.JMS" not found! Go make one!
	javaw -jar "B:\Program Files (No Install)\JarMaker\JARMaker.jar"
)
echo.
echo.


goto PREP