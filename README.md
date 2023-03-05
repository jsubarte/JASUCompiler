# Compilador Pseudocodigo

Proyecto final de la asignatura Lenguajes formales, Automatas y Compiladores de la Universidad Tecnológica de Panamá, creado en el 2013. Este compila Pseudocodigo y ejecuta el programa en su propia maquina virtual basada en Java, este tiene las partes basicas de un compilador como lo son el Analizador Lexico, Analizador Sintaxtico y el Analizador Semantico, ademas incluye la Tabla de Simbolos y la Maquina Virtual propia para ejecutar el programa.
Aqui tambien tienen un archivo txt con ejemplo de programas que puede ejecutar este compilador, tome en cuenta que para ejecutar este programa debe tener instalado Java.

## Ejecutar aplicación

Este compilador fue compilado con el jdk-11 y ejecutado con el jre1.8.0_361 antes de ser subido a Github, no garantizo que funcione en otras versiones sin necesitar algunos cambios al código.
Para ejecutar este programa vaya en su terminal al directorio donde se encuentra el archivo ejecutar.bat y ejecute la siguiente instrucción

    ejecutar.bat

Esto generara los archivos .class en el directorio class de todos los .java que se encuentran en la raiz, luego si todo salio bien creara el archivo JASUCompiler.jar en el directorio bin y finalmente ejecutara el archivo JASUCompiler.jar y estara listo para escribir programas en este lenguaje.

## Puntos a mejorar

Aunque al hacer el analisis sintactico acepta intrucciones mientras anidadas al momento de ejecutar el programa escrito no lo hace de forma correcta y solo acepta instrucciones mientras sin anidación, tambien faltaria añadir muchas cosas mas, pero como dije fue solo un proyecto universitario de una asignatura nunca fue la idea llevarlo más allá.

## Opcional ( Crear .exe )

Para generar un archivo .EXE use la aplicación Launch4j 3.50, no voy a entrar en detalles sobre como lo hace pero esta genera el archivo .EXE a partir del archivo .JAR