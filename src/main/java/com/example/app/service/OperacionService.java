package com.example.app.service;


import java.util.Date;
import java.util.List;

import com.example.app.models.OperationCurrentAccount;
import com.example.app.dto.dtoPerfilConsolidado;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OperacionService {

	Flux<OperationCurrentAccount> findAllOperacion();	
	Mono<OperationCurrentAccount> findByIdOperacion(String id);
	Mono<OperationCurrentAccount> saveOperacion(OperationCurrentAccount producto);
	Mono<OperationCurrentAccount> saveOperacionRetiro(OperationCurrentAccount producto);
	Mono<OperationCurrentAccount> saveOperacionDeposito(OperationCurrentAccount producto);
	Flux<OperationCurrentAccount> findAllOperacionByDniCliente(String dni);
	Flux<OperationCurrentAccount> consultaMovimientos(String numeroTarjeta, String codigo_bancario);
	Mono<OperationCurrentAccount> saveOperacionPagoCredito(OperationCurrentAccount operacion);
	Flux<OperationCurrentAccount> consultaComisiones(Date from, Date to);
	Mono<OperationCurrentAccount> saveOperacionCuentaCuenta(OperationCurrentAccount oper);
	Mono<dtoPerfilConsolidado> perfilConsolidado(String dni);
	
	
}
