//package com.example.client.websocket;
//
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//@Component
//public class ClientWebSocket {
//    private static final int RECONNECT_DELAY = 5000;  // 5 seconds
//    private URI clientUri;
//    private URI serverUri;
//    private WebSocketClient serverClient;
//
//    public ClientWebSocket() throws URISyntaxException {
//        this.clientUri = new URI("ws://localhost:9091/client/ws");
//        this.serverUri = new URI("ws://localhost:9090/server/ws");
//    }
//
//    @PostConstruct
//    public void init() {
//        connectToServer();
//        connectWithRetry();
//    }
//
//    private void connectWithRetry() {
//        new Thread(() -> {
//            while (true) {
//                try {
//                    WebSocketClient client = new WebSocketClient(clientUri) {
//                        @Override
//                        public void onOpen(ServerHandshake handshakedata) {
//                            System.out.println("Connected to Python WebSocket");
//                        }
//
//                        @Override
//                        public void onMessage(String message) {
//                            System.out.println("Received data from Python: " + message);
//                            sendToServer("hello");
//                        }
//
//                        @Override
//                        public void onClose(int code, String reason, boolean remote) {
//                            System.out.println("Disconnected from Python WebSocket. Attempting to reconnect...");
//                        }
//
//                        @Override
//                        public void onError(Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    };
//                    client.connectBlocking();
//                    break;  // Exit the loop if connection is successful
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                try {
//                    Thread.sleep(RECONNECT_DELAY);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//    private void connectToServer() {
//        new Thread(() -> {
//            while (true) {
//                try {
//                    serverClient = new WebSocketClient(serverUri) {
//                        @Override
//                        public void onOpen(ServerHandshake handshakedata) {
//                            System.out.println("Connected to Java WebSocket server");
//                        }
//
//                        @Override
//                        public void onMessage(String message) {
//                            System.out.println("Message from server: " + message);
//                        }
//
//                        @Override
//                        public void onClose(int code, String reason, boolean remote) {
//                            System.out.println("Disconnected from Java WebSocket server");
//                        }
//
//                        @Override
//                        public void onError(Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    };
//                    serverClient.connectBlocking();
//                    break;  // Exit the loop if connection is successful
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                try {
//                    Thread.sleep(RECONNECT_DELAY);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//    private void sendToServer(String message) {
//        try {
//            if (serverClient != null && serverClient.isOpen()) {
//                serverClient.send(message);
//                System.out.println("Message sent to server: " + message);
//            } else {
//                System.out.println("Server connection not open. Message not sent: " + message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
