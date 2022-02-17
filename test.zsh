#!/bin/zsh

start=${1:-3}
if [[ $start != *:* ]]; then
  start=$( date +%H:%M -d @$(( $(date +%s) + $(($start*60)) )) )
fi

step=${2:-1}
step=$(($step*60))

variant=${3:-hg42.debug}

time=$start

for schedule in \
    test9_1     \
    test9_2     \
    test9_3     \
    test9_4     \
    test9_5     \
    test9_6     \
    test9_7     \
    test9_8     \
    special     \
    user        \
    system      \
    ; do
  adbsu am broadcast -a reschedule -e name $schedule -e time $time -n com.machiav3lli.backup.$variant/com.machiav3lli.backup.services.CommandReceiver
  time=$( date +%H:%M -d @$(( $(date +%s -d $time) + $step )) )
done