package dynamicqr.resource;

import dynamicqr.domain.Qr;
import dynamicqr.service.QrService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/qr")
public class QrResource {

    private final QrService qrService;

    public QrResource(QrService qrService) {
        this.qrService = qrService;
    }

    @GetMapping
    public List<Qr> findAll() {
        return qrService.findAll();
    }

    @GetMapping("/{id}")
    public Qr findById(@PathVariable Integer id) {
        return qrService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Qr> create(@RequestBody Qr qr) {
        Qr creado = qrService.create(qr);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getQrId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public Qr update(@PathVariable Integer id, @RequestBody Qr qr) {
        return qrService.update(id, qr);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        qrService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
