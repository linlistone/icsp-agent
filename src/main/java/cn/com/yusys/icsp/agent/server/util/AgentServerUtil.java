package cn.com.yusys.icsp.agent.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yusys.icsp.agent.common.beans.ExecResult;
import cn.com.yusys.icsp.agent.common.util.AgentUtil;
import cn.com.yusys.icsp.agent.server.beans.StartParameter;
import cn.com.yusys.icsp.agent.server.os.OSHandlerFactory;

public final class AgentServerUtil {
	private static final Logger logger = LoggerFactory.getLogger(AgentServerUtil.class);

	public static boolean isListen(final boolean isWin, final int port) {
		final String command = AgentUtil.buffer(50, isWin ? "netstat -ano | findstr " : "netstat -anp |grep ", port);
		final String listen = isWin ? "LISTENING" : "LISTEN ";
		try {
			final ExecResult execResult = ShellUtils.execShell(new String[] { command }, null);
			return execResult.getOut().toLowerCase().contains("tcp") && execResult.getOut().contains(listen);
		} catch (Exception e) {
			AgentServerUtil.logger.error(e.getMessage());
			return false;
		}
	}

	public static boolean isListen(final int port) {
		return isListen(OSHandlerFactory.isWin(), port);
	}

	public static void parse(final String[] args) {
		final StartParameter startParameter = getDefaultStartParameter();
		if (args != null) {
			for (final String str : args) {
				final String[] keyValue = str.split("=");
				keyValue[0] = keyValue[0].substring(1);
				AgentUtil.setObjectFieldValue(startParameter, AgentUtil.getField(startParameter, keyValue[0]),
						convertValue(keyValue[0], keyValue[1]));
			}
		}
		StartParameter.parameter = startParameter;
	}

	public static StartParameter getDefaultStartParameter() {
		final StartParameter startParameter = new StartParameter();
		startParameter.setFilePath("/home/agent/app");
		startParameter.setHostname(AgentUtil.getHostName());
		startParameter.setHostAddress(AgentUtil.getHostAddress());
		startParameter.setRmiPort(1099);
		//startParameter.setRegRmiPort(9199);
		startParameter.setSocketPort(8085);
		startParameter.setHtmlPort(8082);
		startParameter.setRegistryAddresses("127.0.0.1:8100");
		startParameter.setOsName(System.getProperty("os.name").toLowerCase());
		return startParameter;
	}

	public static final Object convertValue(final String name, final Object value) {
		if ("socketPort,rmiPort,htmlPort".contains(name)) {
			return Integer.valueOf(String.valueOf(value));
		}
		if ("openHtml".contains(name)) {
			return Boolean.valueOf(String.valueOf(value));
		}
		return value;
	}

}
