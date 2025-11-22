package com.acare.backend.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.dto.ResponseDTO;
import com.acare.backend.entity.Service;
import com.acare.backend.repository.ServiceRepository;
import com.acare.backend.service.ServiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceRepository repo;

    private final ServiceService serviceService;

    @GetMapping /* tạo đường dẫn, phương thức GET */
    public ResponseEntity<List<Service>> getService() {
        List<Service> services = repo.findAll();
        services.sort(Comparator.comparing((Service s) -> s.getPrice()).reversed());
        return ResponseEntity.ok(services);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> addService(@RequestBody Service addedService) {
        System.out.println(addedService);
        List<Service> services = repo.findByName(addedService.getName().trim());
        if (!services.isEmpty()) {
            return ResponseEntity
                    .ok(new ResponseDTO(409, false, "SERVICE ALREADY EXIST", addedService));
        }
        Service newService = repo.save(addedService);
        return ResponseEntity
                .ok(new ResponseDTO(201, true, "ADD SERVICE SUCCESSFULLY", newService));

    }

    @GetMapping("/{id}")
    public Optional<Service> getService(@PathVariable Long id) {
        return repo.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateService(@PathVariable Long id, @RequestBody Service update) {
        Optional<Service> services = repo.findById(id);
        if (services.isEmpty()) {
            return ResponseEntity
                    .ok(new ResponseDTO(404, false, "SERVICE NOT FOUND", id));
        }
        Service service = services.get();
        if (update.getName() != null)
            service.setName(update.getName());
        if (update.getDescription() != null)
            service.setDescription(update.getDescription());
        if (update.getPrice() != null)
            service.setPrice(update.getPrice());

        repo.save(service);

        return ResponseEntity
                .ok(new ResponseDTO(200, true, "UPDATE SERVICE SUCCESSFULLY", service));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateService(@PathVariable Long id) {
        Optional<Service> services = repo.findById(id);
        if (services.isEmpty()) {
            return ResponseEntity
                    .ok(new ResponseDTO(404, false, "UPDATE SERVICE FAILED", id));
        }
        Service service = services.get();
        repo.deleteById(id);
        return ResponseEntity
                .ok(new ResponseDTO(200, true, "UPDATE SERVICE SUCCESSFULLY", service));

    }

    /* request Param */
    @GetMapping("/search")
    public ResponseEntity<List<Service>> searchService(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<Service> services = serviceService.searchServices(name, minPrice, maxPrice);
        services.sort(Comparator.comparing((Service s) -> s.getPrice()).reversed());
        return ResponseEntity.ok(services);
    }
    // Cach search: search?name=Hello&minPrice=100000&maxPrice=500000&active=true
}
/*
 * controller: nơi tiếp nhận thông tin và trả về thông tin
 * ví dụ: user truy cập vào 1 url (api endpoint) thì là 1 request lên server,
 * request đi qua controller, controller xử lí rồi gọi các tầng service bên dưới
 * rồi trả lại kết quả cho user
 */