package org.rag4j.agent.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FavouritesMcpRemoteApp {
    public static void main(String[] args) {
        SpringApplication.run(FavouritesMcpRemoteApp.class, args);
    }

    @Bean
    public ToolCallbackProvider favouritesTools(FavouritesService favouritesService) {
        return MethodToolCallbackProvider.builder().toolObjects(favouritesService).build();
    }
}
