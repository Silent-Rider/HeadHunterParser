package com.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

public class Parser {

    private static String URL_FORMAT = "https://hh.ru/search/vacancy?text=%s&page=%d";
    static final Logger logger = LogManager.getLogger(Parser.class);
    //Main logic of parsing
    static List<Vacancy> parseHeadhunter(String profession) throws IllegalArgumentException, IOException {
        List<Vacancy> vacancies = new ArrayList<>();
        int page = 0;
        do {
            Document document = getDocument(profession, page);
            Elements els = document.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy vacancy-serp__vacancy_standard_plus");
            if(els.isEmpty()){
                if(page == 0) throw new IllegalArgumentException();
                break;
            }
            for (Element element: els) {
                Element titleEl = element.getElementsByAttributeValue("data-qa", "serp-item__title").getFirst();
                String title = titleEl.text();
                String url = titleEl.attr("href");
                String city = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-address").getFirst().text();
                String company = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-employer").getFirst().text();
                Vacancy vacancy = new Vacancy(title, city, company, url);
                for (Element el : element.getElementsByAttributeValueContaining("class", "compensation")) {
                    if (el.text().contains("Опыт"))
                        vacancy.setExperience(el.text());
                    Elements spanTags = el.getElementsByTag("span");
                    if (!spanTags.isEmpty() && spanTags.getFirst().text().contains("₽"))
                        vacancy.setSalary(spanTags.getFirst().text());
                }
                vacancies.add(vacancy);
            }
            page++;
        } while(true);
        return vacancies;
    }
    //Establishing connection with hh.ru
    private static Document getDocument(String profession, int page) throws IOException {
        Document document;
        try{
            document = Jsoup.connect(String.format(URL_FORMAT, profession, page)).get();
            logger.debug("Connection successfully established");
        } catch (IOException e){
            logger.error("An error occurred while connecting the website");
            throw e;
        }
        return document;
    }
    //Changing URL for searching without/with work experience attribute
    static void changeURL(boolean experience){
        if(experience) URL_FORMAT = "https://hh.ru/search/vacancy?text=%s&page=%d";
        else URL_FORMAT = "https://hh.ru/search/vacancy?text=%s&experience=noExperience&page=%d";
    }
}
