package com.house.rental.service;

import com.house.rental.entity.Favorite;
import java.util.List;

public interface FavoriteService {
    void addFavorite(Long userId, Long houseId);
    void removeFavorite(Long userId, Long houseId);
    List<Favorite> getUserFavorites(Long userId);
    boolean isFavorite(Long userId, Long houseId);
} 