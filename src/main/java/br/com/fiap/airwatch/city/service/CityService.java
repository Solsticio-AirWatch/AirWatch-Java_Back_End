package br.com.fiap.airwatch.city.service;

import br.com.fiap.airwatch.city.dto.*;
import br.com.fiap.airwatch.city.model.City;
import br.com.fiap.airwatch.city.repository.CityRepository;
import br.com.fiap.airwatch.country.service.CountryService;
import br.com.fiap.airwatch.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository repo;
    private final CountryService countryService;

    public Page<CityResponse> findAll(Pageable p) {
        return repo.findAll(p).map(CityResponse::from);
    }

    public Page<CityResponse> findByCountry(Long cid, Pageable p) {
        return repo.findByCountry_Id(cid, p).map(CityResponse::from);
    }

    public CityResponse findById(Long id) {
        return CityResponse.from(findOrThrow(id));
    }

    public CityResponse create(CityRequest req) {
        var co = countryService.findOrThrow(req.countryId());
        return CityResponse.from(repo.save(City.builder().country(co).name(req.name()).state(req.state())
                .latitude(req.latitude()).longitude(req.longitude()).altitudeM(req.altitudeM()).population(req.population()).build()));
    }

    public CityResponse update(Long id, CityRequest req) {
        var c = findOrThrow(id);
        c.setCountry(countryService.findOrThrow(req.countryId()));
        c.setName(req.name());
        c.setState(req.state());
        c.setLatitude(req.latitude());
        c.setLongitude(req.longitude());
        c.setAltitudeM(req.altitudeM());
        c.setPopulation(req.population());
        return CityResponse.from(repo.save(c));
    }

    public void delete(Long id) {
        findOrThrow(id);
        repo.deleteById(id);
    }

    public City findOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found: " + id));
    }
}
