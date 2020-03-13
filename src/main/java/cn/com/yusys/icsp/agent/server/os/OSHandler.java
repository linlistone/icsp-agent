package cn.com.yusys.icsp.agent.server.os;

import java.io.File;

import cn.com.yusys.icsp.agent.common.beans.ExecResult;
import cn.com.yusys.icsp.agent.common.exception.AgentException;

public interface OSHandler
{
    ExecResult execCmd(ExecResult p0, StringBuilder p1, StringBuilder p2, String[] p3, File p4, String... p5) throws AgentException;
}
