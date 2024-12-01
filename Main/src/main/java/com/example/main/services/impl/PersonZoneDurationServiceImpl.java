package com.example.main.services.impl;

import com.example.main.models.entities.PersonInZoneDuration;
import com.example.main.models.entities.PersonLastSeen;
import com.example.main.repositories.PersonInZoneDurationRepository;
import com.example.main.repositories.PersonLastSeenRepository;
import com.example.main.services.PersonInZoneDurationService;
import com.example.main.services.PersonService;
import com.example.main.services.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonZoneDurationServiceImpl implements PersonInZoneDurationService {

    private final PersonLastSeenRepository personLastSeenRepository;
    private final PersonInZoneDurationRepository personInZoneDurationRepository;
    private final PersonService personService;
    private final ZoneService zoneService;

    @Transactional
    @Scheduled(cron = "0 */30 * * * *") // Выполняется каждые 30 минут
    public void calculateDurations() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime halfHourAgo = now.minusMinutes(30);

        // Получаем записи, где lastSeen попадает в интервал
        List<PersonLastSeen> lastSeenRecords = personLastSeenRepository.findAllByLastSeenBetween(halfHourAgo, now);

        Map<UUID, Map<Long, List<PersonLastSeen>>> groupedByPersonAndZone = lastSeenRecords.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getPerson().getId(),
                        Collectors.groupingBy(record -> record.getZone().getId())
                ));

        groupedByPersonAndZone.forEach((personId, zoneRecords) -> zoneRecords.forEach((zoneId, records) -> processRecordsForZone(personId, zoneId, records, halfHourAgo, now)));
    }

    private void processRecordsForZone(UUID personId, Long zoneId, List<PersonLastSeen> records, OffsetDateTime start, OffsetDateTime end) {
        // Получаем последнюю запись расчета
        PersonInZoneDuration lastCalculation = personInZoneDurationRepository.findByPersonIdAndZoneId(personId, zoneId)
                .orElse(null);

        OffsetDateTime lastCalculateTime = lastCalculation != null
                ? lastCalculation.getLastCalculateTime()
                : start;

        long duration = calculateDuration(lastCalculateTime, end, records);

        if (lastCalculation == null) {
            // Создаем новую запись
            PersonInZoneDuration newCalculation = PersonInZoneDuration.builder()
                    .person(personService.findById(personId))
                    .zone(zoneService.findById(zoneId))
                    .duration(duration)
                    .lastCalculateTime(end)
                    .build();
            personInZoneDurationRepository.save(newCalculation);
        } else {
            // Обновляем существующую запись
            lastCalculation.setDuration(lastCalculation.getDuration() + duration);
            lastCalculation.setLastCalculateTime(end);
            personInZoneDurationRepository.save(lastCalculation);
        }
    }

    private long calculateDuration(OffsetDateTime start, OffsetDateTime end, List<PersonLastSeen> records) {
        long duration = 0;
        OffsetDateTime current = start;

        for (PersonLastSeen record : records) {
            if (record.getLastSeen().isAfter(current)) {
                duration += Duration.between(current, record.getLastSeen()).toMinutes();
                current = record.getLastSeen();
            }
        }
        if (current.isBefore(end)) {
            duration += Duration.between(current, end).toMinutes();
        }

        return duration;
    }
}
