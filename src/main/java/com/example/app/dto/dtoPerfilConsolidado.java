package com.example.app.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class dtoPerfilConsolidado {
	
	private String dni;
	private String ApellidoNombre;
	private List<dtoDescProductos> Desproductos;
}
