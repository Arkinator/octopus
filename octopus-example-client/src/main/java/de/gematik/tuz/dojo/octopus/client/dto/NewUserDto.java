package de.gematik.tuz.dojo.octopus.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NewUserDto {

  private final String name;
  private final long id;
}
