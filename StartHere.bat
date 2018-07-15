@echo off
DEL *.class
javac -Xlint:deprecation candyEliminate.java
java candyEliminate
