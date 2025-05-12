package ru.promelectronika.util_stuff;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.promelectronika.dto.ExchangeableData;
import ru.promelectronika.dto.records.EvseDto;
import ru.promelectronika.dto.records.TransactionDto;

import java.io.FileWriter;
import java.io.IOException;

public class JsonFileCreator {

    public static void createEvseJsonFile(ExchangeableData exchangeable, String folderAddress) {
        if (exchangeable instanceof EvseDto dto) {
            try (var fileWriter = new FileWriter(folderAddress + "/" + dto.evseId() + ".json")) {
                String json = new ObjectMapper().writeValueAsString(dto);
                fileWriter.write(json);
                ColorTuner.printCyanText("JSON_FILE_WAS_WRITTEN");
                ColorTuner.printCyanText(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (exchangeable instanceof TransactionDto dto) {
            try (var fileWriter = new FileWriter(folderAddress + "/" + dto.transactionInfo().getTransactionId() + ".json")) {
                String json = new ObjectMapper().writeValueAsString(dto);
                fileWriter.write(json);
                ColorTuner.printCyanText("JSON_FILE_WAS_WRITTEN");
                ColorTuner.printCyanText(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
