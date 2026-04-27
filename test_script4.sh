#!/bin/bash
DEVICE="F21PC2304070689"

echo "Clearing logcat..."
adb -s $DEVICE logcat -c

echo "Navigating to player (DOWN x 3)"
for i in {1..3}; do
  adb -s $DEVICE shell input keyevent 20
  sleep 0.5
done
sleep 1

echo "Selecting (ENTER)"
adb -s $DEVICE shell input keyevent 66
sleep 1

echo "Dumping logcat..."
adb -s $DEVICE logcat -d -s DPAD_FOCUS ViewRootImpl
