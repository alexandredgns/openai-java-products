package br.com.alura.ecommerce;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.Arrays;
import java.util.Scanner;

public class ProductCategorizer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite as categorias válidas: ");
        var categories = scanner.nextLine();

        while(true) {

            System.out.println("Digite o nome do produto: ");
            var user = scanner.nextLine();

//        var user = """
//                Celular
//                Raquete
//                Pasta
//                """;
            var system = """
                    você é um categorizador de produtos de um ecommerce e deve responder apenas o nome da
                    categoria do produto informado.
                    
                    Escolha uma categoria dentre as opções abaixo:
                    
                    %s
                    
                    ######## Exemplo de Uso:
                    Pergunta: Bola de futebol
                    Resposta: Esportes
                    
                    (Não escrever "Pergunta" e "Resposta", apenas o nome do produto e da categoria)
                    
                    ####### Regras a serem seguidas:
                    Caso o usuário pergunte algo que não seja de categorização de produtos, voce
                    deve responder que não pode ajudar pois o seu papel é apenas responder a 
                    categoria do produto informado.
                    
                    """.formatted(categories);


            sendRequest(user, system);
        }

    }

    public static void sendRequest(String user, String system) {

        OpenAiService service = new OpenAiService(System.getenv("OPENAI_KEY"), Duration.ofSeconds(30));
        var completionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4")
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
}
