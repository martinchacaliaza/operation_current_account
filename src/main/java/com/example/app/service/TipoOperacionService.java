package com.example.app.service;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.app.models.TypeOperation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TipoOperacionService {
	
	Flux<TypeOperation> findAllTipoproducto();
	Mono<TypeOperation> findByIdTipoProducto(String id);
	Mono<TypeOperation> saveTipoProducto(TypeOperation tipoProducto);
	Mono<Void> deleteTipo(TypeOperation tipoProducto);
	
}
