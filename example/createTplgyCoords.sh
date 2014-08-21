#!/bin/bash

##############################################################################
#		CREATE TOPOLOGY COORDINATES for READDY
##############################################################################
echo "create tplgy coords..."


PROGRAMPATH=../dist/

PROGRAM=$PROGRAMPATH/ReaDDy_TplgyCoordsCreator.jar

INPUTFOLDER=./input/
OUTPUTFOLDER=./output/

INPUTCOPYNUMBERS=in_copyNumbers.csv
OUTPUTTPLGYCOORDS=tplgy_coordinates.xml
OUTPUTTPLGYGROUPS=tplgy_groups.xml
PARAMGLOBAL=param_global.xml
PARAMPARTICLES=param_particles.xml
PARAMGROUPS=param_groups.xml
PARAMPOTENTIALTEMPLATES=param_potentialTemplates.xml
TPLGYPOTENTIALS=tplgy_potentials.xml

LOGFILENAME=log_createTplgyCoords.log
ERRLOGFILENAME=log_createTplgyCoords.err.log

START=`date '+%s'`
STARTDATE=`date`



echo $STARTDATE > $OUTPUTFOLDER/$LOGFILENAME

java -jar $PROGRAM  	\
-input_copyNumbers $INPUTFOLDER/$INPUTCOPYNUMBERS \
-param_global $INPUTFOLDER/$PARAMGLOBAL 	\
-param_particles $INPUTFOLDER/$PARAMPARTICLES 		\
-param_groups $INPUTFOLDER/$PARAMGROUPS	\
-param_potentialTemplates $INPUTFOLDER/$PARAMPOTENTIALTEMPLATES 		\
-tplgy_potentials $INPUTFOLDER/$TPLGYPOTENTIALS 				\
-output_tplgyCoords $OUTPUTFOLDER/$OUTPUTTPLGYCOORDS \
-output_tplgyGroups $OUTPUTFOLDER/$OUTPUTTPLGYGROUPS \
 2>&1 >> $OUTPUTFOLDER/$LOGFILENAME | tee -a $OUTPUTFOLDER/$ERRLOGFILENAME >> $OUTPUTFOLDER/$LOGFILENAME
# output both sterr and stout to the logfile but split the error to a separate errorLogfile

END=`date '+%s'`
ENDDATE=`date`
TIMEELAPSED=`echo "$END-$START" | bc`

echo "" >> $OUTPUTFOLDER/$LOGFILENAME
echo $ENDDATE >> $OUTPUTFOLDER/$LOGFILENAME
echo "seconds elapsed $TIMEELAPSED" >> $OUTPUTFOLDER/$LOGFILENAME


LOG=$OUTPUTFOLDER/$LOGFILENAME

echo "tplgy created!"
echo "see the log in $LOG"

