package de.gematik.tuz.dojo.octopus.shopping;

import de.gematik.octopussi.octopus.Octopus;
import de.gematik.octopussi.octopus.OctopusFactory;
import de.gematik.octopussi.user.UserInformation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("inventory")
@Slf4j
public class InventoryController {

    private static final double STARTING_MONEY = 1000.;
    private final OctopusFactory octopusFactory;
    private final AuthorizationService authorizationService;
    private final Map<Long, UserShoppingInformation> userMap = new HashMap<>();

    @GetMapping()
    public List<Octopus> status(@RequestHeader("Authorization") String authorizationHeader) {
        final long userId = authorizationService.verifyAuthorizedAndReturnUserId(authorizationHeader);
        log.info("Retrieving for user with id {}", userId);
        return userMap.get(userId).getOctopusList();
    }

    @PostMapping("trade")
    public void doTrade(@RequestHeader("Authorization") String authorizationHeader,
        @RequestParam("octopusName") String octopusName,
        @RequestParam("otherUserId") Long otherUserId,
        @RequestParam("moneyToGive") Double moneyToGive) {
        log.info("id={}, name={}, money={}", otherUserId, octopusName, moneyToGive);
        final long userId = authorizationService.verifyAuthorizedAndReturnUserId(authorizationHeader);

        UserShoppingInformation otherUser = userMap.get(otherUserId);
        log.info("other user: {}", otherUser);
        UserShoppingInformation thisUser = userMap.get(userId);
        otherUser.setMoney(otherUser.getMoney() + moneyToGive);
        thisUser.setMoney(thisUser.getMoney() - moneyToGive);

        Octopus octopus = thisUser.getOctopusList().stream()
            .filter(o -> o.getName().equals(octopusName))
            .findAny().orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        otherUser.addOctopus(octopus);
        thisUser.removeOctopus(octopus);
    }

    @PostMapping("generate")
    public List<Octopus> generateForNewUser(@RequestParam Long id) {
        log.info("Generating for user with id {}", id);
        userMap.put(id, UserShoppingInformation.builder()
            .octopusList(new ArrayList<>(List.of(
                octopusFactory.createRandomOktopus(),
                octopusFactory.createRandomOktopus())))
            .money(STARTING_MONEY)
            .build());
        log.info("Returning {}", userMap.get(id));
        return userMap.get(id).getOctopusList();
    }

    @GetMapping("status")
    public String status() {
        return "OK";
    }
}
