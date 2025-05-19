package com.usb.SongsInventory.Services;

import com.usb.SongsInventory.Entities.SongEntity;
import com.usb.SongsInventory.Repositories.SongRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class SongService {

    private final SongRepository songRepository;

    public ResponseEntity<?> getAllSongs(Pageable pageable) {
        Page<SongEntity> songs = songRepository.findAll(pageable);
        return ResponseEntity.ok(songs);
    }

    public ResponseEntity<?> getSongById(UUID id) {
        SongEntity song = songRepository.findById(id).orElse(null);
        if (song == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(song);
    }

    public ResponseEntity<?> getSongsBySongName(String songName, Pageable pageable) {
        Page<SongEntity> songs = songRepository.findAllBySongNameContaining(songName, pageable);
        return ResponseEntity.ok(songs);
    }

    private ResponseEntity<?> getResponseEntity(Page<SongEntity> songs) {
        Map<String, Object> response = new HashMap<>();
        response.put("TotalElements", songs.getTotalElements());
        response.put("TotalPages", songs.getTotalPages());
        response.put("CurrentPage", songs.getNumber());
        response.put("NumberOfElements", songs.getNumberOfElements());
        response.put("Songs", songs.getContent());
        return ResponseEntity.ok(response);
    }

    //******//

    public ResponseEntity<?> addSong(SongEntity songToAdd) {
        Page<SongEntity> song = songRepository.findAllBySongNameContaining(
                songToAdd.getSongName(),
                Pageable.unpaged());
        if (song.getTotalElements() > 0) {
            return new ResponseEntity<>(Collections.singletonMap("Status", String.format("Song already exists with %d coincidences.", song.getTotalElements())), HttpStatus.CONFLICT);
        } else {
            SongEntity savedSong = songRepository.save(songToAdd);
            return new ResponseEntity<>(Collections.singletonMap("Status", String.format("Added Song with ID %s", savedSong.getId())), HttpStatus.CREATED);
        }
    }

    public ResponseEntity<?> updateSong(UUID id, SongEntity songToUpdate) {
        Optional<SongEntity> song = songRepository.findById(id);
        if (song.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("Status", String.format("Song with ID %s not found.", id)), HttpStatus.NOT_FOUND);
        }
        SongEntity existingSong = song.get();

        existingSong.setSongName(songToUpdate.getSongName());
        existingSong.setSongArtist(songToUpdate.getSongArtist());
        existingSong.setSongAlbum(songToUpdate.getSongAlbum());
        existingSong.setSongYear(songToUpdate.getSongYear());
        songRepository.save(existingSong);

        return ResponseEntity.ok(Collections.singletonMap("Status", String.format("Updated Song with ID %s", existingSong.getId())));
    }

    public ResponseEntity<?> deleteSong(UUID id) {
        Optional<SongEntity> song = songRepository.findById(id);
        if (song.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("Status", String.format("Song with ID %s doesn't exist.", id)),HttpStatus.NOT_FOUND);
        }
        songRepository.deleteById(id);
        return ResponseEntity.ok(Collections.singletonMap("Status", String.format("Deleted Song with ID %s", id)));
    }
}
