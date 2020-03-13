package cn.com.yusys.icsp.agent.server.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yusys.icsp.agent.common.beans.ExecResult;
import cn.com.yusys.icsp.agent.common.exception.AgentException;

public abstract class AbstractOSHandle implements OSHandler {

	Logger logger = LoggerFactory.getLogger(AbstractOSHandle.class);

	@Override
	public ExecResult execCmd(final ExecResult result, final StringBuilder in, final StringBuilder errIn,
			final String[] env, final File dir, final String... cmd) throws AgentException {
		Process process = null;
		try {
			if (Objects.isNull(cmd) || cmd.length == 0) {
				throw new AgentException("为传递执行命令参数!");
			}
			final String[] execCmd = this.addCmdPrefix(cmd);
			for (int i = 0; i < execCmd.length; i++) {
				logger.info("execCmd:" + execCmd[i]);
			}
			process = Runtime.getRuntime().exec(execCmd, env, dir);
			try (final BufferedReader bufferIn = new BufferedReader(
					new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
					final BufferedReader bufferErrIn = new BufferedReader(
							new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = bufferIn.readLine()) != null) {
//					if (line.indexOf("/bin/sh -c") > -1) {
//						logger.info("过滤 line:" + line);
//					} else {
						in.append(line).append(System.lineSeparator());
						logger.info("line:" + line);
//					}
				}
				while ((line = bufferErrIn.readLine()) != null) {
					errIn.append(line).append(System.lineSeparator());
				}
				final int exitCode = process.waitFor();
				result.setExitCode(exitCode + "");
			}
		} catch (InterruptedException ie) {
		} catch (IOException e) {
			throw new AgentException("AGENT_EX", e.getMessage());
		} finally {
			closeProcess(process);
		}
		return result;
	}

	public abstract String[] addCmdPrefix(final String[] p0);

	private static void closeProcess(final Process process) {
		if (Objects.nonNull(process)) {
			process.destroy();
		}
	}
}
