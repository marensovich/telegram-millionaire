package me.marensovich.backend.services;

import me.marensovich.backend.database.models.AllUsers;
import me.marensovich.backend.database.models.User;
import me.marensovich.backend.database.repositories.AllUserRepository;
import me.marensovich.backend.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired UserRepository userRepository;
    @Autowired AllUserRepository allUserRepository;



    public boolean checkAllUserExist(long userId) {
        return allUserRepository.existsById(userId);
    }
    public boolean checkUsersExist(long userId) {
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


    public void addUser(long userId){
        AllUsers user = new AllUsers();
        user.setId(userId);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        allUserRepository.save(user);
    }

}
