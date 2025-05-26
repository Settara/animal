package ru.social.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.social.animal.model.Region;
import ru.social.animal.repository.RegionRepo;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {

    @Autowired
    private RegionRepo regionRepo;

    public List<Region> findAll() {
        return regionRepo.findAll();
    }

    public Optional<Region> findById(Long id) {
        return regionRepo.findById(id);
    }
}
