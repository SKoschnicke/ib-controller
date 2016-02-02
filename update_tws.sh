#!/bin/bash
read -p "This script will replace the files in directory tws with a newer version. Do you want to continue (y/n)? " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
  TWS_DIR=tws
  echo "Replacing current IB Gateway with newest from website..."
  set -eux
  curl -O https://download2.interactivebrokers.com/download/unixmacosx_latest.jar
  unzip unixmacosx_latest.jar
  rm unixmacosx_latest.jar
  rm -rf META-INF
  rm -rf $TWS_DIR/*
  # IBController expects the jars in a directory matching the version number of tws
  mkdir -p $TWS_DIR/952
  mv IBJts/* $TWS_DIR/952
  mkdir -p $TWS_DIR/952/jars
  mv $TWS_DIR/952/*.jar $TWS_DIR/952/jars
  # IBController expects this file, but it is not included in newest the TWS installation
  echo "" > $TWS_DIR/952/tws.vmoptions
  rm -rf IBJts
  echo "Done."
else
  echo "Aborted by user request."
fi
