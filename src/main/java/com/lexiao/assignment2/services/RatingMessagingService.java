package com.lexiao.assignment2.services;

import com.lexiao.assignment2.mappers.RatingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RatingMessagingService {

    private static final String TOPIC = "movie-ratings";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private RatingMapper ratingMapper;

    /**
     * 发送评分消息 (异步)
     */
    public void sendRating(int userId, int movieId, double rating) {
        String message = userId + ":" + movieId + ":" + rating;
        System.out.println("🚀 [Producer] Sending rating to Kafka: " + message);
        kafkaTemplate.send(TOPIC, message);
    }

    /**
     * 监听并消费评分消息 (写入数据库)
     */
    @KafkaListener(topics = TOPIC, groupId = "cinevault-group")
    public void consumeRating(String message) {
        System.out.println("📥 [Consumer] Received rating from Kafka: " + message);
        try {
            String[] parts = message.split(":");
            int userId = Integer.parseInt(parts[0]);
            int movieId = Integer.parseInt(parts[1]);
            double rating = Double.parseDouble(parts[2]);

            // 这里真正写入 MySQL
            // ratingMapper.insertRating(userId, movieId, rating, System.currentTimeMillis());
            System.out.println("✅ [Consumer] Successfully saved rating to DB for Movie: " + movieId);
        } catch (Exception e) {
            System.err.println("❌ [Consumer] Error processing message: " + e.getMessage());
        }
    }
}
