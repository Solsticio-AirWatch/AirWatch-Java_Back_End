package br.com.fiap.airwatch.airreading.api;

import br.com.fiap.airwatch.airreading.dto.AirReadingRequest;
import br.com.fiap.airwatch.airreading.dto.AirReadingResponse;
import br.com.fiap.airwatch.airreading.service.AirReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/air-readings")
@RequiredArgsConstructor
@Tag(name = "AirReading", description = "Air quality readings")
public class AirReadingController {

    private final AirReadingService service;

    @GetMapping
    @Operation(summary = "List all air readings")
    public ResponseEntity<Page<AirReadingResponse>> findAll(@PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findAll(p));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find reading by ID")
    public ResponseEntity<EntityModel<AirReadingResponse>> findById(@PathVariable Long id) {
        AirReadingResponse response = service.findById(id);
        EntityModel<AirReadingResponse> model = EntityModel.of(response,
            linkTo(methodOn(AirReadingController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(AirReadingController.class).findAll(Pageable.unpaged())).withRel("air-readings"),
            linkTo(methodOn(AirReadingController.class).findByCity(response.cityId(), Pageable.unpaged())).withRel("readings-by-city")
        );
        return ResponseEntity.ok(model);
    }

    @GetMapping("/city/{cityId}")
    @Operation(summary = "List readings by city")
    public ResponseEntity<Page<AirReadingResponse>> findByCity(
            @PathVariable Long cityId, @PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findByCity(cityId, p));
    }

    @PostMapping
    @Operation(summary = "Create air reading")
    public ResponseEntity<EntityModel<AirReadingResponse>> create(@RequestBody @Valid AirReadingRequest req) {
        AirReadingResponse response = service.create(req);
        EntityModel<AirReadingResponse> model = EntityModel.of(response,
            linkTo(methodOn(AirReadingController.class).findById(response.id())).withSelfRel(),
            linkTo(methodOn(AirReadingController.class).findAll(Pageable.unpaged())).withRel("air-readings")
        );
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reading")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
