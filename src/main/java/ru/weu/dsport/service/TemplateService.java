package ru.weu.dsport.service;

import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.domain.Exercise;
import ru.weu.dsport.domain.TemplateExercise;
import ru.weu.dsport.domain.TemplateSet;
import ru.weu.dsport.domain.WorkoutTemplate;
import ru.weu.dsport.dto.TemplateCreateRequest;
import ru.weu.dsport.dto.TemplateResponse;
import ru.weu.dsport.exception.NotFoundException;
import ru.weu.dsport.mapper.TemplateMapper;
import ru.weu.dsport.repository.ExerciseRepository;
import ru.weu.dsport.repository.WorkoutTemplateRepository;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final ExerciseRepository exerciseRepository;
    private final CurrentUserService currentUserService;
    private final TemplateMapper templateMapper;

    @Transactional
    public TemplateResponse createTemplate(TemplateCreateRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        WorkoutTemplate template = WorkoutTemplate.builder()
                .ownerUser(user)
                .name(request.getName())
                .createdAt(now)
                .updatedAt(now)
                .build();
        Map<Long, Exercise> exercisesById = loadExercises(user, request.getExercises());
        applyExercises(template, request.getExercises(), exercisesById);
        WorkoutTemplate savedTemplate = workoutTemplateRepository.save(template);
        return templateMapper.toResponse(savedTemplate);
    }

    public List<TemplateResponse> listTemplates() {
        AppUser user = currentUserService.getCurrentUser();
        return workoutTemplateRepository.findByOwnerUserIdAndDeletedAtIsNull(user.getId()).stream()
                .map(templateMapper::toResponse)
                .toList();
    }

    public TemplateResponse getTemplate(Long id) {
        AppUser user = currentUserService.getCurrentUser();
        WorkoutTemplate template = workoutTemplateRepository
                .findByIdAndOwnerUserIdAndDeletedAtIsNull(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Template not found"));
        return templateMapper.toResponse(template);
    }

    @Transactional
    public TemplateResponse updateTemplate(Long id, TemplateCreateRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        WorkoutTemplate template = workoutTemplateRepository
                .findByIdAndOwnerUserIdAndDeletedAtIsNull(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Template not found"));
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        template.setName(request.getName());
        template.setUpdatedAt(now);
        template.getExercises().clear();
        Map<Long, Exercise> exercisesById = loadExercises(user, request.getExercises());
        applyExercises(template, request.getExercises(), exercisesById);
        WorkoutTemplate savedTemplate = workoutTemplateRepository.save(template);
        return templateMapper.toResponse(savedTemplate);
    }

    @Transactional
    public void deleteTemplate(Long id) {
        AppUser user = currentUserService.getCurrentUser();
        WorkoutTemplate template = workoutTemplateRepository
                .findByIdAndOwnerUserIdAndDeletedAtIsNull(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Template not found"));
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        template.setDeletedAt(now);
        template.setUpdatedAt(now);
        workoutTemplateRepository.save(template);
    }

    private Map<Long, Exercise> loadExercises(AppUser user, List<TemplateCreateRequest.TemplateExerciseRequest> requests) {
        Set<Long> ids = requests.stream()
                .map(TemplateCreateRequest.TemplateExerciseRequest::getExerciseId)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<Exercise> exercises = exerciseRepository.findAvailableByIdsForUser(user.getId(), ids);
        if (exercises.size() != ids.size()) {
            throw new NotFoundException("Exercise not found");
        }
        return exercises.stream().collect(Collectors.toMap(Exercise::getId, Function.identity()));
    }

    private void applyExercises(
            WorkoutTemplate template,
            List<TemplateCreateRequest.TemplateExerciseRequest> requests,
            Map<Long, Exercise> exercisesById
    ) {
        for (TemplateCreateRequest.TemplateExerciseRequest exerciseRequest : requests) {
            Exercise exercise = exercisesById.get(exerciseRequest.getExerciseId());
            if (exercise == null) {
                throw new NotFoundException("Exercise not found");
            }
            TemplateExercise templateExercise = TemplateExercise.builder()
                    .template(template)
                    .exercise(exercise)
                    .orderIndex(exerciseRequest.getOrderIndex())
                    .build();
            applySets(templateExercise, exerciseRequest.getSets());
            template.getExercises().add(templateExercise);
        }
    }

    private void applySets(
            TemplateExercise templateExercise,
            Collection<TemplateCreateRequest.TemplateSetRequest> requests
    ) {
        for (TemplateCreateRequest.TemplateSetRequest setRequest : requests) {
            TemplateSet templateSet = TemplateSet.builder()
                    .templateExercise(templateExercise)
                    .orderIndex(setRequest.getOrderIndex())
                    .plannedReps(setRequest.getPlannedReps())
                    .plannedDurationSeconds(setRequest.getPlannedDurationSeconds())
                    .build();
            templateExercise.getSets().add(templateSet);
        }
    }
}
