package ru.weu.dsport.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.domain.SetEntry;
import ru.weu.dsport.domain.TemplateExercise;
import ru.weu.dsport.domain.TemplateSet;
import ru.weu.dsport.domain.WorkoutExercise;
import ru.weu.dsport.domain.WorkoutSession;
import ru.weu.dsport.domain.WorkoutTemplate;
import ru.weu.dsport.dto.StartWorkoutRequest;
import ru.weu.dsport.dto.WorkoutSessionResponse;
import ru.weu.dsport.exception.NotFoundException;
import ru.weu.dsport.mapper.WorkoutMapper;
import ru.weu.dsport.repository.WorkoutSessionRepository;
import ru.weu.dsport.repository.WorkoutTemplateRepository;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final CurrentUserService currentUserService;
    private final WorkoutMapper workoutMapper;

    @Transactional
    public WorkoutSessionResponse startWorkout(StartWorkoutRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        WorkoutTemplate template = null;
        String title = request.getTitle();
        if (request.getTemplateId() != null) {
            template = workoutTemplateRepository
                    .findByIdAndOwnerUserIdAndDeletedAtIsNull(request.getTemplateId(), user.getId())
                    .orElseThrow(() -> new NotFoundException("Template not found"));
            if (title == null || title.isBlank()) {
                title = formatTitle(now, template.getName());
            }
        } else if (title == null) {
            title = formatTitle(now, null);
        }
        WorkoutSession session = WorkoutSession.builder()
                .user(user)
                .template(template)
                .title(title)
                .startedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
        if (template != null) {
            for (TemplateExercise templateExercise : template.getExercises()) {
                WorkoutExercise workoutExercise = WorkoutExercise.builder()
                        .workoutSession(session)
                        .exercise(templateExercise.getExercise())
                        .orderIndex(templateExercise.getOrderIndex())
                        .build();
                for (TemplateSet templateSet : templateExercise.getSets()) {
                    SetEntry setEntry = SetEntry.builder()
                            .workoutExercise(workoutExercise)
                            .orderIndex(templateSet.getOrderIndex())
                            .reps(templateSet.getPlannedReps())
                            .durationSeconds(templateSet.getPlannedDurationSeconds())
                            .weight(null)
                            .build();
                    workoutExercise.getSetEntries().add(setEntry);
                }
                session.getExercises().add(workoutExercise);
            }
        }
        WorkoutSession savedSession = workoutSessionRepository.save(session);
        return workoutMapper.toResponse(savedSession);
    }

    private String formatTitle(OffsetDateTime now, String templateName) {
        LocalDate date = now.toLocalDate();
        if (templateName == null || templateName.isBlank()) {
            return date.toString();
        }
        return date + " " + templateName;
    }
}
