package com.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vacancy implements Comparable<Vacancy> {

    private final String title;
    private String salary = "Доход не указан";
    private final String city;
    private final String companyName;
    private String experience = "Без опыта";
    private final String url;

    public Vacancy(String title, String city, String companyName, String url) {
        this.title = title;
        this.city = city;
        this.companyName = companyName;
        this.url = url;
    }

    void setSalary(String salary) {
        this.salary = salary;
    }

    void setExperience(String experience) {
        this.experience = experience;
    }

    @Override
    public String toString(){
        return "  " + title + "\n  "
                + city + "\n  " +
                companyName + "\n  " +
                experience + "\n  " +
                salary + "\n  " + url + "\n";
    }

    @Override
    public int compareTo(Vacancy o) {
        return income()-o.income();
    }

    public int income(){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(salary);
        int income = 0;
        if(matcher.find()){
            income = Integer.parseInt(matcher.group());
            if(matcher.find()){
                int second = Integer.parseInt(matcher.group());
                income = (second + income)/2;
            }
        }
        return income;
    }
}
