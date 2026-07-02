package anonymizer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Point d'entrée du programme.
 *
 * Usage :
 *   java anonymizer.Main <fichier_entree> [fichier_sortie]
 *
 * Si le fichier de sortie n'est pas précisé, il est écrit dans
 * test/output/ avec le suffixe "_anonymise".
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java anonymizer.Main <fichier_entree> [fichier_sortie]");
            System.exit(1);
        }

        Path inputPath = Paths.get(args[0]);
        Path outputPath = (args.length >= 2)
                ? Paths.get(args[1])
                : defaultOutputPath(inputPath);

        try {
            new Anonymizer().process(inputPath, outputPath);
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de " + inputPath + " : " + e.getMessage());
            System.exit(1);
        }
    }

    private static Path defaultOutputPath(Path inputPath) {
        String fileName = inputPath.getFileName().toString();
        String baseName = fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf('.'))
                : fileName;
        return Paths.get("test", "output", baseName + "_anonymise.txt");
    }
}
