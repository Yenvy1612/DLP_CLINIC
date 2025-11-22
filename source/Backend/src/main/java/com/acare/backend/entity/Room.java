package com.acare.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "rooms",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "location"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120, nullable = false)
    private String name;

    @Column(name = "room_type", length = 80, nullable = false)
    private String roomType;

    @Column(length = 120, nullable = false)
    private String location;
}
