package sopt.jeolloga.domain.templestay.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sopt.jeolloga.domain.templestay.core.Templestay;
import sopt.jeolloga.domain.templestay.core.TemplestayRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoordinatesService {

    @Value("${geocoding.api.key}") // application.properties에서 API 키를 불러옵니다.
    private String apiKey;

    private final WebClient webClient;
    private final TemplestayRepository templestayRepository;

    public CoordinatesService(WebClient.Builder webClientBuilder, TemplestayRepository templestayRepository) {
        this.webClient = webClientBuilder.baseUrl("https://maps.googleapis.com/maps/api/geocode/json").build();
        this.templestayRepository = templestayRepository;
    }

    public Map<String, BigDecimal> getCoordinates(String address) {
        Map<String, BigDecimal> coordinates = new HashMap<>();

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("address", address)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("API response is null");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            JsonNode location = root.path("results").path(0).path("geometry").path("location");

            if (location.isMissingNode() || !location.has("lat") || !location.has("lng")) {
                throw new RuntimeException("Invalid response structure: missing location data");
            }

            BigDecimal latitude = location.path("lat").decimalValue();
            BigDecimal longitude = location.path("lng").decimalValue();

            coordinates.put("latitude", latitude);
            coordinates.put("longitude", longitude);

        } catch (Exception e) {
            System.err.println("Error while fetching coordinates: " + e.getMessage());
            throw new RuntimeException("Failed to fetch coordinates", e);
        }
        return coordinates;
    }

    public void updateAllAdressToCoordinate(){

        List<Templestay> templeStays = templestayRepository.findAll();

        for (Templestay templeStay : templeStays) {

            if(templeStay.getLongitude() != null){
                continue;
            }

            String address = templeStay.getAddress();

            try {
                Map<String, BigDecimal> coordinates = getCoordinates(address);

                BigDecimal latitude = coordinates.get("latitude");
                BigDecimal longitude = coordinates.get("longitude");

                templeStay.setLatitude(latitude);
                templeStay.setLongitude(longitude);

                templestayRepository.save(templeStay);

            } catch (Exception e) {
                // 6. 예외 처리 및 로그 출력
                e.printStackTrace();
            }
        }

    }
}
