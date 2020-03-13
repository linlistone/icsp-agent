package cn.com.yusys.icsp.agent.server.start;

import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.Objects;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import cn.com.yusys.icsp.agent.common.util.AgentUtil;
import cn.com.yusys.icsp.agent.server.beans.StartParameter;
import cn.com.yusys.icsp.agent.server.mxbean.AgentManagement;
import cn.com.yusys.icsp.agent.server.mxbean.ApplicationManagement;

public final class JMXServiceRegistry {
	private static final Logger logger = LoggerFactory.getLogger(JMXServiceRegistry.class);

	private static JMXConnectorServer connServer;

	public static void start() throws Exception {

		LocateRegistry.createRegistry(StartParameter.parameter.getRmiPort());

		MBeanServer server = ManagementFactory.getPlatformMBeanServer();

		server.registerMBean(new ApplicationManagement(), new ObjectName("Agent:name=ApplicationManagement"));

		server.registerMBean(new AgentManagement(), new ObjectName("Agent:name=AgentManagement"));

		if (StartParameter.parameter.isOpenHtml()) {
			final ObjectName adapterName = new ObjectName("Agent:name=htmladapter");
			final HtmlAdaptorServer adapter = new HtmlAdaptorServer(StartParameter.parameter.getHtmlPort());
			adapter.start();
			server.registerMBean(adapter, adapterName);
		}

		String jmxUrl = AgentUtil.builder(100, "service:jmx:rmi://localhost",
				StartParameter.parameter.getRegRmiPort() > 0 ? ":" + StartParameter.parameter.getRegRmiPort() : "",
				"/jndi/rmi://localhost:", StartParameter.parameter.getRmiPort(), "/jmxrmi");

		logger.info("jmxUrl" + jmxUrl);
		final JMXServiceURL url = new JMXServiceURL(jmxUrl);

		connServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, null);
		server.registerMBean(connServer, new ObjectName("JMX:name=JMXConnectorServer"));

		connServer.start();

		// (JMXServiceRegistry.connServer =
		// JMXConnectorServerFactory.newJMXConnectorServer(url, null, server)).start();
	}

	public static void stop() throws Exception {
		if (Objects.nonNull(JMXServiceRegistry.connServer)) {
			if (StartParameter.parameter.isOpenHtml()) {
				JMXServiceRegistry.connServer.getMBeanServer()
						.unregisterMBean(new ObjectName("Agent:name=htmladapter"));
			}
			JMXServiceRegistry.connServer.stop();
		}
	}

	static {
		JMXServiceRegistry.connServer = null;
	}
}
