package com.example.server.repository;

import com.example.server.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DeviceRepository extends MongoRepository<Device, String> {
    List<Device> findByClientId(String clientId);
}
