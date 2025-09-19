package me.marensovich.backend.database.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @Column(name = "userId", unique = true, nullable = false)
    private Long userId;

    @Column(name = "isAdmin", nullable = false)
    private boolean isAdmin;

    @Column(name = "createdAt", nullable = false)
    private Timestamp createdAt;

}
