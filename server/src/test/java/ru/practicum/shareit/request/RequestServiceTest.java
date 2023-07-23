package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.models.ItemRequestedData;
import ru.practicum.shareit.request.models.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.interfaces.RequestService;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    private final RequestService itemRequestService;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private User userOne;
    private User userTwo;
    private ItemRequestedData itemRequestData;
    private Request itemRequest;


    @BeforeEach
    void setUp() {
        userOne = new User();
        userOne.setName("userOneName");
        userOne.setEmail("userOneEmail@email.ru");
        userOne.setId(1L);
        userTwo = new User();
        userTwo.setName("userTwoName");
        userTwo.setEmail("userTwoEmail@email.ru2");
        itemRequest = new Request();
        itemRequest.setRequester(userOne);
        itemRequest.setDescription("itemRequestDescription");
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestInputDto itemRequestInputDto = new ItemRequestInputDto("itemRequestInputDescription",
                LocalDateTime.now());
        itemRequestData = new ItemRequestedData(itemRequestInputDto, userOne.getId());
    }

    @Test
    void postItemRequestTest() {
        userRepository.save(userOne);
        String itemDescription = itemRequestService.createRequest(itemRequestData).getDescription();
        Assertions.assertEquals("itemRequestInputDescription", itemDescription);
    }

    @Test
    void getItemRequestsTest() {
        userRepository.save(userOne);
        requestRepository.save(itemRequest);
        Assertions.assertEquals("itemRequestDescription",
                itemRequestService.getRequests(1L).get(0).getDescription());
    }

    @Test
    void getItemRequestsInPagesTest() {
        userRepository.save(userOne);
        userRepository.save(userTwo);
        requestRepository.save(itemRequest);
        Assertions.assertEquals("itemRequestDescription",
                itemRequestService.getRequestsInPages(2L, 0L, 4L).get(0).getDescription());
    }

    @Test
    void getItemRequestTest() {
        userRepository.save(userOne);
        requestRepository.save(itemRequest);
        Assertions.assertEquals("itemRequestDescription",
                itemRequestService.getRequest(1L, 1L).getDescription());
    }
}