package com.alarm.dto;


import com.alarm.City;

public class AlarmDto {
    long id;
    String text;
    String city;
    int time;

    @Override
    public String toString() {
        return "AlarmDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", city=" + city +
                ", time=" + time +
                '}';
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public AlarmDto(long id, String text, String city, int time) {
        this.id = id;
        this.text = text;
        this.city = city;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AlarmDto() {
    }

}
