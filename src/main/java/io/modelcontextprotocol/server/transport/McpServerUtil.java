package io.modelcontextprotocol.server.transport;

import com.alibaba.fastjson2.JSON;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerSession;
import mcp.server.constant.MessageEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;

public class McpServerUtil {
    private static final Logger logger = LoggerFactory.getLogger(McpServerUtil.class);

    public static final String SSE_SESSION_ID = "sseSessionId";


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


    public static Object buildTypedArgument(Object value, Parameter parameter) {
        if (value == null) {
            return null;
        }
        Class<?> type = parameter.getType();
        Assert.notNull(value, "value cannot be null");
        Assert.notNull(type, "type cannot be null");

        var javaType = ClassUtils.resolvePrimitiveIfNecessary(type);

        if (javaType == String.class) {
            return value.toString();
        }
        else if (javaType == Byte.class) {
            return Byte.parseByte(value.toString());
        }
        else if (javaType == Integer.class) {
            return Integer.parseInt(value.toString());
        }
        else if (javaType == Short.class) {
            return Short.parseShort(value.toString());
        }
        else if (javaType == Long.class) {
            return Long.parseLong(value.toString());
        }
        else if (javaType == Double.class) {
            return Double.parseDouble(value.toString());
        }
        else if (javaType == Float.class) {
            return Float.parseFloat(value.toString());
        }
        else if (javaType == Boolean.class) {
            return Boolean.parseBoolean(value.toString());
        }
        else if (javaType.isEnum()) {
            return Enum.valueOf((Class<Enum>) javaType, value.toString());
        }

        try {
            Type parameterizedType = parameter.getParameterizedType();
            return JSON.parseObject(value.toString(), parameterizedType);
        } catch (Exception e) {
            logger.error("io.modelcontextprotocol.server.transport.McpServerUtil.buildTypedArgument", e);
        }

        String json = JsonParser.toJson(value);
        return JsonParser.fromJson(json, javaType);
    }
}
