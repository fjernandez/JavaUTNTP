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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.com.cdt.javaUTN.bo.EstudianteBO;
import ar.com.cdt.javaUTN.entity.EstudianteEntity;
import ar.com.cdt.javaUTN.exception.TPDatabaseException;
import ar.com.cdt.javaUTN.exception.TPDuplicatedStudentException;
import ar.com.cdt.javaUTN.exception.TPInvalidActionException;
import ar.com.cdt.javaUTN.exception.TPNoStudentFoundException;
import ar.com.cdt.javaUTN.models.EstudianteModel;
import ar.com.cdt.javaUTN.models.MessageModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@SpringBootApplication
@RestController
@RequestMapping("/api/estudiante")
public class EstudianteService {
	
	private static final Logger logger = LoggerFactory.getLogger(EstudianteService.class);
	
	@Autowired
	EstudianteBO estudianteBO;
	
	
	@Operation(summary = "Registra un estudiante con Nombre, Apellido, Dni y Edad")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Registró estudiante correctamente",
					content = { @Content(mediaType = "application/json",
					schema = @Schema(implementation = EstudianteEntity.class)) }),
			@ApiResponse(responseCode = "400", description = "Estudiante ya se encuentra registrado",
			content = @Content),
			@ApiResponse(responseCode = "500", description = "Ocurrio un error en la aplicacion",
			content = @Content)
	})
	@PostMapping("/guardar")
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
	@Operation(summary = "Consulta Estudiante por DNI")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Devuelve estudiante registrado",
					content = { @Content(mediaType = "application/json",
					schema = @Schema(implementation = EstudianteEntity.class)) }),
			@ApiResponse(responseCode = "400", description = "El estudiante no se encuentra registrado",
			content = @Content),
			@ApiResponse(responseCode = "500", description = "Ocurrio un error en la aplicacion",
			content = @Content)
	})
	@PostMapping("/consultar")
	public ResponseEntity<?> getByDni(@RequestBody EstudianteModel estudiante) {
		logger.info("EstudianteService - entra en getByDni con DNI: {}", estudiante.DNI);
		try {
			EstudianteEntity entity = estudianteBO.getEstudiante(estudiante.DNI);
			return new ResponseEntity<>(entity, HttpStatus.OK);
		} catch (TPNoStudentFoundException e) {
			logger.error("Error - No se encontro estudiante con el DNI: {}", estudiante.DNI);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("Error al consultar estudiante: ", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Consulta todos los estudiantes")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Devuelve estudiantes registrados",
					content = { @Content(mediaType = "application/json",
					schema = @Schema(implementation = EstudianteEntity.class)) }),
			@ApiResponse(responseCode = "400", description = "No hay estudiantes registrados",
			content = @Content),
			@ApiResponse(responseCode = "500", description = "Ocurrio un error en la aplicacion",
			content = @Content)
	})
	@GetMapping("/consultarTodos")
	public ResponseEntity<?> getAll(){
		logger.info("EstudianteSerive - Entra en getAll");
		try {
			List<EstudianteEntity> estudiantes = estudianteBO.getAllEstudiantes();
			return new ResponseEntity<>(estudiantes, HttpStatus.OK);
		} catch (TPNoStudentFoundException e) {
			logger.error("Error - no hay estudiantes registrados");
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("Error al consultar estudiantes: ", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Actualiza, Baja y Alta a un estudiante, la accion del servicio la determina el Path {accion}, esta puede ser 'modificar, alta o baja'")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", 
					description = "Accion- modificar: Devuelve el estudiante modificado | Accion - baja: Devuelve mensaje de baja | Accion - alta: Devuelve mensaje de alta",
					content = { @Content(mediaType = "application/json",
					schema = @Schema(implementation = MessageModel.class)) }),
			@ApiResponse(responseCode = "400", description = "No hay estudiante registrado para modificar|baja|alta - La accion ingresada en el Path no es válida",
			content = @Content),
			@ApiResponse(responseCode = "409", description = "Ocurrio un error con la Query/DB",
			content = @Content),
			@ApiResponse(responseCode = "500", description = "Ocurrio un error en la aplicacion",
			content = @Content)
	})
	@PostMapping("/actualizar/{accion}")
	public ResponseEntity<?> updateStudent(@RequestBody EstudianteModel estudiante, @PathVariable("accion") String accion) {
		logger.info("EstudianteService - entra en actualizarEstudiante para: {}", accion);
		try {
			MessageModel message = estudianteBO.updateStudent(accion, estudiante);
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (TPNoStudentFoundException | TPInvalidActionException e) {
			logger.error("Error al invocar la accion | Error al modificar/baja - estudiante no registrado");
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (TPDatabaseException e) {
			logger.error("Error al modificar/baja - Error en DB");
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			logger.error("Error al modificar/baja");
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Devuelve la cantidad de packs que hay que comprar para la cantidad de estudiantes * edad")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retorna un mensaje con la cantidad de packs",
					content = { @Content(mediaType = "application/json",
					schema = @Schema(implementation = MessageModel.class)) }),
			@ApiResponse(responseCode = "500", description = "Ocurrio un error en la aplicacion",
			content = @Content)
	})
	@GetMapping("/calcularBebidas")
	public ResponseEntity<?> calculateDrinks() {
		try {
			MessageModel ent = estudianteBO.calculateDrinks();
			return new ResponseEntity<>(ent, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error al calcular total de bebidas", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}