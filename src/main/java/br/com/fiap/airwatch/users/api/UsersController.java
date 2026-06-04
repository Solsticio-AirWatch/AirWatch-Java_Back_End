package br.com.fiap.airwatch.users.api;

import br.com.fiap.airwatch.users.dto.*;
import br.com.fiap.airwatch.users.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management and authentication")
public class UsersController {

    private final UsersService service;

    @PostMapping("/api/auth/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<EntityModel<UserResponse>> register(@RequestBody @Valid UserRequest req) {
        UserResponse response = service.create(req);
        EntityModel<UserResponse> model = EntityModel.of(response,
            linkTo(methodOn(UsersController.class).findById(response.id())).withSelfRel(),
            linkTo(methodOn(UsersController.class).findAll(Pageable.unpaged())).withRel("users")
        );
        var uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(model);
    }

    @PostMapping("/api/auth/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest req) {
        return ResponseEntity.ok(service.authenticate(req));
    }

    @GetMapping("/api/users")
    @Operation(summary = "List all users")
    public ResponseEntity<Page<UserResponse>> findAll(Pageable p) {
        return ResponseEntity.ok(service.findAll(p));
    }

    @GetMapping("/api/users/{id}")
    @Operation(summary = "Find user by ID")
    public ResponseEntity<EntityModel<UserResponse>> findById(@PathVariable Long id) {
        UserResponse response = service.findById(id);
        EntityModel<UserResponse> model = EntityModel.of(response,
            linkTo(methodOn(UsersController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(UsersController.class).findAll(Pageable.unpaged())).withRel("users")
        );
        return ResponseEntity.ok(model);
    }

    @PutMapping("/api/users/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<EntityModel<UserResponse>> update(
            @PathVariable Long id, @RequestBody @Valid UserRequest req) {
        UserResponse response = service.update(id, req);
        EntityModel<UserResponse> model = EntityModel.of(response,
            linkTo(methodOn(UsersController.class).findById(id)).withSelfRel(),
            linkTo(methodOn(UsersController.class).findAll(Pageable.unpaged())).withRel("users")
        );
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/api/users/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
