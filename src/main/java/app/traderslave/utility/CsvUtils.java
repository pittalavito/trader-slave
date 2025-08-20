package app.traderslave.utility;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@UtilityClass
public class CsvUtils {

    @SneakyThrows
    public <T> List<T> readCsvFile(MultipartFile file, Class<T> type) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();

        } catch (Exception e) {
            //todo create custom exception
            throw new RuntimeException("Error reading CSV file", e);
        }
    }
}
