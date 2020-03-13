package cn.com.yusys.icsp.agent.server.os;

public class WinShellHandler extends AbstractOSHandle
{
    private String[] winCmd;
    
    public WinShellHandler() {
        this.winCmd = new String[] { "cmd.exe", "/C" };
    }
    
    @Override
    public String[] addCmdPrefix(final String[] cmd) {
        final String[] exec = new String[this.winCmd.length + cmd.length];
        System.arraycopy(this.winCmd, 0, exec, 0, this.winCmd.length);
        System.arraycopy(cmd, 0, exec, 2, cmd.length);
        return exec;
    }
}
