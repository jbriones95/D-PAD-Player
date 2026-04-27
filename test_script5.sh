#!/bin/bash
DEVICE="F21PC2304070689"

echo "Clearing logcat..."
adb -s $DEVICE logcat -c

echo "Press BACK to go Home"
adb -s $DEVICE shell input keyevent 4
sleep 2

echo "Dumping logcat..."
adb -s $DEVICE logcat -d -s DPAD_FOCUS ViewRootImpl
