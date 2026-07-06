# Partie 6 — Analyse syntaxique (Membre 2)

> Fichier source : [`grammar/Anonymizer.jj`](../grammar/Anonymizer.jj) (productions `Document`, `Element`, `PersonName`).

## 6.1 Objectif

Après le découpage lexical (Partie 4), l'analyse syntaxique vérifie que la
**suite de tokens** produite forme un document valide, et **structure** cette
suite. Le texte à anonymiser étant libre, la grammaire doit accepter **toute**
séquence de tokens ; son intérêt est surtout de **regrouper** les noms propres
consécutifs (éventuellement précédés d'une civilité) en une entité de plus haut
niveau : `PersonName`.

## 6.2 Grammaire (règles de production)

```
Document   ::= ( Element )* <EOF>

Element    ::= <EMAIL>
             | <PHONE>
             | <DATE>
             | <AMOUNT>
             | PersonName
             | <WORD>
             | <NUMBER>
             | <OTHER>

PersonName ::= <TITLE> <PERSON> ( <PERSON> )*     (civilité + nom composé)
             | <PERSON> ( <PERSON> )*             (nom composé)
             | <TITLE>                            (civilité isolée)
```

- **`Document`** est l'axiome : une répétition d'éléments jusqu'à la fin de
  fichier (`<EOF>`).
- **`Element`** dérive soit un token sensible, soit un groupe `PersonName`, soit
  un token neutre. Ses alternatives sont distinguées par leur **premier token**
  (grammaire LL(1)).
- **`PersonName`** capture un **nom composé** (`Cheikh Anta Diop` = 3 `PERSON`
  consécutifs) et absorbe une **civilité** de tête (`Monsieur Amadou Diallo`).

## 6.3 Traduction JavaCC

```javacc
void Document() : {}
{ ( Element() )* <EOF> }

void Element() : {}
{
    <EMAIL> | <PHONE> | <DATE> | <AMOUNT>
  | PersonName()
  | <WORD> | <NUMBER> | <OTHER>
}

void PersonName() : {}
{
    LOOKAHEAD( <TITLE> <PERSON> ) <TITLE> <PERSON> ( LOOKAHEAD(1) <PERSON> )*
  | <PERSON> ( LOOKAHEAD(1) <PERSON> )*
  | <TITLE>
}
```

### Rôle des `LOOKAHEAD`
- `LOOKAHEAD( <TITLE> <PERSON> )` : **lookahead syntaxique** qui départage
  « civilité **suivie** d'un nom » (alt. 1) de « civilité **isolée** » (alt. 3) —
  les deux commencent par `<TITLE>`.
- `LOOKAHEAD(1) <PERSON>` dans les `(…)*` : rend **explicite** le choix glouton
  (« continuer à empiler les noms propres du nom composé »). Sans lui, JavaCC
  émet un avertissement de conflit de choix ; avec, la grammaire **compile sans
  aucun avertissement**.

## 6.4 Exemples de dérivation

**« Monsieur Amadou Diallo est … »**
```
Element ⇒ PersonName ⇒ <TITLE:Monsieur> <PERSON:Amadou> <PERSON:Diallo>
Element ⇒ <OTHER:" "> … Element ⇒ <WORD:est> …
```
Une seule entité `PersonName` couvre « Monsieur Amadou Diallo ».

**« Cheikh Anta Diop »** (nom composé sans civilité)
```
Element ⇒ PersonName ⇒ <PERSON:Cheikh> <PERSON:Anta> <PERSON:Diop>
```

## 6.5 Validation

La méthode `main` de `AnonymizerParser` appelle `Document()` sur chaque fichier
de test et affiche :

```
=== Analyse syntaxique : OK (suite de tokens valide) ===
```

confirmant que la suite de tokens produite par le lexer est **acceptée** par la
grammaire pour les 5 fichiers de `test/input/`.
