package com.example.adoptions.adoptions;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
@ResponseBody
class DogAdoptionController {

    private final DogAdoptionService dogAdoptionService;

    DogAdoptionController(DogAdoptionService dogAdoptionService) {
        this.dogAdoptionService = dogAdoptionService;
    }

    @GetMapping("/dogs")
    Collection<Dog> all() {
        return this.dogAdoptionService.all();
    }

    @PostMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable int dogId, @RequestParam String owner) {
        this.dogAdoptionService.adopt(dogId, owner);
    }

}

@Service
@Transactional
class DogAdoptionService {

    private final DogRepository dogRepository;
    private final ApplicationEventPublisher applicationEventPublisher;


    DogAdoptionService(DogRepository dogRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.dogRepository = dogRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    Collection<Dog> all() {
        return this.dogRepository.findAll();
    }

    void adopt(int id, String owner) {
        this.dogRepository.findById(id).ifPresent(dog -> {
            var updated = this.dogRepository.save(
                    new Dog(dog.id(), dog.name(), owner, dog.description()));
            applicationEventPublisher.publishEvent(new DogAdoptedEvent(updated.id()));
            System.out.println("adopted [" + updated + "]");
        });

    }
}


interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record Dog(@Id int id, String name, String owner, String description) {
}