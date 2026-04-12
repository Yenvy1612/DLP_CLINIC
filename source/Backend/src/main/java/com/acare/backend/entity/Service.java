package com.acare.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false, unique = true)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 120, nullable = false)
    @Builder.Default
    private String department = "GENERAL";

    @Column(name = "specialty_id", nullable = false)
    private Long specialtyId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", insertable = false, updatable = false)
    private Specialty specialtyMaster;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Service withDefaults() {
        return this.toBuilder()
                .active(this.active == null ? Boolean.TRUE : this.active)
                .department(this.department == null || this.department.isBlank() ? "GENERAL" : this.department)
                .build();
    }

    public Service mergeFrom(Service update) {
        if (update == null) {
            return this;
        }

        return this.toBuilder()
                .name(update.getName() != null ? update.getName() : this.name)
                .description(update.getDescription() != null ? update.getDescription() : this.description)
                .department(update.getDepartment() != null ? update.getDepartment() : this.department)
            .specialtyId(update.getSpecialtyId() != null ? update.getSpecialtyId() : this.specialtyId)
                .price(update.getPrice() != null ? update.getPrice() : this.price)
                .active(update.getActive() != null ? update.getActive() : this.active)
                .build();
    }
}
