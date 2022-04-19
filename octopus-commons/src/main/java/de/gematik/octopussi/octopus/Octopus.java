package de.gematik.octopussi.octopus;

import static java.text.MessageFormat.format;

import lombok.Data;
import lombok.Getter;
import lombok.val;

@Data
public class Octopus {

  @Getter private final String name;
  private final Long genome;

  protected Octopus(String name, Long genome) {
    this.name = name;
    this.genome = genome;
  }

  /**
   * The raw value is always an absolute value (unsigned). The LSB represents the gender and is
   * masked out by intention. Use the {@link #getGender()} method to retrieve the LSB
   *
   * @return the raw genome without the gender-bit
   */
  public Long getGenome() {
    return genome & Long.MAX_VALUE;
  }

  public String getGenomeString() {
    StringBuilder bin = new StringBuilder(Long.toBinaryString(genome));
    while (bin.length() < 64) {
      bin.insert(0, "0"); // ensure that always 64 bits for positive numbers
    }
    return bin.toString();
  }

  public Gender getGender() {
    return (genome < 0) ? Gender.FEMALE : Gender.MALE;
  }

  public Double getFitness() {
    //        val abs = Math.abs(gen);          // Math.abs does not provide absolute values!?
    val abs = genome & Long.MAX_VALUE; // gender neutral value!
    return abs
        * 100.0
        / Long.MAX_VALUE; // rule of three where Long.MAX_VALUE equals to 100% fitness!
  }

  @Override
  public String toString() {
    return format("{0} ({1})", name, getGender());
  }
}
