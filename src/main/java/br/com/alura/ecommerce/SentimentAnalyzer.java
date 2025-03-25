package br.com.alura.ecommerce;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SentimentAnalyzer {

    public static void main(String[] args) {
        try {
            var system = """
                    Você é um analisador de sentimentos de avaliações de produtos.
                    Escreva um parágrafo com até 50 palavras resumindo as avaliações e depois atribua qual o sentimento geral para o produto.
                    Identifique também 3 pontos fortes e 3 pontos fracos identificados a partir das avaliações.
                    
                    #### Formato de saída
                    Nome do produto:
                    Resumo das avaliações: [resuma em até 50 palavras]
                    Sentimento geral: [deve ser: POSITIVO, NEUTRO ou NEGATIVO]
                    Pontos fortes: [3 bullets points]
                    Pontos fracos: [3 bullets points]
                    """;

//        var product = "grill-eletrico-para-churrasco";
//        var user = loadFile(product);
//      -------------------------------------------------
//      --> BATCH:

            var reviewsDirectory = Path.of("src/main/resources/reviews");

            var reviewsFiles = Files
                    .walk(reviewsDirectory, 1)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .collect(Collectors.toList());

            for(Path file : reviewsFiles) {
                System.out.println("Starting review of: " + file.getFileName());

                var user = loadFile(file);

                var request = ChatCompletionRequest
                        .builder()
                        .model("gpt-4o-mini")
                        .messages(Arrays.asList(
                                new ChatMessage(
                                        ChatMessageRole.SYSTEM.value(),
                                        system),
                                new ChatMessage(
                                        ChatMessageRole.USER.value(),
                                        user)))
                        .build();

                var apiKey = System.getenv("OPENAI_KEY");
                var service = new OpenAiService(apiKey, Duration.ofSeconds(60));

                var attempts = 0;
                while (attempts++ > 5) {
                    try {
                        var response = service
                                .createChatCompletion(request)
                                .getChoices().get(0).getMessage().getContent();
                    } catch (OpenAiHttpException e) {
                        var errorCode = e.statusCode;
                        switch (errorCode) {
                            case 401 -> throw new RuntimeException("API Key error!", e);
                            case 429 -> {
                                System.out.println("Rate limit! Trying to connect again");
                                Thread.sleep(1000 * 5);
                            }
                            case 500, 503 -> {
                                System.out.println("API down! Trying to connect again");
                                Thread.sleep(1000 * 5);
                            }
                        }
                    }
                }

                saveAnalysis(file.getFileName().toString().replace(".txt", ""), response);

                System.out.println("Finished Review!");
            }
        } catch (Exception e) {
            System.out.println("Error processing sentiment analyzer!");
        }
    }

    private static String loadFile(Path file) {
        try {
//            var path = Path.of("src/main/resources/reviews/avaliacoes-" + file +".txt");
            return Files.readAllLines(file).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error loading file!", e);
        }
    }

    private static void saveAnalysis(String file, String analysis) {
        try {
            var path = Path.of("src/main/resources/analysis/sentiment-analysis-" + file +".txt");
            Files.writeString(path, analysis, StandardOpenOption.CREATE_NEW);
        } catch (Exception e) {
            throw new RuntimeException("Error saving file!", e);
        }
    }


}
