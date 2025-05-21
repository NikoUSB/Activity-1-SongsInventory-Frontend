package com.usb.SongsInventoryFront.Controllers;

import com.usb.SongsInventoryFront.Entities.SongEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Controller
@RequestMapping("/web/songs")
public class SongWebController {

    @Value("${app.url}")
    private String appUrl;
    private final String backendUrl = appUrl+"/api/v1/songs";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String listSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "songName,asc") String sort,
            Model model
    ) {
        String url = backendUrl + "?page=" + page + "&size=" + size + "&sort=" + sort;

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
        Map<String, Object> responseBody = response.getBody();

        List<LinkedHashMap<String, Object>> content = (List<LinkedHashMap<String, Object>>) responseBody.get("content");
        List<SongEntity> songs = new ArrayList<>();

        for (Map<String, Object> item : content) {
            SongEntity song = new SongEntity();
            song.setId((String) item.get("id"));
            song.setSongName((String) item.get("songName"));
            song.setSongArtist((String) item.get("songArtist"));
            song.setSongAlbum((String) item.get("songAlbum"));
            song.setSongYear((String) item.get("songYear"));
            songs.add(song);
        }

        model.addAttribute("songs", songs);
        model.addAttribute("currentPage", responseBody.get("number"));
        model.addAttribute("totalPages", responseBody.get("totalPages"));

        return "song-list";
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

        // No establecer el ID manualmente si lo hace el backend
        HttpEntity<SongEntity> request = new HttpEntity<>(song, headers);
        restTemplate.postForEntity(backendUrl, request, SongEntity.class);

        return "redirect:/web/songs";
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        ResponseEntity<Map> response = restTemplate.getForEntity(backendUrl + "/" + id, Map.class);
        Map<String, Object> songMap = response.getBody(); // Ya no accedemos a "Song"

        SongEntity song = new SongEntity();
        song.setId((String) songMap.get("id"));
        song.setSongName((String) songMap.get("songName"));
        song.setSongArtist((String) songMap.get("songArtist"));
        song.setSongAlbum((String) songMap.get("songAlbum"));
        song.setSongYear((String) songMap.get("songYear"));

        model.addAttribute("song", song);
        return "song-update";
    }



    @PostMapping("/update/{id}")
    public String updateSong(@PathVariable String id, @ModelAttribute SongEntity song) {
        song.setId(id);  // Asignar el ID como String directamente

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SongEntity> request = new HttpEntity<>(song, headers);

        restTemplate.exchange(backendUrl + "/" + id, HttpMethod.PUT, request, Void.class);
        return "redirect:/web/songs";
    }


    @GetMapping("/delete/{id}")
    public String deleteSong(@PathVariable UUID id) {
        restTemplate.delete(backendUrl + "/" + id);
        return "redirect:/web/songs";
    }
}
