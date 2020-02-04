package com.example.app.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.app.models.TypeOperation;

public interface TipoOperacionDao extends ReactiveMongoRepository<TypeOperation, String> {

}
