#!/bin/bash
# Edit this file to match your folders

echo -e "\n************"
echo      "Uninstalling"
echo      "************"
adb uninstall org.rti.tangy  
echo -e "\n**********"
echo      "Installing"
echo      "**********"
adb install bin/Tangerine-debug.apk
