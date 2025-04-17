package io.modelcontextprotocol.server.transport;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerSession;
import mcp.server.constant.MessageEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.Map;

public class McpServerUtil {
    private static final Logger logger = LoggerFactory.getLogger(McpServerUtil.class);


    /**
     * 添加自定义的请求处理器
     * @param session
     * @param sessionId
     */
    public static void addCustomerRequestHandler(McpServerSession session, String sessionId) {
        try {
            Class<? extends McpServerSession> sessionClass = session.getClass();
            Field requestHandlersField = sessionClass.getDeclaredField("requestHandlers");
            requestHandlersField.setAccessible(true);
            Map requestHandlers = (Map) requestHandlersField.get(session);
            requestHandlers.put(MessageEnum.sessionCloseGracefully.name(), (McpServerSession.RequestHandler) (exchange, request) -> {
                session.closeGracefully().block();
                return Mono.just(new McpSchema.JSONRPCResponse(MessageEnum.sessionCloseGracefully.name(), MessageEnum.sessionCloseGracefully.ordinal(), null, null));
            });
        } catch (Exception e) {
            logger.error("解析方法失败: {}", e.getMessage());
        }
    }
}
