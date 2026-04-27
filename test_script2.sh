#!/bin/bash
DEVICE="F21PC2304070689"

echo "Clearing logcat..."
adb -s $DEVICE logcat -c

echo "Starting app..."
adb -s $DEVICE shell am start -n com.example.dpadplayer/.MainActivity
sleep 2

echo "Navigating to Albums (DOWN + ENTER)"
adb -s $DEVICE shell input keyevent 20
adb -s $DEVICE shell input keyevent 66
sleep 2

echo "Moving to 2nd album (DOWN) and opening (ENTER)"
adb -s $DEVICE shell input keyevent 20
adb -s $DEVICE shell input keyevent 66
sleep 2

echo "Going back (BACK)"
adb -s $DEVICE shell input keyevent 4
sleep 2

echo "Navigating to player (DOWN x 15)"
for i in {1..15}; do
  adb -s $DEVICE shell input keyevent 20
  sleep 0.2
done
sleep 1

echo "Going back up (UP x 2)"
adb -s $DEVICE shell input keyevent 19
sleep 0.5
adb -s $DEVICE shell input keyevent 19
sleep 1

echo "Selecting album (ENTER)"
adb -s $DEVICE shell input keyevent 66
sleep 1

echo "Dumping logcat..."
adb -s $DEVICE logcat -d -s DPAD_FOCUS ViewRootImpl
