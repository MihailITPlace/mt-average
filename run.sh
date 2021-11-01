#java -classpath ./build/libs/mt-average-1.0-SNAPSHOT.jar jade.Boot -agents '
#  1st:AverageAgent(1,2st,3st);
#  2st:AverageAgent(5,1st,3st);
#  3st:AverageAgent(7,2st,1st)
#'

java -classpath ./build/libs/mt-average-1.0-SNAPSHOT.jar jade.Boot -agents '
  1st:AverageAgent(1,2st);
  2st:AverageAgent(5,1st,3st);
  3st:AverageAgent(7,2st,4st,5st);
  4st:AverageAgent(11,3st);
  5st:AverageAgent(4,3st,6st);
  6st:AverageAgent(20,5st)
'