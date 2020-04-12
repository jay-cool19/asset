package com.jay.asset;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import com.jay.asset.core.AddressEntity;
import com.jay.asset.core.AssetEntity;
import com.jay.asset.db.AssetDaoHib;
import com.jay.asset.health.TemplateHealthCheck;
import com.jay.asset.resources.AssetResource;
import com.jay.asset.resources.HelloWorldResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class AssetApplication extends Application<AssetConfiguration> {

    final static Logger log = LoggerFactory.getLogger(AssetApplication.class);

    // add entity classes to be mapped by Hibernate here
    private final HibernateBundle<AssetConfiguration> hibernateBundle = new HibernateBundle<AssetConfiguration>(AssetEntity.class, AddressEntity.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(AssetConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(String[] args) throws Exception {
        new AssetApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<AssetConfiguration> bootstrap) {

        bootstrap.addBundle(hibernateBundle);

        bootstrap.addBundle(new MigrationsBundle<AssetConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(AssetConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(new SwaggerBundle<AssetConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AssetConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(AssetConfiguration assetConfiguration, Environment environment) throws Exception {
        final TemplateHealthCheck healthcheck = new TemplateHealthCheck(assetConfiguration.getTemplate());
        environment.healthChecks().register("template", healthcheck);
        environment.jersey().register(new HelloWorldResource(assetConfiguration.getTemplate(),assetConfiguration.getDefaultName()));
        final AssetDaoHib assetDaoHib = new AssetDaoHib(hibernateBundle.getSessionFactory());
        AssetResource assetResource = new AssetResource(assetDaoHib);
        environment.jersey().register(assetResource);
    }

    private void migrateDb(AssetConfiguration configuration, Environment environment){
        //if (configuration.isMigrateSchemaOnStartup())
        log.info("Running Schema migration");
        ManagedDataSource dataSource = createMigrationDataSource(configuration,environment);
        try (Connection connection = dataSource.getConnection()){
            JdbcConnection conn = new JdbcConnection(connection);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation((DatabaseConnection) conn);
            Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
            log.info("Migration completed!");
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to migrate database", ex);
        } finally {
            try {
                dataSource.stop();
            } catch (Exception ex) {
                log.info("Unable to stop data source used to execute schema migration");
            }
        }
    }

    private ManagedDataSource createMigrationDataSource(AssetConfiguration configuration, Environment environment) {
        DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();
        return dataSourceFactory.build(environment.metrics(), "migration-ds");
    }

    @Override
    public String getName() {
        return "asset-application";  // name must match the name of the yaml config file
    }
}
