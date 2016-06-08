package com.piotrek.transactions.exception;

/**
 * This class represents an exception in the system configuration which cannot be resolved at runtime,
 * such as a missing resource in the classpath, a missing property in the properties file, etcetera.
 *
 * @author Piotrek
 */
public class SystemConfigurationException extends RuntimeException {

    // Constants ----------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Constructs a SystemConfigurationException with the given detail message.
     *
     * @param message The detail message of the DAOConfigurationException.
     */
    public SystemConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs a SystemConfigurationExceptionn with the given root cause.
     *
     * @param cause The root cause of the SystemConfigurationException.
     */
    public SystemConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SystemConfigurationException with the given detail message and root cause.
     *
     * @param message The detail message of the SystemConfigurationException.
     * @param cause   The root cause of the SystemConfigurationException.
     */
    public SystemConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
