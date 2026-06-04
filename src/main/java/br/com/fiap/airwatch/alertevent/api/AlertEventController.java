package br.com.fiap.airwatch.alertevent.api;

import br.com.fiap.airwatch.alertevent.dto.AlertEventRequest;
import br.com.fiap.airwatch.alertevent.dto.AlertEventResponse;
import br.com.fiap.airwatch.alertevent.service.AlertEventService;
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
@RequestMapping("/api/alert-events")
@RequiredArgsConstructor
@Tag(name = "AlertEvent", description = "Alert event history")
public class AlertEventController {

    private final AlertEventService service;

    @GetMapping
    @Operation(summary = "List all alert events")
    public ResponseEntity<Page<AlertEventResponse>> findAll(@PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findAll(p));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find alert event by ID")
    public ResponseEntity<EntityModel<AlertEventResponse>> findById(@PathVariable Long id) {
        AlertEventResponse response = service.findById(id);
        EntityModel<AlertEventResponse> model = EntityModel.of(response,
            linkTo(methodOn(AlertEventController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(AlertEventController.class).findAll(Pageable.unpaged())).withRel("alert-events"),
            linkTo(methodOn(AlertEventController.class).findByCity(response.cityId(), Pageable.unpaged())).withRel("events-by-city")
        );
        return ResponseEntity.ok(model);
    }

    @GetMapping("/city/{cityId}")
    @Operation(summary = "List by city")
    public ResponseEntity<Page<AlertEventResponse>> findByCity(
            @PathVariable Long cityId, @PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findByCity(cityId, p));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List by user")
    public ResponseEntity<Page<AlertEventResponse>> findByUser(
            @PathVariable Long userId, @PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findByUser(userId, p));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List by status (PENDING, SENT, ERROR, IGNORED)")
    public ResponseEntity<Page<AlertEventResponse>> findByStatus(
            @PathVariable String status, @PageableDefault(size = 20) Pageable p) {
        return ResponseEntity.ok(service.findByStatus(status, p));
    }

    @PostMapping
    @Operation(summary = "Create alert event")
    public ResponseEntity<EntityModel<AlertEventResponse>> create(@RequestBody @Valid AlertEventRequest req) {
        AlertEventResponse response = service.create(req);
        EntityModel<AlertEventResponse> model = EntityModel.of(response,
            linkTo(methodOn(AlertEventController.class).findById(response.id())).withSelfRel(),
            linkTo(methodOn(AlertEventController.class).findAll(Pageable.unpaged())).withRel("alert-events")
        );
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(model);
    }

    @PatchMapping("/{id}/send")
    @Operation(summary = "Mark as SENT")
    public ResponseEntity<EntityModel<AlertEventResponse>> markAsSent(@PathVariable Long id) {
        AlertEventResponse response = service.markAsSent(id);
        EntityModel<AlertEventResponse> model = EntityModel.of(response,
            linkTo(methodOn(AlertEventController.class).findById(id)).withSelfRel()
        );
        return ResponseEntity.ok(model);
    }

    @PatchMapping("/{id}/ignore")
    @Operation(summary = "Mark as IGNORED")
    public ResponseEntity<EntityModel<AlertEventResponse>> markAsIgnored(@PathVariable Long id) {
        AlertEventResponse response = service.markAsIgnored(id);
        EntityModel<AlertEventResponse> model = EntityModel.of(response,
            linkTo(methodOn(AlertEventController.class).findById(id)).withSelfRel()
        );
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete alert event")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
