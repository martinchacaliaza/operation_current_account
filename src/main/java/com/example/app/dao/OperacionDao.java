package com.example.app.dao;




import java.util.Date;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.app.models.OperationCurrentAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface OperacionDao extends ReactiveMongoRepository<OperationCurrentAccount, String> {
	@Query("{ 'dni' : ?0 }")
	Flux<OperationCurrentAccount> viewDniCliente(String dni);

	@Query("{ 'dni' : ?0 , 'cuenta_origen' : ?1, 'codigo_bancario_destino' : ?2 }")
	Flux<OperationCurrentAccount> consultaMovimientos(String dni, String numTarjeta, String codigo_bancario);
	
	
	@Query("{'fechaOperacion' : {'$gt' : ?0, '$lt' : ?1}}")
	Mono<OperationCurrentAccount> consultaComisiones(Date from, Date to);
}
