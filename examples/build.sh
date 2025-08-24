#!/bin/bash

# Компилируем пример, используя библиотеку
javac -d . -cp "../lib/fb2parser.jar" src/Main.java

echo "Пример скомпилирован. Запуск:"
echo "java -cp .:../lib/fb2parser.jar Main"
