package com.tuckersoft.branchengine;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import java.time.Instant; import java.util.*;

class ApiProblem extends RuntimeException { final HttpStatus status; final String error; ApiProblem(HttpStatus s,String e,String m){super(m);status=s;error=e;} static ApiProblem notFound(String m){return new ApiProblem(HttpStatus.NOT_FOUND,"NOT_FOUND",m);} static ApiProblem conflict(String m){return new ApiProblem(HttpStatus.CONFLICT,"CONFLICT",m);} static ApiProblem forbidden(String m){return new ApiProblem(HttpStatus.FORBIDDEN,"FORBIDDEN",m);} static ApiProblem bad(String m){return new ApiProblem(HttpStatus.BAD_REQUEST,"VALIDATION_ERROR",m);} }

@RestControllerAdvice
class ApiExceptionHandler {
 @ExceptionHandler(ApiProblem.class) ResponseEntity<?> problem(ApiProblem e,HttpServletRequest q){return response(e.status,e.error,e.getMessage(),q);}
 @ExceptionHandler({MethodArgumentNotValidException.class,HttpMessageNotReadableException.class,IllegalArgumentException.class}) ResponseEntity<?> bad(Exception e,HttpServletRequest q){String m=e instanceof MethodArgumentNotValidException v?v.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getField()+": "+x.getDefaultMessage()).orElse("Datos inválidos"):"Datos inválidos";return response(HttpStatus.BAD_REQUEST,"VALIDATION_ERROR",m,q);}
 @ExceptionHandler(BadCredentialsException.class) ResponseEntity<?> auth(Exception e,HttpServletRequest q){return response(HttpStatus.UNAUTHORIZED,"INVALID_CREDENTIALS","Credenciales incorrectas",q);}
 ResponseEntity<?> response(HttpStatus s,String e,String m,HttpServletRequest q){return ResponseEntity.status(s).body(Map.of("error",e,"message",m,"timestamp",Instant.now(),"path",q.getRequestURI()));}
}
