package com.example.app.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.app.models.OperationCurrentAccount;
import com.example.app.service.OperacionService;
import com.example.app.service.TipoOperacionService;

import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/api/OperCuentasCorrientes")
@RestController
public class OperacionControllers {

	@Autowired
	private OperacionService productoService;

	@Autowired
	private TipoOperacionService tipoProductoService;

	@ApiOperation(value = "Muestra todos las operaciones de cuentas corrientes existentes", notes="")
	@GetMapping
	public Mono<ResponseEntity<Flux<OperationCurrentAccount>>> findAll() {
		return Mono.just(
				ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(productoService.findAllOperacion())

		);
	}

	@ApiOperation(value = "Filtra todas cuentas bancarias por id", notes="")
	@GetMapping("/{id}")
	public Mono<ResponseEntity<OperationCurrentAccount>> viewId(@PathVariable String id) {
		return productoService.findByIdOperacion(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}


	@ApiOperation(value = "actualiza cuenta bancaria", notes="")
	@PutMapping
	public Mono<OperationCurrentAccount> updateProducto(@RequestBody OperationCurrentAccount producto) {
		System.out.println(producto.toString());
		return productoService.saveOperacion(producto);
	}
	

	//
	
	@ApiOperation(value = "Realiza una Transaccion(RETIROS) /guardando en el microservicio operaciones(movimientos) / "
			+ "Y Actualiza el saldo de la tarjeta(retiro) /mayor a 4 transacciones ya sea deposito o retiro, se les cobrara"
			+ " un monto de comision por tipo de tarjeta ", notes="")
	@PostMapping("/retiro")
	public Mono<OperationCurrentAccount> saveOperacionRetiro(@RequestBody OperationCurrentAccount producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionRetiro(producto);
	}

	@ApiOperation(value = "Realiza una Transaccion(Deposito)"
			+ "Y Actualiza el saldo de la tarjeta(deposito) "
			+ " mayor a 4 transacciones ya sea deposito o retiro, se les cobrara un monto de comision por tipo de tarjeta", notes="")
	@PostMapping("/deposito")
	public Mono<OperationCurrentAccount> saveOperacionDeposito(@RequestBody OperationCurrentAccount producto) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionDeposito(producto);
	}
	
	@ApiOperation(value = "Pago de una cuenta de corriente a una de credito", notes="")
	@PostMapping("/cuenta_a_credito")
	public Mono<OperationCurrentAccount> saveOperacionCuentaCuenta(@RequestBody OperationCurrentAccount oper) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionPagoCredito(oper);
	}
	
	@ApiOperation(value = "Transferencias de cuenta a cuenta", notes="")
	@PostMapping("/cuenta_a_cuenta")
	public Mono<OperationCurrentAccount> saveOperacionPagoCredito(@RequestBody OperationCurrentAccount oper) {
		//System.out.println(producto.toString());
		return productoService.saveOperacionCuentaCuenta(oper);
	}
		
	@ApiOperation(value = "Guarda una operacion bancaria", notes="")
	@PostMapping
	public Mono<OperationCurrentAccount> guardarProducto(@RequestBody OperationCurrentAccount prod) {
		return productoService.saveOperacion(prod);
	}	
	
	@ApiOperation(value = "Muestra todas los movimientos bancarios por el numero de dni del cliente", notes="")
	@GetMapping("/dni/{dni}")
	public Flux<OperationCurrentAccount> listProductoByDicliente(@PathVariable String dni) {
		Flux<OperationCurrentAccount> oper = productoService.findAllOperacionByDniCliente(dni);
		return oper;
	}
	
	@ApiOperation(value = "Muestra todos los movimientos bancarios por cliente y "
			+ " numero tarjeta(cuenta de ahorros)", notes="")
	@GetMapping("/MovimientosBancarios/{dni}/{numTarjeta}")
	public Flux<OperationCurrentAccount> movimientosBancarios(@PathVariable String dni, @PathVariable String numTarjeta,  
			@PathVariable String codigo_bancario) {
		Flux<OperationCurrentAccount> oper = productoService.consultaMovimientos(dni, numTarjeta, codigo_bancario);
		return oper;
	}
	
	@ApiOperation(value = "Reporte de movimientos con comisiones por periodo de tiempo", notes="")
	@GetMapping("consultaRangoFecha/{fecha1}")
	public Mono<ResponseEntity<OperationCurrentAccount>> consultaMovimientosComisiones(@PathVariable String fecha1) throws ParseException{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
			String f1 = fecha1.split("&&")[0]+" 00:00:00.000 +0000";
			Date from = format.parse(f1);
			Date to = format.parse(fecha1.split("&&")[1]+" 00:00:00.000 +0000");
			System.out.println(format.format(from));
			return productoService.consultaComisiones(from,to).map(p-> ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(p))
					.defaultIfEmpty(ResponseEntity.notFound().build());
		}
}



