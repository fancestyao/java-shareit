package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.services.classes.UserServiceImpl;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceTest {
    private final UserServiceImpl userService;
    private static UserDto userOne;
    private static UserDto userTwo;

    @BeforeEach
    void beforeEach() {
        userOne = new UserDto(1L, "userdtoemail@mail.ru", "userdtoname");
        userTwo = new UserDto(2L, "userdtotwoemail@mail.ru", "userdtotwoname");
    }

    @Test
    public void createUser() {
        UserDto userToSave = userService.createUser(userOne);
        assertThat(userToSave).isEqualTo(userOne);
    }


    @Test
    public void getUser() {
        UserDto userToSave = userService.createUser(userOne);
        UserDto userToGet = userService.getUser(1L);
        assertThat(userToGet).isEqualTo(userToSave);
    }

    @Test
    public void getAllUsers() {
        UserDto userToSaveOne = userService.createUser(userOne);
        UserDto userToSaveTwo = userService.createUser(userTwo);
        List<UserDto> listOfUsers = List.of(userToSaveOne, userToSaveTwo);
        assertThat(listOfUsers).isEqualTo(userService.getAllUsers());
    }

    @Test
    public void updateUser() {
        UserDto userToSave = userService.createUser(userOne);
        userToSave.setEmail("userdtoname");
        UserDto userToUpdate = userService.updateUser(1L, userToSave);
        assertThat(userToUpdate.getName()).isEqualTo(userToSave.getName());
    }

    @Test
    public void updateUserIfWrongId() {
        UserDto userToSave = userService.createUser(userOne);
        userToSave.setEmail("updatedemail@mail.ru");
        assertThatThrownBy(() -> userService
                .updateUser(2L, userToSave))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void updateUserIfNameIsNotAssigned() {
        UserDto userToSave = userService.createUser(userOne);
        userToSave.setName(null);
        UserDto userToUpdate = userService.updateUser(1L, userToSave);
        assertThat(userToUpdate.getEmail()).isEqualTo("userdtoemail@mail.ru");
    }

    @Test
    public void updateUserIfEmailIsNotAssigned() {
        UserDto userToSave = userService.createUser(userOne);
        userToSave.setEmail(null);
        UserDto userToUpdate = userService.updateUser(1L, userToSave);
        assertThat(userToUpdate.getEmail()).isEqualTo("userdtoemail@mail.ru");
    }

    @Test
    public void getUserIfWrongId() {
        userService.createUser(userOne);
        assertThatThrownBy(() -> userService
                .getUser(2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void deleteUser() {
        userService.createUser(userOne);
        userService.removeUser(1L);
        assertThatThrownBy(() -> userService
                .getUser(1L))
                .isInstanceOf(NotFoundException.class);
    }
}