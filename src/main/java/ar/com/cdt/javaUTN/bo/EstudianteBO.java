package ar.com.cdt.javaUTN.bo;

import java.util.List;

import ar.com.cdt.javaUTN.entity.EstudianteEntity;
import ar.com.cdt.javaUTN.exception.TPDatabaseException;
import ar.com.cdt.javaUTN.exception.TPDuplicatedStudentException;
import ar.com.cdt.javaUTN.exception.TPInvalidActionException;
import ar.com.cdt.javaUTN.exception.TPNoStudentFoundException;
import ar.com.cdt.javaUTN.models.EstudianteModel;
import ar.com.cdt.javaUTN.models.MessageModel;

public interface EstudianteBO {
	
	public EstudianteEntity guardarEstudiante(EstudianteModel estudiante) throws TPDuplicatedStudentException;
	
	public EstudianteEntity getEstudiante(String dni) throws TPNoStudentFoundException;
	
	public List<EstudianteEntity> getAllEstudiantes() throws TPNoStudentFoundException;

	public MessageModel updateStudent(String accion, EstudianteModel estudiante) throws TPNoStudentFoundException, TPInvalidActionException, TPDatabaseException;

}
