package com.gem.baize.admin.id.service.impl;

import com.gem.baize.admin.id.service.IDService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 默认的ID生成服务实现 UUID
 */
@Service
public class BasicIDService implements IDService<String, String> {
    private static final Pattern ID_PATTERN = Pattern.compile("^([a-z]{3})-[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}$");

    @Override
    public String generate(String entityType) {
        return encode(null, entityType);
    }

    @Override
    public String decode(String publicId) {
        return publicId;
    }

    @Override
    public String encode(String internalId, String entityType) {
        if (entityType == null || entityType.length() != 3) {
            throw new IllegalArgumentException("Entity type must be 3 characters long");
        }
        return entityType.toLowerCase() + "-" + UUID.randomUUID();
    }

    @Override
    public boolean validate(String id) {
        return id != null && ID_PATTERN.matcher(id).matches();
    }

    @Override
    public String getType(String id) {
        if (!validate(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }
        return id.substring(0, id.indexOf("-"));
    }
}
