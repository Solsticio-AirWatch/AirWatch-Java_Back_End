package br.com.fiap.airwatch.airreading.service;

import br.com.fiap.airwatch.airreading.dto.*;
import br.com.fiap.airwatch.airreading.model.AirReading;
import br.com.fiap.airwatch.airreading.repository.AirReadingRepository;
import br.com.fiap.airwatch.city.service.CityService;
import br.com.fiap.airwatch.exception.ResourceNotFoundException;
import br.com.fiap.airwatch.sensor.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AirReadingService {
    private final AirReadingRepository repo;
    private final CityService cityService;
    private final SensorService sensorService;

    public Page<AirReadingResponse> findAll(Pageable p) {
        return repo.findAll(p).map(AirReadingResponse::from);
    }

    public Page<AirReadingResponse> findByCity(Long cityId, Pageable p) {
        return repo.findByCity_IdOrderByReadingAtDesc(cityId, p).map(AirReadingResponse::from);
    }

    public AirReadingResponse findById(Long id) {
        return AirReadingResponse.from(findOrThrow(id));
    }

    public AirReadingResponse create(AirReadingRequest req) {
        var city = cityService.findOrThrow(req.cityId());
        var sensor = req.sensorId() != null ? sensorService.findOrThrow(req.sensorId()) : null;
        var r = AirReading.builder().city(city).sensor(sensor).pm25(req.pm25()).pm10(req.pm10())
                .co2(req.co2()).co(req.co()).no2(req.no2()).so2(req.so2()).o3(req.o3())
                .temperature(req.temperature()).humidity(req.humidity()).aqi(req.aqi())
                .category(req.category()).source(req.source()).readingAt(req.readingAt()).build();
        return AirReadingResponse.from(repo.save(r));
    }

    public void delete(Long id) {
        findOrThrow(id);
        repo.deleteById(id);
    }

    public AirReading findOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("AirReading not found: " + id));
    }
}
