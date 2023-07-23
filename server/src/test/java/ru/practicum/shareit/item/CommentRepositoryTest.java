package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.models.Comment;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.models.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
})
@ExtendWith(SpringExtension.class)
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CommentRepository commentRepository;
    User user;
    Item item;
    Comment comment;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("userName");
        user.setEmail("userEmail@mail.ru");
        item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(Boolean.TRUE);
        item.setUser(user);
        comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setText("commentText");
        comment.setAuthor(user);
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void findAllByItemId() {
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(comment);
        List<Comment> comments = commentRepository.findAllByItemId(1L);
        Assertions.assertTrue(comments.isEmpty());
    }
}
