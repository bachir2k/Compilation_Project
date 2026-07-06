# Partie 4 — Analyse lexicale (Membre 2)

> Fichier source : [`grammar/Anonymizer.jj`](../grammar/Anonymizer.jj)
> Généré/compilé/exécuté et validé sur les 5 fichiers de `test/input/`.

## 4.1 Objectif

L'analyse lexicale découpe le texte d'entrée en **tokens** (unités lexicales) et
attribue à chacun un **type**. Sept types utiles sont définis, plus deux types
auxiliaires privés :

| Type | Rôle | Marqueur d'anonymisation |
|------|------|--------------------------|
| `EMAIL`  | Adresse électronique | `<EMAIL>` |
| `PHONE`  | Numéro de téléphone (SN / international) | `<TELEPHONE>` |
| `DATE`   | Date (numérique ou en toutes lettres) | `<DATE>` |
| `AMOUNT` | Montant financier en FCFA | `<MONTANT>` |
| `TITLE`  | Civilité (Monsieur, Madame, M., …) | supprimé |
| `PERSON` | Nom propre (majuscule + minuscules) | `<PERSONNE>` |
| `WORD`   | Mot ordinaire | conservé |
| `NUMBER` | Entier isolé (non date/tél./montant) | conservé |
| `OTHER`  | Espace, ponctuation, saut de ligne | conservé |

> **Choix de conception.** Comme l'outil doit **restituer le texte à l'identique**
> (seules les informations sensibles changent), **aucun caractère n'est ignoré** :
> il n'y a pas de règle `SKIP`. Les espaces, la ponctuation et les retours à la
> ligne sont capturés par le token `OTHER` (règle catch-all `~[]`) et réémis tels
> quels. Le lexer couvre donc **la totalité** du flux d'entrée.

## 4.2 Tokens auxiliaires (privés)

```javacc
< #DIGIT : ["0"-"9"] >
< #MAJ   : [ "A"-"Z", "À","Á","Â","Ã","Ä","Ç","È","É","Ê","Ë", … ] >   // majuscules accentuées
< #MIN   : [ "a"-"z", "à","á","â","ã","ä","ç","è","é","ê","ë", … ] >   // minuscules accentuées
< #LET   : <MAJ> | <MIN> >
< #MONTH : "janvier" | "février" | … | "décembre" >
```

Le préfixe `#` indique un token **privé** : il sert de brique aux autres règles
mais ne produit jamais de token seul. Les classes `MAJ`/`MIN` incluent les
lettres accentuées françaises (é, è, à, ç, ï, û…), ce qui traite la difficulté
« accents » identifiée en Partie 1.3.

## 4.3 Règles lexicales (extraits commentés)

### EMAIL
```javacc
< EMAIL :
    ( <LET> | <DIGIT> | "." | "_" | "%" | "+" | "-" | "'" )+   // partie locale
    "@"
    ( <LET> | <DIGIT> | "-" )+ ( "." ( <LET> | <DIGIT> | "-" )+ )+   // domaine + ≥1 point
>
```
La partie locale autorise les caractères usuels (points, tirets, souligné,
apostrophe pour `o'brien`). Le domaine impose **au moins un point** — d'où la
reconnaissance de `mail.co.uk` (deux points) aussi bien que `gmail.com`.

### PHONE
```javacc
< PHONE :
    ( "+" <DIGIT> <DIGIT> <DIGIT> ( " " )? )?          // indicatif +221 optionnel
    <DIGIT> <DIGIT> ( " " | "-" )
    <DIGIT> <DIGIT> <DIGIT> ( " " | "-" )
    <DIGIT> <DIGIT> ( " " | "-" )
    <DIGIT> <DIGIT>                                    // groupes 2-3-2-2
  | <DIGIT> <DIGIT> <DIGIT> <DIGIT> <DIGIT> <DIGIT> <DIGIT> <DIGIT> <DIGIT>  // 9 chiffres accolés
>
```
Couvre `77 123 45 67`, `33 889 00 00`, `+221 77 654 32 10`, `70-987-65-43`
(séparateur espace **ou** tiret) et `781234567` (accolé).

### DATE
```javacc
< DATE :
    <DIGIT> (<DIGIT>)? ("/"|"-") <DIGIT> (<DIGIT>)? ("/"|"-") <DIGIT><DIGIT><DIGIT><DIGIT>  // JJ/MM/AAAA, JJ-MM-AAAA
  | <DIGIT><DIGIT><DIGIT><DIGIT> ("/"|"-") <DIGIT> (<DIGIT>)? ("/"|"-") <DIGIT> (<DIGIT>)?  // AAAA/MM/JJ
  | <DIGIT> (<DIGIT>)? " " <MONTH> " " <DIGIT><DIGIT><DIGIT><DIGIT>                          // 5 juin 2026
>
```

### AMOUNT
```javacc
< AMOUNT :
    <DIGIT> (<DIGIT>)* ( ( " " | "." ) <DIGIT><DIGIT><DIGIT> )*   // 250000 | 1 500 000 | 75.000
    ( " " )? ( "FCFA" | "F" ( " " )? "CFA" | "CFA" )              // devise OBLIGATOIRE
>
```
La **devise obligatoire** est le point clé : elle distingue un montant d'un
simple nombre ou d'un numéro de téléphone (voir Partie 5, conflit montant/tél.).

### TITLE, PERSON, WORD, NUMBER, OTHER
```javacc
< TITLE  : "Monsieur" | "Madame" | "Mademoiselle" | "Mlle" | "Mme" | "M." | "Dr" | "Docteur" | … >
< PERSON : <MAJ> ( <MIN> )+ >                    // une majuscule suivie de minuscules
< WORD   : <LET> ( <LET> | "'" | "-" )* >        // mot, apostrophe/tiret internes autorisés
< NUMBER : ( <DIGIT> )+ >
< OTHER  : ~[] >                                 // tout autre caractère (catch-all)
```

## 4.4 Résultat d'instrumentation (test1)

Sortie réelle du lexer sur `test/input/test1_exemple_sujet.txt`
(voir [`instrumentation_test1.txt`](instrumentation_test1.txt)) :

```
[TITRE : "Monsieur"]   [PERSONNE : "Amadou"]   [PERSONNE : "Diallo"]
[MOT : "est"] … [TELEPHONE : "77 123 45 67"] [AUTRE : "."]
[PERSONNE : "Son"] … [EMAIL : "amadou.diallo@gmail.com"] [AUTRE : "."]
[PERSONNE : "Le"] … [MONTANT : "250000 FCFA"] … [DATE : "15/06/2026"]
```

Les cinq catégories sensibles sont correctement isolées, ainsi que la civilité
« Monsieur ».
