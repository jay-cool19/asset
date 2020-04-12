package com.jay.asset.resources.mapper;

import com.jay.asset.api.Asset;
import com.jay.asset.core.AssetEntity;

import java.util.ArrayList;
import java.util.List;

public class AssetMapper {
    public static List<Asset> mapToAssetList(List<AssetEntity> all) {
        List<Asset> assets = new ArrayList<Asset>();
        for (AssetEntity assetEntity : all) {
            assets.add(mapToAsset(assetEntity));
        }
        return assets;
    }

    public static Asset mapToAsset(AssetEntity assetEntity) {
        return new Asset(assetEntity.getId(), assetEntity.getSerialNumber(), assetEntity.getModelName(),
                AddressMapper.mapToAddress(assetEntity.getAddress()));
    }
}
