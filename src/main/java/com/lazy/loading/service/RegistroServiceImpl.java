package com.lazy.loading.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.UploadedFile;

import com.lazy.loading.beans.RegistroBean;
import com.lazy.loading.dao.RegistroDao;
import com.lazy.loading.entities.Registro;
import com.lazy.loading.vo.RegistroVO;


@Stateless
public class RegistroServiceImpl implements RegistroService, RegistroServiceRemote {

	@Resource
	private SessionContext contexto;

	@EJB
	private RegistroDao registroDao;

	@Override
	public List<RegistroVO> listarRegistros() {
		List<Registro> registro;
		List<RegistroVO> registroVO;

		try {
			registro = registroDao.findAll();
			if (registro != null) {

				registroVO = new ArrayList<RegistroVO>();
				registroVO = registroToRegistroVO(registro);

				return registroVO;
			} else
				return registroVO = null;
		} catch (Throwable t) {
			contexto.setRollbackOnly();
			return null;
		}
	}

	private List<RegistroVO> registroToRegistroVO(List<Registro> registro) {
		List<RegistroVO> registroVO = new ArrayList<RegistroVO>();
		for (Registro item : registro) {
			RegistroVO vo = new RegistroVO();
			vo.setId(item.getId());
			vo.setCedula(item.getCedula());
			vo.setFechaRegistro(item.getFechaRegistro());

			registroVO.add(vo);
		}

		return registroVO;
	}

	@Override
	public String insertRegistro(RegistroVO registroVO, UploadedFile file) {

		try{
			String path = saveFileTmp(file);
			ArrayList<Integer> valores = obtenerValores(path);

			String pathDescarga = crearArchivoSalida(valores, registroVO.getCedula());
			
			if(pathDescarga != null){
				Registro registro = new Registro();
				
				registro.setCedula(registroVO.getCedula());
	
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				Timestamp fRegistro = new Timestamp(now.getTime());
				registro.setFechaRegistro(fRegistro);
				
				registroDao.insertRegistro(registro);
				
				RegistroBean registroBean = new RegistroBean();
				registroBean.messageSuccess("Se creó el archivo de salida, ya lo puedes descargar!!");
			}
			return pathDescarga;
		}catch(Throwable t){
			contexto.setRollbackOnly();
			return "";
		}	

	}
	
	private String saveFileTmp(UploadedFile file){

		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		
		String path = servletContext.getRealPath("") + File.separatorChar + "resources" + File.separatorChar + "archivo"
				+ File.separatorChar + file.getFileName();
		
		try {
			FileInputStream in = (FileInputStream) file.getInputstream(); 
			FileOutputStream out = new FileOutputStream(path);
			byte[] buffer = new byte [(int) file.getSize()];
			int c = 0;
			while((c = in.read(buffer)) != -1 ){
				out.write(buffer, 0, c);
			}
			in.close();
			out.flush();
			out.close();

		} catch (Exception e) {
			RegistroBean RegistroBean = new RegistroBean();
			RegistroBean.messageError(e.getMessage());
			e.printStackTrace();
		}
		
		return path;
	}
	
	private ArrayList<Integer> obtenerValores(String path){
		File archivo = new File(path);
        FileReader fr = null;
        BufferedReader br = null;
        String linea;
        ArrayList<Integer> valores = null;
        
		try {
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            valores = new ArrayList<Integer>();
            
            while ((linea = br.readLine()) != null) {
            	int val = Integer.parseInt(linea);
            	valores.add(val);
            }
            
        } catch (Exception e) {
            RegistroBean RegistroBean = new RegistroBean();
			RegistroBean.messageError("El archivo está mal, no tiene la estructura correcta!!");
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		
		return valores;
	}
	
	private String crearArchivoSalida(ArrayList<Integer> valores, String cedula){
		int numeroDias = valores.get(0);
		int numeroElementos = valores.get(1);
		int posicion = 2;
		
		FileWriter fw = null;
        BufferedWriter bw = null;

        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        
        String archivoOut = servletContext.getRealPath("") + File.separatorChar + "resources" + File.separatorChar + "archivo"
				+ File.separatorChar + "out";
        
        String archivoLocal = servletContext.getRealPath("") + File.separatorChar + "resources" + File.separatorChar + "archivo"
        		+ File.separatorChar + "out" + File.separatorChar + cedula;
        
        String nombreArchivo = "lazy_loading_output.txt";
        
        String pathDescarga = servletContext.getRealPath("") + File.separatorChar + "resources" + File.separatorChar + "archivo"
				+ File.separatorChar + "out";
        
		File crearCarpeta;
        
        crearCarpeta = new File(archivoOut);
		if(!crearCarpeta.exists())
			crearCarpeta.mkdirs();
		
		crearCarpeta = new File(archivoLocal);
		if(!crearCarpeta.exists())
			crearCarpeta.mkdirs();
		
		File archivo = new File(archivoLocal + File.separatorChar + nombreArchivo);
		
		try {
            
            if(1 <= numeroDias && numeroDias <= 500 ){
            	fw = new FileWriter(archivo);
                bw = new BufferedWriter(fw);
                
				for(int i=1; i <= numeroDias; i++){
					if(1 <= numeroElementos && numeroElementos <= 100 ){
						ArrayList<Integer> auxList = new ArrayList<Integer>();
						for(int j=0; j < numeroElementos; j++){
							int posAux = posicion++;
							if(1 <= valores.get(posAux) && valores.get(posAux) <= 100){
								auxList.add(valores.get(posAux));
							}else{
								RegistroBean registroBean = new RegistroBean();
								registroBean.messageWarning("El valor del peso no está en el rango permitido!!");
								return null;
							}
						}
						
						int cont = 0;
						int sum = 0;
						boolean flag = false;
						for(Integer item: auxList){
							if(item >= 50)
								cont++;
							else{
								sum += item;
							}
							
							if(cont > 0){
								flag = true;
							}
						}
						
						if(flag)
							cont += (sum / 50);
						
						if(!flag){
							for(Integer item: auxList){
								if((item + item) >= 50 && (auxList.size() % 2) == 0)
									cont++;
							}
							if(cont > 0)
								flag = true;
						}
						
						if(!flag){
							for(Integer item: auxList){
								if(auxList.size() > 3)
									sum += item;
							}
							cont += (sum / 50);
							
							if(cont > 0)
								flag = true;
						}
						
						if(cont == 0 && auxList.size() > 0)
							cont++;
			            
			            bw.write("Case #" + i + ": " + cont);
			            bw.newLine();
			            
			            if(i != numeroDias){
				            numeroElementos = valores.get(posicion);
							posicion++;
			            }
					}else{
		            	RegistroBean registroBean = new RegistroBean();
						registroBean.messageWarning("El número de elementos por día no está en el rango permitido!!");
						return null;
		            }
				}
				return pathDescarga;
            }else{
            	RegistroBean registroBean = new RegistroBean();
				registroBean.messageWarning("El número de días no está en el rango permitido!!");
				return null;
            }
			
		}catch (Exception e) {
			e.getMessage();
            e.printStackTrace();
            return null;
        }finally {
            try {
                if (bw != null) {
                	bw.close();
                }
                if (fw != null) {
                	fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	public String guardarLogoEnFicheroTemp(UploadedFile file, String nombreArchivo) {
		return null;
	}

	@Override
	public void guardarArchivosMultiple(UploadedFile file, String nombreArchivo) {

	}

	@Override
	public void updateRegistro(RegistroVO registroVO) {

	}

	@Override
	public void deleteRegistro(RegistroVO registroVO) {

	}

}
