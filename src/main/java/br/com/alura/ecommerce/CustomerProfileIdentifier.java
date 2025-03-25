package br.com.alura.ecommerce;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;

public class CustomerProfileIdentifier {
    public static void main(String[] args) {

            var system = """
                    Identifique o perfil de compra de cada cliente
                    
                    A resposta deve ser:
                    
                    Cliente - descreva o perfil do cliente em tres palavras
                    """;

            var user = getClientsFromFile("customer_shoplist/lista_de_compras_100_clientes.csv");

        var tokenCount = countTokens(user);
        System.out.println("Token Count: " + tokenCount);

        sendRequest(user, system);

//        var model = "gpt-3.5-turbo";
//        var sizeOfExpectedResponse = 2048;
//
//        if(tokenCount > 4096 - sizeOfExpectedResponse) {
//            model = "gpt-3.5-turbo-16k";
//        }

//        System.out.println("Model: " + model);

//        sendRequest(user, system, model, sizeOfExpectedResponse);

    }

    public static void sendRequest(String user, String system) {

        OpenAiService service = new OpenAiService(System.getenv("OPENAI_KEY"), Duration.ofSeconds(90));
        var completionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4o-mini")
//                .maxTokens(sizeOfExpectedResponse)
                .messages(Arrays.asList(
                        new ChatMessage(ChatMessageRole.USER.value(), user),
                        new ChatMessage(ChatMessageRole.SYSTEM.value() , system)
                ))
                .temperature(1.0)
                .build();

        service
                .createChatCompletion(completionRequest)
                .getChoices()
                .forEach(c -> System.out.println(c.getMessage().getContent()));
    }

    private static String getClientsFromFile(String file) {
        try {
            var path = Path.of(ClassLoader
                    .getSystemResource(file)
                    .toURI());
            return Files.readAllLines(path).toString();
        } catch (Exception e) {
            throw new RuntimeException("Wasn't possible to load the file");
        }
    }

    private static int countTokens(String prompt) {
        var registry = Encodings.newDefaultEncodingRegistry();
        var enc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);
        return enc.countTokens(prompt);
    }

}
