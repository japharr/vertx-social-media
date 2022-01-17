package com.japharr.socialmedia.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import org.testcontainers.containers.PostgreSQLContainer;

public class WebVerticle extends AbstractVerticle {
  PgConnectOptions options;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.4");
    postgres.start();

    options = new PgConnectOptions()
        .setPort(postgres.getMappedPort(5432))
        .setHost(postgres.getContainerIpAddress())
        .setDatabase(postgres.getDatabaseName())
        .setUser(postgres.getUsername())
        .setPassword(postgres.getPassword());

    Pool pool = Pool.pool(vertx, options, new PoolOptions().setMaxSize(4));

    // create a test table
    pool.query("create table test(id int primary key, name varchar(255))")
        .execute()
        .compose(r ->
            // insert some test data
            pool
                .query("insert into test values (1, 'Hello'), (2, 'World')")
                .execute()
        ).compose(r ->
            // query some data
            pool
                .query("select * from test")
                .execute()
        ).onSuccess(rows -> {
          for (Row row : rows) {
            System.out.println("row = " + row.toJson());
          }
        }).onFailure(Throwable::printStackTrace);
  }
}
