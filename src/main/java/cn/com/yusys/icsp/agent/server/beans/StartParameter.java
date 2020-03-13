package cn.com.yusys.icsp.agent.server.beans;

public class StartParameter {
	public static StartParameter parameter ;
	private String hostname;
	private String hostAddress;
	private int rmiPort;
	private int regRmiPort;
	private int socketPort;
	private int htmlPort;
	private String registryAddresses;
	private String filePath;
	private String osName;
	private boolean openHtml;

	public StartParameter() {
		this.openHtml = true;
	}

	public String getHostname() {
		return this.hostname;
	}

	public void setHostname(final String hostname) {
		this.hostname = hostname;
	}

	public int getRmiPort() {
		return this.rmiPort;
	}

	public void setRmiPort(final int rmiPort) {
		this.rmiPort = rmiPort;
	}

	public int getSocketPort() {
		return this.socketPort;
	}

	public void setSocketPort(final int socketPort) {
		this.socketPort = socketPort;
	}

	public String getRegistryAddresses() {
		return this.registryAddresses;
	}

	public void setRegistryAddresses(final String registryAddresses) {
		this.registryAddresses = registryAddresses;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

	public int getHtmlPort() {
		return this.htmlPort;
	}

	public void setHtmlPort(final int htmlPort) {
		this.htmlPort = htmlPort;
	}

	public String getHostAddress() {
		return this.hostAddress;
	}

	public void setHostAddress(final String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public boolean isOpenHtml() {
		return this.openHtml;
	}

	public void setOpenHtml(final boolean openHtml) {
		this.openHtml = openHtml;
	}

	public int getRegRmiPort() {
		return regRmiPort;
	}

	public void setRegRmiPort(int regRmiPort) {
		this.regRmiPort = regRmiPort;
	}

	public String getOsName() {
		return osName;
	}
	public void setOsName(String osName) {
		this.osName = osName;
	}
	

}
