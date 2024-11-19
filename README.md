[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=bugs)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=coverage)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)

# Polarion ALM extension to analyze WI quality

This Extension provides the possibility to inspect WI descriptions and writes the feedback to the Custom Field "Smell Description" of each inspected WI.
The inspector is implemented in [scripts](https://github.com/SchweizerischeBundesbahnen/python-requirements-inspector/).

## Build

This extension can be produced using maven:
```bash
mvn clean package
```

## Installation to Polarion

To install the extension to Polarion `ch.sbb.polarion.extension.<extension_name>-<version>.jar`
should be copied to `<polarion_home>/polarion/extensions/ch.sbb.polarion.extension.<extension_name>/eclipse/plugins`
It can be done manually or automated using maven build:
```bash
mvn clean install -P install-to-local-polarion
```
For automated installation with maven env variable `POLARION_HOME` should be defined and point to folder where Polarion is installed.

Changes only take effect after restart of Polarion.

## Polarion configuration

### Scheduler

This Extension must be added as Job to the Scheduler like this:

    <job cronExpression="0 0 0 * * ?" id="InspectRequirements" name="[your task name]" scope="project:[your project]">
        <types>[WI Type ID], [WI Type ID], ...</types>
        <addMissingLanguage>[true/false]</addMissingLanguage>
        <filter>[additional filter query]</filter>
        <inspectTitle>[true/false]</inspectTitle>
        <inspectFields>[field ID], [field ID], ...</inspectFields>
    </job>

All values in brackets needs to be changed to your use case.

The following Parameters are available:

- **types**: Comma-seperated type ids of workitems to be inspected.
- **addMissingLanguage** (Optional): If set to true it updates the language field if it wasn't set and the plugin could detect the language. default false.
- **filter** (Optional): An filter query that will be added with AND to the existing query to filter for specific workitems.
- **inspectTitle** (Optional): If set to true the plugin will inspect the title (currently only Processword check) and doesn't check if set to false. If not defined the default value is true.
- **inspectFields** (Optional): Comma-seperated ids of customfields that should be inspected. If not defined the default is empty.

## Extension Configuration

### python_requirements_inspector as Service

This extension supports using `python_requirements_inspector` as a service. More details can be found [here](https://github.com/SchweizerischeBundesbahnen/polarion-requirements-inspector-service/).
This feature can be used by setting the following property in file `<POLARION_HOME>/etc/polarion.properties`:

```properties
ch.sbb.polarion.extension.requirements-inspector.requirements.inspector.service=http://host.docker.internal:9081
```

NOTE: If the requirements inspector service is running on the host machine's localhost, the following option can be added to docker polarion run:

```bash
docker run ... --add-host host.docker.internal:host-gateway ...
```

The service will be available under `http://host.docker.internal:9081` in the container as shown in the property above.

## Usage

### Custom Fields

The Workitems types that shall be inspected need Custom Fields for the results. If not defined beforehand they will be added as String Fields after inspection and can be defined with the correct types afterwards. Following Custom Fields can
be added:

| ID                 | Name                | Type    |
|--------------------|---------------------|---------|
| smellDescription   | Smell Description   | Text    |
| smellPassive       | Smell Passive       | Integer |
| smellComplex       | Smell Complex       | Integer |
| smellComparative   | Smell Comparative   | Integer |
| smellWeakword      | Smell Weakword      | Integer |
| missingProcessword | Missing Processword | Boolean |

Also, the Language field should exist.

Wait for your scheduled time or start it manually by:

1. Open Monitor
2. Select the Task with `[your task name]`
3. Press Button "Execute now"

## Changelog

| Version | Changes                                                                                                            |
|---------|--------------------------------------------------------------------------------------------------------------------|
| v3.0.0  | Renamed quality_analysis to requirements_inspector                                                                 |
| v2.1.0  | Usage of the new @Discoverable annotation to render about page information                                         |
| v2.0.1  | Added polarion_quality_analysis_service to qualityanalysis.model.QualityAnalysisVersion                            |
| v2.0.0  | Removed feature to run quality analysis through cli                                                                |
| v1.6.1  | Added missing new configuration fields to about page                                                               |
| v1.6.0  | About page help now is generating based on README.md. Added support for python_requirements_inspector as a Service |
| v1.5.1  | Configuration properties in about page                                                                             |
| v1.5.0  | Migration to generic extension                                                                                     |
