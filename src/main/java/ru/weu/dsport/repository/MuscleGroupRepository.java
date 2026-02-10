package ru.weu.dsport.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.weu.dsport.domain.MuscleGroup;

public interface MuscleGroupRepository extends JpaRepository<MuscleGroup, String> {

    List<MuscleGroup> findAllByOrderByCodeAsc();
}
