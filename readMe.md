----------------------------------------------------------------------------------------------------------------------------------
The project folder contains a report which explains how the three java codes submitted work. 

There is also A output file which shows the execution of the QUERY command and also shows the 
edge cases and the different invalid inputs. 

All the java programs are well documented inside the code and the important critical sections are also explained. 
I have also attached a sample log file. 

The output format for the query command is as explained in the question. The start time in the time range is inclusive 
while the endTime is exclusive and the start time should always be strictly greater than the end time.

----------------------------------------------------------------------------------------------------------------------------------

The LogGenerator.java takes two command line inputs both of which are optional:
1. Preferred log file_name: If not given, takes the default name are logs.txt		
2. Path to the directory in which you want to store the file: creates the file in the root directory

If the both are not a given, logs.txt file is created by default in the root directory

The logs are recorded by default from 2014-10-31 00:00 to 2014-10-31 23:59. These are values are fixed in the program i.e. hardcoded
as constants.

----------------------------------------------------------------------------------------------------------------------------------

The QueryCommand.java file takes one optional command line input:

Path to the directory in which the log file is present: If not given, checks for logs.txt file in the root directory and uses it. 

It also asks for an input through stdin, which takes in the Query Command(case sensitive):
1. QUERY IP cpu_id time_start time_end.

time_start > time_end
QUERY - Case sensitive(should be in upper case)
CPU ID: 0 or 1

Either time_start or time_end should be between the range 2014-10-31 00:00 and 2014-10-31 23:59.

2. EXIT <- exits out of the program. (Not case sensitive)
