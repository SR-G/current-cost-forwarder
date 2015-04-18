#!/bin/sh
{
  echo "MONIT-WRAPPER date"
  date
  echo "MONIT-WRAPPER env"
  env
  echo "MONIT-WRAPPER $@"
  $@
  R=$?
  echo "MONIT-WRAPPER exit code $R"
} >/var/log/monitw-${MONIT_SERVICE}.log 2>&1