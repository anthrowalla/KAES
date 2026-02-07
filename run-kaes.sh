#!/bin/bash
# KAES Launcher for macOS
# This script runs the KAES application with proper macOS settings

cd "$(dirname "$0")"

java -Dapple.awt.application.name=KAES \
     -Dapple.laf.useScreenMenuBar=true \
     -Dsun.java2d.uiScale=1 \
     -jar dist/KAES_KAM305.jar
