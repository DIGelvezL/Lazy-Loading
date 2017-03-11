package com.lazy.loading.service;

import java.util.List;

import javax.ejb.Local;

import org.primefaces.model.UploadedFile;

import com.lazy.loading.vo.RegistroVO;;

@Local
public interface RegistroService {

	public List<RegistroVO> listarRegistros();

	public String insertRegistro(RegistroVO registroVO, UploadedFile file);
	
	public String guardarLogoEnFicheroTemp(UploadedFile file, String nombreArchivo);
	
	public void guardarArchivosMultiple(UploadedFile file, String nombreArchivo);

	public void updateRegistro(RegistroVO registroVO);

	public void deleteRegistro(RegistroVO registroVO);
}
