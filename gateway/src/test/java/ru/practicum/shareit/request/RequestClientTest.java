package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.models.Request;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(RequestClient.class)
public class RequestClientTest {
    @Autowired
    private RequestClient requestClient;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private ObjectMapper objectMapper;
    private String request;
    private String result;
    private ItemRequestInputDto itemRequestInputDto;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        user.setId(1L);
        Request Request = new Request();
        Request.setRequester(user);
        Request.setDescription("requestDescription");
        Request.setId(1L);
        Request.setCreated(LocalDateTime.now());
        itemRequestInputDto = new ItemRequestInputDto("itemRequestInputDtoDescription",
                LocalDateTime.now());
        request = objectMapper.writeValueAsString(itemRequestInputDto);
        result = objectMapper.writeValueAsString(Request);
    }

    @Test
    void postItemRequest() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(content().json(request))
                .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = this.requestClient.addRequest(itemRequestInputDto, 1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getItemRequests() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(anything())
                .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = this.requestClient.getRequests(1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getItemRequestsInPages() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/requests/all?from=0&size=1"))
                .andExpect(anything())
                .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = this.requestClient.getRequestInPages(1L, 0L, 1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getItemRequest() {
        this.mockRestServiceServer.expect(requestTo("http://localhost:9090/requests/1"))
                .andExpect(anything())
                .andRespond(withSuccess(result, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = this.requestClient.getRequest(1L, 1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}