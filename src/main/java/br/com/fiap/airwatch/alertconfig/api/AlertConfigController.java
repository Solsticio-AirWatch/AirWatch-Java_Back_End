package br.com.fiap.airwatch.alertconfig.api;

import br.com.fiap.airwatch.alertconfig.dto.AlertConfigRequest;
import br.com.fiap.airwatch.alertconfig.dto.AlertConfigResponse;
import br.com.fiap.airwatch.alertconfig.service.AlertConfigService;
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
@RequestMapping("/api/alert-configs")
@RequiredArgsConstructor
@Tag(name = "AlertConfig", description = "Alert configuration management")
public class AlertConfigController {

    private final AlertConfigService service;

    @GetMapping
    @Operation(summary = "List all alert configs")
    public ResponseEntity<Page<AlertConfigResponse>> findAll(@PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findAll(p));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find alert config by ID")
    public ResponseEntity<EntityModel<AlertConfigResponse>> findById(@PathVariable Long id) {
        AlertConfigResponse response = service.findById(id);
        EntityModel<AlertConfigResponse> model = EntityModel.of(response,
            linkTo(methodOn(AlertConfigController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(AlertConfigController.class).findAll(Pageable.unpaged())).withRel("alert-configs"),
            linkTo(methodOn(AlertConfigController.class).findByUser(response.userId(), Pageable.unpaged())).withRel("configs-by-user"),
            linkTo(methodOn(AlertConfigController.class).findByCity(response.cityId(), Pageable.unpaged())).withRel("configs-by-city")
        );
        return ResponseEntity.ok(model);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List by user")
    public ResponseEntity<Page<AlertConfigResponse>> findByUser(
            @PathVariable Long userId, @PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findByUser(userId, p));
    }

    @GetMapping("/city/{cityId}")
    @Operation(summary = "List by city")
    public ResponseEntity<Page<AlertConfigResponse>> findByCity(
            @PathVariable Long cityId, @PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findByCity(cityId, p));
    }

    @PostMapping
    @Operation(summary = "Create alert config")
    public ResponseEntity<EntityModel<AlertConfigResponse>> create(@RequestBody @Valid AlertConfigRequest req) {
        AlertConfigResponse response = service.create(req);
        EntityModel<AlertConfigResponse> model = EntityModel.of(response,
            linkTo(methodOn(AlertConfigController.class).findById(response.id())).withSelfRel(),
            linkTo(methodOn(AlertConfigController.class).findAll(Pageable.unpaged())).withRel("alert-configs")
        );
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update alert config")
    public ResponseEntity<EntityModel<AlertConfigResponse>> update(
            @PathVariable Long id, @RequestBody @Valid AlertConfigRequest req) {
        AlertConfigResponse response = service.update(id, req);
        EntityModel<AlertConfigResponse> model = EntityModel.of(response,
            linkTo(methodOn(AlertConfigController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(AlertConfigController.class).findAll(Pageable.unpaged())).withRel("alert-configs")
        );
        return ResponseEntity.ok(model);
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle active/inactive")
    public ResponseEntity<EntityModel<AlertConfigResponse>> toggle(@PathVariable Long id) {
        AlertConfigResponse response = service.toggle(id);
        EntityModel<AlertConfigResponse> model = EntityModel.of(response,
            linkTo(methodOn(AlertConfigController.class).findById(id)).withSelfRel()
        );
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete alert config")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
