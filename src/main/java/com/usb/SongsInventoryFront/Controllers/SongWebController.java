package com.usb.SongsInventoryFront.Controllers;

import com.usb.SongsInventoryFront.Entities.SongEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;


@Controller
@RequestMapping("/api/v1/songs")
public class SongWebController {

    private final String backendUrl = "https://activity-1-songsinventory-backend-production.up.railway.app";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping({"/",""})
    public String songList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "songName,asc") String[] sort,
            Model model
    ){
        String sortParam = String.join(",", sort);
        String url = backendUrl + "?page=" + page + "&size=" + size + "&sort=" + sortParam;

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
        Map<String, Object> responseBody = response.getBody();

        model.addAttribute("songs", responseBody.get("Movies"));
        model.addAttribute("currentPage", responseBody.get("CurrentPage"));
        model.addAttribute("totalPages", responseBody.get("TotalPages"));
        model.addAttribute("size", size);
        model.addAttribute("sort", sortParam);

        return "songs-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("song", new SongEntity());
        return "song-new";
    }

    @PostMapping
    public String createSong(@ModelAttribute SongEntity song) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        song.setId(UUID.randomUUID()); // Generar ID en el frontend
        HttpEntity<SongEntity> request = new HttpEntity<>(song, headers);
        restTemplate.postForEntity(backendUrl, request, SongEntity.class);
        return "redirect:/api/v1/songs";
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        ResponseEntity<Map> response = restTemplate.getForEntity(backendUrl + "/" + id, Map.class);
        Map<String, Object> responseBody = response.getBody();

        // Extraer el mapa anidado de la clave "Song"
        LinkedHashMap<String, Object> songsMap = (LinkedHashMap<String, Object>) responseBody.get("Song");

        // Convertir el mapa a SongEntity manualmente
        SongEntity song = new SongEntity();
        song.setId(UUID.fromString((String) songsMap.get("id")));
        song.setSongName((String) songsMap.get("songName"));
        song.setSongArtist((String) songsMap.get("songArtist"));
        song.setSongAlbum((String) songsMap.get("songAlbum"));
        song.setSongYear((String) songsMap.get("songYear"));

        System.out.println("Objeto recibido: " + song);

        model.addAttribute("song", song);
        return "song-update";
    }

    @PostMapping("/update/{id}")
    public String updateSong(@PathVariable UUID id, @ModelAttribute SongEntity song) {
        song.setId(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SongEntity> request = new HttpEntity<>(song, headers);

        restTemplate.exchange(backendUrl + "/" + id, HttpMethod.PUT, request, Void.class);
        return "redirect:/api/v1/songs";
    }

    @GetMapping("/delete/{id}")
    public String deleteSong(@PathVariable UUID id) {
        restTemplate.delete(backendUrl + "/" + id);
        return "redirect:/api/v1/songs";
    }

}
