#!/bin/bash

mkdir -p build
rm -rf build/

# Компиляция
javac -d build -cp "src" src/com/kursx/parser/fb2/fonts/*.java src/com/kursx/parser/fb2/*.java

# Создание JAR с включением README и LICENSE
jar cfm lib/fb2parser.jar META-INF/MANIFEST.MF -C build . docs/LICENSE docs/README.md

echo "Библиотека собрана: lib/fb2parser.jar"
