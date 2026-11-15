@echo off
setlocal
rem uses the bundled Gradle wrapper (needs JDK 21)
call gradlew.bat build
if errorlevel 1 (
  echo Build failed. Copy the first Gradle error above when asking for help.
  pause
  exit /b 1
)
echo.
echo Build complete. Check cyclone\build\libs\ for cycloneskies-1.9.9.jar
pause
