package com.project.hashnote.notefolder.dao;

import com.project.hashnote.notefolder.document.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FolderRepository extends MongoRepository<Folder, String> {
    List<Folder> findByAuthor(String username);
}
