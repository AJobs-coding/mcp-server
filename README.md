# 项目依赖
spring ai 1.0.0-M7

# 版本异常问题
 ## 配置问题  
org.springframework.ai.mcp.server.autoconfigure.McpServerProperties 中出现的一下配置属性不生效
- baseUrl
- sseMessageEndpoint

解决办法：  
重写类  
- mcp.server.autoconfiguration.McpWebMvcServerTransportAutoConfiguration
- io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider  
![img.png](imgs/1-img.png)  


# 自定义功能
## session没有timeout功能  
解决办法：  
- org.springframework.ai.mcp.server.autoconfigure.McpServerProperties 新增 sessionTimeOutSecond 属性，单位：秒
![img.png](imgs/img.png)
- 重写： io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider   
1.充血 构造函数  
![img_1.png](imgs/img_1.png)  
2.io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider.handleMessage  
![img_2.png](imgs/img_2.png)

## 新增手动清除session的message
message种类： mcp.server.constant.MessageEnum  
message注入形式： io.modelcontextprotocol.server.transport.McpServerUtil  

client需要新增代码,这里用java的客户端 McpAsyncClient 为例，新增方法：
```java
	/**
	 * 会话优雅关闭
	 * @return
	 */
	public Mono<McpSchema.JSONRPCResponse> sessionCloseGracefully() {
		return this.mcpSession.sendRequest(MessageEnum.sessionCloseGracefully.name(), null,
				new TypeReference<>() {
				});
	}
```

