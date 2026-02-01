package ru.weu.dsport.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.weu.dsport.domain.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByOwnerUserIdAndDeletedAtIsNull(Long ownerUserId);

    List<Exercise> findByOwnerUserIsNullAndDeletedAtIsNull();

    @Query("select e from Exercise e "
            + "where e.deletedAt is null and (e.ownerUser.id = :ownerUserId or e.ownerUser is null)")
    List<Exercise> findAvailableForUser(@Param("ownerUserId") Long ownerUserId);
}
