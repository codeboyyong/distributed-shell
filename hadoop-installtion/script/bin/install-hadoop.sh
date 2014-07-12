#!/usr/bin/env bash

# get the environment from sbt or vagrant  or passed from command line
#$1 :HADOOP_HOST=localhost
#$2 :HADOOP_VERSION=2.3.0-cdh5.0.0
#$3 :HADOOP_URL=http://archive.cloudera.com/cdh5/cdh/5/hadoop-2.3.0-cdh5.0.0.tar.gz
#$4 :HADOOP_HOME=~/alpinehadoop
#$5 :HADOOP_INSTALL=/Users/zhaoyong/dev/adl_new/HadoopInstallation/

if [ "$1" != "" ]
then
    HADOOP_HOST="$1"
fi
echo "HADOOP_HOST=$HADOOP_HOST"

if [ "$2" != "" ]
then
    HADOOP_VERSION="$2"
fi
echo "HADOOP_VERSION=$HADOOP_VERSION"


if [ "$3" != "" ]
then
    HADOOP_URL="$3"
fi
echo "HADOOP_URL=$HADOOP_URL"

if [ "$4" != "" ]
then
    HADOOP_HOME="$4"
fi
echo "HADOOP_HOME=$HADOOP_HOME"

if [ "$5" != "" ]
then
    HADOOP_INSTALL="$5"
fi
echo "HADOOP_INSTALL=$HADOOP_INSTALL"

echo "-------------------------------------------------------"
echo "Starting install hadoop version $HADOOP_VERSION"

#1 download installation image
#if  exsits, ask wheter to remove it
  #rm -r ./hadoop-2.3.0
#if [ -d $HADOOP_HOME ]
#    then
#    echo "Will install into $HADOOP_HOME "
#else
#    echo "File $HADOOP_HOME does not exists will create one"
#    mkdir $HADOOP_HOME
#fi


HADOOP_DIR=${HADOOP_HOME}/hadoop-${HADOOP_VERSION}


if [ -d $HADOOP_DIR ]
    then
    echo "Found HADOOP_DIR ,will overwrite in the install"
else
    echo "File $HADOOP_DIR does not exists will create one"
    mkdir -p $HADOOP_DIR
fi

#rm -rf */.
HADOOP_IMAGE_FILE="$HADOOP_HOME/hadoop-${HADOOP_VERSION}.tar.gz"

if [ -f $HADOOP_IMAGE_FILE ]
     then
    echo "Found HADOOP_IMAGE_FILE = $HADOOP_IMAGE_FILE "
else
    echo "File $HADOOP_IMAGE_FILE does not exists"
    echo "Downloading from $HADOOP_URL"
    echo "Please wait..."

    wget -q -O $HADOOP_IMAGE_FILE $HADOOP_URL
fi

echo "tar -xf $HADOOP_IMAGE_FILE  -C $HADOOP_HOME"
echo "Please wait..."
tar -xf $HADOOP_IMAGE_FILE  -C $HADOOP_HOME

#update the configuration
echo "cp -f $HADOOP_INSTALL/conf/$HADOOP_VERSION/*.xml $HADOOP_DIR/etc/hadoop"
cp -f $HADOOP_INSTALL/conf/$HADOOP_VERSION/*.xml $HADOOP_DIR/etc/hadoop


for FILE in `ls $HADOOP_DIR/etc/hadoop/*.xml`
do
 sed 's|HADOOP_HOST|'$HADOOP_HOST'|g' < $FILE > TMP_00
 sed 's|HADOOP_HOME|'$HADOOP_DIR'|g' < TMP_00 > TMP_01
 mv TMP_01 $FILE
done

rm TMP_00


mkdir $HADOOP_DIR/data
mkdir $HADOOP_DIR/name
mkdir $HADOOP_DIR/tmp

#update hadoop env and yarn env

if [ "$JAVA_HOME" != "" ]
then
    echo "updating java home for hadoop env : $JAVA_HOME"
else
    echo "JAVA_HOME not found"

fi

#$HADOOP_DIR/etc/hadoop/hadoop-env.sh
#$HADOOP_DIR/etc/hadoop/yarn-env.sh

#update the start script

#install service

echo "$HADOOP_DIR/bin/hadoop namenode -format"
$HADOOP_DIR/bin/hadoop namenode -format

# make it can ssh to localhost

#cd ~/.ssh
#ssh-keygen -t rsa -P ""
#cat  ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

# make it can ssh to each other ssh

echo "Install finished"


