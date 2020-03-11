package com.project.hashnote.dao;

import com.project.hashnote.user.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
