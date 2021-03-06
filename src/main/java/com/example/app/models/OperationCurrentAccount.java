package com.example.app.models;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	private String codigoBancarioOrigen;
	@NotEmpty
	private String cuentaOrigen;
	@NotEmpty
	private String codigoBancarioDestino;
	@NotEmpty
	private String cuentaDestino;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fechaOperacion;
	@NotEmpty
	private TypeOperation tipoOperacion;
	@NotEmpty
	private double montoPago;
	private Double comision = 0.0;
		
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
	public Date fechaOperacion() {
		return fechaOperacion;
	}
}










