package ru.weu.dsport.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.dto.ExerciseScope;
import ru.weu.dsport.repository.projection.ExerciseSummaryRow;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByOwnerUserIdAndDeletedAtIsNull(Long ownerUserId);

    List<Exercise> findByOwnerUserIsNullAndDeletedAtIsNull();

    @Query("select e from Exercise e "
            + "where e.deletedAt is null and (e.ownerUser.id = :ownerUserId or e.ownerUser is null)")
    List<Exercise> findAvailableForUser(@Param("ownerUserId") Long ownerUserId);

    @Query("select e from Exercise e "
            + "where e.deletedAt is null and e.id in :ids "
            + "and (e.ownerUser.id = :ownerUserId or e.ownerUser is null)")
    List<Exercise> findAvailableByIdsForUser(
            @Param("ownerUserId") Long ownerUserId,
            @Param("ids") Collection<Long> ids
    );

    @Query("""
            select e.id as id,
                   e.name as name,
                   e.type as type,
                   mg.code as muscleCode,
                   case when e.ownerUser is null then true else false end as systemOwned
            from Exercise e
            left join e.muscleGroups mg
            where e.deletedAt is null
              and (e.ownerUser.id = :ownerUserId or e.ownerUser is null)
              and (:query is null or lower(e.name) like lower(concat('%', :query, '%')))
              and (:muscleGroup is null or exists (
                   select 1
                   from e.muscleGroups mgFilter
                   where mgFilter.code = :muscleGroup
              ))
              and (:scope = ru.weu.dsport.dto.ExerciseScope.ALL
                   or (:scope = ru.weu.dsport.dto.ExerciseScope.SYSTEM and e.ownerUser is null)
                   or (:scope = ru.weu.dsport.dto.ExerciseScope.MY and e.ownerUser.id = :ownerUserId))
            order by case when e.ownerUser is null then 0 else 1 end,
                     lower(e.name),
                     e.id
            """)
    List<ExerciseSummaryRow> findSummaryRowsForPicker(
            @Param("ownerUserId") Long ownerUserId,
            @Param("scope") ExerciseScope scope,
            @Param("query") String query,
            @Param("muscleGroup") String muscleGroup
    );
}
