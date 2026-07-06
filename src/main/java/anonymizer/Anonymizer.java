package anonymizer;

import anonymizer.lexer.AnonymizerParser;
import anonymizer.lexer.AnonymizerParserConstants;
import anonymizer.lexer.AnonymizerParserTokenManager;
import anonymizer.lexer.SimpleCharStream;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cœur du système d'anonymisation (Partie 7, responsable M3).
 *
 * Intègre le lexer/parser JavaCC livré par M2 (grammar/Anonymizer.jj,
 * paquetage anonymizer.lexer, généré dans grammar/ puis compilé dans out/) :
 *   - lecture d'un fichier texte,
 *   - génération des tokens via le TokenManager généré par JavaCC,
 *   - anonymisation via anonymizer.lexer.AnonymizerParser.anonymize(),
 *   - écriture du fichier de sortie.
 *
 * Prérequis pour compiler/exécuter (voir grammar/build.sh ou build.ps1) :
 *   1. java -cp lib/javacc.jar javacc -OUTPUT_DIRECTORY=grammar grammar/Anonymizer.jj
 *   2. javac -encoding UTF-8 -d out grammar/*.java src/main/java/anonymizer/*.java
 *   3. java -cp out anonymizer.Main test/input/test1_exemple_sujet.txt
 */
public class Anonymizer {

    /**
     * Lit le fichier d'entrée et renvoie son contenu intégral.
     */
    public String readFile(Path inputPath) throws IOException {
        return Files.readString(inputPath, StandardCharsets.UTF_8);
    }

    /**
     * Génération des tokens (Partie 7) via le lexer JavaCC généré par M2.
     * Les tokens purement blancs (espaces, retours à la ligne) ne sont pas
     * inclus dans la liste retournée, pour garder un affichage lisible
     * (même convention que l'instrumentation de M2, Partie 8) ; ils restent
     * bien pris en compte lors de l'anonymisation, qui relit le texte
     * intégralement via son propre passage sur le lexer.
     */
    public List<Token> generateTokens(String text) {
        List<Token> tokens = new ArrayList<>();
        AnonymizerParserTokenManager lexer = new AnonymizerParserTokenManager(
                new SimpleCharStream(new StringReader(text)));

        anonymizer.lexer.Token t;
        while ((t = lexer.getNextToken()).kind != AnonymizerParserConstants.EOF) {
            if (t.image.trim().isEmpty()) {
                continue;
            }
            tokens.add(new Token(nomType(t.kind), t.image));
        }
        return tokens;
    }

    /**
     * Anonymisation : délègue au lexer/parser JavaCC de M2
     * (anonymizer.lexer.AnonymizerParser.anonymize), qui remplace les
     * tokens sensibles par leurs marqueurs (<PERSONNE>, <EMAIL>,
     * <TELEPHONE>, <DATE>, <MONTANT>) et restitue le reste du texte
     * à l'identique.
     */
    public String anonymize(String text) throws Exception {
        return AnonymizerParser.anonymize(text);
    }

    /**
     * Écrit le texte de sortie dans le fichier cible.
     */
    public void writeFile(Path outputPath, String content) throws IOException {
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        Files.writeString(outputPath, content, StandardCharsets.UTF_8);
    }

    /**
     * Traite un fichier de bout en bout : lecture -> tokens -> anonymisation -> écriture.
     * Retourne le récapitulatif des tokens par type (utile pour la Partie 9 - Validation).
     */
    public Map<String, Integer> process(Path inputPath, Path outputPath) throws Exception {
        String text = readFile(inputPath);

        List<Token> tokens = generateTokens(text);
        Map<String, Integer> recap = new LinkedHashMap<>();
        for (Token tok : tokens) {
            recap.merge(tok.type, 1, Integer::sum);
        }

        System.out.println("Tokens générés (" + tokens.size() + ") pour " + inputPath.getFileName() + " :");
        recap.forEach((type, count) -> System.out.println("  " + type + " : " + count));

        String anonymized = anonymize(text);
        writeFile(outputPath, anonymized);
        System.out.println("Fichier de sortie écrit : " + outputPath);

        return recap;
    }

    /** Nom lisible du type de token (retire les chevrons du tokenImage JavaCC). */
    private static String nomType(int kind) {
        String image = AnonymizerParserConstants.tokenImage[kind];
        if (image.startsWith("\"")) {
            return "OTHER"; // token littéral (ponctuation, etc.) sans nom déclaré
        }
        return image.replace("<", "").replace(">", "");
    }
}
