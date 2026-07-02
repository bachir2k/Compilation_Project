package anonymizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Cœur du système d'anonymisation (Partie 7).
 *
 * État actuel (début de la Partie 7, phase "Théorie & squelette") :
 *   - lecture d'un fichier texte,
 *   - génération BRUTE des tokens (simple découpage par séparateurs),
 *   - écriture d'un fichier de sortie.
 *
 * TODO (à faire dès que la grammaire JavaCC de M2 - Partie 4/6 - est prête) :
 *   - remplacer generateRawTokens() par l'appel au lexer/parser généré par JavaCC,
 *   - remplacer la classification "WORD/OTHER" par les vrais types
 *     EMAIL, PHONE, DATE, AMOUNT, PERSON, WORD, OTHER,
 *   - implémenter l'anonymisation réelle (remplacement par les marqueurs
 *     <PERSONNE>, <EMAIL>, <TELEPHONE>, <DATE>, <MONTANT>).
 */
public class Anonymizer {

    /**
     * Lit le fichier d'entrée et renvoie son contenu intégral.
     */
    public String readFile(Path inputPath) throws IOException {
        return Files.readString(inputPath, StandardCharsets.UTF_8);
    }

    /**
     * Génération BRUTE des tokens (provisoire, sans JavaCC).
     * Découpe le texte en mots et ponctuation, sans classification fine.
     * Sert uniquement à valider la chaîne lecture -> tokens -> sortie
     * en attendant le lexer JavaCC.
     */
    public List<Token> generateRawTokens(String text) {
        List<Token> tokens = new ArrayList<>();
        // Découpage naïf : mot (lettres/accents/chiffres) OU un caractère isolé
        // (ponctuation, espace...). Remplacé plus tard par les règles JavaCC.
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("[\\p{L}\\p{N}@.\\-']+|[^\\s\\p{L}\\p{N}]")
                .matcher(text);
        while (m.find()) {
            String lexeme = m.group();
            String type = lexeme.matches("[\\p{L}\\p{N}@.\\-']+") ? "WORD" : "OTHER";
            tokens.add(new Token(type, lexeme));
        }
        return tokens;
    }

    /**
     * Anonymisation (placeholder). Pour l'instant, renvoie le texte
     * inchangé : la classification réelle des tokens (PERSON, EMAIL,
     * PHONE, DATE, AMOUNT) n'est pas encore disponible.
     */
    public String anonymize(String text, List<Token> tokens) {
        // TODO : remplacer chaque token classifié par son marqueur
        // <PERSONNE>, <EMAIL>, <TELEPHONE>, <DATE>, <MONTANT>.
        return text;
    }

    /**
     * Écrit le texte de sortie dans le fichier cible.
     */
    public void writeFile(Path outputPath, String content) throws IOException {
        Files.writeString(outputPath, content, StandardCharsets.UTF_8);
    }

    /**
     * Traite un fichier de bout en bout : lecture -> tokens -> anonymisation -> écriture.
     */
    public void process(Path inputPath, Path outputPath) throws IOException {
        String text = readFile(inputPath);

        List<Token> tokens = generateRawTokens(text);
        System.out.println("Tokens bruts générés (" + tokens.size() + ") pour " + inputPath.getFileName() + " :");
        for (Token t : tokens) {
            System.out.println("  " + t);
        }

        String anonymized = anonymize(text, tokens);
        writeFile(outputPath, anonymized);
        System.out.println("Fichier de sortie écrit : " + outputPath);
    }
}
