package dynamicqr.resource;

// Uso de escaneos pendiente. Al desplegar en un servidor publico, descomentar
// esta clase, QrService.registrarEscaneoYDestino y el payload de urlRedireccion
// para que el QR pase por /r/{id}, registre la lectura y redirija a destinoUrl.

// import dynamicqr.service.QrService;
// import jakarta.servlet.http.HttpServletRequest;
// import java.net.URI;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RestController;
//
// @RestController
// public class QrRedirectResource {
//
//     private final QrService qrService;
//
//     public QrRedirectResource(QrService qrService) {
//         this.qrService = qrService;
//     }
//
//     @GetMapping("/r/{id}")
//     public ResponseEntity<String> redirect(@PathVariable Integer id, HttpServletRequest request) {
//         String destino = qrService.registrarEscaneoYDestino(id, request);
//         HttpHeaders headers = new HttpHeaders();
//         headers.setLocation(URI.create(destino));
//         headers.setCacheControl("no-store");
//         return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
//     }
// }

public class QrRedirectResource {
}
