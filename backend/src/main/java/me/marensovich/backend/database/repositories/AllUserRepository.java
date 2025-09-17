package me.marensovich.backend.database.repositories;

import me.marensovich.backend.database.models.AllUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllUserRepository extends JpaRepository<AllUsers, Long> {
}
