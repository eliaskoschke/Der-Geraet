package com.example;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DatenbankController {
    private static ConfigRepository configRepository;

    public DatenbankController(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    /**
     * Schreibt einen Neuen wert mit dem mitgegebenen namen und wert
     * in die Datenbank.
     */
    public static void saveInDatabase(String name, int wert) {
        if (wert != 0) {
           configRepository.save(new Config(name, wert));
        }
    }

    /**
     * Sucht nach einem Wert mit dem mitgegebenen namen
     */
    public static int getValueByName(String name) {
        Optional<Config> config = configRepository.findByName(name);

        return config.map(Config::getWert).orElse(0);
    }

    /**
     * Überschreibt einen wert falls er gefunden wird.
     */
    public static void updateByName(String name, int neuerWert) {
        configRepository.findByName(name).ifPresent(config -> {
            config.setWert(neuerWert);
            configRepository.save(config);
        });
    }

    public static List<Integer> getDefaultValues() {
        List<String> names = List.of("standartVorwaertsGeschwindigkeit", "standartVorwaertsDauer",
                "standartPauseDauer", "standartRueckwaertsGeschwindigkeit", "StandartRueckwaertsDauer");

        List<Integer> values = new ArrayList<>();

        for(String name : names) {
            values.add(getValueByName(name));
        }

        return values;
    }
}
