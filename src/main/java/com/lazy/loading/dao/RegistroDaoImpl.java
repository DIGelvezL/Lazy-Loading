package com.lazy.loading.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.lazy.loading.entities.Registro;

@Stateless
public class RegistroDaoImpl implements RegistroDao {
	
	@PersistenceContext(unitName = "LazyLoadingTestPU")
	EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<Registro> findAll() {
		return em.createNamedQuery("Registro.findAll").getResultList();
	}

	@Override
	public void insertRegistro(Registro registro) {
		em.persist(registro);
	}

	@Override
	public void updateRegistro(Registro registro) {
		em.merge(registro);
	}

	@Override
	public void deleteRegistro(Registro registro) {
		registro = em.find(Registro.class, registro.getId());
		em.remove(registro);
	}

}
