javac -d %CD%\class *.java
COPY %CD%\images\logo.png %CD%\class\logo.png
jar cvfe %CD%\bin\JASUCompiler.jar Compilador -C %CD%\class .
java -jar %CD%\bin\JASUCompiler.jar