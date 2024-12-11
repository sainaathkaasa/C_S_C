package com.example.server.controller;

import com.example.server.model.Client;
import com.example.server.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    @Autowired
    private ClientRepository clientRepository;

    @PostMapping("/registeruser")
    public Client createClient(@RequestBody Client client) {

        return clientRepository.save(client);
    }

    @GetMapping("/{id}")
    public Client getClientById(@PathVariable String id) {
        return clientRepository.findById(id).orElse(null);
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
}
