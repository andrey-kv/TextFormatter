package com.ankov.textformatter.model;

import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Data
@Table(name = "toc")
public class Toc {

    public Toc() { }

    public Toc(String toc, int parentCode) {
        if (toc.matches("Section(\\s+)(\\d{1,2}):\\s.*")) {
            this.tocType = TocType.SECTION;
            this.code = extractSectionCode(toc);
        } else if (toc.matches("(\\d{1,2})\\.\\s[A-Z|0-9].+")) {
            this.tocType = TocType.HEADER;
            this.code = extractHeaderCode(toc);
            this.parentCode = parentCode;
        } else if (toc.matches("Quiz\\s\\d{1,2}:\\s.+")) {
            this.tocType = TocType.QUIZ;
        } else {
            this.tocType = TocType.UNDEFINED;
        }
        this.id = this.tocType.getValue() * 1000 + this.code;
        this.isActive = true;
        this.item = toc;
    }

    private int extractHeaderCode(String toc) {
        Pattern pattern = Pattern.compile("^(.*?)\\.\\s");
        Matcher matcher = pattern.matcher(toc);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new RuntimeException("Header number dit not found for text: " + toc);
    }

    private int extractSectionCode(String toc) {
        Pattern pattern = Pattern.compile("Section (.*?):");
        Matcher matcher = pattern.matcher(toc);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new RuntimeException("Section number dit not found for text: " + toc);
    }

    @Id
    private int id;

    @Column(columnDefinition = "code", nullable = false)
    private int code;

    @Column(columnDefinition = "parent_code")
    private int parentCode;

    @Column(columnDefinition = "toc_type")
    private TocType tocType;

    @Column(columnDefinition = "is_active")
    private boolean isActive;

    @Column(columnDefinition = "item")
    private String item;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "code", insertable=false, updatable=false)
    private TextContent textContent;
}
