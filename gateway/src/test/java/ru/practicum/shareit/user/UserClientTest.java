package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(UserClient.class)
public class UserClientTest {
    @Autowired
    private UserClient userClient;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private ObjectMapper objectMapper;
    private static UserDto userOne;
    private static UserDto userTwo;

    @BeforeEach
    void beforeEach() {
        userOne = new UserDto();
        userOne.setId(1L);
        userOne.setName("userName");
        userOne.setEmail("userEmail@mail.ru");
        userTwo = new UserDto();
        userTwo.setId(2L);
        userTwo.setName("userdtotwoname");
        userTwo.setEmail("userdtotwoemail@mail.ru");
    }

    @Test
    public void createUser() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(userTwo);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/users"))
                .andExpect(content().json(request))
                .andRespond(withSuccess());
        ResponseEntity<Object> addItemResponse = this.userClient.createUser(userTwo);
        Assertions.assertNotNull(addItemResponse);
        Assertions.assertEquals(HttpStatus.OK, addItemResponse.getStatusCode());
    }

    @Test
    public void getUser() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(userOne);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> itemResponse = this.userClient.getUser(1L);
        Assertions.assertNotNull(itemResponse);
        Assertions.assertEquals(HttpStatus.OK, itemResponse.getStatusCode());
    }

    @Test
    public void getAllUsers() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(userTwo);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/users"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> userItems = this.userClient.getUsers();
        Assertions.assertNotNull(userItems);
        Assertions.assertEquals(HttpStatus.OK, userItems.getStatusCode());
    }

    @Test
    public void updateUser() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(userTwo);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(content().json(request))
                .andRespond(withSuccess());
        ResponseEntity<Object> updateResponse = this.userClient.updateUser(1L, userTwo);
        Assertions.assertNotNull(updateResponse);
        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

    @Test
    public void deleteUser() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess());
        this.userClient.deleteUser(1L);
    }
}