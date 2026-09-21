package com.tuckersoft.branchengine;
import jakarta.validation.constraints.*; import java.time.Instant; import java.util.*;

record RegisterRequest(@NotBlank @Email String email,@NotBlank @Size(min=6) String password,@NotBlank @Size(min=3,max=60) String displayName,String role){}
record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
record RoleRequest(@NotBlank String role){}
record NodeRequest(@NotBlank @Size(min=3,max=40) String nodeCode,@NotBlank @Size(min=3,max=80) String title,@NotBlank @Size(min=10) String sceneText,@NotNull @Positive Integer branchCapacity,String primaryBranchCode,String glitchBranchCode){}
record PlayRequest(@NotBlank @Size(min=2,max=40) String playerTag,@NotBlank String startNodeCode){}
record DecisionRequest(@NotNull Long playthroughId,@NotBlank @Size(min=10) String rawInput,@NotBlank String impactLevel){}

final class Views { private Views(){}
 static Map<String,Object> auth(User u,String token){Map<String,Object> m=new LinkedHashMap<>();m.put("token",token);m.put("type","Bearer");m.put("email",u.email);m.put("displayName",u.displayName);m.put("role",u.role);return m;}
 static Map<String,Object> user(User u){Map<String,Object> m=new LinkedHashMap<>();m.put("id",u.id);m.put("email",u.email);m.put("displayName",u.displayName);m.put("role",u.role);m.put("createdAt",u.createdAt);return m;}
 static Map<String,Object> node(StoryNode n){Map<String,Object> m=new LinkedHashMap<>();m.put("id",n.id);m.put("nodeCode",n.nodeCode);m.put("title",n.title);m.put("sceneText",n.sceneText);m.put("branchCapacity",n.branchCapacity);m.put("currentBranches",n.currentBranches);m.put("primaryBranchCode",n.primaryBranchCode);m.put("glitchBranchCode",n.glitchBranchCode);m.put("createdAt",n.createdAt);return m;}
 static Map<String,Object> play(Playthrough p){Map<String,Object> m=new LinkedHashMap<>();m.put("id",p.id);m.put("playerTag",p.playerTag);m.put("ownerEmail",p.user.email);m.put("startNodeCode",p.startNodeCode);m.put("currentNodeCode",p.currentNode.nodeCode);m.put("lucidity",p.lucidity);m.put("controlLevel",p.controlLevel);m.put("status",p.status);m.put("endingCode",p.endingCode);m.put("createdAt",p.createdAt);m.put("updatedAt",p.updatedAt);return m;}
 static Map<String,Object> decision(Decision d){Playthrough p=d.playthrough;Map<String,Object> m=new LinkedHashMap<>();m.put("id",d.id);m.put("playthroughId",p.id);m.put("playerTag",p.playerTag);m.put("sourceNodeCode",d.node.nodeCode);m.put("resolvedNodeCode",d.resolvedNodeCode);m.put("rawInput",d.rawInput);m.put("branchType",d.branchType);m.put("impactLevel",d.impactLevel);m.put("handlerUnit",d.handlerUnit);m.put("outcomeCode",d.outcomeCode);m.put("status",d.status);m.put("playthroughStatus",p.status);m.put("lucidity",p.lucidity);m.put("controlLevel",p.controlLevel);m.put("endingCode",p.endingCode);m.put("createdAt",d.createdAt);m.put("updatedAt",d.updatedAt);return m;}
 static Map<String,Object> log(RealityLog l){Map<String,Object> m=new LinkedHashMap<>();m.put("id",l.id);m.put("decisionId",l.decision.id);m.put("recipientEmail",l.recipientEmail);m.put("subject",l.subject);m.put("logStatus",l.logStatus);m.put("errorMessage",l.errorMessage);m.put("sentAt",l.sentAt);m.put("createdAt",l.createdAt);return m;}
}
