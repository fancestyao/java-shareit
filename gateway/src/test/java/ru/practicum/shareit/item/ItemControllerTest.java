package ru.practicum.shareit.item;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.controllers.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;
    @Mock
    private ItemClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private ItemDtoWithBooking itemDtoWithBooking;
    private ItemDto itemDto;

    private static final String CUSTOM_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(itemController).build();
        itemDtoWithBooking = ItemDtoWithBooking
                .builder()
                .id(1L)
                .name("itemDtoWithBookingName")
                .description("itemDtoWithBookingDescription")
                .available(Boolean.TRUE)
                .build();
        itemDto = ItemDto
                .builder()
                .name("itemDtoName")
                .description("itemDtoDescription")
                .available(Boolean.TRUE)
                .build();
    }

    @Test
    void getUserItemsTest() throws Exception {
        List<Object> list = List.of(itemDtoWithBooking);
        Mockito.when(client.getUserItems(anyLong()))
                .thenReturn(ResponseEntity.ok(list));
        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header(CUSTOM_USER_ID_HEADER, 2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name")
                        .value("itemDtoWithBookingName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description")
                        .value("itemDtoWithBookingDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(true));
    }

    @Test
    void getItemTest() throws Exception {
        Mockito.when(client.getItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(itemDtoWithBooking));
        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header(CUSTOM_USER_ID_HEADER, 2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("itemDtoWithBookingName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("itemDtoWithBookingDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @Test
    void addTest() throws Exception {
        Mockito.when(client.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(itemDto));
        mockMvc.perform(post("/items", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(CUSTOM_USER_ID_HEADER, 2)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("itemDtoName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("itemDtoDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @Test
    void update() throws Exception {
        Mockito.when(client.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(ResponseEntity.ok(itemDto));
        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", 1L)
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value("itemDtoName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("itemDtoDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @Test
    void searchItem() throws Exception {
        String searchText = "test";
        List<ItemDto> itemList = Arrays.asList(
                new ItemDto(1L, "Item 1", "Description 1", true, null),
                new ItemDto(2L, "Item 2", "Description 2", true, null)
        );
        Mockito.when(client.searchItem(any(String.class), anyLong())).thenReturn(ResponseEntity.ok(itemList));
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .param("text", searchText))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Item 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is("Description 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is("Item 2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description", Matchers.is("Description 2")));
        Mockito.verify(client, Mockito.times(1)).searchItem(any(String.class), anyLong());
    }

    @Test
    void createComment() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        String commentText = "commentText";
        CommentDto commentDto = new CommentDto(1L, commentText, "authorName");
        CommentDtoInput inputCommentDto = new CommentDtoInput();
        inputCommentDto.setText(commentText);
        Mockito.when(client.createComment(userId, itemId, inputCommentDto)).thenReturn(ResponseEntity.ok(commentDto));
        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemId)
                        .header(CUSTOM_USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentText)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName", Matchers.is(commentDto.getAuthorName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Matchers.is(commentText)));
        Mockito.verify(client, Mockito.times(1)).createComment(userId, itemId, inputCommentDto);
    }
}