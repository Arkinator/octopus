package de.gematik.tuz.dojo.octopus.identity;

import de.gematik.octopussi.user.UserInformation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class UserRepository {

  private final Map<String, UserInformation> userMap = new HashMap<>();
  private final AtomicLong counter = new AtomicLong(0);

  public Optional<UserInformation> findUserByName(String username) {
    return Optional.ofNullable(userMap.get(username));
  }

  public UserInformation addUser(
      UserInformation userInformation) {
    final UserInformation newUser =
        userInformation.toBuilder().id(counter.getAndIncrement()).build();

    userMap.put(userInformation.getUsername(), newUser);

    return newUser;
  }

  public void deleteUser(String username) {
    userMap.remove(username);
  }
}
