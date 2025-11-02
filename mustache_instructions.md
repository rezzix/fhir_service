# Using the Mustache Template to Generate FHIR Bundles

This document provides instructions on how to use the provided Mustache template (`declaration.mustache`) to generate FHIR declaration bundles in JSON format.

## What is Mustache?

Mustache is a simple and logic-less template engine. It is available for many programming languages. It works by expanding tags in a template using values provided in a hash or object.

You can find more information about Mustache and its various implementations on the official website: [https://mustache.github.io/](https://mustache.github.io/)

## Preparing the Data Model

The data model for the Mustache template is the same as for the Freemarker template. You need to create a data model that contains the data for the FHIR bundle. The data model can be a simple Java object (POJO), a map, or a JSON object.

The template `declaration.mustache` expects a data model with the following structure and variables. See the example data model below for a concrete example.

### Root Variables

*   `practitionerId`: The ID of the practitioner.
*   `practitionerFamilyName`: The family name of the practitioner.
*   `practitionerGivenName`: The given name of the practitioner.
*   `organizationId`: The ID of the organization.
*   `organizationName`: The name of the organization.
*   `practitionerRoleId`: The ID of the practitioner role.
*   `practitionerRoleSystem`: The system for the practitioner role code.
*   `practitionerRoleCode`: The code for the practitioner role.
*   `practitionerRoleDisplay`: The display text for the practitioner role.
*   `patientId`: The ID of the patient.
*   `patientIdentifierSystem`: The system for the patient identifier.
*   `patientIdentifierValue`: The value of the patient identifier.
*   `patientFamilyName`: The family name of the patient.
*   `patientGivenName`: The given name of the patient.
*   `patientGender`: The gender of the patient.
*   `patientBirthDate`: The birth date of the patient.
*   `encounterId`: The ID of the encounter.
*   `encounterStatus`: The status of the encounter.
*   `encounterClassSystem`: The system for the encounter class code.
*   `encounterClassCode`: The code for the encounter class.
*   `encounterClassDisplay`: The display text for the encounter class.
*   `encounterStart`: The start date and time of the encounter.
*   `encounterEnd`: The end date and time of the encounter.
*   `conditionId`: The ID of the condition.
*   `conditionClinicalStatusSystem`: The system for the condition clinical status code.
*   `conditionClinicalStatusCode`: The code for the condition clinical status.
*   `conditionClinicalStatusDisplay`: The display text for the condition clinical status.
*   `conditionCodeSystem`: The system for the condition code.
*   `conditionCode`: The code for the condition.
*   `conditionCodeDisplay`: The display text for the condition.

### Lists of Objects

*   `procedures`: A list of procedure objects. Each object should have:
    *   `id`: The ID of the procedure.
    *   `codes`: A list of code objects, each with `system`, `code`, and `display`.
*   `medications`: A list of medication objects. Each object should have:
    *   `id`: The ID of the medication.
    *   `codes`: A list of code objects, each with `system`, `code`, and `display`.
    *   `text`: The text representation of the medication.
*   `medicationAdministrations`: A list of medication administration objects. Each object should have:
    *   `id`: The ID of the administration.
    *   `status`: The status of the administration.
    *   `medicationReference`: The reference to the medication (e.g., `med1`).
    *   `occurrenceDateTime`: The date and time of the administration.
    *   `dosageText`: The text description of the dosage.
*   `immunizations`: A list of immunization objects. Each object should have:
    *   `id`: The ID of the immunization.
    *   `status`: The status of the immunization.
    *   `vaccineCodeSystem`: The system for the vaccine code.
    *   `vaccineCode`: The code for the vaccine.
    *   `vaccineCodeDisplay`: The display text for the vaccine code.
    *   `vaccineCodeText`: The text representation of the vaccine.
    *   `occurrenceDateTime`: The date and time of the immunization.

### Example Data Model (JSON)

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

## Rendering the Template

Once you have a Mustache library for your language, you can render the template with your data model to get the final JSON output.

Here is a basic example of how to do this in Java using the `com.github.mustachejava` library:

First, add the dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>com.github.spullara.mustache.java</groupId>
    <artifactId>compiler</artifactId>
    <version>0.9.10</version>
</dependency>
```

Then, you can use the following code to render the template:

```java
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

public class MustacheExample {

    public static void main(String[] args) throws IOException {

        // 1. Create a Mustache factory
        MustacheFactory mf = new DefaultMustacheFactory();

        // 2. Compile the template
        Mustache mustache = mf.compile("declaration.mustache");

        // 3. Create the data model (you would typically load this from a file or database)
        HashMap<String, Object> dataModel = new HashMap<>();
        // ... populate your data model here based on the structure described above ...

        // 4. Render the template
        StringWriter writer = new StringWriter();
        mustache.execute(writer, dataModel).flush();

        // 5. Print the output
        System.out.println(writer.toString());
    }
}
```

Make sure that `declaration.mustache` is in the classpath or provide the full path to the file.

You will also need to parse your JSON data model into a `HashMap<String, Object>` if you are using the example above.
