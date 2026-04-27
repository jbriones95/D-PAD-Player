#!/bin/bash
export DEVICE="F21PC2304070689"
adb -s $DEVICE logcat -c
adb -s $DEVICE shell am force-stop com.example.dpadplayer
adb -s $DEVICE shell am start -n com.example.dpadplayer/.MainActivity
sleep 3
echo "--- Navigating to Albums ---"
adb -s $DEVICE shell input keyevent 22 # RIGHT
sleep 1
echo "--- Focusing first album ---"
adb -s $DEVICE shell input keyevent 20 # DOWN
sleep 1
echo "--- Clicking album ---"
adb -s $DEVICE shell input keyevent 66 # ENTER
sleep 2
echo "--- Going back ---"
adb -s $DEVICE shell input keyevent 4  # BACK
sleep 1
echo "--- Navigating to mini player (10x DOWN) ---"
for i in {1..10}; do
  adb -s $DEVICE shell input keyevent 20
  sleep 0.2
done
sleep 1
echo "--- Going back up ---"
adb -s $DEVICE shell input keyevent 19 # UP
sleep 1
echo "--- Trying to select different album (UP + ENTER) ---"
adb -s $DEVICE shell input keyevent 19 # UP
sleep 0.5
adb -s $DEVICE shell input keyevent 66 # ENTER
sleep 2
echo "--- Fetching logs ---"
adb -s $DEVICE logcat -d -b main,crash -s DPAD_FOCUS AndroidRuntime ActivityManager
