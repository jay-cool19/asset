package com.jay.asset.db;

import com.jay.asset.core.AssetEntity;

import java.util.List;

public interface AssetDao {
    AssetEntity findById(Long id);

    AssetEntity save(AssetEntity assetEntity);

    void delete(Long id);

    List<AssetEntity> findAll();
}
