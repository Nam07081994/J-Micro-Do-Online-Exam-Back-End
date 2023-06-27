package com.example.demo.common.file;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class CsvUtil {

	public static <T> List<T> readFileCsv(MultipartFile file, Class<T> clazz) {
		List<T> objectList = new ArrayList<>();

		try (Reader reader = new InputStreamReader(file.getInputStream());
				CSVReader csvReader = new CSVReaderBuilder(reader).build()) {
			CsvToBean<T> csvToBean =
					new CsvToBeanBuilder<T>(csvReader)
							.withType(clazz)
							.withIgnoreLeadingWhiteSpace(true)
							.build();

			objectList = csvToBean.parse();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return objectList;
	}
}
