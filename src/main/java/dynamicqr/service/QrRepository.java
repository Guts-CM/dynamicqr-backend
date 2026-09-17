package dynamicqr.service;

import dynamicqr.domain.Qr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrRepository extends JpaRepository<Qr, Integer> {
}
