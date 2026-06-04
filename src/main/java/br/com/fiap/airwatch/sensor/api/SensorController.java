package br.com.fiap.airwatch.sensor.api;

import br.com.fiap.airwatch.sensor.dto.SensorRequest;
import br.com.fiap.airwatch.sensor.dto.SensorResponse;
import br.com.fiap.airwatch.sensor.service.SensorService;
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
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
@Tag(name = "Sensor", description = "IoT sensor management")
public class SensorController {

    private final SensorService service;

    @GetMapping
    @Operation(summary = "List all sensors")
    public ResponseEntity<Page<SensorResponse>> findAll(@PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findAll(p));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find sensor by ID")
    public ResponseEntity<EntityModel<SensorResponse>> findById(@PathVariable Long id) {
        SensorResponse response = service.findById(id);
        EntityModel<SensorResponse> model = EntityModel.of(response,
            linkTo(methodOn(SensorController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(SensorController.class).findAll(Pageable.unpaged())).withRel("sensors"),
            linkTo(methodOn(SensorController.class).findByCity(response.cityId(), Pageable.unpaged())).withRel("sensors-by-city")
        );
        return ResponseEntity.ok(model);
    }

    @GetMapping("/city/{cityId}")
    @Operation(summary = "List sensors by city")
    public ResponseEntity<Page<SensorResponse>> findByCity(
            @PathVariable Long cityId, @PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findByCity(cityId, p));
    }

    @PostMapping
    @Operation(summary = "Create sensor")
    public ResponseEntity<EntityModel<SensorResponse>> create(@RequestBody @Valid SensorRequest req) {
        SensorResponse response = service.create(req);
        EntityModel<SensorResponse> model = EntityModel.of(response,
            linkTo(methodOn(SensorController.class).findById(response.id())).withSelfRel(),
            linkTo(methodOn(SensorController.class).findAll(Pageable.unpaged())).withRel("sensors")
        );
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sensor")
    public ResponseEntity<EntityModel<SensorResponse>> update(
            @PathVariable Long id, @RequestBody @Valid SensorRequest req) {
        SensorResponse response = service.update(id, req);
        EntityModel<SensorResponse> model = EntityModel.of(response,
            linkTo(methodOn(SensorController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(SensorController.class).findAll(Pageable.unpaged())).withRel("sensors")
        );
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sensor")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
