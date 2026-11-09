# oreo-test-data

The test data library of the OREO E2E suite (`oreo-e2e`): it loads the case data, the setup data and the recorded
responses of a Genie / Cucumber suite, and its offline tool `TestDataTool` checks them, writes the product `Examples`,
compacts the recordings and reports coverage. The suite depends on it as a Maven artifact; the suite keeps only what
is its own: the product registry and a few settings.

```xml
<dependency>
    <groupId>com.example.oreo</groupId>
    <artifactId>oreo-test-data</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Build

JDK 21, Maven 3.9:

```shell
mvn install
```

runs the unit tests and installs the jar in the local Maven repository, where the suite's build finds it. To share it,
deploy it to the team's Maven repository (`mvn deploy` with its `distributionManagement`). Jackson
(`jackson-databind`) is `provided`: the suite brings its own version.

## What the suite gives the library

### The product registry

The library knows no product. The suite implements `com.example.testdata.ProductRegistry` (its products, each a
`com.example.testdata.Product`: a name, the end of its trade type, the product ID to send, its capability tags; and the
capability groups of the coverage matrix) in a public class with a no-argument constructor, and names that class in
`src/main/resources/META-INF/services/com.example.testdata.ProductRegistry`. Exactly one registry must be on the
classpath. In oreo-e2e it is the enum `ProductDatFiles` (nested class `ProductDatFiles.Registry`); a new product or
capability is a change of the suite only.

### Settings

`src/test/resources/test-data.properties` (optional):

| Key | Meaning |
|---|---|
| `check.knownMissingSetups` | Comma-separated setup names the code uses but that have no `data/setup/<name>.json` yet, by the team's decision |
| `check.setupNameCalls` | A regular expression of Java method names whose string arguments are setup names (`provision\\w*\|triggerEvent`) |

### Layout

The suite keeps the standard layout: `src/test/resources/features/`, `src/test/resources/data/{api,ui,setup,dat}/`,
`src/main/resources/snippet/`, `src/main/java/`. The project root is found from `target/test-classes/data` on the
classpath, or given with `-Doreo.project.dir=<folder>`.

## Running the tool

From the suite, through an `exec-maven-plugin` execution with `classpathScope` `test` and the main class
`com.example.testdata.tools.TestDataTool`:

```text
TestDataTool check | sync-examples | compact <event>|all | suggest <event> | case <case ID>
             | shared <case ID> | unused-steps | coverage [--html]
```

In oreo-e2e: `mvn -Ptest-data process-test-classes "-Dexec.args=<command>"` (`check` by default). The data format and
the commands are documented in oreo-e2e's `docs/test-data.md`.

## Packages

- `com.example.testdata`: data loading (`TestData`, `TestDataRepository`, `CaseId`, `CaseData`, `SetupData`), JSON
  layering and patches, recorded responses (`ExpectedResponses`, `ResponseMasker`, compaction, suggestions), the
  registry interfaces (`Product`, `ProductRegistry`), `ProjectPaths`, `DateUtil`.
- `com.example.testdata.tools`: `TestDataTool` and its commands.

A change to a rule of the data code goes together with its unit test.
