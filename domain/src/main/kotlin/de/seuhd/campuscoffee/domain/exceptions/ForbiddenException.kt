package de.seuhd.campuscoffee.domain.exceptions

/**
 * Thrown when a user is not authorized to perform an operation (for example, when a non-admin user tries to record
 * a cash deposit).
 */
class ForbiddenException(
    message: String
) : RuntimeException(message)
