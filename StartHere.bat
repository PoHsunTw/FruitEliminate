@echo off
javac -Xlint:deprecation candyEliminate.java
java candyEliminate
DEL *.class
timeout /t 2