package com.tuckersoft.branchengine;
import org.junit.jupiter.api.Test; import static org.junit.jupiter.api.Assertions.*;
class DecisionRulesTest {
 @Test void rebeldiaTienePrecedencia(){assertEquals("REBELDIA",DecisionService.classify("Stefan destruye la camara"));}
 @Test void entradaCorrupta(){assertEquals("ENTRADA_CORRUPTA",DecisionService.classify("1234 !!!"));}
 @Test void normalizaTildes(){assertEquals("RUPTURA_CUARTA_PARED",DecisionService.classify("Mira la CÁMARA fijamente"));}
 @Test void sospecha(){assertEquals("SOSPECHA",DecisionService.classify("Sospecha que lo vigilan"));}
 @Test void obedienciaPorDefecto(){assertEquals("OBEDIENCIA",DecisionService.classify("Stefan acepta la oferta"));}
}
