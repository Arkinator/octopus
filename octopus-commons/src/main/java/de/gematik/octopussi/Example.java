package de.gematik.octopussi;

import static java.text.MessageFormat.format;

import de.gematik.octopussi.octopus.OctopusFactory;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.val;

public class Example {

  public static void main(String[] args) throws IOException {
    val factory = new OctopusFactory();

    for (var i = 0; i < 20; i++) {
      val g1 = factory.createRandomOktopus();
      val g2 = factory.createRandomOktopus();
      if (factory.allowedToMate(g1, g2)) {
        val offspring = factory.mate(g1, g2);

        System.out.println("First:     " + g1);
        System.out.println("Second:    " + g2);
        System.out.println("Offspring: " + offspring);

        val img = factory.createAvatar(offspring);
        File outputfile = new File(format("offspring_{0}.png", i));
        System.out.println(outputfile.getAbsolutePath());
        ImageIO.write(img, "png", outputfile);

        System.out.println("----------------------------");
      } else {
        System.out.println(format("{0} and {1} are not allowed to mate!", g1, g2));
      }
    }
  }
}
