package com.biblioteca.repository;

import com.biblioteca.model.Ejemplar;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EjemplarRepository extends MongoRepository<Ejemplar, String> {
}

