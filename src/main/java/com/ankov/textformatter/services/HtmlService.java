package com.ankov.textformatter.services;

import com.ankov.textformatter.model.Toc;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HtmlService {

    public String assembleHtml(List<Toc> headers, List<Toc> texts) {
        String html = "";

        for(Toc text : texts) {
            String section = "";
            if (text.getTextContent() != null) {
                List<String> original = text.getTextContent().getText().lines().collect(Collectors.toList());
                List<String> translated = text.getTextContent().getTranslate().lines().collect(Collectors.toList());
                section = generateHtml(original, translated);
            }
            String header = wrapWithTag(text.getItem(), "h3");
            html += header + section;
        }
        return wrapWithTag(headers.get(0).getItem(), "h1") + html;
    }

    public String addHeader(String body) {
        body = wrapWithTag(body, "body");
        String style = wrapWithTag(getStyle(), "type=\"text/css\"", "style");
        String head  = wrapWithTag(style, "head");
        return wrapWithTag(head + body, "html");
    }

    private String getStyle() {
        return "td {vertical-align: top;} tr {vertical-align: top;}";
    }

    private String generateHtml(List<String> original, List<String> translated) {
        String rows = "";
        for (int i=0; i< original.size(); i++) {
            String rowOrig = original.get(i);
            String rowTran = translated.get(i);
            rows += addRow(rowOrig, rowTran);
        }
        return wrapWithTag(rows, "table");
    }

    private String wrapWithTag(String text, String attribute, String tag) {
        return "<" + tag + " " + attribute + ">" + text + "</" + tag + ">";
    }

    private String wrapWithTag(String text, String tag) {
        return "<" + tag + ">" + text + "</" + tag + ">";
    }

    private String addRow(String rowOrig, String rowTran) {
        String row = wrapWithTag(rowOrig, "td") + wrapWithTag(rowTran, "td");
        return wrapWithTag(row, "tr");
    }

}
