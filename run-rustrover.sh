#!/bin/sh -v
./gradlew :auto-dark-mode-plugin:runIde -PintellijType=RR -x :auto-dark-mode-linux-gtk:compileCpp
