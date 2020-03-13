package cn.com.yusys.icsp.agent.server.mxbean;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.com.yusys.icsp.agent.common.constants.AgentStatusEnum;
import cn.com.yusys.icsp.agent.common.mxbean.AgentManagementMXBean;
import cn.com.yusys.icsp.agent.server.beans.StartParameter;
import cn.com.yusys.icsp.agent.server.start.AgentRegistry;
import cn.com.yusys.icsp.agent.server.start.JMXServiceRegistry;
import cn.com.yusys.icsp.agent.server.start.SocketServiceRegistry;
import cn.com.yusys.icsp.agent.server.util.AgentServerUtil;

public class AgentManagement implements AgentManagementMXBean
{
    private static final Logger logger;
    
    @Override
    public String getAgentStatus() {
        final boolean rmiIsListen = AgentServerUtil.isListen(StartParameter.parameter.getRmiPort());
        final boolean socketIsListen = AgentServerUtil.isListen(StartParameter.parameter.getSocketPort());
        final Map<String, String> portStatus = new HashMap<String, String>(4);
        portStatus.put("rmiStatus", this.getAgentStatus(rmiIsListen));
        portStatus.put("socketStatus", this.getAgentStatus(socketIsListen));
        final String status = JSON.toJSONString(portStatus);
        AgentManagement.logger.info("anget-server当前状态为:{}", status);
        return status;
    }
    
    public String getAgentStatus(final boolean isListen) {
        return isListen ? AgentStatusEnum.UP.toString() : AgentStatusEnum.DOWN.toString();
    }
    
    @Override
    public String shutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(5000L);
                AgentRegistry.stop();
                JMXServiceRegistry.stop();
                AgentManagement.logger.warn("Agent-Server jmx port stop success!");
                SocketServiceRegistry.stop();
                AgentManagement.logger.warn("Agent-Server socket port stop success!");
            }
            catch (Exception e) {
                AgentManagement.logger.error("Agent-Server 下线失败! cause by:{}", e.getMessage());
            }
            return;
        }).start();
        return "Bye bye!";
    }
    
    static {
        logger = LoggerFactory.getLogger(AgentManagement.class);
    }
}
