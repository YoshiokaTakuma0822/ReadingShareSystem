package com.readingshare.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.readingshare.auth.domain.model.User;
import com.readingshare.auth.domain.repository.IUserRepository;

@Repository
public class UserRepositoryImpl implements IUserRepository {
    @Autowired
    @Lazy
    private UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
