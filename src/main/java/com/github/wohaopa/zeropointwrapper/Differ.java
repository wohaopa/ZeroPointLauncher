package com.github.wohaopa.zeropointwrapper;

import java.util.*;

public class Differ {

    public static List<Map<String, Long>> diff(Map<String, Long> checksum1, Map<String, Long> checksum2) {
        List<Map<String, Long>> list = new ArrayList<>();

        Map<String, Long> inst1have = new HashMap<>();
        Map<String, Long> inst2have = new HashMap<>();
        list.add(inst1have);
        list.add(inst2have);

        for (Map.Entry<String, Long> entry : checksum1.entrySet()) {
            if (checksum2.containsKey(entry.getKey())
                && Objects.equals(checksum2.get(entry.getKey()), entry.getValue())) {
                continue;
            }
            inst1have.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Long> entry : checksum2.entrySet()) {
            if (checksum1.containsKey(entry.getKey())
                && Objects.equals(checksum1.get(entry.getKey()), entry.getValue())) {
                continue;
            }
            inst2have.put(entry.getKey(), entry.getValue());
        }

        return list;
    }
}
