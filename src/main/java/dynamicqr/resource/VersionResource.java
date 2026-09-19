package dynamicqr.resource;

import dynamicqr.dto.VersionResponse;
import dynamicqr.service.VersionService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/versiones")
public class VersionResource {

    private final VersionService versionService;

    public VersionResource(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping
    public List<VersionResponse> findAll(@RequestParam(required = false) Integer qrId) {
        if (qrId == null) {
            return versionService.findAll();
        }
        return versionService.findByQrId(qrId);
    }

    @GetMapping("/{id}")
    public VersionResponse findById(@PathVariable Integer id) {
        return versionService.findById(id);
    }
}
