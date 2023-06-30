package ru.practicum.shareit.item.services.classes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.models.Comment;
import ru.practicum.shareit.item.models.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.services.interfaces.ItemService;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь не найден.");
        }

        Item item = itemMapper.itemDtoToItem(itemDto, userId);

        item.setUser(user.get());
        item = itemRepository.save(item);
        itemDto.setId(item.getId());
        return itemDto;
    }

    @Override
    public List<ItemDtoWithBooking> getUserItems(Long userId) {
        List<Item> items = itemRepository.findItemsByUserIdOrderByIdAsc(userId);
        List<ItemDtoWithBooking> itemDtoWithBookings = itemMapper.itemsToItemsDtoWithBookings(items);
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        Map<Long, List<Booking>> bookingsMap = bookingRepository.findBookingsByItemId(itemsId).stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        setLastAndNextBookings(itemDtoWithBookings, bookingsMap);
        return itemDtoWithBookings;
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Item oldItem = itemRepository.findItemByIdAndOwnerId(itemDto.getId(), userId);
        Item updatedItem = Item.builder()
                .id(oldItem.getId())
                .name(itemDto.getName() != null ? itemDto.getName() : oldItem.getName())
                .description(itemDto.getDescription() != null ? itemDto.getDescription() : oldItem.getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : oldItem.getAvailable())
                .user(oldItem.getUser())
                .build();
        itemRepository.save(updatedItem);
        return itemMapper.itemToItemDTO(updatedItem);
    }

    @Override
    public ItemDtoWithBooking getItem(Long userId, Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (!optionalItem.isPresent()) {
            throw new NotFoundException("Предмет не найден");
        }
        List<CommentDto> comments = commentMapper.toDtoList(commentRepository.findAllByItemId(itemId));

        ItemDtoWithBooking itemDtoWithBooking = itemMapper.itemToItemDTOWithBookings(optionalItem.get(), comments);
        if (itemRepository.getItemByIdAndUserId(itemId, userId).isPresent()) {
            Optional<List<Booking>> ownerBookings = bookingRepository.findBookingsByItemIdOrderByEndDesc(itemId);
            if (ownerBookings.isPresent()) {
                Map<Long, List<Booking>> bookingsMap = ownerBookings.get().stream()
                        .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
                setLastAndNextBookings(Collections.singletonList(itemDtoWithBooking), bookingsMap);
            }
        }
        return itemDtoWithBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItem(String text) {
        if (text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        Optional<List<Item>> items = itemRepository.search(text);
        if (!items.isPresent()) {
            throw new NotFoundException("Предмет с данным описанием не найден.");
        }
        return itemMapper.itemsToDTOItems((items.get()));
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentDtoInput input) {
        List<Booking> booking = bookingRepository.findBookingByBookerIdAndItemIdAndStatusApproved(userId, itemId);
        AtomicReference<CommentDto> commentToSend = new AtomicReference<>();
        if (booking.isEmpty()) {
            throw new BadRequestException("Вещь не находится у Вас в аренде.");
        }
        Optional<Item> item = itemRepository.findById(itemId);
        item.ifPresent(i -> {
            Optional<User> author = userRepository.findById(userId);
            Comment comment = Comment.builder()
                    .author(author.orElseThrow())
                    .text(input.getText())
                    .item(i)
                    .build();
            commentToSend.set(commentMapper.toDto(commentRepository.save(comment)));
        });
        return commentToSend.get();
    }

    private void setLastAndNextBookings(List<ItemDtoWithBooking> itemDtoWithBookings,
                                        Map<Long, List<Booking>> bookingsMap) {
        LocalDateTime currentTime = LocalDateTime.now();

        itemDtoWithBookings.forEach(itemDtoWithBooking -> {
            Long itemId = itemDtoWithBooking.getId();
            List<Booking> itemBookings = bookingsMap.getOrDefault(itemId, Collections.emptyList());


            Optional<Booking> lastBooking = itemBookings.stream()
                    .filter(booking -> booking.getStart().isBefore(currentTime) && booking.getEnd().isAfter(currentTime)
                            || booking.getEnd().isBefore(currentTime))
                    .findFirst();
            if (!lastBooking.isPresent()) {
                return;
            }

            Optional<Booking> nextBooking = itemBookings.stream()
                    .filter(booking -> booking.getStart().isAfter(lastBooking.map(Booking::getEnd).orElse(null)))
                    .min(Comparator.comparing(Booking::getStart));

            nextBooking.ifPresent(
                    b -> itemDtoWithBooking.setNextBooking(bookingMapper.toDtoShortVersion(b)));
            lastBooking.ifPresent(
                    b -> itemDtoWithBooking.setLastBooking(bookingMapper.toDtoShortVersion(b)));
            nextBooking.ifPresent(
                    booking -> itemDtoWithBooking.setNextBookingStart(booking.getStart()));
            nextBooking.ifPresent(
                    booking -> itemDtoWithBooking.setNextBookingEnd(booking.getEnd()));
            lastBooking.ifPresent(
                    booking -> itemDtoWithBooking.setLastBookingStart(booking.getStart()));
            lastBooking.ifPresent(
                    booking -> itemDtoWithBooking.setLastBookingEnd(booking.getEnd()));
        });
    }
}
