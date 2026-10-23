package CatBotJava;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CurrencyService {

    private static final String API_URL = "https://currencyrateapi.com/api/latest";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrencyRateResult(
            @JsonProperty("success") boolean success,
            @JsonProperty("base") String base,
            @JsonProperty("date") String date,
            @JsonProperty("rates") Map<String, Double> rates) {
    }

    public CompletableFuture<Optional<Double>> getRate(String source, String target) {
        String src = source.trim().toLowerCase();
        String tgt = target.trim().toLowerCase();

        URI uri = URI.create(API_URL
                + "?base=" + URLEncoder.encode(src, StandardCharsets.UTF_8)
                + "&codes=" + URLEncoder.encode(tgt, StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        CurrencyRateResult result = mapper.readValue(response.body(), CurrencyRateResult.class);
                        if (result != null && result.success() && result.rates() != null) {
                            return Optional.ofNullable(result.rates().get(tgt));
                        }
                        return Optional.<Double>empty();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}