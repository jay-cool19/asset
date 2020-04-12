package com.jay.asset.resources;

import com.codahale.metrics.annotation.Timed;
import com.jay.asset.api.Asset;
import com.jay.asset.core.AssetEntity;
import com.jay.asset.db.AssetDao;
import com.jay.asset.resources.mapper.AddressMapper;
import com.jay.asset.resources.mapper.AssetMapper;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import io.swagger.annotations.*;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/asset")
@Api("/asset")
@Produces(MediaType.APPLICATION_JSON)
public class AssetResource {

    private AssetDao assetDao;

    public AssetResource(AssetDao assetDao) {
        this.assetDao = assetDao;
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    @Timed
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of asset", response = Asset.class),
            @ApiResponse(code = 404, message = "Asset with given id not found"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Asset findById(@ApiParam(value = "ID number (long)", required = true) @Valid @PathParam("id") LongParam id) {
        AssetEntity assetEntity = assetDao.findById(id.get());
        if (assetEntity == null) {
            throw new NotFoundException("Asset with given id not found");
        }
        return AssetMapper.mapToAsset(assetEntity);
    }

    @POST
    @UnitOfWork
    @Timed
    @ApiOperation(
            value = "Save an Asset to the DB. The provided asset will replace an existing asset with the same id, if found.",
            response = Asset.class
    )
    public Asset save(@ApiParam(required = true) @Valid Asset asset) {
        return AssetMapper.mapToAsset(assetDao.save(
                new AssetEntity(asset.getId(), asset.getSerialNumber(), asset.getModelName(), AddressMapper.mapToAddressEntity(asset.getAddress()))));
    }

    @GET
    @UnitOfWork
    @Timed
    public List<Asset> findAll() {
        return AssetMapper.mapToAssetList(assetDao.findAll());
    }

    @DELETE @Path("/{id}")
    @UnitOfWork
    public void delete(@ApiParam(value = "ID number (long)", required = true) @Valid @PathParam("id") LongParam id) {
        assetDao.delete(id.get());
    }
}