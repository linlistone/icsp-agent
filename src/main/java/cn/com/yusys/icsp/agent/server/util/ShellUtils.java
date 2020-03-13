package cn.com.yusys.icsp.agent.server.util;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yusys.icsp.agent.common.beans.ExecResult;
import cn.com.yusys.icsp.agent.common.exception.AgentException;
import cn.com.yusys.icsp.agent.server.os.AbstractOSHandle;
import cn.com.yusys.icsp.agent.server.os.OSHandler;
import cn.com.yusys.icsp.agent.server.os.OSHandlerFactory;

public class ShellUtils
{
    private static final Logger logger;
    private static OSHandler handler;
    
    public static ExecResult execCmd(final String[] cmd, final File dir) throws AgentException {
        ShellUtils.logger.info("执行命令:{}", Arrays.toString(cmd));
        ExecResult result = new ExecResult();
        final StringBuilder in = new StringBuilder();
        final StringBuilder errIn = new StringBuilder();
        result = ShellUtils.handler.execCmd(result, in, errIn, null, dir, cmd);
        result.setErrOut(errIn.toString());
        result.setOut(in.toString());
        ShellUtils.logger.info("执行命令成功结果:{}", result);
        return result;
    }
    
    public static ExecResult execShell(final String[] cmd, final File dir) throws AgentException {
        ExecResult result = new ExecResult();
        final StringBuilder in = new StringBuilder();
        final StringBuilder errIn = new StringBuilder();
        if (Objects.nonNull(cmd) && cmd.length > 0) {
            for (final String s : cmd) {
                ShellUtils.logger.info("执行的命令为:{}", s);
                result = ShellUtils.handler.execCmd(result, in, errIn, null, dir, s);
                if (!"0".equals(result.getExitCode())) {
                    break;
                }
            }
            result.setErrOut(errIn.toString());
            result.setOut(in.toString());
        }
        ShellUtils.logger.info("执行命令成功结果:{}", result);
        return result;
    }
    
    static {
        logger = LoggerFactory.getLogger(AbstractOSHandle.class);
        ShellUtils.handler = OSHandlerFactory.getOSHandler();
    }
}
