package hr.ja.ba;


import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.*;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvReaderExample {

    public static void main(String[] args) throws Exception {
        String input = "/Users/tag/IdeaProjects/BioAnalyst/misc/taxid_peptide_sorted_1000000.csv";
        String output = "/Users/tag/IdeaProjects/BioAnalyst/misc/taxid_count_peptide_sorted_1000000.csv";

        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(settings);
        parser.beginParsing(new FileReader(input));

        PrintWriter out = new PrintWriter(new FileWriter(output));

        // zaglavlje output CSV-a
        out.println("tax_id,peptides");

        String currentPeptide = null;
        int currentCount = 0;

        Integer currentTax = null;
        List<String> peptideList = new ArrayList<>();

        Record row;

        while ((row = parser.parseNextRecord()) != null) {

            int tax = row.getInt("tax_id");
            String pep = row.getString("peptide");

            if (currentTax == null) {
                currentTax = tax;
                currentPeptide = pep;
                currentCount = 1;
                continue;
            }

            // Ako je isti tax_id
            if (tax == currentTax) {

                // Ako je isti peptid → povećaj count
                if (pep.equals(currentPeptide)) {
                    currentCount++;
                } else {
                    if (currentCount == 1) {
                        peptideList.add(currentPeptide);
                    } else {
                        // novi peptide → dodaj prethodni u listu
                        peptideList.add(currentCount + ":" + currentPeptide);
                        log.info("TaxID {} Peptid {} Count {}", currentTax, currentPeptide, currentCount);
                    }
                    // resetiraj brojač
                    currentPeptide = pep;
                    currentCount = 1;
                }

            } else {
                // tax_id se promijenio → zapiši grupu u output CSV

                peptideList.add(currentCount + ":" + currentPeptide);

                String joined = String.join(";", peptideList);

                out.println(currentTax + "," + joined);

                // reset za novi tax_id
                peptideList.clear();
                currentTax = tax;
                currentPeptide = pep;
                currentCount = 1;
            }
        }

        // zadnji tax_id nakon završetka
        if (currentTax != null) {
            peptideList.add(currentCount + ":" + currentPeptide);
            out.println(currentTax + "," + String.join(";", peptideList));
        }

        out.close();
        parser.stopParsing();
    }
}
