package br.com.alura.ecommerce;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.*;

import java.math.BigDecimal;

public class TokenCounter {

    public static void main(String[] args) {
        var registry = Encodings.newDefaultEncodingRegistry();
        var enc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);
        var countedTokens = enc.countTokens("Identifique o perfil de compra de cada cliente");

        System.out.println("Counted tokens: " + countedTokens);

        var cost = new BigDecimal(countedTokens)
                .divide(new BigDecimal("1000"))
                .multiply(new BigDecimal("0.0010"));

        System.out.println("Cost: $" + cost);

    }
}
