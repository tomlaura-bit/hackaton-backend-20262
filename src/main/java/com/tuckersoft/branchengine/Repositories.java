package com.tuckersoft.branchengine;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import java.util.*;

interface UserRepository extends JpaRepository<User,Long> { Optional<User> findByEmail(String email); boolean existsByEmail(String email); }
interface StoryNodeRepository extends JpaRepository<StoryNode,Long> { Optional<StoryNode> findByNodeCode(String code); boolean existsByNodeCode(String code); }
interface PlaythroughRepository extends JpaRepository<Playthrough,Long> { boolean existsByPlayerTag(String tag); List<Playthrough> findByUserEmailOrderByCreatedAtDesc(String email); }
interface DecisionRepository extends JpaRepository<Decision,Long>, JpaSpecificationExecutor<Decision> { List<Decision> findByPlaythroughIdAndResolvedNodeCodeIsNotNullOrderByCreatedAtAsc(Long id); }
interface RealityLogRepository extends JpaRepository<RealityLog,Long> { List<RealityLog> findByDecisionIdOrderByCreatedAtAsc(Long id); }
