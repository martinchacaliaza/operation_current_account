package com.example.app.dto;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class dtoCurrentAccount {
	
	private String id;
	private String dni;
	private String numero_cuenta;
	private dtoTypeCurrentAccount tipoProducto;
	private Date fecha_afiliacion;
	private String fecha_cierre;
	private double saldo;
	private String usuario;
	private String clave;
	private String codigo_bancario;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
	public Date fecha_afiliacion() {
		return fecha_afiliacion;
	}
	
}










