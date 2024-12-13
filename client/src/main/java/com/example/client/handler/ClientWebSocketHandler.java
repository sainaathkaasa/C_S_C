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
public class ClientWebSocketHandler extends TextWebSocketHandler implements MosquittoMqttSubscriber.MqttMessageListener {

    private final MosquittoMqttSubscriber mqttSubscriber;
    private WebSocketClient serverClient;
    private final URI serverUri;
    private final int RECONNECT_DELAY_MS = 5000; // Delay between reconnection attempts
    private boolean reconnecting = false;

    public ClientWebSocketHandler(MosquittoMqttSubscriber mqttSubscriber) throws URISyntaxException {
        this.mqttSubscriber = mqttSubscriber;
        this.serverUri = new URI("ws://localhost:9090/server/ws");
    }

    @PostConstruct
    public void init() {
        try {
            connectToServer();

            // Subscribe to MQTT messages
            mqttSubscriber.addListener(this);
            mqttSubscriber.subscribe("client1", "device1"); // Replace with dynamic IDs if necessary

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("WebSocket connection established with Python client");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Message received from Python: " + message.getPayload());

        // Send acknowledgment back to the Python client
        session.sendMessage(new TextMessage("Acknowledged: " + message.getPayload()));
    }

    @Override
    public void onMessage(String topic, byte[] message) {
        String payload = new String(message);
        System.out.println("Forwarding MQTT message to WebSocket server: " + payload);

        sendToServer(payload);
    }

    private void connectToServer() {
        while (true) {
            try {
                serverClient = new WebSocketClient(serverUri) {
                    @Override
                    public void onOpen(ServerHandshake handshakedata) {
                        System.out.println("Connected to WebSocket server: " + serverUri);
                    }

                    @Override
                    public void onMessage(String message) {
                        System.out.println("Message from server: " + message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        System.out.println("WebSocket server disconnected: " + reason);
                        scheduleReconnect();
                    }

                    @Override
                    public void onError(Exception ex) {
                        System.out.println("WebSocket error: " + ex.getMessage());
                    }
                };

                System.out.println("Attempting to connect to WebSocket server...");
                serverClient.connectBlocking();
                break;

            } catch (Exception e) {
                System.out.println("WebSocket connection failed. Retrying in 5 seconds...");
                try {
                    Thread.sleep(RECONNECT_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void scheduleReconnect() {
        if (!reconnecting) {
            reconnecting = true;
            new Thread(() -> {
                try {
                    Thread.sleep(RECONNECT_DELAY_MS);
                    connectToServer();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    reconnecting = false;
                }
            }).start();
        }
    }

    private void sendToServer(String message) {
        try {
            if (serverClient != null && serverClient.isOpen()) {
                serverClient.send(message);
                System.out.println("Message sent to WebSocket server: " + message);
            } else {
                System.out.println("WebSocket connection is not open. Cannot send message.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
