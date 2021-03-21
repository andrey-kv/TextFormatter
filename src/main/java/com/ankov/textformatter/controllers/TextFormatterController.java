package com.ankov.textformatter.controllers;

import com.ankov.textformatter.exceptions.BadRequestException;
import com.ankov.textformatter.exceptions.SettingNotFoundException;
import com.ankov.textformatter.model.CorrectWordsDto;
import com.ankov.textformatter.model.CorrectWordsResponse;
import com.ankov.textformatter.model.ExceptionResponse;
import com.ankov.textformatter.services.FormatterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@AllArgsConstructor
@Validated
public class TextFormatterController {

    public static final int MAX_CODE = 999;
    public static final int MIN_CODE = 1;
    private final FormatterService formatterService;

    @PostMapping("/tf")
    public String inputText(@RequestBody String text,
                            @RequestParam(value = "code", required = false)
                            @Min(MIN_CODE) @Max(MAX_CODE)
                                    Integer code) {
        return formatterService.inputText(text, code);
    }

    @GetMapping("/text")
    public String getText(@RequestParam(value = "code")
                          @Min(MIN_CODE) @Max(MAX_CODE)
                                  int code) {
        return formatterService.getText(code);
    }


    @PostMapping("/overwrite")
    public String overwriteText(@RequestBody String text,
                                @RequestParam(value = "code")
                                @Min(MIN_CODE) @Max(MAX_CODE)
                                        int code) {
        return formatterService.overwriteText(text, code);
    }

    @PutMapping("/tf")
    public String inputTranslation(@RequestBody String text) {
        return formatterService.inputTranslation(text);
    }

    @PostMapping("/toc")
    public void createToc(@RequestBody String toc) {
        formatterService.createToc(toc);
    }

    @GetMapping("/html")
    public String getHtml(@RequestParam(value = "id", required = false) Integer sectionId) {
        return formatterService.getHtml(List.of(sectionId));
    }

    @PutMapping("/correct")
    public CorrectWordsResponse correct(@RequestBody CorrectWordsDto correctWordsDto) {
        return formatterService.correct(correctWordsDto);
    }

    @GetMapping("/test")
    public List<String> testRep() {
        return formatterService.testRep();
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            SettingNotFoundException.class,
            BadRequestException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse badRequestHandler(Exception ex) {
        return ExceptionResponse.builder()
                .reason(ex.getClass().getName())
                .message(ex.getMessage())
                .build();
    }
}
