# Repository Guidelines

## Project Structure & Module Organization
The CLI lives in `src/main/java/hr/ja/ba`, with `App` bootstrapping Spring Shell and discrete commands such as `BioanalystRootCommand`, `StatusCommand`, and `TaxIdCsvCompact`. Configuration, logging, and sample dataset paths are in `src/main/resources` (`application.properties`, overrides like `application_bioserv.properties`, and `logback-spring.xml`, which writes `bioanalyst.log` to the repo root). Keep future domain modules under `hr.ja.ba.<feature>` and mirror that layout in `src/test/java`, even though the test tree is currently empty.

## Upute
Ovo je spring boot aplikacija koja koristi Spring Shell za izgradnju CLI alata. Slijedi nekoliko smjernica za rad s ovim repozitorijem:
Odgovaraj mi na hrvatskom, kod pisi na engleskom.
Radi se o aplikaciji za bioinformaticke analize, koja moze pokretati taskove tj. workere u pozadini, i pregledavati njihov status.

