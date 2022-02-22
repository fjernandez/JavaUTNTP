package ar.com.cdt.javaUTN.bo.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.com.cdt.javaUTN.bo.EstudianteBO;
import ar.com.cdt.javaUTN.entity.EstudianteEntity;
import ar.com.cdt.javaUTN.exception.*;
import ar.com.cdt.javaUTN.models.*;
import ar.com.cdt.javaUTN.repository.EstudianteRepository;

@Component
public class EstudianteBOImpl implements EstudianteBO {
	
	public static final Logger logger = LoggerFactory.getLogger(EstudianteBOImpl.class);
	public static final String update = "modificar";
	public static final String setActive = "alta";
	public static final String setInactive = "baja";
	public static final Integer sixPack = 6;
	
	@Autowired
	EstudianteRepository repository;

	@Override
	public EstudianteEntity guardarEstudiante(EstudianteModel estudiante) throws TPDuplicatedStudentException {
		logger.info("entra en guardarEstudiante: {}", estudiante.toString());
		EstudianteEntity estudianteRegistrado = repository.findByDNI(estudiante.DNI);
		if (estudianteRegistrado != null) {
			throw new TPDuplicatedStudentException("Se encontro un usuario registrado con el DNI: " + String.valueOf(estudiante.DNI), "guardarEstudiante");
		}
		EstudianteEntity entity = mapEntityToModel(estudiante);
		return repository.save(entity);
	}
	
	@Override
	public EstudianteEntity getEstudiante(long dni) throws TPNoStudentFoundException {
		logger.info("entra en getEstudiante con el DNI: {}", dni);
		EstudianteEntity entity = repository.findByDNI(dni);
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
	
	@Override
	public MessageModel calculateDrinks() {
		logger.info("Entra en calculateDrinks");
		List<Integer> totalEdades = new ArrayList<>();
		
		List<EstudianteEntity> estudiantes = repository.findAll();
		for (int i = 0; i < estudiantes.size(); i++) {
			EstudianteEntity estudiante = estudiantes.get(i);
			if (estudiante.activo) {
				totalEdades.add(estudiante.getEdad());
			}
			
		}
		
		//List<Integer> de20a30 = IntStream.range(30, 40)
				//		.filter(x -> totalEdades.contains(x))
				//		.boxed()
				//		.collect(Collectors.toList());
		
		List<Integer> de20a30 = totalEdades.stream()
				.filter(x -> x.intValue() >=  20 && x.intValue() <= 30)
				.collect(Collectors.toList());
		
		List<Integer> de31a45 = totalEdades.stream()
				.filter(x -> x.intValue() > 30 && x.intValue() <= 45)
				.collect(Collectors.toList());
		
		List<Integer> de45enAdelante = totalEdades.stream()
				.filter(x -> x.intValue() > 45 && x.intValue() <= 90)
				.collect(Collectors.toList());
		
		Integer totalBebidas20a30 = de20a30.size() * 3;
		Integer totalBebidas31a45 = de31a45.size() * 2;
		Integer totalBebidas45enAdelante = de45enAdelante.size() * 1;
		Integer totalBebidas = totalBebidas20a30 + totalBebidas31a45 + totalBebidas45enAdelante;
		
		Double totalPacks = Math.ceil((double)totalBebidas / sixPack);
		MessageModel message = new MessageModel("Se tiene que comprar: " + totalPacks.intValue() + " packs");		
		return message;
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