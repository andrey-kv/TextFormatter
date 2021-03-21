package com.ankov.textformatter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "settings")
@NoArgsConstructor
@AllArgsConstructor
public class Setting {

    @Id
    private String key;
    private String value;

}
