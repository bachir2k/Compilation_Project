# Partie 8 — Instrumentation (Membre 2)

> Méthode `instrument(...)` de [`grammar/Anonymizer.jj`](../grammar/Anonymizer.jj).
> Capture réelle : [`instrumentation_test1.txt`](instrumentation_test1.txt).

## 8.1 Objectif

L'instrumentation rend **visible** le travail de l'analyseur lexical : pour
chaque token reconnu, elle affiche son **type** et son **lexème**. Elle sert à
vérifier la classification et à documenter le fonctionnement du lexer.

## 8.2 Mécanisme

Le token manager généré par JavaCC est appelé directement, token par token,
jusqu'à `EOF` :

```java
AnonymizerParserTokenManager lexer = newLexer(texte);
Token t;
while ((t = lexer.getNextToken()).kind != EOF) {
    if (t.image.trim().isEmpty()) continue;          // on n'affiche pas les blancs
    System.out.println("  [" + nomType(t.kind) + " : \"" + t.image + "\"]");
}
```

`nomType(kind)` traduit la constante de type en libellé lisible
(`EMAIL → EMAIL`, `PHONE → TELEPHONE`, `PERSON → PERSONNE`, `WORD → MOT`,
`OTHER → AUTRE`, …). Les tokens purement blancs (espaces, retours à la ligne)
sont produits par le lexer mais **non listés**, pour la lisibilité.

## 8.3 Exécution

```bash
javac -d out grammar/*.java
java -Dstdout.encoding=UTF-8 -cp out anonymizer.lexer.AnonymizerParser test/input/test1_exemple_sujet.txt
```

> L'option `-Dstdout.encoding=UTF-8` garantit l'affichage correct des accents
> (é, è, à…) quelle que soit la page de code de la console.

## 8.4 Sortie obtenue (test1)

```
=== Analyse lexicale — instrumentation (type : lexeme) ===
Fichier : test1_exemple_sujet.txt
  [TITRE : "Monsieur"]
  [PERSONNE : "Amadou"]
  [PERSONNE : "Diallo"]
  [MOT : "est"]
  [MOT : "joignable"]
  [MOT : "au"]
  [TELEPHONE : "77 123 45 67"]
  [AUTRE : "."]
  [PERSONNE : "Son"]
  [MOT : "adresse"]
  [MOT : "est"]
  [EMAIL : "amadou.diallo@gmail.com"]
  [AUTRE : "."]
  [PERSONNE : "Le"]
  [MOT : "paiement"]
  [MOT : "de"]
  [MONTANT : "250000 FCFA"]
  [MOT : "a"]
  [MOT : "été"]
  [MOT : "effectué"]
  [MOT : "le"]
  [DATE : "15/06/2026"]
  [AUTRE : "."]
Total : 23 tokens significatifs.
```

## 8.5 Lecture des résultats

- Les cinq catégories sensibles sont isolées avec le **bon type** :
  `TELEPHONE` (« 77 123 45 67 »), `EMAIL` (« amadou.diallo@gmail.com »),
  `MONTANT` (« 250000 FCFA », devise incluse), `DATE` (« 15/06/2026 »).
- La civilité « Monsieur » est reconnue comme `TITRE` (distincte d'un nom propre).
- « Son » et « Le » sont classés `PERSONNE` par le lexer (majuscule initiale) :
  c'est l'**ambiguïté de tête de phrase** (Partie 5), levée à l'anonymisation par
  le lexique de mots outils — ils sont finalement **conservés** dans le texte de
  sortie.

L'instrumentation confirme donc à la fois le bon fonctionnement du lexer et la
nature exacte des ambiguïtés à traiter en aval.
