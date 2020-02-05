	package com.example.app.dto;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class dtoTypeClient {
	private String idTipo;
	private String descripcion;
}
