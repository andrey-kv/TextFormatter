package com.ankov.textformatter.repositories;

import com.ankov.textformatter.model.TextContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<TextContent, Integer> {

    @Query("SELECT c FROM TextContent c WHERE c.translate LIKE %:wrong%")
    List<TextContent> findToCorrect(String wrong);
}
