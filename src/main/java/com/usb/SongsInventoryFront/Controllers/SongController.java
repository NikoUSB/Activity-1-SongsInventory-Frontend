package com.usb.SongsInventoryFront.Controllers;

import com.usb.SongsInventoryFront.Entities.SongEntity;
import com.usb.SongsInventoryFront.Services.SongService;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/songs")
@Validated
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping({"","/"})
    public ResponseEntity<?> getAllCarsMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "songName,desc") String[] sort){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
            return songService.getAllSongs(pageable);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid sorting direction. Use 'asc' or 'desc'.");
        }

    }

    private Sort.Order parseSort(String[] sort) {
        if (sort.length < 2) {
            throw new IllegalArgumentException("Sort parameter must have both field and direction (e.g., 'year,desc').");
        }

        String property = sort[0];
        String direction = sort[1].toLowerCase();

        List<String> validDirections = Arrays.asList("asc", "desc");
        if (!validDirections.contains(direction)) {
            throw new IllegalArgumentException("Invalid sort direction. Use 'asc' or 'desc'.");
        }

        return new Sort.Order(Sort.Direction.fromString(direction), property);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCarsMovieById(@PathVariable UUID id){
        return songService.getSongById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getMoviesByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
        return songService.getSongsBySongName(name, pageable);
    }

    @PostMapping
    public ResponseEntity<?> insertCarsMovie(@Valid @RequestBody SongEntity songEntity){
        return songService.addSong(songEntity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCarsMovie(@PathVariable UUID id, @Valid @RequestBody SongEntity songEntity){
        return songService.updateSong(id,songEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCarsMovie(@PathVariable UUID id){
        return songService.deleteSong(id);
    }

    @GetMapping("/Test")
    public String hello(){
        return "Hello, this is the first version of the API";
    }

}
