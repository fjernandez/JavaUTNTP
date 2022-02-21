package ar.com.cdt.javaUTN.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.com.cdt.javaUTN.entity.*;
import ar.com.cdt.javaUTN.models.MessageModel;


public interface EstudianteRepository extends JpaRepository<EstudianteEntity, Long> {
	
	EstudianteEntity findByDNI(long DNI);
	
	Integer deleteByDNI(long DNI);

}
