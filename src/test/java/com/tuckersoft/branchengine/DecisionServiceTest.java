package com.tuckersoft.branchengine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DecisionServiceTest {
    private DecisionRepository decisions;
    private PlaythroughRepository plays;
    private StoryNodeRepository nodes;
    private UserRepository users;
    private ApplicationEventPublisher publisher;
    private DecisionService service;
    private Playthrough playthrough;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        decisions = mock(DecisionRepository.class);
        plays = mock(PlaythroughRepository.class);
        nodes = mock(StoryNodeRepository.class);
        users = mock(UserRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        service = new DecisionService(decisions, plays, nodes, users, publisher);

        User owner = new User();
        owner.id = 1L;
        owner.email = "stefan@tuckersoft.test";
        owner.role = "ROLE_USER";

        StoryNode node = new StoryNode();
        node.nodeCode = "NODE-LOOP";
        node.primaryBranchCode = "NODE-LOOP";
        node.glitchBranchCode = "NODE-LOOP";

        playthrough = new Playthrough();
        playthrough.id = 10L;
        playthrough.user = owner;
        playthrough.currentNode = node;
        playthrough.lucidity = 100;
        playthrough.controlLevel = 0;
        playthrough.status = "ACTIVA";

        authentication = new UsernamePasswordAuthenticationToken(owner.email, null);
        when(plays.findById(10L)).thenReturn(Optional.of(playthrough));
        when(users.findByEmail(owner.email)).thenReturn(Optional.of(owner));
        when(nodes.findByNodeCode("NODE-LOOP")).thenReturn(Optional.of(node));
        when(decisions.save(any(Decision.class))).thenAnswer(invocation -> {
            Decision decision = invocation.getArgument(0);
            if (decision.id == null) decision.id = 99L;
            return decision;
        });
    }

    @Test
    void camaraTienePrecedenciaSobreDestruye() {
        assertEquals("RUPTURA_CUARTA_PARED",
                DecisionService.classify("Stefan destruye la camara"));
    }

    @Test
    void entradaCorruptaNoModificaLaPartida() {
        Decision result = decide("%%% 0101 ###", "CRITICO");

        assertEquals("ENTRADA_CORRUPTA", result.branchType);
        assertEquals("ERROR", result.status);
        assertEquals(100, playthrough.lucidity);
        assertEquals(0, playthrough.controlLevel);
        assertEquals("ACTIVA", playthrough.status);
        verify(plays, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void impactoCriticoActualizaStatsSinSalirDeLosLimites() {
        decide("Stefan acepta continuar con el guion", "CRITICO");

        assertEquals(60, playthrough.lucidity);
        assertEquals(45, playthrough.controlLevel);
    }

    @Test
    void finalPorControlGanaCuandoLucidezTambienLlegaACero() {
        playthrough.lucidity = 40;
        playthrough.controlLevel = 55;

        Decision result = decide("Stefan acepta continuar con el guion", "CRITICO");

        assertEquals(0, playthrough.lucidity);
        assertEquals(100, playthrough.controlLevel);
        assertEquals("FINALIZADA", playthrough.status);
        assertEquals("ENDING_PAC_SYMBOL", playthrough.endingCode);
        assertEquals("REGISTRADA", result.status);
    }

    @Test
    void decisionNormalPublicaExactamenteUnEvento() {
        decide("Stefan acepta continuar con el guion", "LEVE");

        verify(publisher, times(1)).publishEvent(any(DecisionCommittedEvent.class));
    }

    private Decision decide(String input, String impact) {
        return service.create(new DecisionRequest(10L, input, impact), authentication, null);
    }
}
