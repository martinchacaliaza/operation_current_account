package com.example.app.service.impl;
import java.util.ArrayList;
import java.util.Date;

import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.stdDSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Add;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.app.exception.RequestException;
import com.example.app.models.OperationCurrentAccount;
import com.example.app.dto.dtoBank;
import com.example.app.dto.dtoClient;
import com.example.app.dto.dtoCurrentAccount;
import com.example.app.dto.dtoDescProductos;
import com.example.app.dto.dtoPerfilConsolidado;

import com.example.app.models.TypeOperation;
import com.example.app.repository.OperacionDao;
import com.example.app.service.OperacionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OperacionServiceImpl implements OperacionService {

	@Value("${com.bootcamp.gateway.url}")
	String valorget;

	Double comision = 0.0;

	@Autowired
	public OperacionDao operacionDao;

	@Autowired
	public OperacionDao tipooperacionDao;

	@Override
	public Flux<OperationCurrentAccount> findAllOperacion() {
		return operacionDao.findAll();

	}

	@Override
	public Mono<OperationCurrentAccount> findByIdOperacion(String id) {
		return operacionDao.findById(id);

	}

	@Override
	public Flux<OperationCurrentAccount> findAllOperacionByDniCliente(String dni) {
		return operacionDao.findByDni(dni);

	}

	@Override
	public Mono<dtoPerfilConsolidado> perfilConsolidado(String dni) {
	dtoPerfilConsolidado dto=new dtoPerfilConsolidado();
		dto.setDesproductos(new ArrayList<>());
		Mono<dtoClient> oper1 = WebClient.builder()
				.baseUrl("http://" + valorget + "/clientes/api/Clientes/")
				.build().get()
				.uri("/dni/" + dni)
				.retrieve().bodyToMono(dtoClient.class).log();	
		return oper1.flatMapMany(c1 -> {	
			dto.setDni(c1.getDni());
			dto.setApellidoNombre(c1.getApellidos()+" "+c1.getNombres());	
			
			Flux<dtoBank> oper2=WebClient.builder()
					.baseUrl("http://" + valorget + "/bancos/api/Bancos/")
					.build().get()
					.retrieve().bodyToFlux(dtoBank.class).log();	
			return oper2;
		}).flatMap(p->{
			Flux<dtoCurrentAccount> oper3 = WebClient.builder()
					.baseUrl("http://" + valorget + "/producto_bancario/api/ProductoBancario/")
					.build().get()
					.uri("/dni_codbanco/" + dni +  "/"+p.getCodigo_banco())
					.retrieve().bodyToFlux(dtoCurrentAccount.class).log();	
			return oper3;

			}).map(c->{
				
			dto.getDesproductos().add(new dtoDescProductos(c.getCodigoBancario(),c.getNumeroCuenta(),  String.valueOf(c.getSaldo())));
				
				return c;
			}).then(Mono.just(dto));
	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacionCuentaCuenta(OperationCurrentAccount operacion) {

		Mono<dtoCurrentAccount> oper1 = WebClient.builder()
				.baseUrl("http://" + valorget + "/producto_bancario/api/ProductoBancario/")
				.defaultHeader(HttpHeaders.CONTENT_TYPE).build().get()
				.uri("/numero_cuenta/" + operacion.getCuentaOrigen() + "/" 
				+ operacion.getCodigoBancarioOrigen())
				.retrieve().bodyToMono(dtoCurrentAccount.class).log();
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

			Mono<Long> valor = operacionDao.findByCuentaOrigenAndCodigoBancarioOrigen(
					operacion.getCuentaOrigen(), operacion.getCodigoBancarioOrigen())
					.count();

			return valor.flatMap(p -> {
				// NUMERO DE COMISIONES
				if (p > 3) {
					operacion.setComision(comision);
				}
				Mono<dtoCurrentAccount> oper2 = WebClient.builder()
						.baseUrl("http://" + valorget + "/producto_bancario/api/ProductoBancario/")
						.build()
						.put()
						.uri("/retiro/" + operacion.getCuentaOrigen() + "/" + operacion.getMontoPago() + "/"
								+ operacion.getComision() + "/" + operacion.getCodigoBancarioOrigen())
						.retrieve().bodyToMono(dtoCurrentAccount.class).log();

				return oper2.flatMap(c -> {

					if (c.getNumeroCuenta() == null) {
						return Mono.empty();
					}

					Mono<dtoCurrentAccount> oper3 = WebClient.builder()
							.baseUrl("http://" + valorget + "/producto_bancario/api/ProductoBancario/")
							.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build()
							.put()
							.uri("/deposito/" + operacion.getCuentaDestino() + "/" + operacion.getMontoPago()
							+ "/"+ operacion.getComision() + "/" + operacion.getCodigoBancarioDestino())
							.retrieve().bodyToMono(dtoCurrentAccount.class).log();

					return oper3.flatMap(d -> {

						if (c.getNumeroCuenta() == null) {
							return Mono.empty();
						}

						TypeOperation tipo = new TypeOperation();
						tipo.setIdTipo("4");
						tipo.setDescripcion("Tranferencia de cuenta a cuenta");
						operacion.setTipoOperacion(tipo);

						return operacionDao.save(operacion);
					});

				});
			});
		});

	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacionPagoCredito(OperationCurrentAccount operacion) {
		Mono<dtoCurrentAccount> oper1 = WebClient.builder()
				.baseUrl("http://" + valorget + "/producto_bancario/api/ProductoBancario/")
				//.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.build()
				.get()
				.uri("/numero_cuenta/" + operacion.getCuentaOrigen() + "/" + operacion.getCodigoBancarioOrigen())
				.retrieve().bodyToMono(dtoCurrentAccount.class).log();

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

			Mono<Long> valor = operacionDao.findByCuentaOrigenAndCodigoBancarioOrigen(operacion.getCuentaOrigen(),
					operacion.getCodigoBancarioOrigen()).count();

			return valor.flatMap(p -> {
				// NUMERO DE COMISIONES
				if (p > 3) {
					operacion.setComision(comision);
				}
				Mono<dtoCurrentAccount> oper2 = WebClient.builder()
						.baseUrl("http://" + valorget + "/producto_bancario/api/ProductoBancario/")
						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().put()
						.uri("/retiro/" + operacion.getCuentaOrigen() + "/" + operacion.getMontoPago() + "/"
								+ operacion.getComision() + "/" + operacion.getCodigoBancarioOrigen())
						.retrieve().bodyToMono(dtoCurrentAccount.class).log();

				return oper2.flatMap(c -> {

					if (c.getNumeroCuenta() == null) {
						return Mono.empty();
					}

					Mono<dtoCurrentAccount> oper3 = WebClient.builder()
							.baseUrl("http://" + valorget + "/productos_creditos/api/ProductoCredito/")
							.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build()
							.put()
							.uri("/pago/" + operacion.getCuentaDestino() + "/" + operacion.getMontoPago() + "/"
									+ operacion.getCodigoBancarioDestino())
							.retrieve().bodyToMono(dtoCurrentAccount.class).log();

					return oper3.flatMap(d -> {

						if (c.getNumeroCuenta() == null) {
							return Mono.empty();
						}

						TypeOperation tipo = new TypeOperation();
						tipo.setIdTipo("3");
						tipo.setDescripcion("Pago a cuenta de credito");
						operacion.setTipoOperacion(tipo);

						return operacionDao.save(operacion);
					});

				});
			});
		});
	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacionRetiro(OperationCurrentAccount operacion) {
		Mono<dtoCurrentAccount> oper1 = WebClient.builder()
				.baseUrl("http://" + valorget + "/producto_bancario/api/ProductoBancario/")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().get()
				.uri("/numero_cuenta/" + operacion.getCuentaOrigen() + "/" + operacion.getCodigoBancarioOrigen())
				.retrieve().bodyToMono(dtoCurrentAccount.class).log();

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
			Mono<Long> valor = operacionDao.findByCuentaOrigenAndCodigoBancarioOrigen(operacion.getCuentaOrigen(),
					operacion.getCodigoBancarioOrigen()).count();
			return valor.flatMap(p -> {
				// NUMERO DE COMISIONES
				if (p > 3) {
					operacion.setComision(comision + operacion.getComision());
				} else {

					operacion.setComision(operacion.getComision());
				}
				Mono<dtoCurrentAccount> oper2 = WebClient.builder()
						.baseUrl("http://" + valorget + "/producto_bancario/api/ProductoBancario/")
						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().put()
						.uri("/retiro/" + operacion.getCuentaOrigen() + "/" + operacion.getMontoPago() + "/"
								+ operacion.getComision() + "/" + operacion.getCodigoBancarioOrigen())
						.retrieve().bodyToMono(dtoCurrentAccount.class).log();
				return oper2.flatMap(c -> {

					if (c.getNumeroCuenta() == null) {
						return Mono.empty();
					}

					TypeOperation tipo = new TypeOperation();
					tipo.setIdTipo("2");
					tipo.setDescripcion("Retiro");
					operacion.setTipoOperacion(tipo);

					return operacionDao.save(operacion);

				});
			});
		});
	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacionDeposito(OperationCurrentAccount operacion) {
		Mono<dtoCurrentAccount> oper1 = WebClient.builder()
				.baseUrl("http://" + valorget + "/producto_bancario/api/ProductoBancario/")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE).build().get()
				.uri("/numero_cuenta/" + operacion.getCuentaOrigen() + "/" + operacion.getCodigoBancarioOrigen())
				.retrieve().bodyToMono(dtoCurrentAccount.class).log();
		return oper1.flatMap(c1 -> {
			if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("1")) {
				comision = 2.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("2")) {
				comision = 3.5;

			} else if (c1.getTipoProducto().getIdTipo().equalsIgnoreCase("3")) {
				comision = 4.5;
			}
			Mono<Long> valor = operacionDao.findByCuentaOrigenAndCodigoBancarioOrigen(operacion.getCuentaOrigen(),
					operacion.getCodigoBancarioOrigen()).count();
			return valor.flatMap(p -> {
				if (p > 3) {
					operacion.setComision(comision);
				}
				Mono<dtoCurrentAccount> oper = WebClient.builder()
						.baseUrl("http://" + valorget + "/producto_bancario/api/ProductoBancario/")
						//.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
						.build()
						.put()
						.uri("/deposito/" + operacion.getCuentaOrigen() + "/" + operacion.getMontoPago() + "/"
								+ operacion.getComision() + "/" + operacion.getCodigoBancarioOrigen())

						.retrieve().bodyToMono(dtoCurrentAccount.class).log();
				return oper.flatMap(c -> {
					if (c.getNumeroCuenta() == null) {
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
					return operacionDao.save(operacion);

				});

			});

		});
	}

	@Override
	public Mono<OperationCurrentAccount> saveOperacion(OperationCurrentAccount producto) {
		return operacionDao.save(producto);
	}

	@Override
	public Flux<OperationCurrentAccount> consultaMovimientos(String numTarjeta, String codigo_bancario) {

		return operacionDao.findByCuentaOrigenAndCodigoBancarioOrigen(numTarjeta, codigo_bancario);
	}

	@Override
	public Flux<OperationCurrentAccount> consultaComisiones(Date from, Date to) {
		return operacionDao.consultaComisiones(from, to);
	}



}
