# Utilisation du modèle Mustache pour générer des bundles FHIR

Ce document fournit des instructions sur la façon d'utiliser le modèle Mustache fourni (`declaration.mustache`) pour générer des bundles de déclaration FHIR au format JSON.

## Qu'est-ce que Mustache ?

Mustache est un moteur de modèles simple et sans logique. Il est disponible pour de nombreux langages de programmation. Il fonctionne en développant des balises dans un modèle à l'aide des valeurs fournies dans un hachage ou un objet.

Vous pouvez trouver plus d'informations sur Mustache et ses différentes implémentations sur le site officiel : [https://mustache.github.io/](https://mustache.github.io/)

## Préparation du modèle de données

Le modèle de données pour le modèle Mustache est le même que pour le modèle Freemarker ou velocity. Vous devez créer un modèle de données qui contient les données du bundle FHIR. Le modèle de données peut être un simple objet Java (POJO), une carte ou un objet JSON.

Le modèle `declaration.mustache` s'attend à un modèle de données avec la structure et les variables suivantes. Consultez l'exemple de modèle de données ci-dessous pour un exemple concret.

### Variables racine

*   `practitionerId` : L'ID du praticien qui correspond à l'INPE.
*   `organizationId` : L'ID de l'organisation qui correspond à l'INPE de l'établissement ou au code de l'etablissement s'il ne dispose pas d'INPE.
*   `practitionerRoleId` : L'ID du rôle du praticien.
*   `practitionerRoleSystem` : Le système pour le code de rôle du praticien.
*   `practitionerRoleCode` : Le code pour le rôle du praticien.
*   `patientId` : L'ID du patient.
*   `patientIdentifierSystem` : Le système pour l'identifiant du patient (CIN, )
*   `patientIdentifierValue` : La valeur de l'identifiant du patient.
*   `patientFamilyName` : Le nom de famille du patient.
*   `patientGivenName` : Le prénom du patient.
*   `patientGender` : Le sexe du patient.
*   `patientBirthDate` : La date de naissance du patient.
*   `encounterId` : L'ID de la rencontre.
*   `encounterStatus` : Le statut de la rencontre.
*   `encounterClassSystem` : Le système pour le code de classe de la rencontre.
*   `encounterClassCode` : Le code pour la classe de la rencontre.
*   `encounterClassDisplay` : Le texte d'affichage pour la classe de la rencontre.
*   `encounterStart` : La date et l'heure de début de la rencontre.
*   `encounterEnd` : La date et l'heure de fin de la rencontre.
*   `conditionId` : L'ID de la condition.
*   `conditionClinicalStatusSystem` : Le système pour le code d'état clinique de la condition.
*   `conditionClinicalStatusCode` : Le code pour l'état clinique de la condition.
*   `conditionClinicalStatusDisplay` : Le texte d'affichage pour l'état clinique de la condition.
*   `conditionCodeSystem` : Le système pour le code de la condition.
*   `conditionCode` : Le code de la condition.
*   `conditionCodeDisplay` : Le texte d'affichage pour la condition.

### Listes d'objets

*   `procedures` : Une liste d'objets de procédure. Chaque objet doit avoir :
    *   `id` : L'ID de la procédure.
    *   `codes` : Une liste d'objets de code, chacun avec `system`, `code` et `display`.
*   `medications` : Une liste d'objets de médicament. Chaque objet doit avoir :
    *   `id` : L'ID du médicament.
    *   `codes` : Une liste d'objets de code, chacun avec `system`, `code` et `display`.
    *   `text` : La représentation textuelle du médicament.
*   `medicationAdministrations` : Une liste d'objets d'administration de médicaments. Chaque objet doit avoir :
    *   `id` : L'ID de l'administration.
    *   `status` : Le statut de l'administration.
    *   `medicationReference` : La référence au médicament (par exemple, `med1`).
    *   `occurrenceDateTime` : La date et l'heure de l'administration.
    *   `dosageText` : La description textuelle de la posologie.
*   `immunizations` : Une liste d'objets de vaccination. Chaque objet doit avoir :
    *   `id` : L'ID de la vaccination.
    *   `status` : Le statut de la vaccination.
    *   `vaccineCodeSystem` : Le système pour le code du vaccin.
    *   `vaccineCode` : Le code du vaccin.
    *   `vaccineCodeDisplay` : Le texte d'affichage pour le code du vaccin.
    *   `vaccineCodeText` : La représentation textuelle du vaccin.
    *   `occurrenceDateTime` : La date et l'heure de la vaccination.

### Exemple de modèle de données (JSON)

```json
{
  "practitionerId": "4587621",
  "practitionerFamilyName": "Bahani",
  "practitionerGivenName": "Samir",
  "organizationId": "9872855",
  "organizationName": "Hopital Zaouia",
  "practitionerRoleId": "xyz",
  "practitionerRoleSystem": "http://snomed.info/sct",
  "practitionerRoleCode": "309343006",
  "practitionerRoleDisplay": "Orthopedic surgeon",
  "patientId": "p1214578",
  "patientIdentifierSystem": "http://msps.ma/mrn",
  "patientIdentifierValue": "MRN-INS",
  "patientFamilyName": "Maalouli",
  "patientGivenName": "Firas",
  "patientGender": "female",
  "patientBirthDate": "1990-05-14",
  "encounterId": "enc1",
  "encounterStatus": "completed",
  "encounterClassSystem": "http://terminology.hl7.org/CodeSystem/v3-ActCode",
  "encounterClassCode": "AMB",
  "encounterClassDisplay": "ambulatory",
  "encounterStart": "2025-09-20T09:00:00Z",
  "encounterEnd": "2025-09-20T11:00:00Z",
  "conditionId": "c1",
  "conditionClinicalStatusSystem": "http://terminology.hl7.org/CodeSystem/condition-clinical",
  "conditionClinicalStatusCode": "active",
  "conditionClinicalStatusDisplay": "Active",
  "conditionCodeSystem": "http://id.who.int/icd/release/11/mms",
  "conditionCode": "NC72.30",
  "conditionCodeDisplay": "Fracture of femur",
  "procedures": [
    {
      "id": "proc1",
      "codes": [
        {
          "system": "http://loinc.org",
          "code": "36626-4",
          "display": "Radiology imaging study"
        },
        {
          "system": "NGAP",
          "code": "C23",
          "display": "IRM"
        }
      ]
    },
    {
      "id": "proc2",
      "codes": [
        {
          "system": "http://snomed.info/sct",
          "code": "387713003",
          "display": "Surgical procedure"
        },
        {
          "system": "NGAP",
          "code": "K35",
          "display": "Réduction de fracture"
        }
      ]
    }
  ],
  "medications": [
    {
      "id": "med1",
      "codes": [
        {
          "system": "http://snomed.info/sct",
          "code": "373270004",
          "display": "Non-steroidal anti-inflammatory drug (product)"
        },
        {
          "system": "http://www.whocc.no/atc",
          "code": "M01AE01",
          "display": "Ibuprofen"
        }
      ],
      "text": "Ibuprofen 400mg tablet (Brufen®)"
    }
  ],
  "medicationAdministrations": [
    {
      "id": "ma1",
      "status": "completed",
      "medicationReference": "med1",
      "occurrenceDateTime": "2025-09-22T10:00:00Z",
      "dosageText": "400mg orally, once"
    }
  ],
  "immunizations": [
    {
      "id": "imm1",
      "status": "completed",
      "vaccineCodeSystem": "http://snomed.info/sct",
      "vaccineCode": "1119349007",
      "vaccineCodeDisplay": "Tetanus vaccine",
      "vaccineCodeText": "Tetanus vaccine",
      "occurrenceDateTime": "2023-05-10"
    },
    {
      "id": "imm2",
      "status": "completed",
      "vaccineCodeSystem": "http://snomed.info/sct",
      "vaccineCode": "871751000000103",
      "vaccineCodeDisplay": "Influenza vaccine",
      "vaccineCodeText": "Influenza vaccine",
      "occurrenceDateTime": "2024-11-15"
    }
  ]
}
```

## Rendu du modèle

Une fois que vous avez une bibliothèque Mustache pour votre langue, vous pouvez rendre le modèle avec votre modèle de données pour obtenir la sortie JSON finale.

Voici un exemple de base de la façon de le faire en Java à l'aide de la bibliothèque `com.github.mustachejava` :

Tout d'abord, ajoutez la dépendance à votre `pom.xml` :
```xml
<dependency>
    <groupId>com.github.spullara.mustache.java</groupId>
    <artifactId>compiler</artifactId>
    <version>0.9.10</version>
</dependency>
```

Ensuite, vous pouvez utiliser le code suivant pour rendre le modèle :

```java
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

public class MustacheExample {

    public static void main(String[] args) throws IOException {

        // 1. Créer une usine Mustache
        MustacheFactory mf = new DefaultMustacheFactory();

        // 2. Compiler le modèle
        Mustache mustache = mf.compile("declaration.mustache");

        // 3. Créer le modèle de données (vous le chargeriez généralement à partir d'un fichier ou d'une base de données)
        HashMap<String, Object> dataModel = new HashMap<>();
        // ... remplissez votre modèle de données ici en fonction de la structure décrite ci-dessus ...

        // 4. Rendre le modèle
        StringWriter writer = new StringWriter();
        mustache.execute(writer, dataModel).flush();

        // 5. Imprimer la sortie
        System.out.println(writer.toString());
    }
}
```

Assurez-vous que `declaration.mustache` se trouve dans le classpath ou fournissez le chemin d'accès complet au fichier.

Vous devrez également analyser votre modèle de données JSON dans un `HashMap<String, Object>` si vous utilisez l'exemple ci-dessus.
