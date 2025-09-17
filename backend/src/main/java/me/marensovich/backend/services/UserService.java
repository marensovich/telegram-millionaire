package me.marensovich.backend.services;

import me.marensovich.backend.database.models.User;
import me.marensovich.backend.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired UserRepository userRepository;

    public boolean checkUserExist(long userId) {
        return userRepository.existsById(userId);
    }

    public void registerUser(long userId,
                             boolean isAdmin){
        User user = new User();
        user.setUserId(userId);
        user.setAdmin(isAdmin);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));


        userRepository.save(user);
    }


}
