package com.hotel.reservation.domain.booking;

public enum BookingStatus {
    CONFIRMED("확정"), CANCELLED("취소"), PENDING("입금확인중"), REFUNDED("환불완료");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
