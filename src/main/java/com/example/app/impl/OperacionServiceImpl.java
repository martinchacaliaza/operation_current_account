package com.example.app.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.app.controllers.WebClientController;
import com.example.app.dao.OperacionDao;
import com.example.app.models.OperationCurrentAccount;
import com.example.app.models.CurrentAccount;
import com.example.app.models.TypeOperation;
import com.example.app.service.OperacionService;
import com.example.app.service.TipoOperacionService;
import com.sistema.app.exception.RequestException;
import com.sistema.app.exception.ResponseStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OperacionServiceImpl implements OperacionService {
	


	@Value("${com.bootcamp.gateway.url}")
	String valorget;
	
	
	Double comision = 0.0;
	
	@Autowired
	public OperacionDao productoDao;

	@Autowired
	public OperacionDao tipoProductoDao;

	@Autowired
	private TipoOperacionService tipoProductoService;

	@Override
	public Flux<OperationCurrentAccount> findAllOperacion() {
		return productoDao.findAll();

	}

	@Override
	public Mono<OperationCurrentAccount> findByIdOperacion(String id) {
		return productoDao.findById(id);

	}

	@Override
	public Flux<OperationCurrentAccount> findAllOperacionByDniCliente(String dni) {
		return productoDao.viewDniCliente(dni);

	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacionCuentaCuenta(OperationCurrentAccount operacion) {
		Mono<CurrentAccount> oper1 = WebClient.builder().baseUrl("http://"+ valorget +"/producto_bancario/api/ProductoBancario/")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().get()
				.uri("/numero_cuenta/" + operacion.getCuenta_origen()).retrieve().bodyToMono(CurrentAccount.class).log();

		return oper1.flatMap(c1 -> {
			if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("1")) {

				comision = 2.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) {

				comision = 3.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("3")) {

				comision = 4.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("4")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("5")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("6")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("7")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("8")) {

				if (c1.getSaldo() == 20) {

					throw new RequestException(
							"Ya no puede realizar retiros, debe tener un monton minimo" + " de S/.20 en su cuenta.");
				}
			}
			Mono<Long> valor = productoDao.consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen())
					.count();
			return valor.flatMap(p -> {
				// NUMERO DE COMISIONES
				if (p > 3) {
					operacion.setComision(comision);
				}
				Mono<CurrentAccount> oper2 = WebClient.builder().baseUrl("http://"+ valorget +"/producto_bancario/api/ProductoBancario/")
						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build()
						.put().uri("/retiro/" + operacion.getCuenta_origen() + "/" + operacion.getMontoPago() + "/"
								+ operacion.getComision()).retrieve().bodyToMono(CurrentAccount.class).log();

				return oper2.flatMap(c -> {

					if (c.getNumero_cuenta() == null) {
						return Mono.empty();
					}

				Mono<CurrentAccount> oper3 = WebClient.builder().baseUrl("http://"+ valorget +"/productos_creditos/api/ProductoCredito/")
						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build()
						.put().uri("/pago/" + operacion.getCuenta_destino() + "/" + operacion.getMontoPago())
						.retrieve().bodyToMono(CurrentAccount.class).log();
				
				return oper3.flatMap(d -> {

					if (c.getNumero_cuenta() == null) {
						return Mono.empty();
					}
					
					TypeOperation tipo = new TypeOperation();
					tipo.setIdTipo("3");
					tipo.setDescripcion("Pago a cuenta de credito");
					operacion.setTipoOperacion(tipo);

					return productoDao.save(operacion);
				});
				
				});
			});
		});
	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacionRetiro(OperationCurrentAccount operacion) {
		Mono<CurrentAccount> oper1 = WebClient.builder().baseUrl("http://"+valorget+"/producto_bancario/api/ProductoBancario/")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().get()
				.uri("/numero_cuenta/" + operacion.getCuenta_origen()).retrieve().bodyToMono(CurrentAccount.class).log();

		return oper1.flatMap(c1 -> {
			if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("1")) {

				comision = 2.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) {

				comision = 3.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("3")) {

				comision = 4.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("4")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("5")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("6")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("7")
					|| c1.getTipoProducto().getIdTipo().equalsIgnoreCase("8")) {

				if (c1.getSaldo() == 20) {

					throw new RequestException(
							"Ya no puede realizar retiros, debe tener un monton minimo" + " de S/.20 en su cuenta.");
				}
			}
			Mono<Long> valor = productoDao.consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen())
					.count();
			return valor.flatMap(p -> {
				// NUMERO DE COMISIONES
				if (p > 3) {
					operacion.setComision(comision);
				}
				Mono<CurrentAccount> oper2 = WebClient.builder().baseUrl("http://"+ valorget +"/producto_bancario/api/ProductoBancario/")
						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().put()
						.uri("/retiro/" + operacion.getCuenta_origen() + "/" + operacion.getMontoPago() + "/"
								+ operacion.getComision())
						.retrieve().bodyToMono(CurrentAccount.class).log();
				return oper2.flatMap(c -> {

					if (c.getNumero_cuenta() == null) {
						return Mono.empty();
					}

					TypeOperation tipo = new TypeOperation();
					tipo.setIdTipo("2");
					tipo.setDescripcion("Retiro");
					operacion.setTipoOperacion(tipo);

					return productoDao.save(operacion);

				});
			});
		});
	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacionDeposito(OperationCurrentAccount operacion) {
		Mono<CurrentAccount> oper1 = WebClient.builder().baseUrl("http://" + valorget +"/producto_bancario/api/ProductoBancario/")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().get()
				.uri("/numero_cuenta/" + operacion.getCuenta_origen()).retrieve().bodyToMono(CurrentAccount.class).log();
		return oper1.flatMap(c1 -> {
			if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("1")) {
				comision = 2.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) {
				comision = 3.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("3")) {
				comision = 4.5;
			}
			Mono<Long> valor = productoDao.consultaMovimientos(operacion.getDni(), operacion.getCuenta_origen())
					.count();
			return valor.flatMap(p -> {
				if (p > 3) {
					operacion.setComision(comision);
				}
				Mono<CurrentAccount> oper = WebClient.builder().baseUrl("http://"+ valorget +"/producto_bancario/api/ProductoBancario/")
						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().put()
						.uri("/deposito/" + operacion.getCuenta_origen() + "/" + operacion.getMontoPago() + "/"
								+ operacion.getComision())
						.retrieve().bodyToMono(CurrentAccount.class).log();
				return oper.flatMap(c -> {
					if (c.getNumero_cuenta() == null) {
						return Mono.error(new InterruptedException("No existe Numero de tarjeta"));
					}

					TypeOperation tipo = new TypeOperation();

					/*
					 * tipo.setIdTipo(operacion.getTipoOperacion().getIdTipo());
					 * tipo.setDescripcion(operacion.getTipoOperacion().getDescripcion());
					 */
					tipo.setIdTipo("1");
					tipo.setDescripcion("Deposito");
					operacion.setTipoOperacion(tipo);
					return productoDao.save(operacion);

				});

			});

		});
	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacion(OperationCurrentAccount producto) {
		return productoDao.save(producto);
	}

	@Override
	public Flux<OperationCurrentAccount> consultaMovimientos(String dni, String numTarjeta) {

		return productoDao.consultaMovimientos(dni, numTarjeta);
	}
	
	@Override
	public Mono<OperationCurrentAccount> consultaComisiones(Date from, Date to) {
		return productoDao.consultaComisiones(from, to);
	}

	

}
