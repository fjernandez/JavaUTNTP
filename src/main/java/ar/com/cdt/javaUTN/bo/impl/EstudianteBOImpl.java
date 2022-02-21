package ar.com.cdt.javaUTN.bo.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.com.cdt.javaUTN.bo.EstudianteBO;
import ar.com.cdt.javaUTN.entity.EstudianteEntity;
import ar.com.cdt.javaUTN.exception.TPDatabaseException;
import ar.com.cdt.javaUTN.exception.TPDuplicatedStudentException;
import ar.com.cdt.javaUTN.exception.TPInvalidActionException;
import ar.com.cdt.javaUTN.exception.TPNoStudentFoundException;
import ar.com.cdt.javaUTN.models.EstudianteModel;
import ar.com.cdt.javaUTN.models.MessageModel;
import ar.com.cdt.javaUTN.repository.EstudianteRepository;

@Component
public class EstudianteBOImpl implements EstudianteBO {
	
	public static final Logger logger = LoggerFactory.getLogger(EstudianteBOImpl.class);
	public static final String update = "modificar";
	public static final String setActive = "alta";
	public static final String setInactive = "baja";
	
	@Autowired
	EstudianteRepository repository;

	@Override
	public EstudianteEntity guardarEstudiante(EstudianteModel estudiante) throws TPDuplicatedStudentException {
		logger.info("entra en guardarEstudiante: {}", estudiante.toString());
		// Valido si el estudiante esta registrado
		EstudianteEntity estudianteRegistrado = repository.findByDNI(estudiante.DNI);
		if (estudianteRegistrado != null) {
			throw new TPDuplicatedStudentException("Se encontro un usuario registrado con el DNI: " + String.valueOf(estudiante.DNI), "guardarEstudiante");
		}
		EstudianteEntity entity = mapEntityToModel(estudiante);
		return repository.save(entity);
	}
	
	@Override
	public EstudianteEntity getEstudiante(String dni) throws TPNoStudentFoundException {
		logger.info("entra en getEstudiante con el DNI: {}", dni);
		Long DNI = Long.valueOf(dni);
		EstudianteEntity entity = repository.findByDNI(DNI);
		if (entity == null) {
			throw new TPNoStudentFoundException("No se encontro ningun estudiante registrado con el DNI: " + dni , "getEstudiante");
		}
		return entity;
	}

	@Override
	public List<EstudianteEntity> getAllEstudiantes() throws TPNoStudentFoundException {
		List<EstudianteEntity> estudiantes = repository.findAll();
		if (estudiantes == null) {
			throw new TPNoStudentFoundException("No se encontraron estudiantes registrados", "getAllEstudiantes");
		}
		return estudiantes;
	}
	
	@Override
	public MessageModel updateStudent(String accion, EstudianteModel estudiante) throws TPNoStudentFoundException, TPInvalidActionException, TPDatabaseException {
		logger.info("entra en updateStudent con el DNI: {}", estudiante.DNI);
		
		Long DNI = Long.valueOf(estudiante.DNI);
		EstudianteEntity entity = repository.findByDNI(DNI);
		
		if (entity == null) {
			throw new TPNoStudentFoundException("No se encontraron estudiantes registrados", "updateStudent");
		}
		
		if (accion.equals(update)) {
			logger.info("Actualizo al estudiante, DNI: {}", estudiante.DNI);
			try {
				
				if (!entity.activo) {
					throw new TPNoStudentFoundException("Error al modificar - usted se encuentra dado de baja", "updateStudent");
				}
				
				entity.setNombre(estudiante.nombre);
				entity.setApellido(estudiante.apellido);
				entity.setEdad(estudiante.edad);
				
				repository.save(entity);
				return new MessageModel("El estudiante se actualizo correctamente");
			} catch (Exception e) {
				logger.error("Ocurrio un error al actualizar el estudiante: {}", e.getMessage());
				throw new TPDatabaseException(e.getMessage(), "updateStudent");
			}
		} else if (accion.equals(setInactive)) {
			logger.info("Baja al estudiante, DNI: {}", estudiante.DNI);
			try {
				if (!entity.activo) {
					throw new TPNoStudentFoundException("Usted ya se encuentra dado de baja", "updateStudent");
				}
				entity.setActivo(false);
				repository.save(entity);
				return new MessageModel("El estudiante se dio de baja correctamente");
			} catch (Exception e) {
				logger.error("Ocurrio un error al dar de baja al estudiante: {}", e.getMessage());
				throw new TPDatabaseException(e.getMessage(), "updateStudent");
			}
		} else if (accion.equals(setActive)) {
			logger.info("Alta a estudiante, DNI: {}", estudiante.DNI);
			try {
				if (!entity.activo) {
					
					entity.setActivo(true);
					repository.save(entity); 
					return new MessageModel("El estudiante se dio de alta correctamente");
				} else {
					throw new TPDuplicatedStudentException("Usted ya se encuentra registrado", "updateStudent");
				}
			} catch (Exception e) {
				logger.error("Ocurrio un error al dar de alta al estudiante: {}", e.getMessage());
				throw new TPDatabaseException(e.getMessage(), "updateStudent");
			}

		} else {
			throw new TPInvalidActionException("La accion no es valida", "updateStudent");
		}
	}
	
	public EstudianteEntity mapEntityToModel(EstudianteModel estudiante) {
		EstudianteEntity entity = new EstudianteEntity();
		entity.setNombre(estudiante.nombre);
		entity.setApellido(estudiante.apellido);
		entity.setDNI(estudiante.DNI);
		entity.setEdad(estudiante.edad);
		entity.setActivo(true);
		return entity;
	}
}
