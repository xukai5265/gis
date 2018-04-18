hadoop jar road.jar org.ctfo.gis.GisDiscoveryMR /guiji/2014/02/01.txt  /compute/result/road/2014/02/01/2 2
for((i=3;i<182;i++))
do 
q=$(($i-1))
hadoop jar road.jar org.ctfo.gis.CopyOfGisDiscoveryMR /compute/result/road/2014/02/01/$q  /compute/result/road/2014/02/01/$i $i
done