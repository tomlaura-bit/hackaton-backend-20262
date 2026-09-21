package com.tuckersoft.branchengine;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.data.domain.Page; import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.*;

@RestController @RequestMapping("/api/v1/decisions") @RequiredArgsConstructor
class DecisionController {private final DecisionService service;private final RealityLogRepository logs;
 @PostMapping ResponseEntity<?> create(@Valid @RequestBody DecisionRequest r,@RequestHeader(value="X-Bandersnatch-Simulate",required=false)String simulate,Authentication a){return ResponseEntity.status(HttpStatus.CREATED).body(Views.decision(service.create(r,a,simulate)));}
 @GetMapping("/{id}") Object one(@PathVariable Long id,Authentication a){return Views.decision(service.get(id,a));}
 @GetMapping("/{id}/reality-logs") Object logs(@PathVariable Long id,Authentication a){service.get(id,a);return logs.findByDecisionIdOrderByCreatedAtAsc(id).stream().map(Views::log).toList();}
 @GetMapping Object all(@RequestParam(required=false)String branchType,@RequestParam(required=false)String impactLevel,@RequestParam(required=false)String status,@RequestParam(required=false)Long playthroughId,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="10")int size,Authentication a){Page<Decision>x=service.list(branchType,impactLevel,status,playthroughId,page,size,a);return Map.of("content",x.getContent().stream().map(Views::decision).toList(),"totalElements",x.getTotalElements(),"totalPages",x.getTotalPages(),"currentPage",Math.max(0,page),"size",x.getSize());}
}
