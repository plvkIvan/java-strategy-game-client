package client.network;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import messagesbase.ResponseEnvelope;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ERequestState;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import reactor.core.publisher.Mono;

public class NetworkService {
    private final WebClient baseWebClient;
    private final String gameId;

    public NetworkService(String serverBaseUrl, String gameId) {
        this.gameId = gameId;
        this.baseWebClient = WebClient.builder()
                .baseUrl(serverBaseUrl + "/games")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
                .build();
    }

    public UniquePlayerIdentifier registerPlayer(String firstName, String lastName, String uAccount) {
        PlayerRegistration registration = new PlayerRegistration(firstName, lastName, uAccount);

        Mono<ResponseEnvelope<UniquePlayerIdentifier>> webAccess = baseWebClient
                .method(HttpMethod.POST)
                .uri("/" + gameId + "/players")
                .body(BodyInserters.fromValue(registration))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEnvelope<UniquePlayerIdentifier>>() {});

        ResponseEnvelope<UniquePlayerIdentifier> response = webAccess.block();

        if (response == null) {
            throw new IllegalStateException("No response for registration.");
        }

        if (response.getState() == ERequestState.Error) {
            throw new IllegalStateException("Registration failed: " + response.getExceptionMessage());
        }

        return response.getData()
                .orElseThrow(() -> new IllegalStateException("Registration response contained no player ID."));
    }

    public void sendHalfMap(PlayerHalfMap halfMap) {
        Mono<ResponseEnvelope<Void>> webAccess = baseWebClient
                .method(HttpMethod.POST)
                .uri("/" + gameId + "/halfmaps")
                .body(BodyInserters.fromValue(halfMap))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEnvelope<Void>>() {});

        ResponseEnvelope<Void> response = webAccess.block();

        if (response == null) {
            throw new IllegalStateException("No response after sending half map.");
        }

        if (response.getState() == ERequestState.Error) {
            throw new IllegalStateException("Half map upload failed: " + response.getExceptionMessage());
        }
    }

    public GameState getGameState(UniquePlayerIdentifier playerId) {
        Mono<ResponseEnvelope<GameState>> webAccess = baseWebClient
                .method(HttpMethod.GET)
                .uri("/" + gameId + "/states/" + playerId.getUniquePlayerID())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEnvelope<GameState>>() {});

        ResponseEnvelope<GameState> response = webAccess.block();

        if (response == null) {
            throw new IllegalStateException("No response for game state.");
        }

        if (response.getState() == ERequestState.Error) {
            throw new IllegalStateException("Game state request failed: " + response.getExceptionMessage());
        }

        return response.getData()
                .orElseThrow(() -> new IllegalStateException("Game state response contained no data."));
    }

    public void sendMove(UniquePlayerIdentifier playerId, EMove move) {
        PlayerMove playerMove = PlayerMove.of(playerId, move);

        Mono<ResponseEnvelope<Void>> webAccess = baseWebClient
                .method(HttpMethod.POST)
                .uri("/" + gameId + "/moves")
                .body(BodyInserters.fromValue(playerMove))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEnvelope<Void>>() {});

        ResponseEnvelope<Void> response = webAccess.block();

        if (response == null) {
            throw new IllegalStateException("No response after sending move.");
        }

        if (response.getState() == ERequestState.Error) {
            throw new IllegalStateException("Move failed: " + response.getExceptionMessage());
        }
    }
}