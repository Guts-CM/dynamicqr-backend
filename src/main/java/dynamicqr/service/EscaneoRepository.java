package dynamicqr.service;

import dynamicqr.domain.Escaneo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscaneoRepository extends JpaRepository<Escaneo, Integer> {
}
