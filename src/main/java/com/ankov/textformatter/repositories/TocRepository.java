package com.ankov.textformatter.repositories;

import com.ankov.textformatter.model.Toc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface TocRepository extends JpaRepository<Toc, Integer> {

    //@Transactional
    //@Query("SELECT t FROM Toc t where (t.id = :sectionId or t.parentCode = :sectionId) and t.isActive order by t.id")
    //Optional<List<Toc>> getSectionTextDTOById(Integer sectionId);

    Optional<List<Toc>> getTocByParentCodeAndIsActiveTrueOrderById(Integer parentCode);

    Optional<List<Toc>> getTocByIdAndIsActiveTrueOrderById(Integer parentCode);
}
