# Ars-Elemental
Add-on to Ars Nouveau, based the elemental schools.

https://www.curseforge.com/minecraft/mc-mods/ars-elemental

Translations on Crowdin:
https://crowdin.com/project/ars-elemental

License: This mod is licensed under LGPL V3.
Every push to this repository is built and published to the [BlameJared](https://maven.blamejared.com) maven, to use
these builds in your project, simply add the following code in your build.gradle

```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    implementation ("com.alexthw.ars_elemental:ars_elemental-[MC_VERSION]:[VERSION]") {transitive=false}
}
```

Current version (1.21.1):
[![Maven](https://img.shields.io/maven-metadata/v?label=&color=C71A36&metadataUrl=https%3A%2F%2Fmaven.blamejared.com%2Fcom%2Falexthw%2Fars_elemental%2Fars_elemental-1.21.1%2Fmaven-metadata.xml&style=flat-square)](https://maven.blamejared.com/com/alexthw/ars_elemental/ars_elemental-1.21.1/)

(remove the v)
