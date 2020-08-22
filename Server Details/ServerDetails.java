/**
 * Server Details
 * 
 * @author Aravind Vicinthangal Prathivaathi
 * @version 1.0
 * @since 2019-07-09
 */
public class ServerDetails {
	// Unix time stamp
	long timestamp;
	// Array which holds 4 IP octets
	int ipAddr[] = new int[4];
	// CPU IDs
	int cpuId1;
	int cpuId2;
	// CPU usage for the 2 CPUs in a server
	int cpuUsage1;
	int cpuUsage2;

	public ServerDetails() {
		// IP suffix 1
		ipAddr[0] = 192;
		// IP Suffix 2
		ipAddr[1] = 168;
	}

}
