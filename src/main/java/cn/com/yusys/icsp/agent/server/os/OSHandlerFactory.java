package cn.com.yusys.icsp.agent.server.os;

public class OSHandlerFactory
{
    private static final String WIN_FLAG = "windows";
    private static String OSName;
    private static boolean win;
    private static OSTypeEnum osType;
    
    public static String getCurrentOS() {
        return OSHandlerFactory.OSName;
    }
    
    public static boolean isWin() {
        return OSHandlerFactory.win;
    }
    
    public static OSHandler getOSHandler() {
        switch (OSHandlerFactory.osType) {
            case WIN: {
                return new WinShellHandler();
            }
            default: {
                return new LinuxShellHandler();
            }
        }
    }
    
    static {
        OSHandlerFactory.OSName = System.getProperty("os.name");
        OSHandlerFactory.win = (OSHandlerFactory.OSName.toLowerCase().indexOf("windows") >= 0);
        OSHandlerFactory.osType = (OSHandlerFactory.win ? OSTypeEnum.WIN : OSTypeEnum.LINUX);
    }
}
