package com.example.demo.service;

import com.example.demo.entity.Location;
import com.example.demo.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {
    
    @Autowired
    private LocationRepository locationRepository;
    
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
    
    public List<Location> getProvinces() {
        return locationRepository.findByParentLocationIsNull();
    }
    
    public List<Location> getDistrictsByProvinceId(Integer provinceId) {
        return locationRepository.findByParentLocationMaDiaDiem(provinceId);
    }
    
    public Optional<Location> getLocationById(Integer id) {
        return locationRepository.findById(id);
    }
    
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }
    
    public Location updateLocation(Location location) {
        return locationRepository.save(location);
    }
    
    public void deleteLocation(Integer id) {
        locationRepository.deleteById(id);
    }
}