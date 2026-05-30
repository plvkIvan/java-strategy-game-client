package client.network;

import client.exception.NetworkCommunicationException;
import messagesbase.ResponseEnvelope;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ERequestState;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
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

    public UniquePlayerIdentifier registerPlayer(String firstName,
                                                 String lastName,
                                                 String uAccount)
            throws NetworkCommunicationException {
        PlayerRegistration registration = new PlayerRegistration(firstName, lastName, uAccount);

        Mono<ResponseEnvelope<UniquePlayerIdentifier>> webAccess = baseWebClient
                .method(HttpMethod.POST)
                .uri("/" + gameId + "/players")
                .body(BodyInserters.fromValue(registration))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEnvelope<UniquePlayerIdentifier>>() {
                });

        ResponseEnvelope<UniquePlayerIdentifier> response = executeRequest(
                webAccess,
                "player registration"
        );

        ensureSuccessfulResponse(response, "Registration failed");

        return response.getData()
                .orElseThrow(() -> new NetworkCommunicationException(
                        "Registration response contained no player ID."
                ));
    }

    public void sendHalfMap(PlayerHalfMap halfMap) throws NetworkCommunicationException {
        Mono<ResponseEnvelope<Void>> webAccess = baseWebClient
                .method(HttpMethod.POST)
                .uri("/" + gameId + "/halfmaps")
                .body(BodyInserters.fromValue(halfMap))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEnvelope<Void>>() {
                });

        ResponseEnvelope<Void> response = executeRequest(
                webAccess,
                "half map upload"
        );

        ensureSuccessfulResponse(response, "Half map upload failed");
    }

    public GameState getGameState(UniquePlayerIdentifier playerId)
            throws NetworkCommunicationException {
        Mono<ResponseEnvelope<GameState>> webAccess = baseWebClient
                .method(HttpMethod.GET)
                .uri("/" + gameId + "/states/" + playerId.getUniquePlayerID())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEnvelope<GameState>>() {
                });

        ResponseEnvelope<GameState> response = executeRequest(
                webAccess,
                "game state request"
        );

        ensureSuccessfulResponse(response, "Game state request failed");

        return response.getData()
                .orElseThrow(() -> new NetworkCommunicationException(
                        "Game state response contained no data."
                ));
    }

    public void sendMove(UniquePlayerIdentifier playerId, EMove move)
            throws NetworkCommunicationException {
        PlayerMove playerMove = PlayerMove.of(playerId, move);

        Mono<ResponseEnvelope<Void>> webAccess = baseWebClient
                .method(HttpMethod.POST)
                .uri("/" + gameId + "/moves")
                .body(BodyInserters.fromValue(playerMove))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEnvelope<Void>>() {
                });

        ResponseEnvelope<Void> response = executeRequest(
                webAccess,
                "move upload"
        );

        ensureSuccessfulResponse(response, "Move failed");
    }

    private <T> ResponseEnvelope<T> executeRequest(Mono<ResponseEnvelope<T>> webAccess,
                                                   String operation)
            throws NetworkCommunicationException {
        try {
            ResponseEnvelope<T> response = webAccess.block();

            if (response == null) {
                throw new NetworkCommunicationException(
                        "No response received during " + operation + "."
                );
            }

            return response;
        } catch (WebClientException exception) {
            throw new NetworkCommunicationException(
                    "Network communication failed during " + operation + ".",
                    exception
            );
        }
    }

    private void ensureSuccessfulResponse(ResponseEnvelope<?> response,
                                          String errorMessage)
            throws NetworkCommunicationException {
        if (response.getState() == ERequestState.Error) {
            throw new NetworkCommunicationException(
                    errorMessage + ": " + response.getExceptionMessage()
            );
        }
    }
}