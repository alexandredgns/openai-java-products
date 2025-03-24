package br.com.alura.ecommerce.main;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        var user = "Gere 5 produtos";
        var system = """
                você é um gerador de produtos ficticios para um ecommerce
                e deve gerar apenas o nome dos produtos solicitados pelo usuário
                """;

        OpenAiService service = new OpenAiService(System.getenv("OPENAI_KEY"));
        var completionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4")
                .messages(Arrays.asList(
                        new ChatMessage(ChatMessageRole.USER.value(), user),
                        new ChatMessage(ChatMessageRole.SYSTEM.value() , system)
                ))
                .build();

        service
                .createChatCompletion(completionRequest)
                .getChoices()
                .forEach(System.out::println);

    }
}
