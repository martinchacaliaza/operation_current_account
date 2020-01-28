package com.example.app.service;


import java.util.Date;
import java.util.List;

import com.example.app.models.OperationCurrentAccount;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OperacionService {

	Flux<OperationCurrentAccount> findAllOperacion();
	
	Mono<OperationCurrentAccount> findByIdOperacion(String id);

	Mono<OperationCurrentAccount> saveOperacion(OperationCurrentAccount producto);
	
	Mono<OperationCurrentAccount> saveOperacionRetiro(OperationCurrentAccount producto);

	Mono<OperationCurrentAccount> saveOperacionDeposito(OperationCurrentAccount producto);
	
	Flux<OperationCurrentAccount> findAllOperacionByDniCliente(String dni);

	/*Flux<Operacion> saveOperacionList(List<Operacion> producto);*/

	Flux<OperationCurrentAccount> consultaMovimientos(String dni, String numeroTarjeta, String codigo_bancario);
	
	Mono<OperationCurrentAccount> saveOperacionPagoCredito(OperationCurrentAccount operacion);

	Mono<OperationCurrentAccount> consultaComisiones(Date from, Date to);
	
	
	Mono<OperationCurrentAccount> saveOperacionCuentaCuenta(OperationCurrentAccount operacion);
	
}
