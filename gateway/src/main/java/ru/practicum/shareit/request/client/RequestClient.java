package ru.practicum.shareit.request.client;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.WebClient;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

@Service
public class RequestClient extends WebClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder rest) {
        super(rest
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> addRequest(ItemRequestInputDto inputDto, Long creatorId) {
        return post("", creatorId, inputDto);
    }

    public ResponseEntity<Object> getRequests(Long creatorId) {
        return get("", creatorId);
    }

    public ResponseEntity<Object> getRequestInPages(Long userId, Long from, Long size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequest(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}