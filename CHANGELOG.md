# Changelog — Droid Inspector X

## 0.6.0 — File Explorer (Phase 6)

- Nouveau module File Explorer accessible depuis « Plus ».
- Navigation dans les dossiers via le Storage Access Framework (SAF) et le sélecteur officiel Android.
- Recherche dans le dossier courant, tri par nom, taille, date et type.
- Affichage optionnel des fichiers cachés.
- Détails techniques : nom, type MIME, taille, date de modification, URI et capacités de lecture/écriture.
- Ouverture avec une application compatible, renommage et suppression lorsque le fournisseur l'autorise.
- Conservation de l'accès persistant lorsque le fournisseur Android le permet.
- Aucune permission globale de stockage ni permission INTERNET ajoutée.


## 0.5.0 — Adaptive UI & Emerald Frosted Glass

- Refonte du tableau de bord : hiérarchie visuelle, cartes émeraude, jauges animées et raccourcis de diagnostic.
- Interface adaptative : navigation inférieure sur les fenêtres compactes et navigation latérale sur les fenêtres de 600 dp et plus.
- Contenu centré avec largeur maximale pour éviter les lignes trop longues sur tablettes et grands écrans.
- Navigation principale alignée sur Dashboard, Hardware, Batterie, Capteurs et Stockage.
- Ajout d'un accès direct à Applications, Système, Rapports et À propos depuis le tableau de bord.
- Signature officielle harmonisée : `@Bienvenu`.
- Suppression des permissions d'état réseau/Wi-Fi non nécessaires ; aucune permission INTERNET.
- CI : acceptation automatisée des licences SDK, tests unitaires ciblés sur `:app`, builds Debug et Release.

## 0.4.0 — Feature completion

- Inventaire réel des capteurs, applications installées et informations système.
- Export PDF local via Storage Access Framework.
- Navigation vers les modules d'inspection et informations À propos.

## 0.3.0 — Phase 3

- Base de l'application Android native et modules de diagnostic initiaux.
