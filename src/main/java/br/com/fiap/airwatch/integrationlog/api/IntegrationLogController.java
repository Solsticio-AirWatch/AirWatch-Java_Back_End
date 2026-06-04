package br.com.fiap.airwatch.integrationlog.api;

import br.com.fiap.airwatch.integrationlog.dto.IntegrationLogRequest;
import br.com.fiap.airwatch.integrationlog.dto.IntegrationLogResponse;
import br.com.fiap.airwatch.integrationlog.service.IntegrationLogService;
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
@RequestMapping("/api/integration-logs")
@RequiredArgsConstructor
@Tag(name = "IntegrationLog", description = "API integration log")
public class IntegrationLogController {

    private final IntegrationLogService service;

    @GetMapping
    @Operation(summary = "List all integration logs")
    public ResponseEntity<Page<IntegrationLogResponse>> findAll(@PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findAll(p));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find by ID")
    public ResponseEntity<EntityModel<IntegrationLogResponse>> findById(@PathVariable Long id) {
        IntegrationLogResponse response = service.findById(id);
        EntityModel<IntegrationLogResponse> model = EntityModel.of(response,
            linkTo(methodOn(IntegrationLogController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(IntegrationLogController.class).findAll(Pageable.unpaged())).withRel("integration-logs")
        );
        return ResponseEntity.ok(model);
    }

    @GetMapping("/api-name/{apiName}")
    @Operation(summary = "List by API name")
    public ResponseEntity<Page<IntegrationLogResponse>> findByApiName(
            @PathVariable String apiName, @PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findByApiName(apiName, p));
    }

    @GetMapping("/result/{result}")
    @Operation(summary = "List by result (SUCCESS, ERROR, TIMEOUT, NO_DATA)")
    public ResponseEntity<Page<IntegrationLogResponse>> findByResult(
            @PathVariable String result, @PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findByResult(result, p));
    }

    @GetMapping("/city/{cityId}")
    @Operation(summary = "List by city")
    public ResponseEntity<Page<IntegrationLogResponse>> findByCity(
            @PathVariable Long cityId, @PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findByCity(cityId, p));
    }

    @GetMapping("/errors")
    @Operation(summary = "List all failed calls")
    public ResponseEntity<Page<IntegrationLogResponse>> findAllErrors(@PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findAllErrors(p));
    }

    @PostMapping
    @Operation(summary = "Create integration log")
    public ResponseEntity<EntityModel<IntegrationLogResponse>> create(@RequestBody @Valid IntegrationLogRequest req) {
        IntegrationLogResponse response = service.create(req);
        EntityModel<IntegrationLogResponse> model = EntityModel.of(response,
            linkTo(methodOn(IntegrationLogController.class).findById(response.id())).withSelfRel(),
            linkTo(methodOn(IntegrationLogController.class).findAll(Pageable.unpaged())).withRel("integration-logs")
        );
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete integration log")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
