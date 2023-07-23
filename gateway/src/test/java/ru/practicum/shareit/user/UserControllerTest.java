package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controllers.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserClient userClient;
    private UserDto userDto;
    private UserDto userDtoTwo;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        userDto = new UserDto(1L, "userdtoemail@mail.ru", "userdtoname");
        userDtoTwo = new UserDto(2L, "userdtotwoemail@mail.ru", "userdtotwoname");
    }

    @Test
    void givenUserDto_whenCreateUser_thenExpectSameEmailAndNameAndOkStatus() throws Exception {
        Mockito.when(userClient.createUser(Mockito.any()))
                .thenReturn(ResponseEntity.ok(userDtoTwo));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoTwo))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is(userDtoTwo.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDtoTwo.getName()), String.class));
    }

    @Test
    void givenUserDto_whenGetUser_thenExpectSameEmailAndNameAndOkStatus() throws Exception {
        Mockito.when(userClient.getUser(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(userDto));
        mockMvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    void givenUserDto_whenGetAllUsers_thenExpectSameEmailAndNameAndOkStatus() throws Exception {
        Mockito.when(userClient.getUsers())
                .thenReturn(ResponseEntity.ok(List.of(userDto)));
        mockMvc.perform(get("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName()), String.class));
    }

    @Test
    void givenUserDto_whenUpdateUser_thenExpectSameEmailAndNameAndOkStatus() throws Exception {
        Mockito.when(userClient.updateUser(Mockito.anyLong(), Mockito.any()))
                        .thenReturn(ResponseEntity.ok(userDto));
        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    void givenUserDto_whenDeleteUser_thenExpectOkStatusAndVerificationOfDeletion() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1")
                        .accept(objectMapper.writeValueAsString("1"))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userClient, Mockito.times(1)).deleteUser(Mockito.anyLong());
    }
}
