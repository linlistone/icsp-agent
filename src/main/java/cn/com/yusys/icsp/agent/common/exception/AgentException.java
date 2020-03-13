package cn.com.yusys.icsp.agent.common.exception;

public class AgentException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private String code;
    
    public AgentException(final String message) {
        super(message);
        this.code = "AGENT_EX";
    }
    
    public AgentException(final String code, final String message) {
        super(message);
        this.code = code;
    }
    
    public String getCode() {
        return this.code;
    }
}
