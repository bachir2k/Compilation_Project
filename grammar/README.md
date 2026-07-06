# Module lexical & syntaxique — JavaCC (Membre 2)

Ce dossier contient la spécification JavaCC du projet d'anonymisation.

- [`Anonymizer.jj`](Anonymizer.jj) — **la** grammaire (analyse lexicale + syntaxique
  + instrumentation). Seul fichier à versionner ici.
- Les fichiers `*.java` de ce dossier sont **générés** par JavaCC et ignorés par
  git (voir `.gitignore`).

## Prérequis

- JDK (testé avec Java 25).
- `lib/javacc.jar` (JavaCC 5.0) à la racine du projet.

## Générer, compiler, exécuter (Windows / PowerShell)

```powershell
.\grammar\build.ps1                                   # génère + compile
java -cp out anonymizer.lexer.AnonymizerParser test\input\test1_exemple_sujet.txt
```

## Générer, compiler, exécuter (bash / Git Bash / Linux)

```bash
./grammar/build.sh
java -cp out anonymizer.lexer.AnonymizerParser test/input/test1_exemple_sujet.txt
```

## Étapes manuelles équivalentes

```bash
# 1. Générer le lexer/parser dans grammar/
java -cp lib/javacc.jar javacc -OUTPUT_DIRECTORY=grammar grammar/Anonymizer.jj

# 2. Compiler vers out/
javac -encoding UTF-8 -d out grammar/*.java

# 3. Exécuter (instrumentation + analyse syntaxique + anonymisation)
java -Dstdout.encoding=UTF-8 -cp out \
     anonymizer.lexer.AnonymizerParser <fichier_entree> [fichier_sortie]
```

## Ce que fait `AnonymizerParser`

1. **Instrumentation (Partie 8)** — affiche `[TYPE : "lexeme"]` pour chaque token.
2. **Analyse syntaxique (Partie 6)** — valide la suite de tokens via `Document()`.
3. **Anonymisation (démo)** — remplace les tokens sensibles par leurs marqueurs
   `<PERSONNE>`, `<EMAIL>`, `<TELEPHONE>`, `<DATE>`, `<MONTANT>`, et écrit le
   résultat si un fichier de sortie est fourni.

> Classes générées dans le paquetage `anonymizer.lexer` (distinct de
> `anonymizer` afin de ne pas entrer en conflit avec le `Token.java` de M3).
> Le module est ainsi directement réutilisable par l'implémentation de M3
> (Partie 7) via `import anonymizer.lexer.*`.
