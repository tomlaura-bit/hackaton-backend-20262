package com.tuckersoft.branchengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.*; import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.*;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.nio.charset.StandardCharsets; import java.time.Instant; import java.util.*;

@Configuration @RequiredArgsConstructor
class SecurityConfig {
  private final JwtFilter filter; private final JsonSecurityError errors;
  @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
  @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c)throws Exception{return c.getAuthenticationManager();}
  @Bean SecurityFilterChain chain(HttpSecurity h)throws Exception{return h.csrf(x->x.disable()).sessionManagement(x->x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(x->x.requestMatchers("/api/v1/auth/**").permitAll().requestMatchers(HttpMethod.POST,"/api/v1/nodes").hasRole("ADMIN").requestMatchers("/api/v1/users","/api/v1/users/*/role").hasRole("ADMIN").anyRequest().authenticated())
    .exceptionHandling(x->x.authenticationEntryPoint(errors).accessDeniedHandler(errors)).addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class).build();}
}

@Service @RequiredArgsConstructor
class DbUserDetails implements UserDetailsService { private final UserRepository users; public UserDetails loadUserByUsername(String e){User u=users.findByEmail(e).orElseThrow(()->new UsernameNotFoundException(e));return org.springframework.security.core.userdetails.User.withUsername(e).password(u.password).authorities(u.role).build();}}

@Service
class JwtService { @Value("${jwt.secret}") String secret; @Value("${jwt.expiration-ms}") long expiration;
  String create(User u){return Jwts.builder().subject(u.email).claim("role",u.role).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+expiration)).signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).compact();}
  Claims parse(String t){return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).build().parseSignedClaims(t).getPayload();}
}

@Component @RequiredArgsConstructor
class JwtFilter extends OncePerRequestFilter { private final JwtService jwt; private final DbUserDetails userDetails;
 protected void doFilterInternal(HttpServletRequest q,HttpServletResponse s,FilterChain c)throws ServletException,IOException{String h=q.getHeader("Authorization");if(h!=null&&h.startsWith("Bearer "))try{Claims p=jwt.parse(h.substring(7));UserDetails u=userDetails.loadUserByUsername(p.getSubject());var a=new UsernamePasswordAuthenticationToken(u,null,u.getAuthorities());org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(a);}catch(Exception ignored){}c.doFilter(q,s);}
}

@Component
class JsonSecurityError implements org.springframework.security.web.AuthenticationEntryPoint,org.springframework.security.web.access.AccessDeniedHandler { private final ObjectMapper mapper=new ObjectMapper().findAndRegisterModules();
 public void commence(HttpServletRequest q,HttpServletResponse s,AuthenticationException e)throws IOException{write(q,s,401,"UNAUTHORIZED","Autenticación requerida o token inválido");}
 public void handle(HttpServletRequest q,HttpServletResponse s,org.springframework.security.access.AccessDeniedException e)throws IOException{write(q,s,403,"FORBIDDEN","No tiene permiso para realizar esta operación");}
 void write(HttpServletRequest q,HttpServletResponse s,int code,String err,String msg)throws IOException{s.setStatus(code);s.setContentType(MediaType.APPLICATION_JSON_VALUE);mapper.writeValue(s.getWriter(),Map.of("error",err,"message",msg,"timestamp",Instant.now(),"path",q.getRequestURI()));}
}
