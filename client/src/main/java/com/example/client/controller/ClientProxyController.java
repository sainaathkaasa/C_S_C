package com.example.client.controller;

import com.example.client.config.ClientWebSocketConfig;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ClientProxyController {

    @Autowired
    private ClientWebSocketConfig config;

    @PostMapping("/createuser")
    public ResponseEntity<String> createUser(@RequestBody Map<String, String> userData) {
        try {
            // URL of the server
            URL url = new URL(config.getServerUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            // Convert data to JSON
            Gson gson = new Gson();
            String jsonData = gson.toJson(userData);

            // Send JSON data to the server
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get response from the server
            int responseCode = connection.getResponseCode();
            return new ResponseEntity<>("Response Code: " + responseCode, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("clientid/{clientId}/registerdevice")
    public ResponseEntity<String> registerDevice(@PathVariable String clientId, @RequestBody Map<String, String> registredData) {
        try {
            // Construct the URL for the other Spring Boot endpoint
            String urlString = "http://localhost:9090/devices/clientid/" + clientId + "/deviceregister";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the HTTP request method to POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            // Serialize the request data into JSON format
            Gson gson = new Gson();
            String jsonData = gson.toJson(registredData);

            // Write the JSON data to the connection's output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Log response headers
            connection.getHeaderFields().forEach((key, values) -> {
                System.out.println(key + ": " + values);
            });

            // Get the response code from the server
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                // If the request was successful, return a success message
                return new ResponseEntity<>("Device registered successfully", HttpStatus.OK);
            } else {
                // Otherwise, return an error message with the response code
                return new ResponseEntity<>("Error: " + responseCode, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
