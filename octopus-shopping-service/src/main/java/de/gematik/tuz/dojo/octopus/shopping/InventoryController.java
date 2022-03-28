package de.gematik.tuz.dojo.octopus.shopping;

import de.gematik.octopussi.octopus.Octopus;
import de.gematik.octopussi.octopus.OctopusFactory;
import de.gematik.octopussi.user.UserInformation;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("inventory")
public class InventoryController {

    private final OctopusFactory octopusFactory;
    private final AuthorizationService authorizationService;
    private final Map<Long, List<Octopus>> octopusMap;

    @GetMapping()
    public Object status(@RequestHeader("Authorization") String authorizationHeader) {
        authorizationService.verifyAuthorized(authorizationHeader);
        return octopusMap;
    }

    @PutMapping("generate")
    public List<Octopus> generateForNewUser(UserInformation userInformation) {
        octopusMap.put(userInformation.getId(), List.of(
            octopusFactory.createRandomOktopus(),
            octopusFactory.createRandomOktopus()));

        return octopusMap.get(userInformation.getId());
    }

    @GetMapping("status")
    public String status() {
        return "OK";
    }
}
