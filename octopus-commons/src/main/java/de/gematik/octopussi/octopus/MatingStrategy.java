package de.gematik.octopussi.octopus;

import java.util.Random;
import java.util.function.BiFunction;
import lombok.Getter;
import lombok.val;

public interface MatingStrategy extends BiFunction<Long, Long, Long> {

  @Override
  default Long apply(Long first, Long second) {
    return mate(first, second);
  }

  Long mate(Long first, Long second);

  enum Defaults {
    NAND((first, second) -> ~(first & second)),
    XOR((first, second) -> first ^ second);

    @Getter private MatingStrategy strategy;

    Defaults(MatingStrategy strategy) {
      this.strategy = strategy;
    }
  }

  static MatingStrategy randomStrategy(Random rnd) {
    val defaults = Defaults.values();
    return defaults[rnd.nextInt(defaults.length)].getStrategy();
  }
}
