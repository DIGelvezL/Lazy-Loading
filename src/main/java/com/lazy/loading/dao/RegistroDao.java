package com.lazy.loading.dao;

import java.util.List;

import com.lazy.loading.entities.Registro;

public interface RegistroDao {

	public List<Registro> findAll();

	public void insertRegistro(Registro registro);

	public void updateRegistro(Registro registro);

	public void deleteRegistro(Registro registro);
}
