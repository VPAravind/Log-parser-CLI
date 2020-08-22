import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * <h1>Creates log file</h1> 
 * The LogGenerator class as previously mentioned
 * creates the Log file. It logs each CPU usage for 1000 servers every minute.
 * Therefore, writing 2000 logs every minute for 24 hrs i.e. 1440 minutes. All
 * the logs are written to a single text file.
 *
 * @author Aravind Vicinthangal Prathivaathi
 * @version 1.0
 * @since 2019-07-09
 */

public class LogGenerator {
	//Starting Time stamp given in the question 
	static String givenTimeStamp = "31/10/2014 00:00";
	//The format of the given time stamp
	static String dateFormat = "dd/MM/yyyy HH:mm";
	//The header of the log file to be created
	static String header = "timestamp IP cpu_id usage";
	
	// Total number of minutes in a day
	final static int MINUTES = 1440;

	// Total Number of servers
	final static int SERVERS = 1000;
	
	// IP Address is a 32 bit number meaning each part is 8 bit and the maximum
	// value each 8 bit part can take is 255
	final static int IP_OCTET_MAX = 255;

	/**
	 * Convert time stamp to Unix time
	 * 
	 * @param timeCount timeCount to increment time stamp by minute
	 * @return string Unix time stamp
	 */
	public static String convertToUnixTime(int timeCount) {
		SimpleDateFormat dateF = new SimpleDateFormat(dateFormat);
		String unixTime = null;

		try {
			Date date = dateF.parse(givenTimeStamp);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.MINUTE, timeCount);
			long time = (long) cal.getTime().getTime();
			long epochs = time / 1000L;
			unixTime = Long.toString(epochs);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return unixTime;

	}

	/**
	 * Generate data and write it to the created log file.
	 *
	 * @param file File object for the given file
	 *
	 */
	static void generateLog(final File file) {
		ServerDetails sd[] = new ServerDetails[SERVERS];
		String logContent;

		final Random rand = new Random();
		try (BufferedWriter bWrite = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {

			bWrite.write(header);
			bWrite.newLine();

			for (int timeCount = 0; timeCount < MINUTES; timeCount++) {
				int ip3 = 1;
				int ip4 = 1;
				for (int serverCount = 0; serverCount < SERVERS; serverCount++) {
					sd[serverCount] = new ServerDetails();
					final String unixTime = convertToUnixTime(timeCount);

					sd[serverCount].cpuId1 = 0;
					sd[serverCount].cpuId2 = 1;

					sd[serverCount].cpuUsage1 = rand.nextInt(100);
					sd[serverCount].cpuUsage2 = rand.nextInt(100);

					if (ip4 <= IP_OCTET_MAX) {
						sd[serverCount].ipAddr[2] = ip3;
						sd[serverCount].ipAddr[3] = ip4++;
					} else {
						ip4 = 1;
						ip3++;
						sd[serverCount].ipAddr[2] = ip3;
						sd[serverCount].ipAddr[3] = ip4++;
					}

					String ip = "";

					for (int i = 0; i < 3; i++) {
						ip = ip + sd[serverCount].ipAddr[i] + ".";
					}
					ip += sd[serverCount].ipAddr[3];
					logContent = unixTime + " " + ip + " " + sd[serverCount].cpuId1 + " " + sd[serverCount].cpuUsage1;
					bWrite.write(logContent);
					bWrite.newLine();
					logContent = unixTime + " " + ip + " " + sd[serverCount].cpuId2 + " " + sd[serverCount].cpuUsage2;
					bWrite.write(logContent);
					bWrite.newLine();
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Create a directory
	 *
	 * @param file File object for the given directory name
	 *
	 */
	static void createDirectory(final File dir) {
		if (!dir.exists()) {
			System.out.println("Directory does not exist");
			if (!(dir.mkdirs())) {
				System.out.println("Unable to create directory");
			}

		}
	}

	/**
	 * Create a new log file
	 *
	 * @param file     File object for the given file
	 * @param fileName Name of the file
	 */
	static void createFile(final File file, String fileName) throws IOException {
		if (!file.exists()) {
			if (!(file.createNewFile())) {
				System.out.println("Unable to create new file, " + fileName);
				System.exit(1);
			}
		}
	}

	/**
	 * Main method which controls the flow of the program
	 *
	 * @param args command line arguments specifying the name of the file and the
	 *             path of the directory at which the file should be stored/
	 */
	public static void main(String[] args) {

		String fileName;
		File file;
		if (args.length != 0) {
			fileName = args[0];
		} else {
			fileName = "cpuLogs.txt";
		}

		try {
			if (args.length > 1) {
				final File logDir = new File(args[1]);
				createDirectory(logDir);

				file = new File(logDir, fileName);

			} else {
				file = new File(fileName);
			}

			generateLog(file);
			createFile(file, fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
