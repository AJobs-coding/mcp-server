package mcp.server;

import io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class McpScheduleTask {
    @Autowired
    private WebMvcSseServerTransportProvider webMvcSseServerTransportProvider;

    /**
     * 每十分钟一次
     * 出现 org.apache.catalina.connector.ClientAbortException 后会断开sse
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void checkPing() {
        webMvcSseServerTransportProvider.checkPing();
    }
}
