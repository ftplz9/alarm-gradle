package com.alarm;

public enum City {

    KIEV("м_Київ"),
    KIEV_OBLAST("Київська_область"),
    VINNITSA_OBLAST("Вінницька_область"),
    DRIPRO_OBLAST("Дніпропетровська_область"),
    ROVNO_OBLAST("Рівненська_область");

    String cityName;

    City(String cityName) {
        this.cityName = cityName;
    }
}
