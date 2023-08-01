package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemToRequestDto;
import ru.practicum.shareit.request.models.Request;
import ru.practicum.shareit.user.models.User;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
})
@ExtendWith(SpringExtension.class)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    private Item itemOne;
    private Item itemTwo;
    private User userOne;
    private User userTwo;
    private Request itemRequest;

    @BeforeEach
    void beforeEach() {
        userOne = new User();
        userOne.setName("userOneName");
        userOne.setEmail("userOneEmail@email.ru");
        userTwo = new User();
        userTwo.setName("TestUser2");
        userTwo.setEmail("TestUser2@email.ru");
        itemRequest = new Request();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(userTwo);
        itemRequest.setDescription("itemRequestDescription");
        itemOne = new Item(null, userOne, "itemOneName", "itemOneDescription", true,
                null);
        itemTwo = new Item(null, userOne, "itemTwoName", "itemTwoDescription", true,
                itemRequest);
        Item itemThree = new Item(null, userOne, "itemThreeName", "itemThreeDescription",
                false, null);
        Booking booking2 = new Booking();
        Booking booking3 = new Booking();
        booking2.setId(2L);
        booking2.setItem(itemTwo);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(booking2.getStart().plusHours(10));
        booking3.setId(3L);
        booking3.setItem(itemThree);
        booking3.setStart(LocalDateTime.now());
        booking3.setEnd(booking3.getStart().plusHours(10));
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void findItemByIdAndOwnerId() {
        entityManager.persist(userOne);
        Assertions.assertNull(itemOne.getId());
        entityManager.persist(itemOne);
        Assertions.assertNotNull(itemOne.getId());
    }

    @Test
    void search() {
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        Assertions.assertNull(itemOne.getId());
        entityManager.persist(itemOne);
        Assertions.assertNotNull(itemOne.getId());
        Optional<List<Item>> itemInList = itemRepository.search("itemOne");
        Assertions.assertTrue((itemInList).isPresent());
        Assertions.assertEquals(itemOne, itemInList.get().get(0));
    }

    @Test
    void findAllByRequests() {
        entityManager.persist(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemRequest);
        entityManager.persist(itemTwo);
        TypedQuery<ItemToRequestDto> query = entityManager.getEntityManager()
                .createQuery("select new ru.practicum.shareit.request.dto" +
                        ".ItemToRequestDto(i.id, i.name, i.description, i.available , i.request.id)" +
                        " from Item as i " +
                        "where i.request.id IN :requestsId", ItemToRequestDto.class);
        ItemToRequestDto itemForRequest = query.setParameter("requestsId", List.of(itemRequest.getId()))
                .getSingleResult();
        Assertions.assertEquals(itemTwo.getDescription(), itemForRequest.getDescription());
        Assertions.assertEquals(true, itemForRequest.getAvailable());
    }
}
