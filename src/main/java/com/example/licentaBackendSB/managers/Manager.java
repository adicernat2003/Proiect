package com.example.licentaBackendSB.managers;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class Manager {

    public String getRandomRoomNumber(String numeCamin) {
        int roomNum = new Random().nextInt(9999 - 1000) + 1000;
        return numeCamin.toUpperCase() + "-" + roomNum;
    }

    public List<Integer> getListOfYears(String anUniversitar) {
        return IntStream.rangeClosed(2021, 2099).boxed()
                .filter(an -> an != Integer.parseInt(anUniversitar)).toList();
    }
}
