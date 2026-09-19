package dynamicqr.service;

import dynamicqr.domain.Escaneo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscaneoRepository extends JpaRepository<Escaneo, Integer> {

    List<Escaneo> findByQrIdOrderByFechaCreacionDesc(Integer qrId);
}
