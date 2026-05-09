package com.acare.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="dlp_logs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DlpLog {
    @Id
    private Long id;

    private Long userId;
    private String action;
    private String contentSnippet;
    private String violationType;

}