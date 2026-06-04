package br.com.fiap.airwatch.country.api;

import br.com.fiap.airwatch.country.dto.CountryRequest;
import br.com.fiap.airwatch.country.dto.CountryResponse;
import br.com.fiap.airwatch.country.service.CountryService;
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
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@Tag(name = "Country", description = "Country management")
public class CountryController {

    private final CountryService service;

    @GetMapping
    @Operation(summary = "List all countries")
    public ResponseEntity<Page<CountryResponse>> findAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find country by ID")
    public ResponseEntity<EntityModel<CountryResponse>> findById(@PathVariable Long id) {
        CountryResponse response = service.findById(id);
        EntityModel<CountryResponse> model = EntityModel.of(response,
            linkTo(methodOn(CountryController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(CountryController.class).findAll(Pageable.unpaged())).withRel("countries")
        );
        return ResponseEntity.ok(model);
    }

    @PostMapping
    @Operation(summary = "Create country")
    public ResponseEntity<EntityModel<CountryResponse>> create(@RequestBody @Valid CountryRequest req) {
        CountryResponse response = service.create(req);
        EntityModel<CountryResponse> model = EntityModel.of(response,
            linkTo(methodOn(CountryController.class).findById(response.id())).withSelfRel(),
            linkTo(methodOn(CountryController.class).findAll(Pageable.unpaged())).withRel("countries")
        );
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update country")
    public ResponseEntity<EntityModel<CountryResponse>> update(
            @PathVariable Long id, @RequestBody @Valid CountryRequest req) {
        CountryResponse response = service.update(id, req);
        EntityModel<CountryResponse> model = EntityModel.of(response,
            linkTo(methodOn(CountryController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(CountryController.class).findAll(Pageable.unpaged())).withRel("countries")
        );
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete country")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
