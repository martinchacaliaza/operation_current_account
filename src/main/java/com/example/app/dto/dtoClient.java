package com.example.app.dto;

import javax.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

//clase de la collection CLIENTES y su tipo de cliente
@Getter
@Setter
public class dtoClient {

	private String id;
	private String dni;
	private String nombres;
	private String apellidos;
	private String sexo;
	private String telefono;
	private String edad;
	private String correo;
	private dtoTypeClient tipoCliente;
	private String codigoBancario;

}
