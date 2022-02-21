package ar.com.cdt.javaUTN.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.com.cdt.javaUTN.bo.EstudianteBO;
import ar.com.cdt.javaUTN.entity.EstudianteEntity;
import ar.com.cdt.javaUTN.exception.TPDatabaseException;
import ar.com.cdt.javaUTN.exception.TPDuplicatedStudentException;
import ar.com.cdt.javaUTN.exception.TPInvalidActionException;
import ar.com.cdt.javaUTN.exception.TPNoStudentFoundException;
import ar.com.cdt.javaUTN.models.EstudianteModel;
import ar.com.cdt.javaUTN.models.MessageModel;

@SpringBootApplication
@RestController
public class EstudianteService {
	
	private static final Logger logger = LoggerFactory.getLogger(EstudianteService.class);
	
	@Autowired
	EstudianteBO estudianteBO;
	
	@PostMapping("/guardarEstudiante")
	public ResponseEntity<?> save(@RequestBody EstudianteModel estudiante) throws TPDuplicatedStudentException {
		logger.info("EstudianteService - entra en save con estudiante: {}", estudiante);
		try {
			EstudianteEntity entity = estudianteBO.guardarEstudiante(estudiante);
			return new ResponseEntity<>(entity, HttpStatus.OK);
		} catch (TPDuplicatedStudentException e) {
			logger.error("Error - El estudiante ya se encuentra registrado", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("Error al registrar estudiante: ", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/consultarEstudiante/{dni}")
	public ResponseEntity<?> getByDni(@PathVariable("dni") String dni) {
		logger.info("EstudianteService - entra en getByDni con DNI: {}", dni);
		try {
			EstudianteEntity entity = estudianteBO.getEstudiante(dni);
			return new ResponseEntity<>(entity, HttpStatus.OK);
		} catch (TPNoStudentFoundException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/consultarEstudiantes")
	public ResponseEntity<?> getAll(){
		logger.info("EstudianteSerive - Entra en getAll");
		try {
			List<EstudianteEntity> estudiantes = estudianteBO.getAllEstudiantes();
			return new ResponseEntity<>(estudiantes, HttpStatus.OK);
		} catch (TPNoStudentFoundException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/actualizarEstudiante/{accion}")
	public ResponseEntity<?> updateStudent(@RequestBody EstudianteModel estudiante, @PathVariable("accion") String accion) {
		logger.info("EstudianteService - entra en actualizarEstudiante para: {}", accion);
		try {
			MessageModel message = estudianteBO.updateStudent(accion, estudiante);
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (TPNoStudentFoundException | TPInvalidActionException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (TPDatabaseException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	

}