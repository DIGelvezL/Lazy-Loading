package com.lazy.loading.vo;

import java.io.Serializable;
import java.util.Date;


public class RegistroVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String cedula;
	private Date fechaRegistro;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCedula() {
		return cedula;
	}
	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
	public Date getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	
}
