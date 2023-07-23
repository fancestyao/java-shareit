package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.controllers.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.services.interfaces.ItemService;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;
    @Mock
    private ItemService itemService;
    private ItemDto itemDto;
    private ItemDtoWithBooking itemDtoWithBooking;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String CUSTOM_USER_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        itemDto = ItemDto
                .builder()
                .name("itemDtoName")
                .description("itemDtoDescription")
                .available(Boolean.TRUE)
                .build();
        itemDtoWithBooking = ItemDtoWithBooking
                .builder()
                .id(1L)
                .name("itemDtoWithBookingName")
                .description("itemDtoWithBookingDescription")
                .available(Boolean.TRUE)
                .build();
    }

    @Test
    void givenItemDto_whenCreateItem_thenExpectSameNameAndDescriptionAndAvailabilityAndStatus() throws Exception {
        Mockito.when(itemService.createItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);
        mockMvc.perform(post("/items", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(CUSTOM_USER_HEADER, 2)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("itemDtoName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("itemDtoDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void givenItemDto_whenGetItem_thenExpectSameNameAndDescriptionAndAvailabilityAndStatus() throws Exception {
        Mockito.when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDtoWithBooking);
        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(CUSTOM_USER_HEADER, 2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("itemDtoWithBookingName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("itemDtoWithBookingDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void givenItemDto_whenUpdateItem_thenExpectSameNameAndDescriptionAndAvailabilityAndStatus() throws Exception {
        itemDto.setName("itemDtoNewName");
        Mockito.when(itemService.updateItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);
        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(CUSTOM_USER_HEADER, 1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("itemDtoNewName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("itemDtoDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void givenTextToSearch_whenSearchItem_thenExpectSameSizeOfListAndNameAndDescriptionAndStatus() throws Exception {
        String searchText = "itemDto";
        List<ItemDto> itemList = List.of(itemDto);
        Mockito.when(itemService.searchItem(searchText)).thenReturn(itemList);
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header(CUSTOM_USER_HEADER, 1L)
                        .param("text", searchText))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("itemDtoName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description",
                        Matchers.is("itemDtoDescription")))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(itemService, Mockito.times(1)).searchItem(searchText);
    }

    @Test
    void givenCommentDto_whenCreateComment_thenExpectSameIdAndAuthorNameAndTextAndStatus() throws Exception {
        String commentText = "commentText";
        CommentDto commentDto = new CommentDto(1L, commentText, "authorName");
        CommentDtoInput inputCommentDto = new CommentDtoInput();
        inputCommentDto.setText(commentText);
        Mockito.when(itemService.createComment(1L, 1L, inputCommentDto)).thenReturn(commentDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 1L)
                        .header(CUSTOM_USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentText)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName",
                        Matchers.is(commentDto.getAuthorName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Matchers.is(commentText)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(itemService, Mockito.times(1))
                .createComment(1L, 1L, inputCommentDto);
    }

    @Test
    void getUserItemsTest() throws Exception {
        Mockito.when(itemService.getUserItems(Mockito.anyLong()))
                .thenReturn(Collections
                .singletonList(itemDtoWithBooking));
        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header(CUSTOM_USER_HEADER, 2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name")
                        .value("itemDtoWithBookingName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description")
                        .value("itemDtoWithBookingDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(true));
    }
}
