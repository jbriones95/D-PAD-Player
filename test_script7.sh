#!/bin/bash
DEVICE="F21PC2304070689"

echo "Clearing logcat..."
adb -s $DEVICE logcat -c

echo "Starting app..."
adb -s $DEVICE shell am start -n com.example.dpadplayer/.MainActivity
sleep 2

echo "Navigating to Albums (DOWN + RIGHT)"
adb -s $DEVICE shell input keyevent 20
adb -s $DEVICE shell input keyevent 22
sleep 1
adb -s $DEVICE shell input keyevent 66
sleep 2

echo "Moving to 2nd album (DOWN) and down to mini player (DOWN x 3)"
adb -s $DEVICE shell input keyevent 20
for i in {1..3}; do
  adb -s $DEVICE shell input keyevent 20
  sleep 0.5
done
sleep 1

echo "Selecting mini player (ENTER)"
adb -s $DEVICE shell input keyevent 66
sleep 2

echo "Going back up (UP) and selecting (ENTER)"
adb -s $DEVICE shell input keyevent 19
sleep 1
adb -s $DEVICE shell input keyevent 66

echo "Dumping logcat..."
adb -s $DEVICE logcat -d -s DPAD_FOCUS ViewRootImpl
