package com.example.app.models;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection ="Operaciones")
public class OperationCurrentAccount {

	@NotEmpty
	private String dni;
	@NotEmpty
	private String cuenta_origen;
	@NotEmpty
	private String cuenta_destino;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaOperacion;
	@NotEmpty
	private TypeOperation tipoOperacion;
	@NotEmpty
	private double montoPago;
	
	private Double comision = 0.0;
		
	//private tipoProducto tipoCliente;
}










