package com.example.app.dto;


import javax.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@Document(collection ="bancos")
public class dtoBank {
	

	@Id
	@NotEmpty
	private String id;
	@NotEmpty
	private String codigo_banco;
	@NotEmpty
	private String ruc;
	@NotEmpty
	private String razon_social;
	@NotEmpty
	private String direccion;
	@NotEmpty
	private String telefono;

	
}










