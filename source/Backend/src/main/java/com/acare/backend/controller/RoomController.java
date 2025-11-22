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

import com.acare.backend.entity.Room;
import com.acare.backend.repository.RoomRepository;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomRepository repo;

    @GetMapping
    public List<Room> getRoom() {
        List<Room> rooms = repo.findAll();
        rooms.sort(Comparator.comparing((Room r) -> r.getLocation())
        .thenComparing((r) -> r.getName()));
        return rooms;
    }

    @GetMapping("/search")
    public List<Room> searchRooms(
            @RequestParam(required = false) String floor) {
        System.out.println("Search called with floor: " + floor);
        List<Room> rooms = repo.findAll();
        System.out.println("Total rooms before filter: " + rooms.size());
        
        // Filter by floor if provided
        if (floor != null && !floor.isEmpty()) {
            rooms = rooms.stream()
                    .filter(r -> {
                        boolean match = r.getLocation() != null && r.getLocation().contains(floor);
                        System.out.println("Room: " + r.getName() + ", Location: " + r.getLocation() + ", Match: " + match);
                        return match;
                    })
                    .collect(java.util.stream.Collectors.toList());
            System.out.println("Rooms after filter: " + rooms.size());
        }
        
        // Sort by location and name
        rooms.sort(Comparator.comparing((Room r) -> r.getLocation())
                .thenComparing((r) -> r.getName()));
        return rooms;
    }

    
    @GetMapping("/{id}")
    public Optional<Room> getRoomById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @PostMapping
    public ResponseEntity<String> addRoom(@RequestBody Room newRoom) {
        Optional<Room> rooms = repo.findByNameAndLocation(newRoom.getName(), newRoom.getLocation());
        if (rooms.isPresent()) return ResponseEntity.ok("ROOM EXISTED");
        repo.save(newRoom);
        return ResponseEntity.ok("ADDED ROOM SUCCESSFULLY");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.ok("DELETED ROOM SUCCESSFULY");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateRoom(@PathVariable Long id, @RequestBody Room update) {
        Optional<Room> rooms = repo.findById(id);
        Room room = rooms.get();
        if (update.getRoomType() != null) room.setRoomType(update.getRoomType());
        if (update.getLocation() != null) room.setLocation(update.getLocation());
        if (update.getName() != null) room.setName(update.getName());
        repo.save(room);
        return ResponseEntity.ok("UPDATED ROOM SUCCESSFULLY");
    }
}
