package ru.weu.dsport.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.weu.dsport.domain.SetEntry;

public interface SetEntryRepository extends JpaRepository<SetEntry, Long> {

    List<SetEntry> findByWorkoutExerciseIdIn(Collection<Long> workoutExerciseIds);

    @Query("""
            select setEntry
            from SetEntry setEntry
            join fetch setEntry.workoutExercise workoutExercise
            join fetch workoutExercise.workoutSession workoutSession
            where setEntry.id = :setEntryId
            """)
    Optional<SetEntry> findByIdWithSession(Long setEntryId);
}
