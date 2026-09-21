package com.tuckersoft.branchengine;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity @Table(name="users") @Getter @Setter @NoArgsConstructor
class User { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(unique=true,nullable=false) String email; @Column(nullable=false) String password; String displayName; String role; Instant createdAt; @OneToMany(mappedBy="user") List<Playthrough> playthroughs=new ArrayList<>(); }

@Entity @Getter @Setter @NoArgsConstructor
class StoryNode { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(unique=true,nullable=false) String nodeCode; String title; @Column(columnDefinition="TEXT") String sceneText; Integer branchCapacity; Integer currentBranches; String primaryBranchCode; String glitchBranchCode; Instant createdAt; @OneToMany(mappedBy="currentNode") List<Playthrough> playthroughs=new ArrayList<>(); @OneToMany(mappedBy="node") List<Decision> decisions=new ArrayList<>(); }

@Entity @Getter @Setter @NoArgsConstructor
class Playthrough { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(unique=true,nullable=false) String playerTag; @ManyToOne(optional=false) User user; String startNodeCode; @ManyToOne(optional=false) StoryNode currentNode; Integer lucidity; Integer controlLevel; String status; String endingCode; Instant createdAt; Instant updatedAt; @OneToMany(mappedBy="playthrough") List<Decision> decisions=new ArrayList<>(); }

@Entity @Getter @Setter @NoArgsConstructor
class Decision { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @ManyToOne(optional=false) Playthrough playthrough; @ManyToOne(optional=false) StoryNode node; @Column(columnDefinition="TEXT") String rawInput; String branchType; String impactLevel; String handlerUnit; String outcomeCode; String resolvedNodeCode; String status; Instant createdAt; Instant updatedAt; @OneToMany(mappedBy="decision") List<RealityLog> realityLogs=new ArrayList<>(); }

@Entity @Getter @Setter @NoArgsConstructor
class RealityLog { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @ManyToOne(optional=false) Decision decision; String recipientEmail; String subject; String logStatus; @Column(columnDefinition="TEXT") String errorMessage; Instant sentAt; Instant createdAt; }
