package com.tuckersoft.branchengine;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.security.authentication.*; import org.springframework.security.core.Authentication; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.time.Instant; import java.util.*;

@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor
class AuthController { private final UserRepository users;private final PasswordEncoder encoder;private final JwtService jwt;private final AuthenticationManager auth;
 @PostMapping("/register") ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r){if(users.existsByEmail(r.email()))throw ApiProblem.conflict("El email ya está registrado");User u=new User();u.email=r.email();u.password=encoder.encode(r.password());u.displayName=r.displayName();u.role="ROLE_USER";u.createdAt=Instant.now();users.save(u);return ResponseEntity.status(201).body(Views.auth(u,jwt.create(u)));}
 @PostMapping("/login") Object login(@Valid @RequestBody LoginRequest r){auth.authenticate(new UsernamePasswordAuthenticationToken(r.email(),r.password()));User u=users.findByEmail(r.email()).orElseThrow(()->new BadCredentialsException("Credenciales inválidas"));return Views.auth(u,jwt.create(u));}
}

@RestController @RequestMapping("/api/v1/users") @RequiredArgsConstructor
class UserController {private final UserRepository users;
 User current(Authentication a){return users.findByEmail(a.getName()).orElseThrow(()->ApiProblem.notFound("Usuario no encontrado"));}
 @GetMapping("/me") Object me(Authentication a){return Views.user(current(a));}
 @GetMapping Object all(){return users.findAll().stream().map(Views::user).toList();}
 @PatchMapping("/{id}/role") Object role(@PathVariable Long id,@Valid @RequestBody RoleRequest r,Authentication a){if(!Set.of("ROLE_USER","ROLE_ADMIN").contains(r.role()))throw ApiProblem.bad("Rol inválido");User actor=current(a);if(actor.id.equals(id))throw ApiProblem.bad("No puede cambiar su propio rol");User u=users.findById(id).orElseThrow(()->ApiProblem.notFound("Usuario no encontrado"));u.role=r.role();return Views.user(users.save(u));}
}
