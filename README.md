# DROID INSPECTOR X
### Android System Intelligence & Hardware Diagnostics
**Développé par @Bienvenu**

Droid Inspector X est une application Android native d'inspection et de diagnostic informatif. Elle privilégie les données réellement exposées par Android, le traitement local et une interface Jetpack Compose.

## Fonctionnalités

- **Dashboard** : aperçu des ressources et état général du terminal.
- **Hardware Explorer** : SoC, architecture CPU, cœurs, mémoire, écran, ABI et OpenGL ES. Les fréquences non exposées par le noyau sont signalées comme indisponibles.
- **Battery Monitor** : niveau, état, santé, température, tension et mesures complémentaires lorsque le fabricant les expose.
- **Sensor Lab** : inventaire des capteurs détectés et lecture en direct du capteur sélectionné.
- **App Manager** : recherche et inventaire des applications visibles pour l'application, version, SDK cible, dates et permissions déclarées.
- **Storage Analyzer** : volumes et espace disponible.
- **System Inspector** : identité du terminal, version Android, build, correctif de sécurité, SELinux et indicateurs système accessibles.
- **Report Center** : export local d'un rapport PDF via le sélecteur de fichiers Android.
- **Navigation adaptative** : barre inférieure sur téléphone compact, rail latéral sur les écrans d'au moins 600 dp.
- **Dashboard adaptatif** : jauges et raccourcis réorganisés selon la largeur, contenu centré sur tablette et grand écran.

## Architecture

- Kotlin 2.0.21, Jetpack Compose et Material 3
- Clean Architecture / MVVM
- Kotlin Coroutines & StateFlow
- Navigation Compose
- Dagger Hilt
- Room et DataStore disponibles pour l'évolution du stockage local
- Gradle Kotlin DSL et GitHub Actions

## Confidentialité et limites Android

Les mesures sont effectuées localement. L'application ne demande pas l'accès Internet. Android limite volontairement les informations visibles par les applications ordinaires : certaines fréquences CPU, informations de batterie, packages installés ou propriétés système peuvent donc être indisponibles. Une valeur non exposée n'est jamais remplacée par une donnée inventée.

Les indicateurs SELinux/root sont informatifs et ne constituent pas une certification de sécurité. L'inventaire d'applications respecte les règles de visibilité des packages Android.

## Compilation

Prérequis : JDK 17 et Android SDK Platform 35.

```bash
chmod +x gradlew
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

APK debug : `app/build/outputs/apk/debug/app-debug.apk`.

## Version du code source

- Version applicative : **0.5.0**
- Signature officielle : **@Bienvenu**
- Évolution : refonte visuelle Emerald Frosted Glass, navigation adaptative et tableau de bord responsive.
- CI : tests unitaires du module `app`, compilation Debug et Release, publication des APK comme Artifacts GitHub.

© @Bienvenu — DROID INSPECTOR X

## Interface adaptative

- Fenêtre compacte (moins de 600 dp) : navigation inférieure à cinq destinations.
- Fenêtre étendue (600 dp et plus) : navigation latérale.
- Contenu limité à 1180 dp et centré sur les grandes fenêtres.
- Le tableau de bord passe de deux cartes de ressources à trois cartes sur les largeurs étendues ; les raccourcis passent de deux à trois colonnes.

## Signature Release (facultative)

La CI produit toujours un APK Debug installable pour les tests. Pour signer aussi l'APK Release, configure les secrets du dépôt GitHub :

- `KEYSTORE_BASE64` : fichier keystore encodé en Base64.
- `KEY_ALIAS` : alias de la clé.
- `KEYSTORE_PASSWORD` : mot de passe du keystore.
- `KEY_PASSWORD` : mot de passe de la clé.

Sans ces secrets, le build Release est généré sans signature personnalisée. Ne place jamais le keystore ni ses mots de passe dans le dépôt Git.
