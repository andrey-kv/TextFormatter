package com.ankov.textformatter.services;

import com.ankov.textformatter.exceptions.BadRequestException;
import com.ankov.textformatter.exceptions.ResourceNotFoundException;
import com.ankov.textformatter.model.*;
import com.ankov.textformatter.repositories.ContentRepository;
import com.ankov.textformatter.repositories.TocRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FormatterService {

    public static final String CR2 = "\n\n";
    public static final String CR = "\n";
    private static final String LAST_ADDED_CODE = "lastAddedCode";
    private static final String SETTING_ERROR = "Please provide resource code, because stored code was not found.";
    private static final String RESOURCE_ALREADY_EXISTS = "Resource with code %d already exists.";

    public static final int MAX_LENGTH = 4999;

    private final ContentRepository repository;
    private final TocRepository tocRepository;
    private final SettingService settingService;
    private final HtmlService htmlService;

    @Transactional
    public String inputText(String text, Integer code) {
        if (null == code) {
            code = getCodeFromSettings();
        }
        if (repository.existsById(code)) {
            throw new BadRequestException(String.format(RESOURCE_ALREADY_EXISTS, code));
        }
        settingService.storeValue(LAST_ADDED_CODE, code.toString());
        return overwriteText(text, code);
    }

    private Integer getCodeFromSettings() {
        Integer code;
        String settingCode = settingService.getValue(LAST_ADDED_CODE, SETTING_ERROR);
        try {
            code = Integer.parseInt(settingCode);
        } catch (Exception e) {
            throw new BadRequestException("Please provide valid resource code.");
        }
        return code + 1;
    }

    public String overwriteText(String text, int code) {
        List<String> lines = text.lines().filter(l -> l.length() > 0).collect(Collectors.toList());
        List<String> sentences = splitOnSentences(lines.stream().collect(Collectors.joining(" ")));
        String result = sentences.stream().map(s -> s.trim()).collect(Collectors.joining(CR));
        saveContent(code, result);
        return splitIfLonger5000("" + code + CR2 + result);
    }

    private String splitIfLonger5000(String text) {
        if (text.length() <= MAX_LENGTH) {
            return text;
        }
        int pos = text.lastIndexOf(CR, MAX_LENGTH);
        return text.substring(0, pos) + CR + " ".repeat(MAX_LENGTH - pos) + splitIfLonger5000(text.substring(pos + 1));
    }

    private void saveContent(int code, String text) {
        TextContent content = new TextContent()
                .setId(code)
                .setText(text);
        repository.save(content);
    }

    private List<String> splitOnSentences(String source) {
        List<String> result = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(source);
        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            result.add(source.substring(start,end));
        }
        return result;
    }

    public String inputTranslation(String translation) {
        int pos = translation.indexOf(CR2);
        String text = "";
        if (pos > 0) {
            String code = translation.substring(0, pos);
            if (code.endsWith(".")) {
                code = code.substring(0, code.length() - 1);
            }
            int id = Integer.parseInt(code);;
            text = translation.substring(pos + CR2.length());
            if (repository.existsById(id)) {
                TextContent old = repository.findById(id).get();
                old.setTranslate(text);
                repository.save(old);
            }
        }
        return text;
    }

    public String getHtml(Integer sectionId) {

        List<Toc> headers = tocRepository.getTocByIdAndIsActiveTrueOrderById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Header with id = " + sectionId + " not found."));

        List<Toc> texts = tocRepository.getTocByParentCodeAndIsActiveTrueOrderById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Texts with sectionId = " + sectionId + " not found."));

        String body = htmlService.assembleHtml(headers, texts);
        return htmlService.addHeader(body);
    }

    public CorrectWordsResponse correct(CorrectWordsDto correctWordsDto) {
        String wrong = correctWordsDto.getWrong();
        String right = correctWordsDto.getRight();
        List<TextContent> toCorrect = repository.findToCorrect(wrong);
        List<Correction> corrections = new ArrayList<>();
        int totalCount = 0;
        for (TextContent content : toCorrect) {
            String trans = content.getTranslate();
            String englishText = content.getText();
            List<String> lines = trans.lines().collect(Collectors.toList());
            List<String> englishLines = englishText.lines().collect(Collectors.toList());

            for (int i=0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.contains(wrong)) {
                    Correction correction = new Correction();
                    correction.setItemId(content.getId());
                    correction.setLine(i);
                    correction.setBefore(line);
                    correction.setEnglish(englishLines.get(i));
                    if (allowCorrection(right)) {
                        String corrected = correctLine(line, wrong, right);
                        lines.set(i, corrected);
                        correction.setAfter(corrected);
                    }
                    corrections.add(correction);
                    totalCount++;
                }
            }
            content.setTranslate(lines.stream().collect(Collectors.joining(CR)));
        }
        if (allowCorrection(right)) {
            repository.saveAll(toCorrect);
        }
        return new CorrectWordsResponse(totalCount, corrections);
    }

    private boolean allowCorrection(String right) {
        return ! (null == right || right.isEmpty() || right.startsWith("-"));
    }

    private String correctLine(String line, String wrong, String right) {
        int pos = line.indexOf(wrong);
        String restOfLine = line.substring(pos + wrong.length());
        int j = 0;
        for (char c : restOfLine.toCharArray()) {
            if (!Character.isLetter(c)) break;
            j++;
        }
        return line.substring(0, pos) + right + line.substring(pos + wrong.length() + j);
    }

    @Transactional
    public void createToc(String toc) {
        List<String> tocList = toc.lines()
                .map(s -> s.trim())
                .filter(this::filterToc)
                .collect(Collectors.toList());
        int parentSection = 0;
        int parentHeader = 0;
        List<Toc> tocs = new ArrayList<>();
        for (String s : tocList) {
            Toc tocItem = new Toc(s, parentSection);
            if (tocItem.getTocType() == TocType.SECTION) {
                parentSection = tocItem.getCode();
            } else if (tocItem.getTocType() == TocType.HEADER) {
                parentHeader = tocItem.getCode();
            } else {
                tocItem.setParentCode(parentHeader);
            }
            if (tocItem.getTocType() == TocType.UNDEFINED) {
                String prevItem = tocs.get(tocs.size() - 1).getItem();
                tocs.get(tocs.size() - 1).setItem(prevItem + " " + tocItem.getItem());
            } else {
                tocs.add(tocItem);
            }
        }
        tocRepository.saveAll(tocs);
    }

    private boolean filterToc(String s) {
        if (s.isEmpty()) return false;
        if (s.matches("[0-9]{1,2}min")) return false;
        if (s.matches("\\d{1,2}\\s\\W\\s\\d{1,2}\\W\\d{1,2}min")) return false;
        return true;
    }

    public List<String> testRep() {
        List<String> result = new ArrayList<>();
        return result;
    }

    public String getText(int code) {
        TextContent textContent = repository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("Content with id = " + code + " not found."));
        return splitIfLonger5000("" + code + CR2 + textContent.getText());
    }
}
