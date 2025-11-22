package com.acare.backend.repository;

import com.acare.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByNameAndLocation(String name, String location);
    List<Room> findByRoomType(String roomType);
}
