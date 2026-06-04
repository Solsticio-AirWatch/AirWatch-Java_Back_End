package br.com.fiap.airwatch.city.api;

import br.com.fiap.airwatch.city.dto.CityRequest;
import br.com.fiap.airwatch.city.dto.CityResponse;
import br.com.fiap.airwatch.city.service.CityService;
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
@RequestMapping("/api/cities")
@RequiredArgsConstructor
@Tag(name = "City", description = "City management")
public class CityController {

    private final CityService service;

    @GetMapping
    @Operation(summary = "List all cities")
    public ResponseEntity<Page<CityResponse>> findAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find city by ID")
    public ResponseEntity<EntityModel<CityResponse>> findById(@PathVariable Long id) {
        CityResponse response = service.findById(id);
        EntityModel<CityResponse> model = EntityModel.of(response,
            linkTo(methodOn(CityController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(CityController.class).findAll(Pageable.unpaged())).withRel("cities"),
            linkTo(methodOn(CityController.class).findByCountry(response.countryId(), Pageable.unpaged())).withRel("cities-by-country")
        );
        return ResponseEntity.ok(model);
    }

    @GetMapping("/country/{countryId}")
    @Operation(summary = "List cities by country")
    public ResponseEntity<Page<CityResponse>> findByCountry(
            @PathVariable Long countryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.findByCountry(countryId, pageable));
    }

    @PostMapping
    @Operation(summary = "Create city")
    public ResponseEntity<EntityModel<CityResponse>> create(@RequestBody @Valid CityRequest req) {
        CityResponse response = service.create(req);
        EntityModel<CityResponse> model = EntityModel.of(response,
            linkTo(methodOn(CityController.class).findById(response.id())).withSelfRel(),
            linkTo(methodOn(CityController.class).findAll(Pageable.unpaged())).withRel("cities")
        );
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update city")
    public ResponseEntity<EntityModel<CityResponse>> update(
            @PathVariable Long id, @RequestBody @Valid CityRequest req) {
        CityResponse response = service.update(id, req);
        EntityModel<CityResponse> model = EntityModel.of(response,
            linkTo(methodOn(CityController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(CityController.class).findAll(Pageable.unpaged())).withRel("cities")
        );
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete city")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
