package com.house.rental.service;

public interface NotificationService {
    void notifyFavoriteAdded(Long userId, Long houseId, String houseTitle);
    void notifyFavoriteRemoved(Long userId, Long houseId, String houseTitle);
    void notifyHouseStatusChange(Long houseId, String statusText);
} 