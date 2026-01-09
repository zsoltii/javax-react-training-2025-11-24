package training.springbootreactiv;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.ollama.OllamaContainer;

@Testcontainers
@SpringBootTest
public abstract class TestBase {

    @Container
    protected static OllamaContainer ollama =
            new OllamaContainer("ollama/ollama:latest")
                    .withFileSystemBind("../docker/ollama", "/root/.ollama");

    @DynamicPropertySource
    protected static void ollamaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.ai.ollama.base-url", ollama::getEndpoint);
    }
}
