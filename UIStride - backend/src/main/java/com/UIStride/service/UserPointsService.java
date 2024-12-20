package com.UIStride.service;

import com.UIStride.model.Account;
import com.UIStride.model.Points;
import com.UIStride.model.UserPoints;
import com.UIStride.repository.AccountRepository;
import com.UIStride.repository.PointsRepository;
import com.UIStride.repository.UserPointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserPointsService {

    @Autowired
    private PointsRepository pointsRepository; // Repository untuk tabel points
    @Autowired
    private UserPointsRepository userPointsRepository; // Repository untuk tabel user_points
    @Autowired
    private AccountRepository accountRepository; // Repository untuk tabel account

    // Fungsi untuk menghitung total poin berdasarkan account
    public UserPoints updateUserPoints(Long accountId) {
        // Mencari account berdasarkan accountId
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Ambil data poin dari tabel points berdasarkan account
        List<Points> pointsList = pointsRepository.findByAccountId(accountId);

        // Hitung total poin yang diperoleh
        int totalPoints = pointsList.stream()
                .mapToInt(Points::getPointsAwarded)
                .sum();

        // Cek apakah sudah ada data user_points untuk account
        UserPoints userPoints = userPointsRepository.findByAccount(account);
        if (userPoints == null) {
            // Jika tidak ada, buat data baru
            userPoints = new UserPoints();
            userPoints.setAccount(account);
            userPoints.setTotalPoints(totalPoints);
            userPoints.setCreatedAt(LocalDateTime.now()); // Set waktu pembuatan
        } else {
            // Jika sudah ada, cek apakah total poin baru lebih besar dari yang ada
            int currentPoints = userPoints.getTotalPoints();
            if (totalPoints > currentPoints) {
                userPoints.setTotalPoints(totalPoints); // Update total points
            }
        }

        // Set lastUpdated untuk menandakan pembaruan terakhir
        userPoints.setLastUpdated();

        // Simpan atau update data user_points
        return userPointsRepository.save(userPoints);
    }
}

