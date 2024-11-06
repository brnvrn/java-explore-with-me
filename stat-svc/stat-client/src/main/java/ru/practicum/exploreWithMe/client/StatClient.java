package ru.practicum.exploreWithMe.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.exploreWithMe.dto.EndpointHitsDto;
import ru.practicum.exploreWithMe.dto.StatisticsDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class StatClient {
    private final RestTemplate restTemplate;
    public static final String EMPTY = "0";

    public StatClient(@Value("${stats-server.url}") String statsServerUrl, RestTemplateBuilder builder) {
        restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(statsServerUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public void sendHttpRequest(EndpointHitsDto endpointHitsDto) {
        try {
            HttpEntity<EndpointHitsDto> requestEntity = new HttpEntity<>(endpointHitsDto, defaultHeaders());
            restTemplate.postForEntity("/hit", requestEntity, HttpStatus.class);
        } catch (RestClientException exception) {
            throw new IllegalArgumentException("Ошибка соединения с сервером");
        }
    }

    public List<StatisticsDto> getStats(LocalDateTime start,
                                        LocalDateTime end,
                                        String[] uris,
                                        Boolean unique) {
        if (uris == null) {
            uris = new String[]{EMPTY};
        }
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        ResponseEntity<StatisticsDto[]> responseListDto;
        try {
            responseListDto = restTemplate.getForEntity("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                    StatisticsDto[].class, parameters);
        } catch (RestClientException exception) {
            throw new IllegalArgumentException("Ошибка соединения с сервером");
        }
        return Arrays.asList(Objects.requireNonNull(responseListDto.getBody()));
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}