package com.project.hashnote.dao;

import com.project.hashnote.document.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FolderRepository extends MongoRepository<Folder, String> {
}
