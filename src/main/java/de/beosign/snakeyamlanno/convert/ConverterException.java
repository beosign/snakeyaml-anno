package de.beosign.snakeyamlanno.convert;

import org.yaml.snakeyaml.error.YAMLException;

/**
 * Can be thrown if conversion is not possible.
 * 
 * @author florian
 */
public class ConverterException extends YAMLException {
    private static final long serialVersionUID = 1L;

    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
