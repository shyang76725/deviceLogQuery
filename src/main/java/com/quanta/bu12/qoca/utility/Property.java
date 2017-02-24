package com.quanta.bu12.qoca.utility;

public class Property {
	private static String serverPort;
	private static String hostUser;
	private static String hostPw;
	private static String nodeUser;
	private static String nodepw;
	private static String nodeIp;
	private static String deviceLogLocation;
	private static String OPENAM_INTERNAL_URI;
	private static String OPENAM_ADMIN_USERNAME;
	private static String OPENAM_ADMIN_PASSWORD;
	private static String dbIp;
	private static String dbUser;
	private static String dbPw;
	
	public static String getServerPort() {
		return serverPort;
	}
	public static void setServerPort(String serverPort) {
		Property.serverPort = serverPort;
	}
	public static String getHostUser() {
		return hostUser;
	}
	public static void setHostUser(String hostUser) {
		Property.hostUser = hostUser;
	}
	public static String getHostPw() {
		return hostPw;
	}
	public static void setHostPw(String hostPw) {
		Property.hostPw = hostPw;
	}
	public static String getNodeUser() {
		return nodeUser;
	}
	public static void setNodeUser(String nodeUser) {
		Property.nodeUser = nodeUser;
	}
	public static String getNodepw() {
		return nodepw;
	}
	public static void setNodepw(String nodepw) {
		Property.nodepw = nodepw;
	}
	public static String getNodeIp() {
		return nodeIp;
	}
	public static void setNodeIp(String nodeIp) {
		Property.nodeIp = nodeIp;
	}
	public static String getDeviceLogLocation() {
		return deviceLogLocation;
	}
	public static void setDeviceLogLocation(String deviceLogLocation) {
		Property.deviceLogLocation = deviceLogLocation;
	}
	public static String getOPENAM_INTERNAL_URI() {
		return OPENAM_INTERNAL_URI;
	}
	public static void setOPENAM_INTERNAL_URI(String oPENAM_INTERNAL_URI) {
		OPENAM_INTERNAL_URI = oPENAM_INTERNAL_URI;
	}
	public static String getOPENAM_ADMIN_USERNAME() {
		return OPENAM_ADMIN_USERNAME;
	}
	public static void setOPENAM_ADMIN_USERNAME(String oPENAM_ADMIN_USERNAME) {
		OPENAM_ADMIN_USERNAME = oPENAM_ADMIN_USERNAME;
	}
	public static String getOPENAM_ADMIN_PASSWORD() {
		return OPENAM_ADMIN_PASSWORD;
	}
	public static void setOPENAM_ADMIN_PASSWORD(String oPENAM_ADMIN_PASSWORD) {
		OPENAM_ADMIN_PASSWORD = oPENAM_ADMIN_PASSWORD;
	}
	public static String getDbIp() {
		return dbIp;
	}
	public static void setDbIp(String dbIp) {
		Property.dbIp = dbIp;
	}
	public static String getDbUser() {
		return dbUser;
	}
	public static void setDbUser(String dbUser) {
		Property.dbUser = dbUser;
	}
	public static String getDbPw() {
		return dbPw;
	}
	public static void setDbPw(String dbPw) {
		Property.dbPw = dbPw;
	}

}
