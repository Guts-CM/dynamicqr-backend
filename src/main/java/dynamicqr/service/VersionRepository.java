package dynamicqr.service;

import dynamicqr.domain.Version;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionRepository extends JpaRepository<Version, Integer> {

    List<Version> findByQrIdOrderByFechaCreacionDesc(Integer qrId);
}
