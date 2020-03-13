package cn.com.yusys.icsp.agent.server.os;

public class LinuxShellHandler extends AbstractOSHandle
{
    private String[] linuxCmd;
    
    public LinuxShellHandler() {
        this.linuxCmd = new String[] { "/bin/sh", "-c" };
    }
    
    @Override
    public String[] addCmdPrefix(final String[] cmd) {
        final String[] exec = new String[this.linuxCmd.length + cmd.length];
        System.arraycopy(this.linuxCmd, 0, exec, 0, this.linuxCmd.length);
        System.arraycopy(cmd, 0, exec, 2, cmd.length);
        return exec;
    }
}
