package mcp.server.autoconfiguration;

import mcp.server.McpScheduleTask;
import org.springframework.ai.mcp.server.autoconfigure.McpServerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class McpCustomConfiguration {


    @Bean
    @ConditionalOnProperty(prefix = McpServerProperties.CONFIG_PREFIX, name = "checkPing", havingValue = "true")
    public McpScheduleTask mcpScheduleTask() {
        return new McpScheduleTask();
    }
}
