package ru.weu.dsport.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.weu.dsport.domain.SetEntry;

public interface SetEntryRepository extends JpaRepository<SetEntry, Long> {

    List<SetEntry> findByWorkoutExerciseIdIn(Collection<Long> workoutExerciseIds);
}
