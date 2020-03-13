package cn.com.yusys.icsp.agent.server.mxbean;

import java.io.File;

import cn.com.yusys.icsp.agent.common.beans.ExecResult;
import cn.com.yusys.icsp.agent.common.exception.AgentException;
import cn.com.yusys.icsp.agent.common.mxbean.ApplicationManagementMXBean;
import cn.com.yusys.icsp.agent.server.util.ShellUtils;

public class ApplicationManagement implements ApplicationManagementMXBean
{
    @Override
    public ExecResult runCmd(final String dir, final String[] cmd) throws AgentException {
        return ShellUtils.execCmd(cmd, (dir == null) ? null : new File(dir));
    }
    
    @Override
    public ExecResult runShell(final String dir, final String[] cmd) throws AgentException {
        return ShellUtils.execShell(cmd, (dir == null) ? null : new File(dir));
    }
}
