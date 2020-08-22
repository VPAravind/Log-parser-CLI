import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <h1>Implement the QUERY command</h1> The program takes the QUERY command from
 * the user and finds the CPU usage value for the time range given for the
 * respective IP Address and CPU ID. The program splits the given large file
 * with logs for 24 hrs into 3 equal part containing 960,000 logs each. By doing
 * this we can narrow the search down to subset of data. We can then cache the
 * subset of data and find the usage value from the cache.
 * <p>
 * <b>Note:</b> The program takes approximately 1 sec to return the usage values
 * for max range, that is 23 hrs 59 mins.
 *
 * @author Aravind Vicinthangal Prathivaathi
 * @version 1.0
 * @since 2019-07-09
 */
public class QueryCommand {
	// Split files into 480 minutes(8 hrs) of data each. So since there are 1000
	// servers
	// with 2 CPUs producing 2 logs every minute. Therefore
	// the number of lines per file would be 480 * 2000.
	final static int LINES_PER_FILE = 480 * 2000;

	// Number of servers as per the question.
	final static int SERVERS = 1000;

	// IP Address is a 32 bit number meaning each part is 8 bit and the maximum
	// value each 8 bit part can take is 255
	final static int IP_OCTET_MAX = 255;

	// Total number of seconds in a day.
	final static int TOTAL_SEC = 60 * 24 * 60;

	// Format of the time stamp given in the question
	static String stampFormat = "yyyy-MM-dd HH:mm";

	// Map to cache the required data for fast access
	static Map<String, ArrayList<String>> cache = new HashMap<>();

	// File suffix for the data split
	static int currFileSuffix = -1;

	// This array contains the startTimes of each file that was split from the large
	// file
	static long startTimes[] = new long[4];

	// This is the default date and time given in the question
	static String defaultStartTime = "2014-10-31 00:00";

	// This is the default date and time given in the question
	static String defaultEndTime = "2014-10-31 23:59";

	// Contains the file names of split-up log file.
	static String splitFileNames[] = { "Log0.txt", "Log1.txt", "Log2.txt" };

	/**
	 * Method which converts the given time stamp (YYYY-MM-DD HH:MM) into Unix Time
	 * Stamp
	 * 
	 * @param stampFormat input time stamp
	 * @return string Unix time
	 * @exception ParseException
	 */
	public static String convertToUnix(String timeStamp) {
		SimpleDateFormat dateF = new SimpleDateFormat(stampFormat);
		StringBuilder unixTime = new StringBuilder("");

		try {
			Date date = dateF.parse(timeStamp);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			long time = (long) cal.getTime().getTime();
			long epochs = time / 1000L;
			unixTime.append(epochs);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return unixTime.toString();

	}

	/**
	 * Gives the file suffix to identify the file in which the current IP and CPU Id
	 * could be present in. This gives a faster way to read the log data and
	 * restrict the data to a smaller sample size by considering the time difference
	 * between the start time in the file and the start time given.
	 * 
	 * @param ipAddr IP Address
	 * @param cpuId  CPU Id
	 * @param diff   Time difference between the starting time in the file and the
	 *               given start time
	 * @param lineNo current Line number
	 * @param flag   to determine if we need to calculate a new Line Number
	 * 
	 * @return int This returns the file suffix number in which the input IP Address
	 *         and other data are present
	 */
	public static int getFileSuffix(final String ipAddr, String cpuId, final int diff, int lineNo, boolean flag) {
		if (flag) {
			String[] subIp = ipAddr.split("\\.");// escape character \\. since . is a special regEx character
			lineNo = Integer.parseInt(subIp[3]) + (Integer.parseInt(subIp[2]) * (IP_OCTET_MAX + 1) * 2)
					+ Integer.parseInt(cpuId) + (SERVERS * diff);

		}
		return lineNo / LINES_PER_FILE;

	}

	/**
	 * This Method the given Log file into individual log files containing 8 hrs of
	 * Log data. By splitting into multiple files, we can limit our search and other
	 * operations to a subset of data. This gives us better performance and
	 * effectively shorter search time.
	 * 
	 * @param fileName The generated log file name
	 * @exception IOException
	 * 
	 */
	public static void splitIntoFiles(final String fileName) {
		File fi = new File(fileName);
		if(!fi.exists()) {
			System.out.println("File " + fileName + " does not exist! Give proper command line arguments "
					+ "or check if "+ fileName + " exists in the root.");
			System.exit(1);
		}

		try (BufferedReader bRead = new BufferedReader(new FileReader(fileName))) {
			String logFileName;
			File f;
			for (String file : splitFileNames) {
				f = new File(file);
				if (!f.exists()) {
					f.createNewFile();
				}
			}
			String curLine = null;
			int lineNo = 0;
			int changeFile = 0;

			FileWriter fWrite = null;
			BufferedWriter bWrite = null;

			bRead.readLine();
			while ((curLine = bRead.readLine()) != null) {
				if (changeFile == 0) {
					int fileSuffix = getFileSuffix("", "", 0, lineNo, false);
					logFileName = "Log" + fileSuffix + ".txt";
					fWrite = new FileWriter(logFileName);
					bWrite = new BufferedWriter(fWrite);
					String curTimeStamp[] = curLine.split(" ");
					startTimes[fileSuffix] = Long.parseLong(curTimeStamp[0]);
				}

				lineNo++;
				changeFile++;

				bWrite.write(curLine);
				bWrite.newLine();

				if (changeFile >= LINES_PER_FILE) {
					bWrite.close();
					fWrite.close();
					changeFile = 0;

				}

			}
			if (fWrite != null && bWrite != null) {
				fWrite.close();
				bWrite.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * This method constructs the regular expression used for the validating the
	 * QUERY command.
	 * 
	 * @param None
	 * @return string returns the regular expression
	 */
	public static String constructRegex() {
		StringBuilder command = new StringBuilder(256);
		command.append("^QUERY\\s")
				.append("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\s")
				.append("[0-1]\\s")
				.append("[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]\\s")
				.append("[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]$");

		return command.toString();
	}

	/**
	 * Checks if the input QUERY command is valid.
	 * 
	 * @param query The input QUERY command
	 * @return boolean true if the command is valid, else false
	 */
	public static boolean commandValidator(final String query) {
		return Pattern.matches(constructRegex(), query);
	}

	/**
	 * Checks if the input time stamps are valid for the recorded logs.
	 * 
	 * @param unixStart    The unix time of the input start time
	 * @param unixEnd      The unix time of the input end time
	 * @param defaultStart The unix time of 2014-10-31 00:00
	 * @param defaultEnd   The unix time of 2014-10-31 23:59
	 * 
	 * @return boolean true if the time is invalid, else false
	 */

	public static boolean timeValidator(String unixStart, String unixEnd, String defaultStart, String defaultEnd) {
		boolean validate = false;
		if (Long.parseLong(unixStart) >= Long.parseLong(unixEnd)) {
			System.out.println("Invalid Input! The input start time is greater than or equal to the end time!");
			System.out.println("End time should be greater than the start time!");
			validate = true;
		}

		if (Long.parseLong(unixStart) > Long.parseLong(defaultEnd)) {
			System.out.println("Invalid Input! The input start time is greater than the default end time "
					+ defaultEndTime + " for which the logs were recorded!");
			validate = true;
		}

		if (Long.parseLong(defaultStart) > Long.parseLong(unixEnd)) {
			System.out.println("Invalid Input! The input end time is less than the default start time "
					+ defaultStartTime + " for which the logs were recorded!");
			validate = true;
		}

		return validate;
	}

	/**
	 * This method caches the required data from given text file into a Hash Map.
	 * The hash map uses the IP Address and CPU as its key and a list of cpu usage
	 * as its value for that respective ip + cpu combination,
	 * 
	 * @param fileName name of the file which contains the necessary data
	 * @exception IO Exception
	 */
	public static void cacheIt(final String fileName) {
		String data[] = new String[4];
		cache.clear();
		try (BufferedReader bRead = new BufferedReader(new FileReader(fileName))) {
			StringBuilder key;
			String curLine = null;
			while ((curLine = bRead.readLine()) != null) {
				// split each line into ip, cpu ID, time stamp and cpu usage.
				data = curLine.split(" ");

				// key is set as the concatenated value of IP Address and CPU ID
				key = new StringBuilder(data[1] + " " + data[2]);

				ArrayList<String> content;
				if (cache.containsKey(key.toString())) {
					content = cache.get(key.toString());
				} else {
					content = new ArrayList<String>();
				}

				// The value of each record in the map is a list of usage values for the
				// corresponding concatenated value of IP Address and CPU ID
				content.add(data[3]);
				cache.put(key.toString(), content);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method fetches the usage value of the CPU from cache if the key is
	 * present, else it tells to check the next split-up log file.
	 * 
	 * @param key   the key to the corresponding cached data
	 * @param start the start time to find which index in the list contains the
	 *              value
	 * 
	 * @return usage returns the cpu usage if present else return Next File
	 */
	public static String fetchFromCache(final String key, final String start) {
		// calculates the index at which the usage data could be present in the list.
		final int listIndex = (int) (Long.parseLong(start) - startTimes[currFileSuffix]) / 60;
		ArrayList<String> val = new ArrayList<>();
		String usage = null;

		if (cache.containsKey(key)) {
			// gets the list of usage values for the particular key
			val = cache.get(key);

			// check to see whether the list could contain the usage value
			if (listIndex < val.size()) {

				usage = val.get(listIndex);
			} else {
				// If usage value not present, then return this strings which acts as a flag to
				// move-on to the next file.
				usage = "Next File";
			}
		}

		return usage;

	}

	/**
	 * This method is used the return a list of all CPU usage values for the given
	 * time range, IP Address and CPU ID
	 * 
	 * @param ip        IP Address
	 * @param cpuId     CPU ID
	 * @param unixStart Starting unix time
	 * @param unixEnd   Ending unix time
	 * 
	 * @return ArryList returns a list of CPU usage values
	 */
	public static ArrayList<String> getResult(final String ip, final String cpuId, final String unixStart,
			final String unixEnd) {
		ArrayList<String> result = new ArrayList<String>();
		final String key = ip + " " + cpuId;
		long timeStart = Long.parseLong(unixStart);
		final long timeEnd = Long.parseLong(unixEnd);

		while (timeStart < timeEnd) {
			String usage = fetchFromCache(key, "" + timeStart);

			// If usage is Next File, then we need to move to next file with respect to the
			// suffix of the file and cache the data of the new file.
			if (usage != null && usage.equals("Next File")) {
				currFileSuffix++;
				cacheIt("Log" + currFileSuffix + ".txt");
				continue;

			} else if (usage != null && !usage.equals("Next File")) {

				result.add(usage);
			}
			timeStart += 60;
		}

		return result;
	}

	/**
	 * This method is used to display the result to the user in the format mentioned
	 * in the question
	 * 
	 * @param result list of usage values to display
	 * @param ip     IP Address
	 * @param cpuId  CPU ID
	 * @param start  Starting time
	 * 
	 * @return ArryList returns a list of the CPU IDs for the given time range, IP
	 *         Address and CPU ID
	 * 
	 * @exception ParseException
	 */
	public static void display(ArrayList<String> result, final String ip, final String cpuId, final String start) {

		String startTime;
		SimpleDateFormat dateF = new SimpleDateFormat(stampFormat);

		try {
			Date date = dateF.parse(start);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			startTime = "" + dateF.format(cal.getTime());
			System.out.println("CPU" + cpuId + " usage on " + ip + ": ");
			if (result.size() == 0 || result == null) {
				System.out.println("Log for this IP: " + ip + " and for this CPU ID: " + cpuId + " at this given time " + startTime
						+ " doesn't exist!!");
				return;
			}

			for (int i = 0; i < result.size(); i++) {
				System.out.print("(" + startTime + ", " + result.get(i) + "%)");

				date = dateF.parse(startTime);
				cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.MINUTE, 1);

				startTime = "" + dateF.format(cal.getTime());

				if (i < result.size() - 1) {
					System.out.print(", ");
				}

			}
			System.out.println();

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Controller method which controls the flow of the application.
	 * 
	 * @param fileName The name of the log file
	 * 
	 */
	public static void controller(final String fileName) {

		splitIntoFiles(fileName);
		final String defaultStartUnixTime = convertToUnix(defaultStartTime);
		final String defaultEndUnixTime = convertToUnix(defaultEndTime);

		String ipAddr;
		String cpuId;
		String timeStart;
		String timeEnd;

		boolean flag = true;
		String queryArr[];
		Scanner in = new Scanner(System.in);
		while (flag) {

			System.out.println("Enter the Query");
			String query = in.nextLine();

			if (query.toUpperCase().equals("EXIT")) {
				flag = false;
				continue;
			} else {
				final boolean validation = commandValidator(query);
				if (!validation) {
					System.out.println("Invalid Input");
					System.out.println("Command Format: QUERY IP CPU_ID YYYY-MM-DD HH:MM YYYY-MM-DD HH:MM");
					continue;
				}

				queryArr = query.split(" ");

				ipAddr = queryArr[1];
				cpuId = queryArr[2];
				timeStart = queryArr[3] + " " + queryArr[4];
				timeEnd = queryArr[5] + " " + queryArr[6];

				String unixStart = convertToUnix(timeStart);
				String unixEnd = convertToUnix(timeEnd);

				if (timeValidator(unixStart, unixEnd, defaultStartUnixTime, defaultEndUnixTime)) {
					System.out.println("Command Format: QUERY IP CPU_ID YYYY-MM-DD HH:MM YYYY-MM-DD HH:MM");

					continue;
				}

				if (Long.parseLong(unixStart) < startTimes[0]) {
					unixStart = "" + startTimes[0];
					timeStart = defaultStartTime;

				}

				if (Long.parseLong(unixEnd) > startTimes[0] + TOTAL_SEC) {
					unixEnd = (startTimes[0] + TOTAL_SEC) + "";
				}

				final int timeDiff = (int) (Long.parseLong(unixStart) - startTimes[0]) / 60;

				int fileSuffix = getFileSuffix(ipAddr, cpuId, timeDiff, 0, true);

				if (currFileSuffix != fileSuffix) {
					final String fName = "Log" + fileSuffix + ".txt";
					cacheIt(fName);
					currFileSuffix = fileSuffix;
				}

				ArrayList<String> result = getResult(ipAddr, cpuId, unixStart, unixEnd);
				display(result, ipAddr, cpuId, timeStart);
			}

		}

		in.close();

	}

	/**
	 * Main method : Checks for the name of the file if passed through command line
	 * 
	 * @param args file name
	 * 
	 */

	public static void main(String[] args) {
		String fileName;

		if (args.length == 0) {
			fileName = "cpuLogs.txt";
		} else {

			fileName = args[0];
		}

		controller(fileName);

	}

}
