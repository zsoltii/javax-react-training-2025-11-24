package training.springbootreactiv.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("HelloController tesztek")
class HelloControllerTest {

    private HelloController helloController;

    @BeforeEach
    void setUp() {
        helloController = new HelloController();
    }

    @Test
    @DisplayName("Pozitív teszt: hello() metódus visszaad egy Mono<HelloMessage> objektumot")
    void testHello_ReturnsMonoWithHelloMessage() {
        // When
        Mono<HelloMessage> result = helloController.hello();

        // Then
        assertNotNull(result, "A visszaadott Mono nem lehet null");

        // Ellenőrizzük a Mono tartalmát a StepVerifier segítségével
        StepVerifier.create(result)
                .assertNext(
                        message -> {
                            assertNotNull(message, "A HelloMessage nem lehet null");
                            assertEquals(
                                    "Hello, World!",
                                    message.message(),
                                    "Az üzenet tartalma nem megfelelő");
                        })
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Pozitív teszt: hello() metódus mindig ugyanazt az üzenetet adja vissza")
    void testHello_ConsistentMessage() {
        // Given - több hívás
        Mono<HelloMessage> result1 = helloController.hello();
        Mono<HelloMessage> result2 = helloController.hello();

        // Then - mindkét hívás ugyanazt az üzenetet adja
        StepVerifier.create(result1)
                .expectNextMatches(msg -> msg.message().equals("Hello, World!"))
                .expectComplete()
                .verify();

        StepVerifier.create(result2)
                .expectNextMatches(msg -> msg.message().equals("Hello, World!"))
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Pozitív teszt: hello() metódus nem dob kivételt")
    void testHello_DoesNotThrowException() {
        // When & Then
        assertDoesNotThrow(
                () -> {
                    Mono<HelloMessage> result = helloController.hello();
                    result.block(); // blokkolva várunk az eredményre
                },
                "A hello() metódus nem dobhat kivételt");
    }

    @Test
    @DisplayName("Negatív teszt: az üzenet nem lehet üres string")
    void testHello_MessageIsNotEmpty() {
        // When
        Mono<HelloMessage> result = helloController.hello();

        // Then
        StepVerifier.create(result)
                .assertNext(
                        message -> {
                            assertNotNull(message.message(), "Az üzenet nem lehet null");
                            assertFalse(message.message().isEmpty(), "Az üzenet nem lehet üres");
                            assertTrue(
                                    message.message().length() > 0,
                                    "Az üzenet hossza nagyobb kell legyen mint 0");
                        })
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Negatív teszt: az üzenet nem egyezik meg hibás értékekkel")
    void testHello_MessageDoesNotMatchIncorrectValues() {
        // When
        Mono<HelloMessage> result = helloController.hello();

        // Then
        StepVerifier.create(result)
                .assertNext(
                        message -> {
                            assertNotEquals(
                                    "", message.message(), "Az üzenet nem lehet üres string");
                            assertNotEquals(
                                    "Goodbye!",
                                    message.message(),
                                    "Az üzenet nem lehet 'Goodbye!'");
                            assertNotEquals(
                                    "hello, world!",
                                    message.message(),
                                    "Az üzenet kis-nagybetű érzékeny");
                            assertNotEquals(null, message.message(), "Az üzenet nem lehet null");
                        })
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Negatív teszt: a Mono nem lehet üres")
    void testHello_MonoIsNotEmpty() {
        // When
        Mono<HelloMessage> result = helloController.hello();

        // Then
        StepVerifier.create(result)
                .expectNextCount(1) // Pontosan egy elemet várunk
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Negatív teszt: a Mono nem hibával fejeződik be")
    void testHello_MonoDoesNotCompleteWithError() {
        // When
        Mono<HelloMessage> result = helloController.hello();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(msg -> msg != null && msg.message() != null)
                .expectComplete() // Nem expectError()
                .verify();
    }
}
