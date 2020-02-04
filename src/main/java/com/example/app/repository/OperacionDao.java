package com.example.app.repository;




import java.util.Date;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.app.models.OperationCurrentAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface OperacionDao extends ReactiveMongoRepository<OperationCurrentAccount, String> {

	Flux<OperationCurrentAccount> findByDni(String dni);

	@Query("{ 'dni' : ?0 , 'cuenta_origen' : ?1, 'codigo_bancario_origen' : ?2 }")
	Flux<OperationCurrentAccount> consultaMovimientos(String dni, String numTarjeta, String codigo_bancario);

	@Query("{'fechaOperacion' : {'$gt' : ?0, '$lt' : ?1}}")
	Mono<OperationCurrentAccount> consultaComisiones(Date from, Date to);
}
