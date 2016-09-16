package de.beosign.snakeyamlanno.exception;

import org.yaml.snakeyaml.error.YAMLException;

/**
 * Thrown to indicate that an attempt has been made to access an aliased property by its old name.
 * 
 * @author florian
 */
public class AliasedYAMLException extends YAMLException {
    private static final long serialVersionUID = 1L;

    private final String aliasedProperty;
    private final String alias;

    /**
     * Construct new exception.
     * 
     * @param message message
     * @param aliasedProperty the property where the alias has been defined
     * @param alias the alias under which the old property is now accessible
     */
    public AliasedYAMLException(String message, String aliasedProperty, String alias) {
        super(message);
        this.alias = alias;
        this.aliasedProperty = aliasedProperty;
    }

    /**
     * The name of the original property.
     * 
     * @return name of the original property.
     */
    public String getAliasedProperty() {
        return aliasedProperty;
    }

    /**
     * The alias.
     * 
     * @return alias
     */
    public String getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        return "AliasedYAMLException [aliasedProperty=" + aliasedProperty + ", alias=" + alias + "]";
    }

}
