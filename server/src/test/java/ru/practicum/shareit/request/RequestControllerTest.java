package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import ru.practicum.shareit.request.controllers.RequestController;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.models.Request;
import ru.practicum.shareit.request.service.classes.RequestServiceImpl;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {
    @InjectMocks
    private RequestController requestController;
    @Mock
    private RequestServiceImpl requestService;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Request request;
    private ItemRequestInputDto itemRequestInputDto;
    private RequestDtoWithItems requestDtoWithItems;
    private final String CUSTOM_USER_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
        User user = new User();
        user.setName("userName");
        user.setEmail("userEmail@mail.ru");
        user.setId(1L);
        itemRequestInputDto = new ItemRequestInputDto("itemRequestInputDtoDescription",
                LocalDateTime.now());
        request = new Request();
        request.setRequester(user);
        request.setDescription("requestDescription");
        request.setCreated(LocalDateTime.now());
        requestDtoWithItems = RequestDtoWithItems.builder().build();
        requestDtoWithItems.setDescription("requestDtoWithItemsDescription");
    }

    @Test
    void givenRequest_whenCreateRequest_thenExpectSameIdAndDescription() throws Exception {
        Mockito.when(requestService.createRequest(Mockito.any())).thenReturn(request);
        mockMvc.perform(post("/requests")
                        .header(CUSTOM_USER_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemRequestInputDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description",
                        is(request.getDescription()), String.class));
    }

    @Test
    void givenRequest_whenGetRequests_thenExpectSameIdAndDescription() throws Exception {
        Mockito.when(requestService.getRequests(Mockito.any())).thenReturn(List.of(requestDtoWithItems));
        mockMvc.perform(get("/requests")
                        .header(CUSTOM_USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id",
                        is(requestDtoWithItems.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description",
                        is(requestDtoWithItems.getDescription()), String.class));
    }

    @Test
    void givenRequest_whenGetRequestsInPages_thenExpectSameIdAndDescription() throws Exception {
        Mockito.when(requestService.getRequestsInPages(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(requestDtoWithItems));
        mockMvc.perform(get("/requests/all")
                        .header(CUSTOM_USER_HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id",
                        is(requestDtoWithItems.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description",
                        is(requestDtoWithItems.getDescription()), String.class));
    }

    @Test
    void givenRequest_whenGetRequestsInPagesAndPageIsLessThanZero_thenExpectSameBadRequestExceptionMessage() {
        Assertions.assertThrows(AssertionError.class,
                () -> mockMvc.perform(get("/requests/all?from=-5&size=5")
                                .header(CUSTOM_USER_HEADER, 2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.detailMessage",
                                is("Индекс первого элемента не может быть меньше нуля"))));
    }

    @Test
    void givenRequest_whenGetRequestsInPagesAndPageIsLessThanZeroOrEqualsZero_thenExpectSameBadRequestExceptionMessage() {
        Assertions.assertThrows(AssertionError.class,
                () -> mockMvc.perform(get("/requests/all?from=0&size=-5")
                                .header(CUSTOM_USER_HEADER, 2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.detailMessage",
                                is("Количество элементов для отображения" +
                                " не может быть меньше или равно нулю"))));
    }

    @Test
    void givenRequest_whenGetRequest_thenExpectSameIdAndDescription() throws Exception {
        Mockito.when(requestService.getRequest(Mockito.any(), Mockito.anyLong()))
                .thenReturn(requestDtoWithItems);
        mockMvc.perform(get("/requests/1")
                        .header(CUSTOM_USER_HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id",
                        is(requestDtoWithItems.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description",
                        is(requestDtoWithItems.getDescription()), String.class));
    }
}
