package com.example.app.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.app.models.TypeOperation;

public interface TipoOperacionDao extends ReactiveMongoRepository<TypeOperation, String> {

}
