package com.japharr.socialmedia.auth.config;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.sqlclient.SqlClient;

import java.util.Map;

@VertxGen
public interface CustomSqlAuthentication extends AuthenticationProvider {

  /**
   * Create a JDBC auth provider implementation
   *
   * @param client  the JDBC client instance
   * @return  the auth provider
   */
  static io.vertx.ext.auth.sqlclient.SqlAuthentication create(SqlClient client) {
    return create(client, new SqlAuthenticationOptions());
  }

  /**
   * Create a JDBC auth provider implementation
   *
   * @param client  the JDBC client instance
   * @param options authentication options
   * @return  the auth provider
   */
  static io.vertx.ext.auth.sqlclient.SqlAuthentication create(SqlClient client, SqlAuthenticationOptions options) {
    return new CustomAuthenticationImpl(client, options);
  }

  /**
   * Hashes a password to be stored.
   *
   * See: {@link io.vertx.ext.auth.HashingStrategy#hash(String, Map, String, String)}
   */
  String hash(String id, Map<String, String> params, String salt, String password);

  /**
   * Hashes a password to be stored.
   *
   * See: {@link io.vertx.ext.auth.HashingStrategy#hash(String, Map, String, String)}
   */
  default String hash(String id, String salt, String password) {
    return hash(id, null, salt, password);
  }
}

