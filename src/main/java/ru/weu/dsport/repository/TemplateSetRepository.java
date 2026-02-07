package ru.weu.dsport.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.weu.dsport.domain.TemplateSet;

public interface TemplateSetRepository extends JpaRepository<TemplateSet, Long> {

    List<TemplateSet> findByTemplateExerciseIdIn(Collection<Long> templateExerciseIds);
}
