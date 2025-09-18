# Add an MCP server to manage favorite talks
In this assignment, you will add a new feature to the TalksAgent. You'll add an MCP server to manage the favorite talks of the users. The MCP server is already implemented for you in the favourites-mcp module. You only need configure the MCP client.

```text
> The MCP server is in the favourites-mcp module. First, you need to build the module.
- From the root of the project, run `mvn clean install -DskipTests -pl favourites-mcp`
> Next you can test the MCP server with the MCP inspector. (Optional)
- You can skip this step if you do not have npx installed or don't want to install the inepctor.
- In the file src/test/resources/mcp-servers-config.json, check the configuration and change the absolute path to the jar.
- From the root of the project, run:
npx @modelcontextprotocol/inspector --cli \
  --config ./src/test/resources/mcp-servers-config.json \
  --server location \
  --method tools/list
```

The output of the npx command should be something like this:

```json
{
  "tools": [
    {
      "name": "add-favourite",
      "description": "Add a favourite talk for a user.",
      "inputSchema": {
        "type": "object",
        "properties": {
          "userId": {
            "type": "string"
          },
          "favouriteTalk": {
            "type": "object",
            "properties": {
              "speakers": {
                "type": "array",
                "items": {
                  "type": "string"
                }
              },
              "title": {
                "type": "string"
              }
            },
            "required": [
              "speakers",
              "title"
            ]
          }
        },
        "required": [
          "userId",
          "favouriteTalk"
        ],
        "additionalProperties": false
      }
    },
    {
      "name": "list-favourites",
      "description": "List all favourite talks for a user, with optional filtering by speaker name.",
      "inputSchema": {
        "type": "object",
        "properties": {
          "userId": {
            "type": "string"
          },
          "speakerNameFilter": {
            "type": "string"
          }
        },
        "required": [
          "userId",
          "speakerNameFilter"
        ],
        "additionalProperties": false
      }
    }
  ]
}
```

Now we are ready to configure the MCP client and provide the client to the TalksAgent.

```
> Add the following configuration to the application.yml file in the web project.

spring:
  ai:
    mcp:
      client:
        enabled: true
        type: SYNC
        stdio:
          connections:
            location:
              command: java
              args:
                - -Dspring.ai.mcp.server.stdio=true
                - -jar
                - "${FAVOURITES_MCP_JAR:${user.dir}/favourites-mcp/target/favourites-mcp-1.0.0-SNAPSHOT.jar}"
```

MCP provided tools can be used by an Agent through the registration of a ToolCallbackProvider. The `SyncMcpToolCallbackProvider` that needs a list of `McpSyncClients`. These clients can be injected by Spring.

```
> Inject `List<McpSyncClient> mcpSyncClients` into configuration of the TalksAgent.
> Inject the SyncMcpToolCallbackProvider into the constructor of the TalksAgent.
> In the method doInvoke, add the SyncMcpToolCallbackProvider to the ToolCallbacks.
> Fix all the compile errors.
> Change the prompt of the TalksAgent to include the following text or something similar:

You can also manage favourites for users. You can add talks and list them.
When a user asks for a list of favourite talks, they can filter the talks by speaker.

> Restart the application
> Ask the agent to add a talk to your favourites and list your favourite talks.
```

In the root of your project, you can find a folder logs. In this folder, you can find the log files of the application. The log files are rotated daily. You can use these logs to see what is happening in the application. Check the logs if something does not work as expected. (Hint: check the userId the agent is using).

Next to the folder logs, you can find a folder data. In this folder, the MCP server stores the favourite talks in a file called favourites.json. You can check this file to see if the favourites are stored correctly.

We could add the user id to the prompt, but that would not be very secure. A better solution is to use the userId as the conversationId for the memory and the MCP client. This way, each user has its own memory and its own favourites.

```
> Check the classes `FixArgumentsSyncMcpToolCallback` and `ToolCallbackUtility` in the springai-agent module for an example of how to do this.
> Change the TalksAgent and the configuration to pass the callbacks instead of the provider.
> Ask the agent to add all talks about embabel to your favourites. Check the logs and the favourites.json file to see if the userId is used correctly.
```

