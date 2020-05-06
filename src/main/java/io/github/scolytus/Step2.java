package io.github.scolytus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.scolytus.data.AllData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class Step2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Step2.class);

    private AllData allData = null;

    public Step2() {
        initAllData();
    }

    public void run() {

    }

    private void initAllData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            allData = mapper.readValue(Paths.get("step1.json").toFile(), AllData.class);
        } catch (IOException e) {
            LOGGER.error("Can't read Data", e);
            throw new IllegalStateException("Can't read Data", e);
        }
    }
}
