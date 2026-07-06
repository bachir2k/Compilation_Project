package anonymizer;

/**
 * Représentation d'un token tel que produit par le lexer JavaCC de M2
 * (grammar/Anonymizer.jj, paquetage anonymizer.lexer).
 *
 * Cette classe reste volontairement simple (type + lexème) : elle sert
 * uniquement à l'affichage/au comptage côté implémentation M3
 * (Partie 7 - génération des tokens, Partie 9 - validation).
 * La classification réelle est faite par le lexer généré ; voir
 * {@link Anonymizer#generateTokens(String)}.
 */
public class Token {

    public final String type;   // EMAIL, PHONE, DATE, AMOUNT, PERSON, WORD, OTHER...
    public final String lexeme; // texte reconnu

    public Token(String type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return "[" + type + " : \"" + lexeme + "\"]";
    }
}
