package cn.com.yusys.icsp.agent.common.beans;

public class AgentRegistryInfo {
	private String hostname;
	private String hostAddress;
	private int rmiPort;
	private int socketPort;
	private String registryTime;
	private String version;
	private String status;
	private String rmiStatus;
	private String socketStatus;
	private String appWorkspace;
	private String osName;

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(final String status) {
		this.status = status;
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

	public String getRegistryTime() {
		return this.registryTime;
	}

	public void setRegistryTime(final String registryTime) {
		this.registryTime = registryTime;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getHostname() {
		return this.hostname;
	}

	public void setHostname(final String hostname) {
		this.hostname = hostname;
	}

	public String getHostAddress() {
		return this.hostAddress;
	}

	public void setHostAddress(final String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getRmiStatus() {
		return this.rmiStatus;
	}

	public void setRmiStatus(final String rmiStatus) {
		this.rmiStatus = rmiStatus;
	}

	public String getSocketStatus() {
		return this.socketStatus;
	}

	public void setSocketStatus(final String socketStatus) {
		this.socketStatus = socketStatus;
	}

	public String getAppWorkspace() {
		return this.appWorkspace;
	}

	public void setAppWorkspace(final String appWorkspace) {
		this.appWorkspace = appWorkspace;
	}
}
