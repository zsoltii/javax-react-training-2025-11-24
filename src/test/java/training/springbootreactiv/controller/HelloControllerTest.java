package training.springbootreactiv.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import training.springbootreactiv.gateway.HelloClient;

@DisplayName("HelloController tesztek")
class HelloControllerTest {

    private HelloController helloController;

    // Egyszerű stub implementáció a HelloClient interfészhez
    private static class HelloClientStub implements HelloClient {
        @Override
        public Mono<HelloMessage> hello() {
            return Mono.just(new HelloMessage("Stub Hello Message"));
        }
    }

    @BeforeEach
    void setUp() {
        HelloClient helloClientStub = new HelloClientStub();
        helloController = new HelloController(helloClientStub);
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
                            assertTrue(
                                    message.message().startsWith("Hello, World! The time is:"),
                                    "Az üzenet tartalma nem megfelelő, várt: 'Hello, World! The time is:...', kapott: "
                                            + message.message());
                        })
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Pozitív teszt: hello() metódus mindig ugyanazt az üzenet formátumot adja vissza")
    void testHello_ConsistentMessage() {
        // Given - több hívás
        Mono<HelloMessage> result1 = helloController.hello();
        Mono<HelloMessage> result2 = helloController.hello();

        // Then - mindkét hívás ugyanazt az üzenet formátumot adja
        StepVerifier.create(result1)
                .expectNextMatches(msg -> msg.message().startsWith("Hello, World! The time is:"))
                .expectComplete()
                .verify();

        StepVerifier.create(result2)
                .expectNextMatches(msg -> msg.message().startsWith("Hello, World! The time is:"))
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

    // ========== helloWebCLient() metódus tesztek ==========

    @Test
    @DisplayName(
            "Pozitív teszt: helloWebCLient() metódus visszaad egy Mono<HelloMessage> objektumot")
    void testHelloWebClient_ReturnsMonoWithHelloMessage() {
        // When
        Mono<HelloMessage> result = helloController.helloWebCLient();

        // Then
        assertNotNull(result, "A visszaadott Mono nem lehet null");

        // Ellenőrizzük a Mono tartalmát a StepVerifier segítségével
        StepVerifier.create(result)
                .assertNext(
                        message -> {
                            assertNotNull(message, "A HelloMessage nem lehet null");
                            assertNotNull(message.message(), "Az üzenet tartalma nem lehet null");
                            assertEquals(
                                    "Stub Hello Message",
                                    message.message(),
                                    "Az üzenet tartalma nem egyezik a stub által visszaadott értékkel");
                        })
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName(
            "Pozitív teszt: helloWebCLient() a HelloClient által visszaadott üzenetet használja")
    void testHelloWebClient_UsesHelloClientMessage() {
        // When
        Mono<HelloMessage> result = helloController.helloWebCLient();

        // Then
        StepVerifier.create(result)
                .expectNext(new HelloMessage("Stub Hello Message"))
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Pozitív teszt: helloWebCLient() metódus nem dob kivételt")
    void testHelloWebClient_DoesNotThrowException() {
        // When & Then
        assertDoesNotThrow(
                () -> {
                    Mono<HelloMessage> result = helloController.helloWebCLient();
                    result.block(); // blokkolva várunk az eredményre
                },
                "A helloWebCLient() metódus nem dobhat kivételt");
    }

    @Test
    @DisplayName(
            "Pozitív teszt: helloWebCLient() mindig ugyanazt az üzenetet adja vissza a stub miatt")
    void testHelloWebClient_ConsistentMessageFromStub() {
        // Given - több hívás
        Mono<HelloMessage> result1 = helloController.helloWebCLient();
        Mono<HelloMessage> result2 = helloController.helloWebCLient();

        // Then - mindkét hívás ugyanazt az üzenetet adja
        StepVerifier.create(result1)
                .expectNext(new HelloMessage("Stub Hello Message"))
                .expectComplete()
                .verify();

        StepVerifier.create(result2)
                .expectNext(new HelloMessage("Stub Hello Message"))
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Negatív teszt: helloWebCLient() által visszaadott üzenet nem lehet üres")
    void testHelloWebClient_MessageIsNotEmpty() {
        // When
        Mono<HelloMessage> result = helloController.helloWebCLient();

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
    @DisplayName("Negatív teszt: helloWebCLient() által visszaadott Mono nem lehet üres")
    void testHelloWebClient_MonoIsNotEmpty() {
        // When
        Mono<HelloMessage> result = helloController.helloWebCLient();

        // Then
        StepVerifier.create(result)
                .expectNextCount(1) // Pontosan egy elemet várunk
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Negatív teszt: helloWebCLient() által visszaadott Mono nem hibával fejeződik be")
    void testHelloWebClient_MonoDoesNotCompleteWithError() {
        // When
        Mono<HelloMessage> result = helloController.helloWebCLient();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(msg -> msg != null && msg.message() != null)
                .expectComplete() // Nem expectError()
                .verify();
    }

    @Test
    @DisplayName("Negatív teszt: helloWebCLient() üzenete nem egyezik meg hibás értékekkel")
    void testHelloWebClient_MessageDoesNotMatchIncorrectValues() {
        // When
        Mono<HelloMessage> result = helloController.helloWebCLient();

        // Then
        StepVerifier.create(result)
                .assertNext(
                        message -> {
                            assertNotEquals(
                                    "", message.message(), "Az üzenet nem lehet üres string");
                            assertNotEquals(
                                    "Hello, World!",
                                    message.message(),
                                    "Az üzenet nem egyezhet meg egy másik üzenettel");
                            assertNotEquals(null, message.message(), "Az üzenet nem lehet null");
                        })
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Negatív teszt: helloWebCLient() nem adja vissza a hello() metódus formátumát")
    void testHelloWebClient_DifferentFromHelloMethod() {
        // When
        Mono<HelloMessage> helloResult = helloController.hello();
        Mono<HelloMessage> webClientResult = helloController.helloWebCLient();

        // Then - a két metódus különböző üzeneteket ad vissza
        String helloMessage = helloResult.block().message();
        String webClientMessage = webClientResult.block().message();

        assertNotEquals(
                helloMessage,
                webClientMessage,
                "A két metódus különböző üzeneteket kell visszaadjon");
        assertFalse(
                webClientMessage.startsWith("Hello, World! The time is:"),
                "A webClient üzenet nem tartalmazhatja a hello() metódus formátumát");
    }
}
