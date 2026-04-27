#!/bin/bash
DEVICE="F21PC2304070689"

echo "Clearing logcat..."
adb -s $DEVICE logcat -c

echo "Navigating down to mini player (DOWN x 15)"
for i in {1..15}; do
  adb -s $DEVICE shell input keyevent 20
  sleep 0.2
done
sleep 1

echo "Selecting (ENTER)"
adb -s $DEVICE shell input keyevent 66
sleep 1

echo "Dumping logcat..."
adb -s $DEVICE logcat -d -s DPAD_FOCUS ViewRootImpl
