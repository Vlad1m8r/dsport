package ru.weu.dsport.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.domain.SetEntry;
import ru.weu.dsport.domain.TemplateExercise;
import ru.weu.dsport.domain.TemplateSet;
import ru.weu.dsport.domain.WorkoutExercise;
import ru.weu.dsport.domain.WorkoutSession;
import ru.weu.dsport.domain.WorkoutTemplate;
import ru.weu.dsport.dto.AddSetEntryRequest;
import ru.weu.dsport.dto.AddWorkoutExerciseRequest;
import ru.weu.dsport.dto.StartWorkoutRequest;
import ru.weu.dsport.dto.UpdateSetEntryRequest;
import ru.weu.dsport.dto.WorkoutSessionResponse;
import ru.weu.dsport.dto.WorkoutSummaryResponse;
import ru.weu.dsport.exception.BadRequestException;
import ru.weu.dsport.exception.NotFoundException;
import ru.weu.dsport.mapper.WorkoutMapper;
import ru.weu.dsport.repository.ExerciseRepository;
import ru.weu.dsport.repository.OffsetBasedPageRequest;
import ru.weu.dsport.repository.SetEntryRepository;
import ru.weu.dsport.repository.WorkoutExerciseRepository;
import ru.weu.dsport.repository.WorkoutSessionRepository;
import ru.weu.dsport.repository.WorkoutTemplateRepository;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MIN_LIMIT = 1;
    private static final String WORKOUT_FINISHED_CODE = "WORKOUT_FINISHED";
    private static final String WORKOUT_HAS_EMPTY_SETS_CODE = "WORKOUT_HAS_EMPTY_SETS";

    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ExerciseRepository exerciseRepository;
    private final CurrentUserService currentUserService;
    private final WorkoutMapper workoutMapper;
    private final SetEntryRepository setEntryRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

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
        WorkoutSession savedSession = workoutSessionRepository.saveAndFlush(session);
        return workoutMapper.toResponse(savedSession);
    }

    @Transactional
    public List<WorkoutSummaryResponse> listWorkouts(Integer limit, Integer offset) {
        int resolvedLimit = limit == null ? DEFAULT_LIMIT : limit;
        int resolvedOffset = offset == null ? 0 : offset;
        if (resolvedLimit < MIN_LIMIT) {
            throw new BadRequestException("limit должен быть >= 1");
        }
        if (resolvedOffset < 0) {
            throw new BadRequestException("offset должен быть >= 0");
        }
        AppUser user = currentUserService.getCurrentUser();
        Sort sort = Sort.by(Sort.Order.desc("startedAt"), Sort.Order.desc("id"));
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(resolvedOffset, resolvedLimit, sort);
        return workoutSessionRepository.findByUserId(user.getId(), pageRequest).stream()
                .map(workoutMapper::toSummaryResponse)
                .toList();
    }

    @Transactional
    public WorkoutSessionResponse getWorkout(Long workoutId) {
        AppUser user = currentUserService.getCurrentUser();
        WorkoutSession session = getWorkoutSession(workoutId, user.getId());
        return workoutMapper.toResponse(session);
    }

    @Transactional
    public WorkoutSessionResponse.WorkoutExerciseResponse addExercise(
            Long workoutId,
            AddWorkoutExerciseRequest request
    ) {
        AppUser user = currentUserService.getCurrentUser();
        WorkoutSession session = getWorkoutSession(workoutId, user.getId());
        assertWorkoutNotFinished(session);
        Exercise exercise = exerciseRepository.findAvailableByIdsForUser(
                        user.getId(),
                        List.of(request.getExerciseId())
                )
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Exercise not found"));
        WorkoutExercise workoutExercise = WorkoutExercise.builder()
                .workoutSession(session)
                .exercise(exercise)
                .orderIndex(request.getOrderIndex())
                .build();
        session.getExercises().add(workoutExercise);
        WorkoutExercise savedExercise = workoutExerciseRepository.saveAndFlush(workoutExercise);
        return workoutMapper.toExerciseResponse(savedExercise);
    }

    @Transactional
    public void deleteExercise(Long workoutId, Long workoutExerciseId) {
        AppUser user = currentUserService.getCurrentUser();
        WorkoutSession session = getWorkoutSession(workoutId, user.getId());
        assertWorkoutNotFinished(session);
        WorkoutExercise workoutExercise = session.getExercises().stream()
                .filter(exercise -> exercise.getId().equals(workoutExerciseId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Workout exercise not found"));
        session.getExercises().remove(workoutExercise);
        workoutSessionRepository.save(session);
    }

    @Transactional
    public WorkoutSessionResponse.SetEntryResponse addSetEntry(
            Long workoutId,
            Long workoutExerciseId,
            AddSetEntryRequest request
    ) {
        AppUser user = currentUserService.getCurrentUser();
        WorkoutSession session = getWorkoutSession(workoutId, user.getId());
        assertWorkoutNotFinished(session);
        WorkoutExercise workoutExercise = session.getExercises().stream()
                .filter(exercise -> exercise.getId().equals(workoutExerciseId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Workout exercise not found"));
        SetEntry setEntry = SetEntry.builder()
                .workoutExercise(workoutExercise)
                .orderIndex(request.getOrderIndex())
                .reps(request.getReps())
                .weight(request.getWeight())
                .durationSeconds(request.getDurationSeconds())
                .build();
        workoutExercise.getSetEntries().add(setEntry);
        SetEntry savedSetEntry = setEntryRepository.saveAndFlush(setEntry);
        return workoutMapper.toSetResponse(savedSetEntry);
    }

    @Transactional
    public void deleteSetEntry(Long workoutId, Long workoutExerciseId, Long setEntryId) {
        AppUser user = currentUserService.getCurrentUser();
        WorkoutSession session = getWorkoutSession(workoutId, user.getId());
        assertWorkoutNotFinished(session);
        WorkoutExercise targetExercise = session.getExercises().stream()
                .filter(exercise -> exercise.getId().equals(workoutExerciseId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Workout exercise not found"));
        SetEntry targetSet = targetExercise.getSetEntries().stream()
                .filter(setEntry -> setEntry.getId().equals(setEntryId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Set entry not found"));
        targetExercise.getSetEntries().remove(targetSet);
        workoutSessionRepository.save(session);
    }

    @Transactional
    public WorkoutSessionResponse.SetEntryResponse updateSetEntry(
            Long workoutId,
            Long setEntryId,
            UpdateSetEntryRequest request
    ) {
        AppUser user = currentUserService.getCurrentUser();
        SetEntry setEntry = setEntryRepository.findByIdWithSession(setEntryId)
                .orElseThrow(() -> new NotFoundException("Set entry not found"));
        WorkoutSession session = setEntry.getWorkoutExercise().getWorkoutSession();
        if (!session.getId().equals(workoutId) || !session.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Set entry not found");
        }
        assertWorkoutNotFinished(session);
        Integer updatedReps = request.isRepsProvided() ? request.getReps() : setEntry.getReps();
        Integer updatedDuration = request.isDurationSecondsProvided()
                ? request.getDurationSeconds()
                : setEntry.getDurationSeconds();
        if (updatedReps == null && updatedDuration == null) {
            throw new BadRequestException("reps или durationSeconds должны быть заданы");
        }
        if (request.isOrderIndexProvided()) {
            Integer orderIndex = request.getOrderIndex();
            if (orderIndex == null) {
                throw new BadRequestException("orderIndex должен быть >= 1");
            }
            setEntry.setOrderIndex(orderIndex);
        }
        if (request.isRepsProvided()) {
            setEntry.setReps(request.getReps());
        }
        if (request.isWeightProvided()) {
            setEntry.setWeight(request.getWeight());
        }
        if (request.isDurationSecondsProvided()) {
            setEntry.setDurationSeconds(request.getDurationSeconds());
        }
        setEntryRepository.save(setEntry);
        return workoutMapper.toSetResponse(setEntry);
    }

    @Transactional
    public WorkoutSessionResponse finishWorkout(Long workoutId) {
        AppUser user = currentUserService.getCurrentUser();
        WorkoutSession session = getWorkoutSession(workoutId, user.getId());
        if (session.getFinishedAt() != null) {
            return workoutMapper.toResponse(session);
        }
        boolean hasEmptySets = session.getExercises().stream()
                .flatMap(exercise -> exercise.getSetEntries().stream())
                .anyMatch(this::isEmptySetEntry);
        if (hasEmptySets) {
            throw new BadRequestException(
                    "Тренировка содержит пустые подходы",
                    WORKOUT_HAS_EMPTY_SETS_CODE
            );
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        session.setFinishedAt(now);
        session.setUpdatedAt(now);
        workoutSessionRepository.save(session);
        return workoutMapper.toResponse(session);
    }

    private String formatTitle(OffsetDateTime now, String templateName) {
        LocalDate date = now.toLocalDate();
        if (templateName == null || templateName.isBlank()) {
            return date.toString();
        }
        return date + " " + templateName;
    }

    private WorkoutSession getWorkoutSession(Long workoutId, Long userId) {
        WorkoutSession session = workoutSessionRepository.findByIdAndUserId(workoutId, userId)
                .orElseThrow(() -> new NotFoundException("Workout session not found"));
        preloadSetEntries(session);
        return session;
    }

    private void preloadSetEntries(WorkoutSession session) {
        List<Long> exerciseIds = session.getExercises().stream()
                .map(WorkoutExercise::getId)
                .filter(id -> id != null)
                .toList();
        if (exerciseIds.isEmpty()) {
            return;
        }
        setEntryRepository.findByWorkoutExerciseIdIn(exerciseIds);
    }

    private void assertWorkoutNotFinished(WorkoutSession session) {
        if (session.getFinishedAt() != null) {
            throw new BadRequestException("Тренировка завершена", WORKOUT_FINISHED_CODE);
        }
    }

    private boolean isEmptySetEntry(SetEntry setEntry) {
        Integer reps = setEntry.getReps();
        Integer durationSeconds = setEntry.getDurationSeconds();
        boolean repsInvalid = reps == null || reps == 0 || setEntry.getWeight() == null;
        boolean durationInvalid = durationSeconds == null || durationSeconds == 0;
        return repsInvalid || durationInvalid;
    }
}
