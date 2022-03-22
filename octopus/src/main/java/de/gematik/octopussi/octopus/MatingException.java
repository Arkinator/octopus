package de.gematik.octopussi.octopus;

import static java.text.MessageFormat.format;

public class MatingException extends RuntimeException {

  public MatingException(Octopus first, Octopus second) {
    super(format("It is not allowed to mate the Octopussis {0} and {1}", first, second));
  }
}
