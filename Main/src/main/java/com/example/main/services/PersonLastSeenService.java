package com.example.main.services;

import java.time.Instant;
import java.util.UUID;

public interface PersonLastSeenService {

    void createPersonLastSeen(String url, UUID personId, Instant time);
}
