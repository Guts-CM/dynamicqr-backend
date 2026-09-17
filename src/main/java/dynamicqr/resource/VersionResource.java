package dynamicqr.resource;

import dynamicqr.domain.Version;
import dynamicqr.service.VersionService;
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
@RequestMapping("/api/versiones")
public class VersionResource {

    private final VersionService versionService;

    public VersionResource(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping
    public List<Version> findAll() {
        return versionService.findAll();
    }

    @GetMapping("/{id}")
    public Version findById(@PathVariable Integer id) {
        return versionService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Version> create(@RequestBody Version version) {
        Version creado = versionService.create(version);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getVersionesId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public Version update(@PathVariable Integer id, @RequestBody Version version) {
        return versionService.update(id, version);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        versionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
