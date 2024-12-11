package com.example.client.handler;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class ClientWebSocketHandler extends TextWebSocketHandler {

    private WebSocketClient serverClient;
    private final URI serverUri;
    private final int RECONNECT_DELAY_MS = 5000; // Delay between reconnection attempts (5 seconds)
    private boolean reconnecting = false;

    // Initialize the server URI in the constructor
    public ClientWebSocketHandler() throws URISyntaxException {
        this.serverUri = new URI("ws://localhost:9090/server/ws");
    }

    @PostConstruct
    public void init() {
        try {
            connectToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket connection established with Python client");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Session: " + session);
        System.out.println("Message received from Python: " + message.getPayload());

        // Send acknowledgment back to the Python client
        session.sendMessage(new TextMessage("Acknowledged: " + message.getPayload()));

        // Forward the message to the server
        sendToServer(message.getPayload());
    }

    private void connectToServer() {
        while (true) { // Infinite loop for reconnection attempts
            try {
                serverClient = new WebSocketClient(serverUri) {
                    @Override
                    public void onOpen(ServerHandshake handshakedata) {
                        // Use the connection URI as a unique identifier
                        String connectionInfo = serverClient.getURI().toString();
                        System.out.println("Connected to Java WebSocket server with connection info: " + connectionInfo);
                    }

                    @Override
                    public void onMessage(String message) {
                        System.out.println("Message from server: " + message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        System.out.println("Disconnected from Java WebSocket server: " + reason);
                        scheduleReconnect();
                    }

                    @Override
                    public void onError(Exception ex) {
                        // Handle errors silently or log a custom message
                        System.out.println("Attempting to reconnect...");
                    }
                };

                System.out.println("Attempting to connect to the WebSocket server...");
                serverClient.connectBlocking(); // Blocks until connected
                break; // Exit the loop when connection is successful

            } catch (Exception e) {
                // Log a simple message instead of printing stack trace
                System.out.println("Unable to connect to the WebSocket server. Retrying in 5 seconds...");

                // Delay before the next connection attempt
                try {
                    Thread.sleep(5000); // Wait 5 seconds before retrying
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    break; // Exit loop if interrupted
                }
            }
        }
    }

    private void scheduleReconnect() {
        if (!reconnecting) {
            reconnecting = true; // Avoid multiple reconnection attempts
            new Thread(() -> {
                try {
                    System.out.println("Reconnecting to the server WebSocket in " + RECONNECT_DELAY_MS / 1000 + " seconds...");
                    Thread.sleep(RECONNECT_DELAY_MS);
                    connectToServer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void sendToServer(String message) {
        try {
            if (serverClient == null || !serverClient.isOpen()) {
                System.out.println("Server connection not open. Reconnecting...");
                connectToServer();
            }

            if (serverClient != null && serverClient.isOpen()) {
                serverClient.send(message);
                System.out.println("Message forwarded to server: " + message);
            } else {
                System.out.println("Failed to forward message: Server connection is still not open.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
