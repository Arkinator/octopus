package de.gematik.tuz.dojo.octopus.shopping;

import de.gematik.octopussi.octopus.OctopusFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShoppingServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ShoppingServiceApplication.class, args);
  }

  @Bean
  public OctopusFactory octopusFactory() {
    return new OctopusFactory();
  }
}
