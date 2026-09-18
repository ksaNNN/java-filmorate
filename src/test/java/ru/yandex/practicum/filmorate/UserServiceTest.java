package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
    }

    private User createUser(String login) {
        User user = new User();
        user.setEmail(login + "@mail.ru");
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return userService.create(user);
    }

    @Test
    void addFriend_shouldMakeFriendshipMutual() {
        User user1 = createUser("user1");
        User user2 = createUser("user2");

        userService.addFriend(user1.getId(), user2.getId());

        assertTrue(user1.getFriends().contains(user2.getId()));
        assertTrue(user2.getFriends().contains(user1.getId()));
    }

    @Test
    void addFriend_withSameUser_shouldThrowValidationException() {
        User user1 = createUser("user1");
        assertThrows(ValidationException.class, () -> userService.addFriend(user1.getId(), user1.getId()));
    }

    @Test
    void addFriend_withNonExistentUser_shouldThrowNotFound() {
        User user1 = createUser("user1");
        assertThrows(NotFoundException.class, () -> userService.addFriend(user1.getId(), 999));
    }

    @Test
    void removeFriend_shouldRemoveMutualFriendship() {
        User user1 = createUser("user1");
        User user2 = createUser("user2");
        userService.addFriend(user1.getId(), user2.getId());

        userService.removeFriend(user1.getId(), user2.getId());

        assertFalse(user1.getFriends().contains(user2.getId()));
        assertFalse(user2.getFriends().contains(user1.getId()));
    }

    @Test
    void getFriends_shouldReturnListOfFriends() {
        User user1 = createUser("user1");
        User user2 = createUser("user2");
        userService.addFriend(user1.getId(), user2.getId());

        List<User> friends = userService.getFriends(user1.getId());

        assertEquals(1, friends.size());
        assertEquals(user2.getId(), friends.get(0).getId());
    }

    @Test
    void getCommonFriends_shouldReturnOnlySharedFriends() {
        User user1 = createUser("user1");
        User user2 = createUser("user2");
        User commonFriend = createUser("commonFriend");

        userService.addFriend(user1.getId(), commonFriend.getId());
        userService.addFriend(user2.getId(), commonFriend.getId());

        List<User> common = userService.getCommonFriends(user1.getId(), user2.getId());

        assertEquals(1, common.size());
        assertEquals(commonFriend.getId(), common.get(0).getId());
    }

    @Test
    void getCommonFriends_withSameUser_shouldThrowValidationException() {
        User user1 = createUser("user1");
        assertThrows(ValidationException.class,
                () -> userService.getCommonFriends(user1.getId(), user1.getId()));
    }

    @Test
    void getCommonFriends_withNoSharedFriends_shouldReturnEmptyList() {
        User user1 = createUser("user1");
        User user2 = createUser("user2");
        User friendOfUser1 = createUser("friendOfUser1");
        userService.addFriend(user1.getId(), friendOfUser1.getId());

        List<User> common = userService.getCommonFriends(user1.getId(), user2.getId());

        assertTrue(common.isEmpty());
    }
}