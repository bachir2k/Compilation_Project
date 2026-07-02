# Projet de Compilation — Anonymisation automatique de documents textuels avec JavaCC

M-1 GLSI — ESP Dakar — 2025-2026
Période : 26/06/2026 → 08/07/2026

## Objectif

Développer avec JavaCC un outil qui analyse un texte et remplace les informations
sensibles par des marqueurs anonymisés : `<PERSONNE>`, `<EMAIL>`, `<TELEPHONE>`,
`<DATE>`, `<MONTANT>`.

## Exemple

Entrée :
```
Monsieur Amadou Diallo est joignable au 77 123 45 67.
Son adresse est amadou.diallo@gmail.com.
Le paiement de 250000 FCFA a été effectué le 15/06/2026.
```

Sortie :
```
<PERSONNE> <PERSONNE> est joignable au <TELEPHONE>.
Son adresse est <EMAIL>.
Le paiement de <MONTANT> a été effectué le <DATE>.
```

## Structure du projet

```
compilation_projet/
├── grammar/            Spécification JavaCC (.jj) — M2
├── src/main/java/anonymizer/   Code source Java généré + implémentation — M3 (+M2)
├── lib/                javacc.jar et dépendances
├── test/
│   ├── input/          Jeux de textes d'entrée pour les tests
│   └── output/         Résultats anonymisés produits par le programme
├── docs/                Rapport technique, regex, automates
├── javacc-5.0.tar.gz    Archive source JavaCC
├── javacc-tutorial.pdf  Support du sujet (§1.4 Text processing)
├── Projet_Compilation_Text_Processing.pdf   Sujet du projet
└── Roadmap_Projet_Compilation.pdf           Planning et répartition des rôles
```

## Équipe

- **Membre 1** — Théorie des langages (Parties 1, 2, 3, 5)
- **Membre 2** — Lexical & syntaxique / JavaCC (Parties 4, 5, 6, 8)
- **Membre 3** — Implémentation, validation & rapport (Parties 7, 9, 10)

## Git

Une branche par membre, fusion quotidienne sur `main`.
