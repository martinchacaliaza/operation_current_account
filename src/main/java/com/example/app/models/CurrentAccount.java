package com.example.app.models;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CurrentAccount {
	
	private String id;
	private String dni;
	private String numero_cuenta;
	private TypeCurrentAccount tipoProducto;
	private String fecha_afiliacion;
	private String fecha_cierre;
	private double saldo;
	private String usuario;
	private String clave;
	//

}










