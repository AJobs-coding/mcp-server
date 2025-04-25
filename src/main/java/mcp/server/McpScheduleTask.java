package mcp.server;

import io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class McpScheduleTask {
    @Autowired
    private WebMvcSseServerTransportProvider webMvcSseServerTransportProvider;

    /**
     * 每三十分钟一次,使无效的sse断开
     * org.apache.catalina.connector.ClientAbortException 后使sse断开
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void checkPing() {
        webMvcSseServerTransportProvider.checkPing();
    }
}
