package com.usb.SongsInventoryFront.Repositories;

import com.usb.SongsInventoryFront.Entities.SongEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, UUID> {

    Page<SongEntity> findAllBySongNameContaining(String SongName, Pageable pageable);

    @Override
    Page<SongEntity> findAll(Pageable pageable);

}
