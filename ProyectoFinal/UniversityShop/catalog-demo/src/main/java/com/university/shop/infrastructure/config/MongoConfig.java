package com.university.shop.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Habilita la auditoría de MongoDB para que @CreatedDate en Product
 * se complete automáticamente al persistir el documento.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
