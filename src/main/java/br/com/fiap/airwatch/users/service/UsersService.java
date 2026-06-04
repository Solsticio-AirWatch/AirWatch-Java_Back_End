package br.com.fiap.airwatch.users.service;

import br.com.fiap.airwatch.city.service.CityService;
import br.com.fiap.airwatch.config.security.JwtService;
import br.com.fiap.airwatch.exception.*;
import br.com.fiap.airwatch.users.dto.*;
import br.com.fiap.airwatch.users.model.Users;
import br.com.fiap.airwatch.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsersService implements UserDetailsService {

    private final UsersRepository repo;
    private final CityService cityService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    // Mantemos este método para o controller chamar após validar a senha
    public AuthResponse buildAuthResponse(String email) {
        var user = repo.findByEmail(email).orElseThrow();
        user.setLastLoginAt(LocalDateTime.now());
        repo.save(user);
        return new AuthResponse(jwtService.generateToken(user), user.getEmail(), user.getRole());
    }

    public Page<UserResponse> findAll(Pageable p) {
        return repo.findAll(p).map(UserResponse::from);
    }

    public UserResponse findById(Long id) {
        return UserResponse.from(findOrThrow(id));
    }

    public UserResponse create(UserRequest req) {
        if (repo.existsByEmail(req.email()))
            throw new BusinessException("E-mail already registered: " + req.email());
        var city = req.cityId() != null ? cityService.findOrThrow(req.cityId()) : null;
        var user = Users.builder()
                .city(city)
                .name(req.name())
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .role(req.role() != null && !req.role().isBlank() ? req.role() : "CITIZEN")
                .phone(req.phone())
                .build();
        return UserResponse.from(repo.save(user));
    }

    public UserResponse update(Long id, UserRequest req) {
        var u = findOrThrow(id);
        var city = req.cityId() != null ? cityService.findOrThrow(req.cityId()) : null;
        u.setCity(city);
        u.setName(req.name());
        u.setPhone(req.phone());
        if (req.role() != null && !req.role().isBlank()) u.setRole(req.role());
        return UserResponse.from(repo.save(u));
    }

    public void delete(Long id) {
        findOrThrow(id);
        repo.deleteById(id);
    }

    public Users findOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}