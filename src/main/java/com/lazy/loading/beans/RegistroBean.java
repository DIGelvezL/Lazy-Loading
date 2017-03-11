package com.lazy.loading.beans;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.lazy.loading.service.RegistroService;
import com.lazy.loading.vo.RegistroVO;

@ManagedBean
@ViewScoped
public class RegistroBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private RegistroService registroService;
	
	private Integer id;
	private String cedula;
	private Date fechaRegistro;
	
	private String path;
	private String nombreArchivo = "lazy_loading_output.txt";
	
	private UploadedFile file;
	
	private List<RegistroVO> registroVOList;
	private RegistroVO registroVO;
	
	private StreamedContent fileOut;
	
	@PostConstruct
	public void inicializar() {
		try {
			registroVOList = registroService.listarRegistros();
		} catch (Exception ex) {
			ex.printStackTrace();
        }
	}
	
	public void save() {
        try {
        	if(file.getFileName().equals("")) {
                messageError("Debe seleccionar un archivo!!");
            }else{
            	if(file.getFileName().endsWith("txt")){
        			registroVO = new RegistroVO();
        			registroVO.setCedula(getCedula());
        			path = registroService.insertRegistro(registroVO, file);
            	}else{
                    messageWarning("El archivo no tiene la extensión .txt!!");
            	}
            }
		} catch (Exception ex) {
			ex.printStackTrace();
        }
    }
	
	public void messageSuccess(String msg){
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO!!", msg);
        FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void messageError(String msg){
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!!", msg);
        FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void messageWarning(String msg){
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "WARNING!!", msg);
        FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public List<RegistroVO> getRegistroVOList() {
		return registroVOList;
	}

	public void setRegistroVOList(List<RegistroVO> registroVOList) {
		this.registroVOList = registroVOList;
	}


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
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public StreamedContent getFileOut() {
		try{
			if(path != null){
				Path p = Paths.get(path + "/" + cedula + "/" + nombreArchivo);
		        InputStream is = Files.newInputStream(p);
		        fileOut = new DefaultStreamedContent(is, "text/plain", nombreArchivo);
		        return fileOut;
			}else{
				messageWarning("No se ha generado el archivo de salida!!");
				return null;
			}
		}catch (Exception e) {
			messageError("No se ha creado el archivo de salida con el numero de cedula: " + cedula);
			return null;
		}
		
	}

	public void setFileOut(StreamedContent fileOut) {
		this.fileOut = fileOut;
	}

}
