package de.gematik.octopussi.octopus;

import static org.junit.jupiter.api.Assertions.*;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OctopusTest {

  @Test
  public void shouldHaveMinMaleFitness() {
    val g = new Octopus("Praealtus", 0L);
    assertEquals(0.0, g.getFitness());
    Assertions.assertEquals(Gender.MALE, g.getGender());
  }

  @Test
  public void shouldHaveMinFemaleFitness() {
    val g = new Octopus("Eledone", Long.MIN_VALUE);
    assertEquals(0.0, g.getFitness());
    assertEquals(Gender.FEMALE, g.getGender());
  }

  @Test
  public void shouldHaveMaxMaleFitness() {
    val g = new Octopus("Praealtus", Long.MAX_VALUE);
    assertEquals(100.0, g.getFitness());
    assertEquals(Gender.MALE, g.getGender());
  }

  @Test
  public void shouldHaveMaxFemaleFitness() {
    //        val gen = Long.MIN_VALUE | Long.MAX_VALUE; => -1
    val g = new Octopus("Eledone", -1L);
    assertEquals(100.0, g.getFitness());
    assertEquals(Gender.FEMALE, g.getGender());
  }
}
