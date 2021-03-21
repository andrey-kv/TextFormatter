package com.ankov.textformatter.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "content")
@Data
@Accessors(chain = true)
public class TextContent {

    @Id
    private Integer id;

    private String text;
    @Column(name = "text_size")
    private int textSize;
    private String translate;

    @Column(name = "translate_size")
    private int translateSize;
}
