package ru.weu.dsport.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.weu.dsport.domain.MuscleGroupTranslation;

public interface MuscleGroupTranslationRepository extends JpaRepository<MuscleGroupTranslation, Long> {

    @Query("""
            select translation
            from MuscleGroupTranslation translation
            where translation.muscleGroup.code in :muscleCodes
            order by translation.muscleGroup.code asc, translation.id asc
            """)
    List<MuscleGroupTranslation> findByMuscleCodes(@Param("muscleCodes") Collection<String> muscleCodes);
}
