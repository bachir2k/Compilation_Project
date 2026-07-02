package anonymizer;

/**
 * Représentation provisoire d'un token, en attendant l'intégration
 * du lexer généré par JavaCC (Partie 4, responsable M2).
 *
 * Une fois la grammaire .jj prête, cette classe sera remplacée par
 * les tokens générés automatiquement (type Token de JavaCC / JJT).
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
