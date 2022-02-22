package ar.com.cdt.javaUTN.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ar.com.cdt.javaUTN.entity.*;


public interface EstudianteRepository extends JpaRepository<EstudianteEntity, Long> {
	
	EstudianteEntity findByDNI(long DNI);

}
