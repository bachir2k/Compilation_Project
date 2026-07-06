# Partie 5 — Principe du Maximal Munch (Membre 1 + Membre 2)

> Volet Membre 2 : comportement du Maximal Munch **dans la spécification JavaCC**
> et conflits lexicaux effectivement rencontrés dans `grammar/Anonymizer.jj`.

## 5.1 Le principe

Le **Maximal Munch** (« plus longue correspondance ») est la règle qu'applique
l'analyseur lexical généré par JavaCC :

1. **Longueur maximale** — à une position donnée, le lexer choisit le token qui
   consomme **le plus grand nombre de caractères**.
2. **Ordre de déclaration** — en cas d'**égalité de longueur** entre deux règles,
   JavaCC retient celle **déclarée en premier** dans le fichier `.jj`.

C'est pourquoi l'ordre des tokens dans `Anonymizer.jj` est significatif :

```
EMAIL, PHONE, DATE, AMOUNT, TITLE, PERSON, WORD, NUMBER, OTHER
```

## 5.2 Étude de chaînes d'entrée

### (a) `Amadou` — conflit PERSON / WORD (égalité de longueur)
`< PERSON : <MAJ>(<MIN>)+ >` et `< WORD : <LET>(<LET>|'|-)* >` reconnaissent
**tous deux** « Amadou » (6 caractères). Égalité → **PERSON gagne car déclaré
avant WORD**. Un mot capitalisé est donc, par défaut, un nom propre.

### (b) `Aujourd'hui` — le Maximal Munch prime sur l'ordre
- `PERSON` s'arrête à l'apostrophe : il ne reconnaît que « Aujourd » (7 car.).
- `WORD` inclut l'apostrophe : il reconnaît « Aujourd'hui » (11 car.).

11 > 7 : **WORD l'emporte par la longueur**, bien que PERSON soit déclaré avant.
Effet de bord bénéfique : `d'Afrique`, `l'Ouest`, `l'Université`, `rendez-vous`
restent des `WORD` (conservés) grâce à l'« effet de colle » de l'apostrophe et
du tiret.

### (c) `250000 FCFA` — conflit MONTANT / NOMBRE / TELEPHONE
- `NUMBER` reconnaît « 250000 » (6 car.).
- `AMOUNT` reconnaît « 250000 FCFA » (11 car.) grâce à la **devise obligatoire**.

11 > 6 : **AMOUNT gagne**. Sans la devise, « 250000 » retomberait sur `NUMBER`.
Le même mécanisme évite qu'un montant soit pris pour un téléphone.

### (d) `77 123 45 67` — conflit TELEPHONE / MONTANT
- `AMOUNT` tente « 77 » puis un groupe de milliers « 123 » → « 77 123 », mais
  échoue ensuite (pas de groupe de 3 chiffres, **pas de devise**) : rejeté.
- `PHONE` reconnaît la totalité « 77 123 45 67 » (11 car., motif 2-3-2-2).

**PHONE gagne** : la devise obligatoire de AMOUNT et le motif de groupes du
téléphone se départagent sans ambiguïté.

### (e) `781234567` — conflit TELEPHONE / NOMBRE (égalité de longueur)
Les deux reconnaissent 9 chiffres (9 car.). Égalité → **PHONE gagne car déclaré
avant NUMBER**.

### (f) `Élève` (début de phrase) — conflit PERSON / WORD (égalité)
« Élève » (5 car.) : PERSON et WORD sont à égalité → PERSON gagne (ordre). Le
lexer le classe donc `PERSON`. L'ambiguïté **sémantique** « nom propre vs mot
en tête de phrase » n'est pas résoluble au niveau lexical : elle est traitée en
aval (§5.4).

## 5.3 Conflits lexicaux et leur résolution — synthèse

| Chaîne | Règles en concurrence | Résolution | Mécanisme |
|--------|----------------------|-----------|-----------|
| `Amadou` | PERSON vs WORD | PERSON | Ordre (égalité) |
| `Aujourd'hui` | PERSON vs WORD | WORD | **Longueur** |
| `250000 FCFA` | AMOUNT vs NUMBER | AMOUNT | Longueur (devise) |
| `77 123 45 67` | PHONE vs AMOUNT | PHONE | Motif + devise |
| `781234567` | PHONE vs NUMBER | PHONE | Ordre (égalité) |
| `15/06/2026` | DATE vs NUMBER | DATE | Longueur |
| `Élève` | PERSON vs WORD | PERSON | Ordre (égalité) |

## 5.4 Ambiguïté résiduelle : la majuscule en début de phrase

Le Maximal Munch ne peut **pas** distinguer un vrai nom propre (`Awa`) d'un mot
outil capitalisé en tête de phrase (`Le`, `Son`, `Numéro`, `Bienvenue`, `Élève`).
Cette ambiguïté est **contextuelle**, donc hors de portée du lexer.

Elle est levée en aval, à l'anonymisation, par une heuristique combinant :

- **la position** : un `PERSON` en tête de phrase est *candidat* à la conservation ;
- **un lexique de mots outils** (`MOTS_OUTILS` dans `Anonymizer.jj`) : le token
  n'est conservé que s'il figure dans cette liste (`le`, `la`, `son`, `pour`,
  `numéro`, `bienvenue`, `élève`…).

Ainsi :
- `Le`, `Son`, `Numéro`, `Bienvenue`, `Élève` (tête de phrase + mot outil) → **conservés** ;
- `Awa` (tête de phrase mais **absent** du lexique) → anonymisé `<PERSONNE>` — correct, c'est un prénom ;
- `Amadou`, `Diallo` (hors tête de phrase) → anonymisés.

**Limites connues** (à discuter en Partie 10) : un nom propre situé en milieu de
phrase mais désignant un lieu (`Dakar`, `Sénégal`) est classé `<PERSONNE>` ; un
nom propre rare en tête de phrase absent du lexique est correctement anonymisé,
mais un mot outil rare absent du lexique serait, lui, anonymisé à tort.
