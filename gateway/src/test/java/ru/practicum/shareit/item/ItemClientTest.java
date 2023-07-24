package ru.practicum.shareit.item;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;

@RestClientTest(ItemClient.class)
class ItemClientTest {
    @Autowired
    private ItemClient client;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private ObjectMapper objectMapper;
    private static ItemDto itemDtoOne;
    private static ItemDto itemDtoTwo;
    private static ItemDto itemDtoThree;


    @BeforeEach
    void setUp() {
        itemDtoOne = new ItemDto();
        itemDtoOne.setId(1L);
        itemDtoOne.setName("itemDto1Name");
        itemDtoOne.setDescription("itemDto1Description");
        itemDtoOne.setAvailable(true);
        itemDtoOne.setRequestId(1L);
        itemDtoTwo = new ItemDto();
        itemDtoTwo.setId(2L);
        itemDtoTwo.setName("itemDto2Name");
        itemDtoTwo.setDescription("itemDto2Description");
        itemDtoTwo.setAvailable(true);
        itemDtoTwo.setRequestId(2L);
        itemDtoThree = new ItemDto();
        itemDtoThree.setId(3L);
        itemDtoThree.setName("itemDto3Name");
        itemDtoThree.setDescription("itemDto3Description");
        itemDtoThree.setAvailable(true);
        itemDtoThree.setRequestId(3L);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUserItems() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(itemDtoOne);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> userItems = this.client.getUserItems(1L);
        Assertions.assertNotNull(userItems);
        Assertions.assertEquals(HttpStatus.OK, userItems.getStatusCode());
    }

    @Test
    void getItem() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(itemDtoOne);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items/1"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> itemResponse = this.client.getItem(1L, 1L);
        Assertions.assertNotNull(itemResponse);
        Assertions.assertEquals(HttpStatus.OK, itemResponse.getStatusCode());
    }

    @Test
    void addItem() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(itemDtoOne);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items"))
                .andExpect(content().json(request))
                .andRespond(withSuccess());
        ResponseEntity<Object> addItemResponse = this.client.createItem(1L, itemDtoOne);
        Assertions.assertNotNull(addItemResponse);
        Assertions.assertEquals(HttpStatus.OK, addItemResponse.getStatusCode());
    }

    @Test
    void update() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(itemDtoOne);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items/1"))
                .andExpect(content().json(request))
                .andRespond(withSuccess());
        ResponseEntity<Object> updateResponse = this.client.updateItem(1L, itemDtoOne, 1L);
        Assertions.assertNotNull(updateResponse);
        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

    @Test
    void searchItem() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(Arrays.asList(itemDtoOne, itemDtoTwo, itemDtoThree));
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items/search?text=test"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> searchResponse = this.client.searchItem("test", 1L);
        Assertions.assertNotNull(searchResponse);
        Assertions.assertEquals(HttpStatus.OK, searchResponse.getStatusCode());
    }

    @Test
    void createComment() throws JsonProcessingException {
        CommentDtoInput inputCommentDto = new CommentDtoInput("commentText");
        String request = objectMapper.writeValueAsString(inputCommentDto);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/items/1/comment"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andExpect(content().json(request))
                .andRespond(withSuccess());
        ResponseEntity<Object> createCommentResponse = this.client.createComment(1L, 1L,
                new CommentDtoInput("commentText"));
        Assertions.assertNotNull(createCommentResponse);
        Assertions.assertEquals(HttpStatus.OK, createCommentResponse.getStatusCode());
    }
}