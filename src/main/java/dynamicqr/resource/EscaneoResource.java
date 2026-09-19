package dynamicqr.resource;

// Uso de escaneos pendiente. Al desplegar en un servidor publico, descomentar.

// import dynamicqr.dto.EscaneoResponse;
// import dynamicqr.service.EscaneoService;
// import java.util.List;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
//
// @RestController
// @RequestMapping("/api/escaneos")
// public class EscaneoResource {
//
//     private final EscaneoService escaneoService;
//
//     public EscaneoResource(EscaneoService escaneoService) {
//         this.escaneoService = escaneoService;
//     }
//
//     @GetMapping
//     public List<EscaneoResponse> findAll(@RequestParam(required = false) Integer qrId) {
//         if (qrId == null) {
//             return escaneoService.findAll();
//         }
//         return escaneoService.findByQrId(qrId);
//     }
//
//     @GetMapping("/{id}")
//     public EscaneoResponse findById(@PathVariable Integer id) {
//         return escaneoService.findById(id);
//     }
// }

public class EscaneoResource {
}
