# Log Parser command line tool

----------------------------------------------------------------------------------------------------------------------------------

CLI Command:  QUERY IP cpu_id time_start time_end
Also read: Report.pdf

   The program takes the QUERY command from  the user and finds the CPU usage value for the time range given for the 
    respective IP Address and CPU ID. The program splits the given large file with logs for 24 hrs into 3 equal part 
    containing 960,000 logs each. By doing this we can narrow the search down to subset of data. We can then cache the
    subset of data and find the usage value from the cache.
    The program takes approximately 1 sec to return the usage values for max range, that is 23 hrs 59 mins.
    
#Overview
----------------------------------------------------------------------------------------------------------------------------------
The project folder contains a report which explains how the three java codes submitted work. 

There is also A output file which shows the execution of the QUERY command and also shows the 
edge cases and the different invalid inputs. 

All the java programs are well documented inside the code and the important critical sections are also explained. 
I have also attached a sample log file. 

The output format for the query command is as explained in the question. The start time in the time range is inclusive 
while the endTime is exclusive and the start time should always be strictly greater than the end time.

#Log Generator
----------------------------------------------------------------------------------------------------------------------------------

The LogGenerator.java takes two command line inputs both of which are optional:
1. Preferred log file_name: If not given, takes the default name are logs.txt		
2. Path to the directory in which you want to store the file: creates the file in the root directory

If the both are not a given, logs.txt file is created by default in the root directory

The logs are recorded by default from 2014-10-31 00:00 to 2014-10-31 23:59. These are values are fixed in the program i.e. hardcoded
as constants.

#Query Command
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
