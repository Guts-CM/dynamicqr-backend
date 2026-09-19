package dynamicqr.resource;

import dynamicqr.dto.MensajeResponse;
import dynamicqr.dto.QrCreateRequest;
import dynamicqr.dto.QrResponse;
import dynamicqr.dto.QrUpdateRequest;
import dynamicqr.dto.VersionResponse;
import dynamicqr.security.SecurityUtils;
import dynamicqr.service.QrService;
import dynamicqr.service.VersionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
public class QrResource {

    private static final MediaType SVG = MediaType.parseMediaType("image/svg+xml");

    private final QrService qrService;
    private final VersionService versionService;
    private final SecurityUtils securityUtils;

    public QrResource(
            QrService qrService,
            VersionService versionService,
            SecurityUtils securityUtils) {
        this.qrService = qrService;
        this.versionService = versionService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public List<QrResponse> findAll() {
        return qrService.findAll();
    }

    @GetMapping("/{id}")
    public QrResponse findById(@PathVariable Integer id) {
        return qrService.findById(id);
    }

    @GetMapping(value = "/{id}/svg", produces = "image/svg+xml")
    public ResponseEntity<String> svg(@PathVariable Integer id) {
        return ResponseEntity.ok()
                .contentType(SVG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qr-" + id + ".svg\"")
                .body(qrService.generarSvg(id));
    }

    @GetMapping(value = "/{id}/png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> png(@PathVariable Integer id) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qr-" + id + ".png\"")
                .body(qrService.generarPng(id));
    }

    // Uso de escaneos pendiente hasta desplegar en un servidor publico.
    // Inyectar EscaneoService y descomentar:
    // @GetMapping("/{id}/escaneos")
    // public List<EscaneoResponse> escaneos(@PathVariable Integer id) {
    //     qrService.findById(id);
    //     return escaneoService.findByQrId(id);
    // }

    @GetMapping("/{id}/versiones")
    public List<VersionResponse> versiones(@PathVariable Integer id) {
        qrService.findById(id);
        return versionService.findByQrId(id);
    }

    @PostMapping
    public ResponseEntity<MensajeResponse> create(@Valid @RequestBody QrCreateRequest request) {
        try {
            qrService.create(request, securityUtils.currentUserId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MensajeResponse("qr creado correctamente"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MensajeResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponse("hubo un error al crear el qr"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensajeResponse> update(
            @PathVariable Integer id, @Valid @RequestBody QrUpdateRequest request) {
        try {
            qrService.update(id, request, securityUtils.currentUserId());
            return ResponseEntity.ok(new MensajeResponse("qr actualizado correctamente"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MensajeResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponse("hubo un error al actualizar el qr"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> toggleActivo(@PathVariable Integer id) {
        try {
            boolean activo = qrService.toggleActivo(id, securityUtils.currentUserId());
            String mensaje = activo ? "qr activado correctamente" : "qr eliminado correctamente";
            return ResponseEntity.ok(new MensajeResponse(mensaje));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponse("hubo un error al eliminar o activar el qr"));
        }
    }
}
