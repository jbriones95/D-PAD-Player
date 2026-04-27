#!/bin/bash
DEVICE="F21PC2304070689"
echo "Focus check..."
adb -s $DEVICE shell dumpsys activity top | grep -i "mFocusedWindow\|mFocusedApp"
