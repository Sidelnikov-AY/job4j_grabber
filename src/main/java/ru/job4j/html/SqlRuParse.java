package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.utils.SqlRuDateTimeParser;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        for (int page = 1; page <= 5; page++) {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + page).get();
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            Element parent = td.parent();
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
            System.out.println(timeParser.parse(parent.child(5).text()));
        }
        }
    }
}