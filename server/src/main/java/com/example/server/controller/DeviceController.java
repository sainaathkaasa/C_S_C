package com.example.server.controller;

import com.example.server.model.Client;
import com.example.server.model.Device;
import com.example.server.repository.ClientRepository;
import com.example.server.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
public class DeviceController {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ClientRepository clientRepository;

    @PostMapping("clientid/{clientId}/deviceregister")
    public ResponseEntity<Device> registerDevice(@PathVariable String clientId, @RequestBody Device device) {
//        System.out.println("Received request to register device for client ID: " + clientId);
        List<Client> allClientIDs = clientRepository.findAll();
        boolean clientExists = allClientIDs.stream()
                .anyMatch(client -> client.getId().equals(clientId));

        if (!clientExists) {
            // Return an error response if the client ID does not exist
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        device.setClientId(clientId);
        Device savedDevice = deviceRepository.save(device);
//        System.out.println("savedDevice: "+savedDevice);
//        System.out.println("HttpStatus.CREATED :"+HttpStatus.CREATED );
        return new ResponseEntity<>(savedDevice, HttpStatus.CREATED);
    }

    @GetMapping("/{clientId}")
    public List<Device> getDevicesByClient(@PathVariable String clientId) {
        return deviceRepository.findByClientId(clientId);
    }
}
