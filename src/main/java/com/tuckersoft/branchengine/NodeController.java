package com.tuckersoft.branchengine;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.data.domain.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.time.Instant; import java.util.*;

@RestController @RequestMapping("/api/v1/nodes") @RequiredArgsConstructor
class NodeController {private final StoryNodeRepository nodes;
 @PostMapping ResponseEntity<?> create(@Valid @RequestBody NodeRequest r){if(nodes.existsByNodeCode(r.nodeCode()))throw ApiProblem.conflict("El nodeCode ya existe");StoryNode n=new StoryNode();n.nodeCode=r.nodeCode();n.title=r.title();n.sceneText=r.sceneText();n.branchCapacity=r.branchCapacity();n.currentBranches=0;n.primaryBranchCode=r.primaryBranchCode();n.glitchBranchCode=r.glitchBranchCode();n.createdAt=Instant.now();return ResponseEntity.status(201).body(Views.node(nodes.save(n)));}
 @GetMapping Object all(){return nodes.findAll(Sort.by("createdAt").descending()).stream().map(Views::node).toList();}
 @GetMapping("/{id}") Object one(@PathVariable Long id){return Views.node(nodes.findById(id).orElseThrow(()->ApiProblem.notFound("Nodo no encontrado")));}
}
