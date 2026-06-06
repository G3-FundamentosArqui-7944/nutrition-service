package com.bodymatch.nutrition.shared;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import static io.github.encryptorcode.pluralize.Pluralize.pluralize;

public class SnakeCaseWithPluralizedTablePhysicalNamingStrategy implements PhysicalNamingStrategy {
    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) { return toSnakeCase(identifier); }
    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) { return toSnakeCase(identifier); }
    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) { return toSnakeCase(toPlural(identifier)); }
    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) { return toSnakeCase(identifier); }
    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) { return toSnakeCase(identifier); }

    private Identifier toSnakeCase(final Identifier identifier) {
        if (identifier == null) return null;
        return Identifier.toIdentifier(identifier.getText().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase());
    }

    private Identifier toPlural(final Identifier identifier) {
        return Identifier.toIdentifier(pluralize(identifier.getText()));
    }
}
