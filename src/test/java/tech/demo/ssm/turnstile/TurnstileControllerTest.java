package tech.demo.ssm.turnstile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import tech.demo.ssm.turnstile.sm.DomainState;


@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TurnstileControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void givenJustStarted_shouldBeLocked() {

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/turnstile/user/{userId}/state")
                        .build("User1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("\"%s\"".formatted(DomainState.LOCKED));
    }

    @Test
    void givenJustStarted_whenPush_shouldStayLocked() {

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/turnstile/user/{userId}/push")
                        .build("User1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(getFormattedState(DomainState.LOCKED));
    }

    @Test
    void givenJustStarted_whenCoin_shouldUnlock() {

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/turnstile/user/{userId}/coin")
                        .build("User1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(getFormattedState(DomainState.UNLOCKED));
    }

    @Test
    void givenJustStarted_whenCoinFromUser1_shouldStayLockedForUser2() {

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/turnstile/user/{userId}/coin")
                        .build("User1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(getFormattedState(DomainState.UNLOCKED));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/turnstile/user/{userId}/state")
                        .build("User1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("\"%s\"".formatted(DomainState.UNLOCKED));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/turnstile/user/{userId}/state")
                        .build("User2"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("\"%s\"".formatted(DomainState.LOCKED));
    }

    private String getFormattedState(DomainState state) {
        return "[\"%s\"]".formatted(state);
    }
}