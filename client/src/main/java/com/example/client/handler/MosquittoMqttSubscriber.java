package com.example.client.handler;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MosquittoMqttSubscriber {

    private static final String BROKER_URL = "tcp://13.202.26.35:1883";
    private final List<MqttMessageListener> listeners = new ArrayList<>();

    public void subscribe(String clientId, String deviceId) {
        String topic = String.format("uplink/+/+/%s/%s/telemetry", clientId, deviceId);
        try {
            MqttClient client = new MqttClient(BROKER_URL, clientId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("MQTT connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("MQTT message received: " + new String(message.getPayload()));
                    notifyListeners(topic, message.getPayload());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // No action needed for subscribers
                }
            });

            client.connect(options);
            System.out.println("Connected to MQTT broker: " + BROKER_URL);
            client.subscribe(topic);
            System.out.println("Subscribed to topic: " + topic);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void addListener(MqttMessageListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(String topic, byte[] message) {
        for (MqttMessageListener listener : listeners) {
            listener.onMessage(topic, message);
        }
    }

    public interface MqttMessageListener {
        void onMessage(String topic, byte[] message);
    }
}
