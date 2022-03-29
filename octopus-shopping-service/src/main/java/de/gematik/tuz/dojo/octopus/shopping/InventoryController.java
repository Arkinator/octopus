package de.gematik.tuz.dojo.octopus.shopping;

import de.gematik.octopussi.octopus.Octopus;
import de.gematik.octopussi.octopus.OctopusFactory;
import de.gematik.octopussi.user.UserInformation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("inventory")
@Slf4j
public class InventoryController {

    private final OctopusFactory octopusFactory;
    private final AuthorizationService authorizationService;
    private final Map<Long, List<Octopus>> octopusMap = new HashMap<>();

    @GetMapping()
    public List<Octopus> status(@RequestHeader("Authorization") String authorizationHeader) {
        final long userId = authorizationService.verifyAuthorizedAndReturnUserId(authorizationHeader);
        log.info("Retrieving for user with id {}", userId);
        log.info("Returning {}", octopusMap);
        log.info("Returning {}", octopusMap.get(userId));
        return octopusMap.get(userId);
    }

    @PutMapping("generate")
    public List<Octopus> generateForNewUser(@RequestParam Long id) {
        log.info("Generating for user with id {}", id);
        octopusMap.put(id, List.of(
            octopusFactory.createRandomOktopus(),
            octopusFactory.createRandomOktopus()));
        log.info("Returning {}", octopusMap);
        log.info("Returning {}", octopusMap.get(id));
        return octopusMap.get(id);
    }

    @GetMapping("status")
    public String status() {
        return "OK";
    }
}
