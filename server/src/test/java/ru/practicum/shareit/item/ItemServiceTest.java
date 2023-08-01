package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.services.interfaces.ItemService;
import ru.practicum.shareit.request.models.Status;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceTest {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private User userOne;
    private Item itemOne;
    private Item itemTwo;


    @BeforeEach
    void beforeEach() {
        userOne = new User();
        userOne.setName("userOneName");
        userOne.setEmail("userOneMail@mail.ru");
        itemOne = new Item(null,
                userOne,
                "itemOneName",
                "itemOneDescription",
                true,
                null);
        itemTwo = new Item(null,
                userOne,
                "itemTwoName",
                "itemOneDescription",
                false,
                null);
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(24));
        booking.setEnd(LocalDateTime.now().minusHours(5));
        booking.setStatus(Status.APPROVED);
    }

    @Test
    void getUserItemsTest() {
        userRepository.save(userOne);
        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);

        List<ItemDtoWithBooking> userItems = itemService.getUserItems(1L);

        Assertions.assertAll(() -> assertEquals(itemOne.getDescription(), userItems.get(0).getDescription()),
                () -> assertEquals(itemTwo.getName(), userItems.get(1).getName()));
    }

    @Test
    void getUserItemTest() {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking();
        itemDtoWithBooking.setName(itemOne.getName());
        itemDtoWithBooking.setDescription(itemOne.getDescription());
        itemDtoWithBooking.setAvailable(itemOne.getAvailable());
        itemDtoWithBooking.setId(itemOne.getId());
        itemDtoWithBooking.setComments(new ArrayList<>());
        userRepository.save(userOne);
        itemRepository.save(itemOne);
        itemDtoWithBooking.setId(1L);
        assertEquals(itemDtoWithBooking, itemService.getItem(1L, 1L));
    }

    @Test
    void searchItemWithRegularResultTest() {
        userRepository.save(userOne);
        itemRepository.save(itemOne);
        assertEquals("itemOneDescription", itemService.searchItem("it").get(0).getDescription());
    }

    @Test
    void addItemTest() {
        ItemDto itemDTO = new ItemDto(null, "itemDtoName", "itemDtoDescription",
                true, null);
        userRepository.save(userOne);
        ItemDto addedItemDTO = itemService.createItem(userOne.getId(), itemDTO);
        assertNotNull(addedItemDTO.getId());
        assertEquals(itemDTO.getName(), addedItemDTO.getName());
        assertEquals(itemDTO.getDescription(), addedItemDTO.getDescription());
        assertEquals(1L, addedItemDTO.getId());
    }

    @Test
    void updateItemTest() {
        ItemDto itemDTO = new ItemDto(1L, "updatedItemDtoName", "updatedItemDtoDescription",
                true, null);
        userRepository.save(userOne);
        itemRepository.save(itemOne);
        ItemDto itemAfterUpdating = itemService.updateItem(1L, itemDTO);
        Assertions.assertAll(() -> Assertions.assertEquals(itemDTO.getName(), itemAfterUpdating.getName()),
                () -> Assertions.assertEquals(itemDTO.getDescription(), itemAfterUpdating.getDescription()));
    }
}
