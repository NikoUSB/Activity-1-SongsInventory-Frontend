package com.usb.SongsInventoryFront.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Entity
@Table(name = "songs_entity")
@AllArgsConstructor
@NoArgsConstructor
public class SongEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    @JsonProperty("id")
    private UUID id;

    @Setter
    @Column(name = "song_name", nullable = false, length = 255)
    @JsonProperty("songName")
    @NotBlank(message = "Song name is required")
    @Size(min = 3, max = 100, message = "Song Name must be between 3 and 100 characters")
    private String songName;

    @Setter
    @Column(name = "song_artist", nullable = false, length = 255)
    @JsonProperty("songArtist")
    @NotBlank(message = "Artist name is required")
    @Size(min = 3, max = 100, message = "Artist Name must be between 3 and 100 characters")
    private String songArtist;

    @Setter
    @Column(name = "song_album", nullable = false, length = 255)
    @JsonProperty("songAlbum")
    @NotBlank(message = "Album name is required")
    @Size(min = 3, max = 100, message = "Album Name must be between 3 and 100 characters")
    private String songAlbum;

    @Setter
    @Column(name = "song_year", nullable = false, length = 4)
    @JsonProperty("songYear")
    @NotBlank(message = "Year is required")
    @Pattern(regexp = "^(15|20)\\d{2}$", message = "Year must be a valid 4-digit number (1500-2099)")
    private String songYear;

    @Override
    public String toString() {
        return "PcGamesEntity{" +
                "id='" + id + '\'' +
                ", gameName='" + songName + '\'' +
                ", gameYear='" + songArtist + '\'' +
                ", gameScore=" + songAlbum +
                ", gamePublisher='" + songYear + '\'' +
                '}';
    }
}
