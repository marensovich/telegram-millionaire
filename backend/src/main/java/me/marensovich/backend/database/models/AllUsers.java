package me.marensovich.backend.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Table(name = "allUsers")
@Entity
public class AllUsers {
    @Id
    @Column(name = "userId", unique = true, nullable = false)
    private Long id;

    @Column(name = "createdAt", nullable = false)
    private Timestamp createdAt;
}
