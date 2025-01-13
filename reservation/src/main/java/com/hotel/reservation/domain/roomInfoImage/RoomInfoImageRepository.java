package com.hotel.reservation.domain.roomInfoImage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomInfoImageRepository extends JpaRepository<RoomInfoImage, Long>, RoomInfoImageRepositoryCustom {
}
