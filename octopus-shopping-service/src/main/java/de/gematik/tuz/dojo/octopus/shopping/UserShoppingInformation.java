package de.gematik.tuz.dojo.octopus.shopping;

import de.gematik.octopussi.octopus.Octopus;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShoppingInformation {

    @Builder.Default
    private List<Octopus> octopusList = new ArrayList<>();
    @Builder.Default
    private double money = 0.;

    public void addOctopus(Octopus octopus) {
        octopusList.add(octopus);
    }

    public void removeOctopus(Octopus octopus) {
        octopusList.remove(octopus);
    }
}
