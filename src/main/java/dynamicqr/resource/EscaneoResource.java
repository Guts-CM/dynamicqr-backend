package dynamicqr.resource;

import dynamicqr.domain.Escaneo;
import dynamicqr.service.EscaneoService;
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
@RequestMapping("/api/escaneos")
public class EscaneoResource {

    private final EscaneoService escaneoService;

    public EscaneoResource(EscaneoService escaneoService) {
        this.escaneoService = escaneoService;
    }

    @GetMapping
    public List<Escaneo> findAll() {
        return escaneoService.findAll();
    }

    @GetMapping("/{id}")
    public Escaneo findById(@PathVariable Integer id) {
        return escaneoService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Escaneo> create(@RequestBody Escaneo escaneo) {
        Escaneo creado = escaneoService.create(escaneo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getEscaneosId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public Escaneo update(@PathVariable Integer id, @RequestBody Escaneo escaneo) {
        return escaneoService.update(id, escaneo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        escaneoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
