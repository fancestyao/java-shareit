package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.models.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByUserIdOrderByIdAsc(Long userId);

    @Query("select i "
            + "from Item as i "
            + "join fetch i.user as o where i.id = :itemId and o.id = :userId")
    Item findItemByIdAndOwnerId(@Param("itemId") Long itemId, @Param("userId") Long ownerId);

    @Query("select i "
            + "from Item as i "
            + "where lower(i.name) like lower(concat('%', :search, '%')) "
            + "or lower(i.description) like lower(concat('%', :search,'%')) and i.available != false ")
    Optional<List<Item>> search(@Param("search") String searchString);

    Optional<Item> getItemByIdAndUserId(Long itemId, Long userId);

}
